package com.servicenow.detector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.servicenow.detector.terminalgui.TerminalGUI;

public class Main {
    private static final char one =  '+';
    private static final char zero = 'x';
    private static final char mask = ' ';
    
    public static void main(String[] args) throws IllegalArgumentException, IOException {
        String imageDir = args[0];
        String patternDir = args[1];
        int threshold = 50;
        
        final List<ByteImage> patterns = new ArrayList<>();
        final List<ByteImage> images = new ArrayList<>();
        for (File f : getFiles(imageDir)) {
            images.add(ByteImage.fromFile(f, '+', ' ', 'x'));
        }
        for (File f : getFiles(patternDir)) {
            patterns.add(ByteImage.fromFile(f, '+', 'x', ' '));
        }
        
        final Map<Image, List<MatchResult>> results = Detector.detect(patterns, images, threshold);
        new TerminalGUI().show(results);
    }

    private static List<File> getFiles(String imageDir) {
        List<File> l = new ArrayList<File>();
        for (File file : new File(imageDir).listFiles()) {
            l.add(file);
        }
        return l;
    }
}
