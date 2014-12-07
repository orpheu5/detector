package com.servicenow.detector;

public interface BinaryCharImage {
   
    public char[][] getChars();

    public String getName();

    public char getOneChar();

    public char getZeroChar();

    public char getMaskChar();
}
