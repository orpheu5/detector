package com.servicenow.detector;



public class BitUtil {
    private static final byte[] BYTE_POPCOUNT = new byte[]{0,1,1,2,1,2,2,3,1,2,2,3,2,3,3,4,1,2,2,3,2,3,3,4,2,3,3,4,3,4,4,5,1,2,2,3,2,3,3,4,2,3,3,4,3,4,4,5,2,3,3,4,3,4,4,5,3,4,4,5,4,5,5,6,1,2,2,3,2,3,3,4,2,3,3,4,3,4,4,5,2,3,3,4,3,4,4,5,3,4,4,5,4,5,5,6,2,3,3,4,3,4,4,5,3,4,4,5,4,5,5,6,3,4,4,5,4,5,5,6,4,5,5,6,5,6,6,7,1,2,2,3,2,3,3,4,2,3,3,4,3,4,4,5,2,3,3,4,3,4,4,5,3,4,4,5,4,5,5,6,2,3,3,4,3,4,4,5,3,4,4,5,4,5,5,6,3,4,4,5,4,5,5,6,4,5,5,6,5,6,6,7,2,3,3,4,3,4,4,5,3,4,4,5,4,5,5,6,3,4,4,5,4,5,5,6,4,5,5,6,5,6,6,7,3,4,4,5,4,5,5,6,4,5,5,6,5,6,6,7,4,5,5,6,5,6,6,7,5,6,6,7,6,7,7,8};
   
    public static int hammingDistance2D(byte[][] pattern, byte[][] patternMask, byte[][] text, byte[][] textMask, int xOffset, int yOffset, int maxDist){
        int dist = 0;
        for (int i = 0; i < pattern.length; i++) {
            if (i + yOffset < 0 || i + yOffset >= text.length)
                dist += bitCount(patternMask[i]);
            else 
                dist += hammingDistance1D(pattern[i], patternMask[i], text[i + yOffset], textMask[i + yOffset], xOffset);
            
            if (dist > maxDist) return -1;
        }
        return dist;
    }

    private static int bitCount(byte[] bytes) {
        int c = 0;
        for (byte b : bytes) c += Integer.bitCount(0xFF & b);
        return c;
    }

    protected static int hammingDistance1D(byte[] pattern, byte[] patternMask, byte[] text, byte[] textMask, int textOffset) {
        int dist = 0;
        int[] r = new int[pattern.length];
        for (int i = 0; i < pattern.length; i++) {
            //If the sliding pattern do not overlap with text, use mask for distance 
            if (i + textOffset < 0 || i + textOffset >= text.length)
                r[i] = Integer.bitCount(0xFF & patternMask[i]);
            else 
                r[i] = BYTE_POPCOUNT[(
                        (pattern[i] ^ text[i + textOffset])           //XOR pattern and text to get Hamming distance
                        & patternMask[i]                              //XOR is masked with pattern mask so only bits inside pattern mask contribute to distance
                        | ~textMask[i + textOffset] & patternMask[i]  //Override (OR) if bits fall outside text mask, then only look at pattern mask, actual pattern bits doesn't matter
                        ) & 0xFF];                                    //Only use least significant 8 bits                      
            
            dist += r[i];
        }
        return dist;
    }
    
 
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
