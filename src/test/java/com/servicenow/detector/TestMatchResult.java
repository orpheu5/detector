package com.servicenow.detector;

public class TestMatchResult implements MatchResult{
    private final ByteImage image,pattern;
    private final int x, y, percentage;
    
    public TestMatchResult(ByteImage image, ByteImage pattern, int x, int y, int percentage) {
        this.image = image;
        this.pattern = pattern;
        this.percentage = percentage;
        this.x = x;
        this.y = y;
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
        return x;
    }

    @Override
    public int getOffsetY() {
        return y;
    }

    @Override
    public int getPercentage() {
        return percentage;
    }

    public boolean equalsResult(MatchResult r) {
        if (r == null) return false;
        if (image != r.getImage()) return false;
        if (pattern != r.getPattern()) return false;
        if (percentage != r.getPercentage()) return false;
        if (x != r.getOffsetX()) return false;
        if (y != r.getOffsetY()) return false;
        return true;
    }
}
