package com.servicenow.detector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.servicenow.detector.terminalgui.TerminalGUI;

public class Main {
    private static final Logger log = Logger.getLogger(Main.class.getName());
   
    private static final char one =  '+';
    
    public static void main(String[] args) throws IllegalArgumentException, IOException {
        String imageDir = null;
        String patternDir = null;
        int threshold = 0;
        List<File> images = null;
        List<File> patterns = null;
        try{
            if (args.length != 3) usage("incorrect number of arguments");
            imageDir = args[0];
            patternDir = args[1];
            try{
                threshold = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid threshold " + args[2]);
            }
            
            images = getFiles(imageDir);
            patterns = getFiles(patternDir);
        } catch (Exception e) {
            usage(e.getMessage());
        }
        
        final Map<BinaryCharImage, List<MatchResult>> results = Detector.detect(
                images, 
                patterns,
                one,
                threshold);
        
        for (BinaryCharImage image : results.keySet()) {
            log.info("Detected " + results.get(image).size() + " patterns in image " + image.getName());
        }
        
        new TerminalGUI().show(results);
    }

    private static void usage(String error) {
        System.out.println(
                "Error: " + error + "\n" +
                "usage: <imageDir> <patternDir> <threshold> " + "\n" +
                "\n" +
                "   <imageDir>   directory containing image files to include in detection ('"+one+"' constitute image, ' ' used as non-image)" + "\n" +
                "   <patternDir> directory containing pattern files to include in detection ('"+one+"' constitute pattern, ' ' used as mask)" + "\n" +
                "   <threshold>  percentage [0 - 100] overlap of pattern with part of the image to trigger a match");
        System.exit(1);
    }

    private static List<File> getFiles(String dirPath) {
        List<File> l = new ArrayList<File>();
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) throw new IllegalArgumentException("Invalid directory "  + dirPath);
        
        for (File file : dir.listFiles()) {
            l.add(file);
        }
        if (l.isEmpty()) throw new IllegalArgumentException("Directory "  + dirPath + " is empty");
        return l;
    }
}
