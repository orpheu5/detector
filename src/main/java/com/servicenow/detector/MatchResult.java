package com.servicenow.detector;

public interface MatchResult {
    public BinaryCharImage getImage();
    public BinaryCharImage getPattern();
    public int getOffsetX();
    public int getOffsetY();
    public int getPercentage();
}
