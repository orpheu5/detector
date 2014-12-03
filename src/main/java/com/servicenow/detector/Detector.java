package com.servicenow.detector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class Detector {
    
    public static Map<Image, List<MatchResult>> detect(List<File> imageFiles, List<File> patternFiles, char oneChar, char zeroChar, char maskChar, int threshold) throws IllegalArgumentException, IOException{
        final List<ByteImage> patterns = new ArrayList<>();
        final List<ByteImage> images = new ArrayList<>();
        for (File f : imageFiles) {
            images.add(ByteImage.fromFile(f, oneChar, zeroChar, maskChar));
        }
        for (File f : patternFiles) {
            patterns.add(ByteImage.fromFile(f, oneChar, zeroChar, maskChar));
        }
        return detect(patterns, images, threshold);
    }

    public static Map<Image, List<MatchResult>> detect(List<ByteImage> patterns, List<ByteImage> images, int threshold) {
        if (threshold < 0 || threshold > 100) throw new IllegalArgumentException("Threshold must be a percentage: 0 <= threshold <= 100, not " + threshold);
        
        //Split by pattern
        List<ForkJoinTask<List<MatchResult>>> tasks = new ArrayList<>();
        final ForkJoinPool fjPool = new ForkJoinPool(); //TODO check this
        for (final ByteImage image : images) {
            for (final ByteImage pattern : patterns) {
                for (int i = 0; i < 8; i++) {
                    final byte[][] shiftedPattern = BitUtil.shift2D(pattern.getData(), i);
                    final byte[][] shiftedMask = BitUtil.shift2D(pattern.getMask(), i);
                    final int startXOffset = - pattern.getByteLineLength() * (100-threshold) / 100;
                    final int startYOffset = - pattern.getLineCount() * (100-threshold) / 100;
                    final int distThreshold = pattern.getNonMaskChars() * (100-threshold) / 100;
                    final int patternShift = i;
                    tasks.add(fjPool.submit(() -> {
                            final List<MatchResult> results = new ArrayList<>();
                            for (int y = startYOffset; y < image.getLineCount(); y++) {
                                for (int x = startXOffset; x < image.getByteLineLength(); x++) {
                                    final int dist = BitUtil.hammingDistance2D(shiftedPattern, shiftedMask, image.getData(), image.getMask(), x, y, distThreshold);
                                    if (dist < distThreshold) results.add(new ByteMatchResult(image, pattern, patternShift, x, y, dist));
                                }
                            }
                            return results;
                        }));
                }
            }
        }
        
        final Map<Image,List<MatchResult>> resultMap = new HashMap<>();
        for (ByteImage byteImage : images) {
            resultMap.put(byteImage, new ArrayList<>());
        }
        
        for (ForkJoinTask<List<MatchResult>> task : tasks) {
            try {
                for (MatchResult r : task.get()) {
                    resultMap.get(r.getImage()).add(r);
                }
            } catch (InterruptedException | ExecutionException e) {
                //TODO implement logger and log!
                e.printStackTrace();
            }
        }

        return resultMap;
    }
}
