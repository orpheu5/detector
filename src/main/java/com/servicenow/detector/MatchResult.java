package com.servicenow.detector;

public interface MatchResult {
    public Image getImage();
    public Image getPattern();
    public int getOffsetX();
    public int getOffsetY();
    public int getPercentage();
}
