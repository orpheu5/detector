package com.servicenow.detector.terminalgui;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal.Color;

public class ScreenBuffer {
    final char[][] chars;
    final int[][] intensities;
    final int cols, rows;
    public ScreenBuffer(int cols, int rows, char initChar, int initValue) {
        this.cols = cols;
        this.rows = rows;
        
        chars = new char[rows][cols];
        intensities = new int[rows][cols];
        
        for (char[] c : chars) {
            for (int i = 0; i < c.length; i++) c[i] = initChar;
        }

        for (int[] v : intensities) {
            for (int i = 0; i < v.length; i++) v[i] = initValue;
        }
    }
    
    void setChars(int x, int y, char[] c){
        if (x < 0|| y <0 || y >= chars.length || x >= chars[y].length) return;
        for (int i = 0; i < c.length; i++) {
            if (chars[y].length <= x+i) return;
            chars[y][x+i] = c[i];
        }
    }
    
    void setChar(int x, int y, char c){
        if (x < 0|| y <0 || y >= chars.length || x >= chars[y].length) return;
        chars[y][x] = c;
    }
    
    void setIntensity(int x, int y, int intensity){
        if (x < 0|| y <0 || y >= chars.length || x >= chars[y].length) return;
        intensities[y][x] = intensity;
    }
    
    void incIntensity(int x, int y){
        if (x < 0|| y <0 || y >= chars.length || x >= chars[y].length) return;
        intensities[y][x] += 1;
    }
    
    void flush(Screen screen){
        for (int i = 0; i < chars.length; i++) {
            for (int j = 0; j < chars[i].length; j++) {
                screen.putString(
                        j, 
                        i, 
                        String.valueOf(chars[i][j]), 
                        getFGColor(intensities[i][j]), 
                        getBGColor(intensities[i][j]));
            }
        }
    }

    private static Color getBGColor(int i) {
        return Model.BG_COLOR_MAP[Math.min(i, Model.BG_COLOR_MAP.length-1)];
    }

    private static Color getFGColor(int i) {
        return Model.FG_COLOR_MAP[Math.min(i, Model.FG_COLOR_MAP.length-1)];
    }

    public int getRows(){
        return rows;
    }

    public int getColumns() {
        return cols;
    }

    public int getIntensity(int x, int y) {
        if (x < 0|| y <0 || y >= chars.length || x >= chars[y].length) return -1;
        return intensities[y][x];
    }
}
