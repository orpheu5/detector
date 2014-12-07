package com.servicenow.detector;

import java.util.ArrayList;

public class BytePattern {
    private final ArrayList<byte[][]> shiftedData;
    private final ArrayList<byte[][]> shiftedMask;
    private final ByteImage byteImage;

    public BytePattern(ByteImage byteImage) {
        this.byteImage = byteImage;
        
        shiftedData = new ArrayList<>();
        shiftedMask = new ArrayList<>();
        
        //0 shift is just data and mask
        shiftedData.add(byteImage.getData());
        shiftedMask.add(byteImage.getMask());

        for (int i = 1; i < 8; i++) {
            shiftedData.add(BitUtil.shift2D(byteImage.getData(), i));
            shiftedMask.add(BitUtil.shift2D(byteImage.getMask(), i));
        }
    }
    
    public byte[][] getShiftedData(int shift){
        if (shift < 0 || shift > 7) throw new IllegalArgumentException("Invalid shift argument, must be >=0 and <8");
        return shiftedData.get(shift);
    }
    
    public byte[][] getShiftedMask(int shift){
        if (shift < 0 || shift > 7) throw new IllegalArgumentException("Invalid shift argument, must be >=0 and <8");
        return shiftedMask.get(shift);
    }
    
    public ByteImage getByteImage(){
        return byteImage;
    }
}
