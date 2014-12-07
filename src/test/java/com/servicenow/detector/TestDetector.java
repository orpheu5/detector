package com.servicenow.detector;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TestDetector {
    private static ByteImage pattern1 = getBytePattern("pattern1",
            "+++" + "\n" +
            "+ +" + "\n" +
            "+++" + "\n" +
            "+ +" + "\n" +
            "+++" + "\n");
    
    private static ByteImage pattern2 = getBytePattern("pattern2",
            "+   +" + "\n" +
            " + + " + "\n" +
            " +++ " + "\n" +
            " + + " + "\n" +
            "+   +" + "\n");
    
    private static ByteImage image1 = getByteImage("Image1",
            "               " + "\n" +
            "        +   +  " + "\n" +
            "         + +   " + "\n" +
            "         +++   " + "\n" +
            "         + +   " + "\n" +
            "        +   +  " + "\n" +
            "               " + "\n");
    
//    @Test
    public void testName() throws Exception {
       Map<Image, List<MatchResult>> result = Detector.detect(Arrays.asList(pattern2), Arrays.asList(image1), 90);
       for (Image image : result.keySet()) {
           System.out.println(image.getName() + " match " + result.get(image).size());
       }
    }
    
    public static void main(String[] args) {
        Map<Image, List<MatchResult>> result = Detector.detect(Arrays.asList(pattern1), Arrays.asList(image1), 90);
        for (Image image : result.keySet()) {
            System.out.println(image.getName() + " match " + result.get(image).size());
        }
    }
    
    private static ByteImage getBytePattern(String name, String image) {
        try {
            return ByteImage.fromReader(new InputStreamReader(new ByteArrayInputStream(image.getBytes())), name, '+', 'x', ' ');
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
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
    
    @Test
    public void testName2() throws Exception {
        for (int y = 0; y < image1.getLineCount(); y++) {
            for (int x = 0; x < image1.getByteLineLength(); x++) {
                final int dist = BitUtil.hammingDistance2D(pattern2.getData(), pattern2.getMask(), image1.getData(), image1.getMask(), x, y, 50);
                System.out.println("X="+x + " Y=" + y + " Dist=" + (100 - dist*100/pattern2.getNonMaskChars()) + "   (" + (pattern2.getNonMaskChars()-dist) + "/" +pattern2.getNonMaskChars()+")");
            }
        }
    }
    
    
    
    
    
    
}
