package com.servicenow.detector;

public class Image {
    private final char[][] chars;
    private final String name;
    private final char oneChar;
    private final char zeroChar;
    private final char maskChar;
    
    protected Image(char[][] chars, String name, char oneChar, char zeroChar, char maskChar) {
        this.chars = chars;
        this.name = name;
        this.oneChar = oneChar;
        this.zeroChar = zeroChar;
        this.maskChar = maskChar;
    }

    public char[][] getChars() {
        return chars;
    }

    public String getName() {
        return name;
    }

    public char getOneChar() {
        return oneChar;
    }

    public char getZeroChar() {
        return zeroChar;
    }

    public char getMaskChar() {
        return maskChar;
    }
    
    
}
