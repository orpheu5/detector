package com.servicenow.detector;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class TestBitUtil {

    private static ByteImage image1 = getByteImage("Image1",
            "           " + "\n" +
            "   +   +   " + "\n" +
            "    + +    " + "\n" +
            "    +++    " + "\n" +
            "    +++    " + "\n" +
            "   +   +   " + "\n" +
            "           " + "\n");
    
    private static ByteImage image2 = getByteImage("Image1",
            "                                   " + "\n" +
            "   +   +                   +   +   " + "\n" +
            "    + +                     + +    " + "\n" +
            "    +++                     +++    " + "\n" +
            "    +++                     +++    " + "\n" +
            "   +   +                   +   +   " + "\n" +
            "                                   " + "\n");
    private static ByteImage image3 = getByteImage("Image1",
            //"         " + "\n" +
            "++" + "\n" +
            "++" + "\n" +
            "++" + "\n" +
            "++" + "\n");// +
            //"+ " + "\n" +
            //"       " + "\n");
    
    private static ByteImage image4 = getByteImage("Image1",
            "   ++     " + "\n" +
            "   ++     " + "\n" +
            "   ++     " + "\n" +
            "   ++     " + "\n" +
            "   ++     " + "\n" +
            "   ++     " + "\n" +
            "          " + "\n");
    
    @Test
    public void testName2() throws Exception {
        for (int i = 0; i < image4.getLineCount(); i++) {
            for (int j = 0; j < image4.getByteLineLength(); j++) {
                for (int j2 = 0; j2 < 8; j2++) {
                    byte[][] data = BitUtil.shift2D(image3.getData(), j2);
                    byte[][] mask = BitUtil.shift2D(image3.getMask(), j2);
                    //print(data,mask, j, i);
                    //print(image4.getData(),image4.getMask(), 0, 0);

                    
                    
                    
                    int dist = BitUtil.hammingDistance2D(data, mask, image4.getData(), image4.getMask(), j, i, 50);
                    System.out.println("y=" + i + " x=" + (j*8+j2) + " dist=" + dist);
                }
            }
        }
        
    }
    
    private void print(byte[][] data, byte[][] mask, int x, int y) {
//        for (int i = 0; i < y; i++) {
//            for (int j = 0; j < data.length; j++) {
//                System.out.print("0");
//            }
//        }
        
        for (int i = 0; i < data.length; i++) {
            System.out.println(Hex.toBinary(data[i]) + " : " + Hex.toBinary(mask[i]));
        }
    }

    @Test
    public void testName() throws Exception {
        int[] results = new int[3];
        for (int j = 0; j < results.length; j++) {
            int dist = BitUtil.hammingDistance1D(
                    Hex.fromBinaryString("00110000 11010111"), 
                    Hex.fromBinaryString("11111100 00010000"), 
                    Hex.fromBinaryString("00110011 00110011"), 
                    Hex.fromBinaryString("11111111 11110000"), 
                    j);
            results[j] = dist;
        }
        Assert.assertArrayEquals(new int[]{0, 3, 7}, results);
    }
    
    private static void print(byte[] data, byte[] mask) {
         System.out.println(Hex.toBinary(data) + " : " + Hex.toBinary(mask));
    }

    //@Test
    public void dtest() {
        for (byte[] line : BitUtil.shift2D(image1.getData(),2)) {
            System.out.println(Hex.toBinary(line));
        }
        for (byte[] line : BitUtil.shift2D(image1.getMask(),2)) {
            System.out.println(Hex.toBinary(line));
        }
            
    }
    private static ByteImage getByteImage(String name, String image) {
        try {
            return ByteImage.fromReader(new InputStreamReader(new ByteArrayInputStream(image.getBytes())), name, '+', ' ', 'x');
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
