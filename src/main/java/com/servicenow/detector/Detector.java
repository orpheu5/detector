package com.servicenow.detector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Detector {
    private static final Logger log = Logger.getLogger(Detector.class.getName());
    
    private static class Task implements Callable<List<MatchResult>>{
        private final BytePattern pattern;
        private final ByteImage image;
        private final int threshold;  //Percentage
        private final int shift;
        
        private Task(BytePattern pattern, ByteImage image, int threshold, int shift) {
            this.pattern = pattern;
            this.image = image;
            this.threshold = threshold;
            this.shift = shift;
        }
        
        @Override
        public List<MatchResult> call() throws Exception {
            final int inverseThreshold = (100-threshold);
            final ByteImage pImage = pattern.getByteImage();
            final int startXOffset = - pImage.getByteLineLength() * inverseThreshold / 100 - 1;
            final int startYOffset = - pImage.getLineCount() * inverseThreshold / 100;
            final int distThreshold = pImage.getMaskSize() * inverseThreshold / 100;
            
            final List<MatchResult> results = new ArrayList<>();
            
            for (int y = startYOffset; y < image.getLineCount(); y++) {
                for (int x = startXOffset; x < image.getByteLineLength(); x++) {

                    final int dist = BitUtil.hammingDistance2D(
                            pattern.getShiftedData(shift), 
                            pattern.getShiftedMask(shift), 
                            image.getData(), 
                            image.getMask(), 
                            x, y, 
                            distThreshold);

                    if (dist != -1) results.add(new ByteMatchResult(image, pImage, shift, x, y, dist));
                }
            }
            return results;
        }
    }
    
    /**
     * Detect 2D binary (dictionary=2) patterns in 2D binary images. The patterns are constructed of the "oneChar" argument in both the image and pattern files. 
     * Spaces in the pattern files are treaded as mask bits, while spaces in the image files are treaded as zero bits. Only Ascii character are supported.
     * 
     * @param imageFiles list of image files
     * @param patternFiles list of pattern files
     * @param onechar character representing the pattern/images in the files
     * @param threshold minimum percentage threshold of matches (pattern bit matching image bit versus amount of bits in pattern)  
     * @return map of all pattern matches by supplied images. No matches will have empty match list for an image
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public static Map<BinaryCharImage, List<MatchResult>> detect(List<File> imageFiles, List<File> patternFiles, char oneChar, int threshold) throws IllegalArgumentException, IOException{
        final List<BytePattern> patterns = new ArrayList<>();
        final List<ByteImage> images = new ArrayList<>();
        
        for (File f : patternFiles) {
            patterns.add(new BytePattern(ByteImageFactory.instanceFromFile(f, oneChar, 'x', ' ')));
        }
        
        for (File f : imageFiles) {
            images.add(ByteImageFactory.instanceFromFile(f, oneChar, ' ', 'z'));
        }
        
        return detect(images, patterns, threshold, Runtime.getRuntime().availableProcessors());
    }

    /**
     * Detect 2D binary (dictionary=2) patterns in 2D binary images. See also the {@link #detect(List, List, char, int) detect} method.
     * @param patterns list of patterns
     * @param images list of images
     * @param threshold minimum percentage threshold of matches (pattern bit matching image bit versus amount of bits in pattern)  
     * @param nrThreads number of threads to include in the thread pool, 0 will default to Runtime.getRuntime().availableProcessors()
     * @return map of all pattern matches by supplied images. No matches will have empty match list for an image
     */
    public static Map<BinaryCharImage, List<MatchResult>> detect(List<ByteImage> images, List<BytePattern> patterns, int threshold, int nrThreads) {
        if (threshold < 0 || threshold > 100) throw new IllegalArgumentException("Threshold must be a percentage: 0 <= threshold <= 100, not " + threshold);
        
        //Split by pattern
        final List<Future<List<MatchResult>>> results = new ArrayList<>();
        final ExecutorService executor = Executors.newFixedThreadPool(nrThreads > 0 ? nrThreads : Runtime.getRuntime().availableProcessors());
       
        
        for (final BytePattern pattern : patterns) {
            for (final ByteImage image : images) {
                for (int i = 0; i < 8; i++) {
                    results.add(executor.submit(new Task(pattern, image, threshold, i)));
                }
            }
        }
        
        final Map<BinaryCharImage,List<MatchResult>> resultMap = new HashMap<>();
        //Construct empty list for each image
        for (ByteImage byteImage : images) {
            resultMap.put(byteImage, new ArrayList<>());
        }
        
        //Add all results
        for (Future<List<MatchResult>> future : results) {
            try {
                for (MatchResult r : future.get()) {
                    resultMap.get(r.getImage()).add(r);
                }
            } catch (InterruptedException | ExecutionException e) {
                log.log(Level.WARNING, "Unexpected exception occurred",e);
            }
        }
        
        executor.shutdown();
        
        return resultMap;
    }
}
