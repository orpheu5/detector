package com.servicenow.detector.terminalgui;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.Terminal.Color;

public class ScreenBuffer {
    final char[][] chars;
    final int[][] intensities;
    final Screen screen;
    
    public ScreenBuffer(Screen screen, char initChar, int initValue) {
        this.screen = screen;
        final TerminalSize size = screen.getTerminalSize();
        chars = new char[size.getRows()-Model.HEADING_ROWS-Model.FOOTER_ROWS][size.getColumns()];
        intensities = new int[size.getRows()-Model.HEADING_ROWS-Model.FOOTER_ROWS][size.getColumns()];
        
        for (char[] c : chars) {
            for (int i = 0; i < c.length; i++) c[i] = initChar;
        }

        for (int[] v : intensities) {
            for (int i = 0; i < v.length; i++) v[i] = initValue;
        }
    }
    
    void setChar(int x, int y, char c){
        if (x < 0|| y <0 || y >= chars.length || x >= chars[y].length) return;
        chars[y][x] = c;
    }
    
    void incIntensity(int x, int y){
        if (x < 0|| y <0 || y >= chars.length || x >= chars[y].length) return;
        intensities[y][x] += 1;
    }
    
    void flush(){
        for (int i = 0; i < chars.length; i++) {
            for (int j = 0; j < chars[i].length; j++) {
                screen.putString(
                        j, 
                        i+Model.HEADING_ROWS, 
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
}
