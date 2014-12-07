package com.servicenow.detector;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestUtil {
    public static BytePattern getBytePattern(String name, String image) {
        try {
            return new BytePattern(ByteImageFactory.instanceFromReader(new InputStreamReader(new ByteArrayInputStream(image.getBytes())), name, '+', 'x', ' '));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static ByteImage getByteImage(String name, String image) {
        try {
            return ByteImageFactory.instanceFromReader(new InputStreamReader(new ByteArrayInputStream(image.getBytes())), name, '+', ' ', 'x');
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
