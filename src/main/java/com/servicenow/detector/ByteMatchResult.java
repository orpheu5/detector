package com.servicenow.detector;

public class ByteMatchResult implements MatchResult {
    private final ByteImage pattern;
    private final ByteImage image;
    private final int patternShift;
    private final int xOffset;
    private final int yOffset;
    private final int hamDist;
    
    protected ByteMatchResult(ByteImage image, ByteImage pattern, int patternShift, int xOffset, int yOffset, int hamDist) {
        this.pattern = pattern;
        this.image = image;
        this.patternShift = patternShift;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.hamDist = hamDist;
    }
    
    @Override
    public BinaryCharImage getImage() {
        return image;
    }

    @Override
    public BinaryCharImage getPattern() {
        return pattern;
    }

    @Override
    public int getOffsetX() {
        return xOffset * 8 + patternShift;
    }

    @Override
    public int getOffsetY() {
        return yOffset;
    }

    @Override
    public int getPercentage() {
        return (pattern.getMaskSize() - hamDist) * 100 / pattern.getMaskSize();
    }

}
