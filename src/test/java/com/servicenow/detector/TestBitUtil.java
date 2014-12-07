package com.servicenow.detector;

import org.junit.Assert;
import org.junit.Test;

public class TestBitUtil {

    private static ByteImage test2DPattern = TestUtil.getByteImage("Image1",
            "++" + "\n" +
            "++" + "\n" +
            "++" + "\n" +
            "++" + "\n");// +
    
    private static ByteImage test2DImage = TestUtil.getByteImage("Image1",
            "   ++     " + "\n" +
            "   ++     " + "\n" +
            "   ++     " + "\n" +
            "   ++     " + "\n" +
            "   ++     " + "\n" +
            "   ++     " + "\n" +
            "          " + "\n");
    
    @SuppressWarnings("static-method")
    @Test
    public void test2DHammingDistance() throws Exception {
        int[][] results = new int[7][16];
        for (int i = 0; i < test2DImage.getLineCount(); i++) {
            for (int j = 0; j < test2DImage.getByteLineLength(); j++) {
                for (int j2 = 0; j2 < 8; j2++) {
                    byte[][] data = BitUtil.shift2D(test2DPattern.getData(), j2);
                    byte[][] mask = BitUtil.shift2D(test2DPattern.getMask(), j2);
                    int dist = BitUtil.hammingDistance2D(data, mask, test2DImage.getData(), test2DImage.getMask(), j, i, 50);
                    results[i][j*8+j2] = dist;
                }
            }
        }
        Assert.assertArrayEquals(new int[][]{
                {8,8,4,0,4,8,8,8,8,8,8,8,8,8,8,8},
                {8,8,4,0,4,8,8,8,8,8,8,8,8,8,8,8},
                {8,8,4,0,4,8,8,8,8,8,8,8,8,8,8,8},
                {8,8,5,2,5,8,8,8,8,8,8,8,8,8,8,8},
                {8,8,6,4,6,8,8,8,8,8,8,8,8,8,8,8},
                {8,8,7,6,7,8,8,8,8,8,8,8,8,8,8,8},
                {8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
                }, results);
    }

    @SuppressWarnings("static-method")
    @Test
    public void test1DHamming_Basic() throws Exception {
        int[] results = new int[3];
        
        //Basic pattern matching offset 0 and 1, but not 2 
        for (int j = 0; j < results.length; j++) {
            int dist = BitUtil.hammingDistance1D(
                    Binary.bytesFromString("00110000"),
                    Binary.bytesFromString("11111111"), 
                    Binary.bytesFromString("00110000 00110000"), 
                    Binary.bytesFromString("11111111 11111111"), 
                    j);
            results[j] = dist;
        }
        Assert.assertArrayEquals(new int[]{0, 0, 8}, results);
    }
    
    @SuppressWarnings("static-method")
    @Test
    public void test1DHamming_Basic2() throws Exception {
        //Basic pattern with mask matching offset 0=0 and 1=1, but not 2=mask(4) 
        int[] results = new int[3];
        for (int j = 0; j < results.length; j++) {
            int dist = BitUtil.hammingDistance1D(
                    Binary.bytesFromString("00110000"),
                    Binary.bytesFromString("00111100"), 
                    Binary.bytesFromString("11110011 10010001"), 
                    Binary.bytesFromString("11111111 11111111"), 
                    j);
            results[j] = dist;
        }
        Assert.assertArrayEquals(new int[]{0, 1, 4}, results);
    }
    
    @SuppressWarnings("static-method")
    @Test
    public void test1DHamming_Complex() throws Exception {
        
        //Complex pattern with mask matching
        int[] results = new int[3];
        for (int j = 0; j < results.length; j++) {
            int dist = BitUtil.hammingDistance1D(
                    Binary.bytesFromString("00110000 11010111"), 
                    Binary.bytesFromString("11111100 00010000"), 
                    Binary.bytesFromString("00110011 00110011"), 
                    Binary.bytesFromString("11111111 11110000"), 
                    j);
            results[j] = dist;
        }
        Assert.assertArrayEquals(new int[]{0, 3, 7}, results);
    }
    
    private static ByteImage shiftImage = TestUtil.getByteImage("Image1",
            "               " + "\n" +
            "+  +   +      +" + "\n" +
            "+   + +       +" + "\n" +
            "+    +        +" + "\n" +
            "+++++++++++++++" + "\n");
    
    @SuppressWarnings("static-method")
    @Test
    public void test2DShift() {
        String[] expected = new String[]
                {   
                "000000000000000000000000",
                "100100010000001000000000",
                "100010100000001000000000",
                "100001000000001000000000",
                "111111111111111000000000"
                };
        
        for (int i = 1; i < 8; i++) {
            //Shift expected result
            for (int j = 0; j < expected.length; j++) {
                expected[j] = "0" + expected[j].substring(0, expected[j].length()-1);
            }
            
            byte[][] result = BitUtil.shift2D(shiftImage.getData(),i);
            
            Assert.assertArrayEquals(new byte[][]{
                    Binary.bytesFromString(expected[0]),
                    Binary.bytesFromString(expected[1]),
                    Binary.bytesFromString(expected[2]),
                    Binary.bytesFromString(expected[3]),
                    Binary.bytesFromString(expected[4]),
                    }, 
                    result);
            
        }
    }
}
