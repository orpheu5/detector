package com.servicenow.detector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class TestDetector {   
    private static BytePattern pattern2 = TestUtil.getBytePattern("pattern2",
            "+   +" + "\n" +
            " + + " + "\n" +
            " + + " + "\n" +
            " + + " + "\n" +
            "+   +" + "\n");
    
    private static ByteImage image1 = TestUtil.getByteImage("Image1",
            " +              " + "\n" +
            "+   +    +   +  " + "\n" +
            "+    +    + +   " + "\n" +
            "+    +    + +   " + "\n" +
            " +   +    + +   " + "\n" +
            "    +    +   +  " + "\n" +
            "  +   +         " + "\n" +
            "   + +          " + "\n" +
            "   + +          " + "\n");
    
    @SuppressWarnings("static-method")
    @Test
    public void testSingleMatch() throws Exception {
       Map<BinaryCharImage, List<MatchResult>> result = Detector.detect(Arrays.asList(image1), Arrays.asList(pattern2), 90,0);
       
       Assert.assertEquals(1, result.size());              //1 key returned
       Assert.assertEquals(1, result.get(image1).size());  //1 match
       
       Set<TestMatchResult> expected = new HashSet<>();
       expected.add(new TestMatchResult(image1, pattern2.getByteImage(), 9, 1, 100));
       
       Assert.assertTrue(containAll(expected, result));
    }
    
    @SuppressWarnings("static-method")
    @Test
    public void testMulti50() throws Exception {   
       Map<BinaryCharImage, List<MatchResult>> result = Detector.detect(Arrays.asList(image1), Arrays.asList(pattern2), 50, 0);
       
//       for (BinaryCharImage image : result.keySet()) {
//           System.out.println(image.getName() + " match " + result.get(image).size());
//           for (MatchResult mr : result.get(image)) {
//               System.out.println("Match " + mr.getPercentage() + "% at X=" + mr.getOffsetX() + ", Y=" + mr.getOffsetY());
//           }
//       }
       
       Assert.assertEquals(1, result.size());              //1 key returned
       Assert.assertEquals(5, result.get(image1).size());  //5 match
       
       Set<TestMatchResult> expected = new HashSet<>();
       expected.add(new TestMatchResult(image1, pattern2.getByteImage(), 9, 1, 100));
       expected.add(new TestMatchResult(image1, pattern2.getByteImage(), 1, 4, 50));
       expected.add(new TestMatchResult(image1, pattern2.getByteImage(), 2, 6, 60));
       expected.add(new TestMatchResult(image1, pattern2.getByteImage(), 4, 1, 50));
       expected.add(new TestMatchResult(image1, pattern2.getByteImage(), -3, 0, 50));
       
       Assert.assertTrue(containAll(expected, result));
    }
    
    private static boolean containAll(Set<TestMatchResult> lookFor, Map<BinaryCharImage, List<MatchResult>> result) {
        for (List<MatchResult> results : result.values()) {
            for (MatchResult matchResult : results) {
                boolean found = false;
                for (Iterator<TestMatchResult> iter = lookFor.iterator();iter.hasNext();){
                    TestMatchResult t = iter.next();
                    if (t.equalsResult(matchResult)) {
                        iter.remove();
                        found = true;
                    }
                }
                if (!found) return false;
            }
        }
        return lookFor.isEmpty();
    }
}
