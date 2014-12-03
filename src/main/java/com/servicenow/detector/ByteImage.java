package com.servicenow.detector;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ByteImage extends Image{
    private final byte[][] data;
    private final byte[][] mask;
    private final int byteLineLength, lineCount, nonMaskChars;
    
    private ByteImage(char[][] chars, String name, byte[][] data, byte[][] mask, char oneChar, char zeroChar, char maskChar, int byteLineLength, int lineCount, int nonMaskChars) {
        super(chars, name, oneChar, zeroChar, maskChar);
        this.data = data;
        this.mask = mask;
        this.byteLineLength = byteLineLength;
        this.lineCount = lineCount;
        this.nonMaskChars = nonMaskChars;
    }
    
    public static ByteImage fromFile(File f, char oneChar, char zeroChar, char maskChar) throws IllegalArgumentException, IOException{
        return fromReader(new FileReader(f),f.getName(), oneChar, zeroChar, maskChar);
    }

    public static ByteImage fromReader(InputStreamReader in, String name, char oneChar, char zeroChar, char maskChar) throws IllegalArgumentException, IOException{
        try (BufferedReader reader = new BufferedReader(in)) {
            int lineLength = -1;
            int lineCount = 0;
            int byteLineLength = -1;
            int nonMaskChars = 0;
            
            final ArrayList<byte[]> tempData = new ArrayList<>();
            final ArrayList<byte[]> tempMask = new ArrayList<>();
            final ArrayList<char[]> tempChar = new ArrayList<>();
            
            String l = null;
            
            while ((l = reader.readLine()) != null) {
                final ByteArrayOutputStream linePattern = new ByteArrayOutputStream();
                final ByteArrayOutputStream lineMask = new ByteArrayOutputStream();
                
                if (lineLength != -1 && lineLength != l.length()) throw new IllegalArgumentException("Only rectangle byte images supported, image=" + name);
                lineLength = l.length();
                lineCount++;
                
                int bIndex = 0;
                byte currentByte = 0;
                byte currentMask = 0;
    
                for (int i = 0; i < l.length(); i++) {
                    currentByte <<= 1;
                    currentMask <<= 1;
                    char c = l.charAt(i);
                    
                    if      (c == oneChar) {currentByte |= 0x01; nonMaskChars++;}
                    else if (c == maskChar) currentMask |= 0x01;
                    else if (c == zeroChar) nonMaskChars++;
                    else throw new IllegalArgumentException("Illegal character detected in image=" + name +". Not one of: " + oneChar + "," + zeroChar +","+maskChar);
                    
                    if (++bIndex >= 8){
                        linePattern.write(currentByte);
                        lineMask.write(~currentMask);
                        bIndex = 0;
                        currentByte = 0;
                        currentMask = 0;
                    }
                }
    
                if (bIndex > 0){
                    currentMask = (byte) ~currentMask;
                    currentByte <<= 8-bIndex; 
                    currentMask <<= 8-bIndex;
    
                    linePattern.write(currentByte);
                    lineMask.write(currentMask);
                }
                
                if (byteLineLength != -1 && byteLineLength != linePattern.size()) throw new IllegalArgumentException("Unexpected mismatch in subsequent byte array lengths, image=" + name);
                byteLineLength = linePattern.toByteArray().length;
                
                tempData.add(linePattern.toByteArray());
                tempMask.add(lineMask.toByteArray());
                tempChar.add(l.toCharArray());
            }

            final byte[][] data = new byte[lineCount][byteLineLength];     //NB!!! assign image as consecutive 2D array
            final byte[][] mask = new byte[lineCount][byteLineLength];     //NB!!! assign mask as consecutive 2D array
            
            for (int i = 0; i < lineCount; i++) {
                for (int j = 0; j < byteLineLength; j++) {
                    data[i][j] = tempData.get(i)[j];
                    mask[i][j] = tempMask.get(i)[j];
                }
            }
        
            //Get 2D char array of iamge
            final char[][] chars = new char[tempChar.size()][lineLength];
            for (int i = 0; i < tempChar.size(); i++) {
                chars[i] = tempChar.get(i);
            }
            
            return new ByteImage(chars, name, data, mask, oneChar, zeroChar, maskChar, byteLineLength, lineCount, nonMaskChars);
        }
    }

    public byte[][] getData() {
        return data;
    }
    
    public byte[][] getMask() {
        return mask;
    }

    public int getByteLineLength() {
        return byteLineLength;
    }

    public int getNonMaskChars() {
        return nonMaskChars;
    }

    public int getLineCount() {
        return lineCount;
    }
}
