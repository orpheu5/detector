package com.servicenow.detector;


public class ByteImage implements BinaryCharImage{
    private final char[][] chars;
    private final String name;
    private final char oneChar;
    private final char zeroChar;
    private final char maskChar;
    
    private final byte[][] data;
    private final byte[][] mask;
    
    private final int byteLineLength, lineCount, nonMaskChars;
    
    ByteImage(char[][] chars, String name, byte[][] data, byte[][] mask, char oneChar, char zeroChar, char maskChar, int byteLineLength, int lineCount, int nonMaskChars) {
        this.chars = chars;
        this.name = name;
        this.oneChar = oneChar;
        this.zeroChar = zeroChar;
        this.maskChar = maskChar;
        this.data = data;
        this.mask = mask;
        this.byteLineLength = byteLineLength;
        this.lineCount = lineCount;
        this.nonMaskChars = nonMaskChars;
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

    public int getMaskSize() {
        return nonMaskChars;
    }

    public int getLineCount() {
        return lineCount;
    }
    
    @Override
    public char[][] getChars() {
        return chars;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public char getOneChar() {
        return oneChar;
    }

    @Override
    public char getZeroChar() {
        return zeroChar;
    }

    @Override
    public char getMaskChar() {
        return maskChar;
    }
}
