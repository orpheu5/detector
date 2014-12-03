package com.servicenow.detector;



public class BitUtil {
    private static final byte[] BYTE_POPCOUNT = new byte[]{0,1,1,2,1,2,2,3,1,2,2,3,2,3,3,4,1,2,2,3,2,3,3,4,2,3,3,4,3,4,4,5,1,2,2,3,2,3,3,4,2,3,3,4,3,4,4,5,2,3,3,4,3,4,4,5,3,4,4,5,4,5,5,6,1,2,2,3,2,3,3,4,2,3,3,4,3,4,4,5,2,3,3,4,3,4,4,5,3,4,4,5,4,5,5,6,2,3,3,4,3,4,4,5,3,4,4,5,4,5,5,6,3,4,4,5,4,5,5,6,4,5,5,6,5,6,6,7,1,2,2,3,2,3,3,4,2,3,3,4,3,4,4,5,2,3,3,4,3,4,4,5,3,4,4,5,4,5,5,6,2,3,3,4,3,4,4,5,3,4,4,5,4,5,5,6,3,4,4,5,4,5,5,6,4,5,5,6,5,6,6,7,2,3,3,4,3,4,4,5,3,4,4,5,4,5,5,6,3,4,4,5,4,5,5,6,4,5,5,6,5,6,6,7,3,4,4,5,4,5,5,6,4,5,5,6,5,6,6,7,4,5,5,6,5,6,6,7,5,6,6,7,6,7,7,8};

//    public interface Listener{
//        public void processMatch(int patternId, int textOffset, int diff);
//    }
//    
//    /**
//     * Multiple pattern 1D match
//     */
//    public static void search(byte[][] patterns,byte[][] patternMasks, byte[] text, byte[] textMask, int threshold, Listener listener){
//        final int pLength = patterns[0].length;
//        for (int i = -pLength; i < text.length; i++) {
//            for (int j = 0; j < patterns.length; j++) {
//                final int diff = hammingDistance1D(patterns[j], patternMasks[j], text, textMask, i);
//                if (diff <= threshold) listener.processMatch(j, i, diff);
//            }
//        }
//    }
    
    public static int hammingDistance2D(byte[][] pattern, byte[][] patternMask, byte[][] text, byte[][] textMask, int xOffset, int yOffset, int maxDist){
        int dist = 0;
        for (int i = 0; i < pattern.length; i++) {
            if (i + yOffset < 0 || i + yOffset >= text.length)
                dist = bitCount(patternMask[i]);
            else 
                dist += hammingDistance1D(pattern[i], patternMask[i], text[i + yOffset], textMask[i + yOffset], xOffset);
            
            if (dist >= maxDist) return maxDist;
        }
        return dist;
    }

    private static int bitCount(byte[] bytes) {
        int c = 0;
        for (byte b : bytes) c += Integer.bitCount(0xFF & b);
        return c;
    }

    private static int hammingDistance1D(byte[] pattern, byte[] patternMask, byte[] text, byte[] textMask, int textOffset) {
        int dist = 0;
        int[] r = new int[pattern.length];
        for (int i = 0; i < pattern.length; i++) {
            //If the sliding pattern do not overlap with text, use mask for distance 
            if (i + textOffset < 0 || i + textOffset >= text.length)
                r[i] = Integer.bitCount(0xFF & patternMask[i]);
            else 
                r[i] = BYTE_POPCOUNT[(pattern[i] ^ text[i + textOffset]) & patternMask[i] & textMask[i + textOffset] & 0xFF];
            
            dist += r[i];
        }
//        if (textOffset > 0)System.out.println("P=" + Hex.toBinary(pattern) + "\nT=" + Hex.toBinary(Arrays.copyOfRange(text, textOffset, text.length)) + "\nM="+ Hex.toBinary(mask) + "\nDIST=" + Arrays.toString(r));
        return dist;
    }
    
//    public static byte[][] getShifted(byte[] b){
//        final byte[][] r = new byte[8][b.length + 1];
//        for (int i = 0; i < 8; i++) {
//            byte carry = 0;
//            for (int j = 0; j < b.length; j++) {
//                r[i][j] = (byte)(carry |  ((b[j] & 0xFF) >>> i));
//                carry = (byte) (b[j] << 8-i);
//            }
//            r[i][b.length] = carry;
//        }
//        return r;
//    }
    
//    public static byte[][] getDefaultShiftedMasks(int length){
//        final byte[][] r = new byte[8][length + 1];
//        byte[] mask = new byte[length + 1];
//        Arrays.fill(mask, (byte) 0xFF);
//        
//        
//        for (int i = 0; i < 8; i++) {
//            System.arraycopy(mask, 0, r[i], 0, length + 1);
//            r[i][0] = (byte) ((r[i][0]& 0xFF) >>> i);
//            r[i][length] = (byte) (~r[i][0]);
//        }
//        return r;
//    }
 
    @SuppressWarnings("unused")
    private static String generatePopCountTable() {
        final StringBuilder sb = new StringBuilder();
        sb.append("byte[] table = new byte[]{");
        String suffix= "";
        for (int i = 0; i < 256; i++) {
            sb.append(suffix);
            suffix = ",";
            sb.append(Integer.bitCount(i));
        }
        sb.append("};");
        return sb.toString();
    }
    
//    public static void main(String[] args) {
//        byte[][] patterns = getShifted(Hex.fromBinaryString("01110001 11111110 01110001"));
//        
//        for (byte[] bs : patterns) {
//            System.out.println(Hex.toBinary(bs));
//        }
//        System.out.println();
//        
//        match(patterns, getDefaultShiftedMasks(3),Hex.fromBinaryString("11111111 01110001 10011110 01110001"), 5,  new Listener() {
//            @Override
//            public void processMatch(int patternId, int textOffset, int diff) {
//                System.out.println("Match at " + textOffset + " of id=" + patternId + " with diff="+diff);
//            }
//        });
//        
//    }

    public static byte[][] shift2D(byte[][] data, int shift) {
            if (shift==0) return data;
            int xSize = data[0].length;
            final byte[][] r = new byte[data.length][xSize + 1];
            for (int y = 0; y < data.length; y++) {
                byte carry = 0;
                for (int x = 0; x < xSize; x++) {
                    r[y][x] = (byte)(carry |  ((data[y][x] & 0xFF) >>> shift));
                    carry = (byte) (data[y][x] << 8-shift);
                }
                r[y][xSize] = carry;
            }
            return r;
    }
    
    
    
}
