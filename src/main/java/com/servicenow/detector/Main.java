package com.servicenow.detector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.servicenow.detector.terminalgui.TerminalGUI;

public class Main {
    private static final char one =  '+';
    private static final char zero = ' ';
    private static final char mask = 'x';
    
    public static void main(String[] args) throws IllegalArgumentException, IOException {
        String imageDir = args[0];
        String patternDir = args[1];
        int threshold = 50;
        
        final Map<Image, List<MatchResult>> results = Detector.detect(getFiles(imageDir), getFiles(patternDir), one, zero, mask, threshold);
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
