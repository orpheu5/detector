package com.servicenow.detector.terminalgui;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.servicenow.detector.Image;
import com.servicenow.detector.MatchResult;

public class Model{
    static final int HEADING_ROWS = 1;     //number of rows in terminal heading used for non-image stuff
    static final int FOOTER_ROWS = 1;      //number of rows in terminal footer used for non-image stuff
    static final Color[] FG_COLOR_MAP = new Color[]{Color.GREEN, Color.BLACK};
    static final Color[] BG_COLOR_MAP = new Color[]{Color.BLACK, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.RED, Color.YELLOW, Color.WHITE};
    
    static final char 
        C_THRESH_UP = 'U',
        C_THRESH_DOWN = 'D',
        C_NEXT_IMAGE= 'N',
        C_PREV_IMAGE= 'P',
        C_EXIT= 'X';
    
    private final Screen screen;
    private final ScreenWriter writer;
    private final List<MatchResult> matches;
    private final Image image;
    
    private int threshold;
    private int xOffset = 0;
    private int yOffset = 0;

    public Model(Image image, List<MatchResult> matches, Screen screen, ScreenWriter writer) {
        xOffset = 0;
        yOffset = 0;
        
        this.writer = writer;
        this.screen = screen;
        this.image = image;
        this.matches = matches;
        
        Collections.sort(this.matches, (r1, r2) -> Integer.compare(r1.getPercentage(), r2.getPercentage()));
        Collections.reverse(this.matches);
        
        this.threshold = this.matches.stream().mapToInt(r -> r.getPercentage()).min().orElse(-1);
    }

    private void redrawImage(){
        final ScreenBuffer toScreen = new ScreenBuffer(screen, 'X', 0);
        
        //Add image at offset
        final char[][] chars = image.getChars();
        for (int i = 0; i < chars.length; i++) {
            for (int j = 0; j < chars[i].length; j++) {
                toScreen.setChar(xOffset + j, yOffset + i, chars[i][j]);
            }
        }
        
        //Add all matches that meet threshold
        for (MatchResult match : matches) {
            //Only add matches that meet threshold
            if (match.getPercentage() < threshold) continue;
            final int x = match.getOffsetX() + xOffset, y = match.getOffsetY() + yOffset;
            
            //Add the title of the pattern with hamDist
            final char[] title = (match.getPattern().getName() + ":" + match.getPercentage()).toCharArray();
            for (int j = 0; j < title.length; j++) {
                toScreen.setChar(x + j, y, title[j]);
            }
            
            //increment color intensities for each '+' in pattern that matches
            final char[][] pattern = match.getPattern().getChars();

            for (int i = 0; i < pattern.length; i++) {
                for (int j = 0; j < pattern[i].length; j++) {
                    if (pattern[i][j] != ' ') {
                        toScreen.incIntensity(x + j, y + i);
                    }
                }
            }
        }
        
        toScreen.flush();
    }

    private void redrawCanvas(){
        screen.updateScreenSize();
        screen.clear();
        final TerminalSize size = screen.getTerminalSize();

        //Draw header
        invertedColors();
        writer.drawString(0, 0, StringUtils.repeat(" ",size.getColumns()));
        writer.drawString(3, 0, "Bliffoscope Detector v1           " + image.getName() + "[matches=" + matches.size() + ", showing > " + threshold +"%]");
        defaultColors();

        //Draw footer
        int col = 0;
        drawOption("^"+ C_EXIT       , " Exit"      , 0, col++);
        drawOption("" + C_NEXT_IMAGE , " Next Image", 0, col++);
        drawOption("" + C_PREV_IMAGE , " Prev Image", 0, col++);
        drawOption("" + C_THRESH_UP  , " Inc Thresh", 0, col++);
        drawOption("" + C_THRESH_DOWN, " Dec Thresh", 0, col++);
        drawOption("Arrows"          , " to pan"    , 0, col++);
        
        screen.setCursorPosition(size.getColumns()-1, size.getRows()-1);
    }

    private void drawOption(String key, String name, int row, int col){
        TerminalSize size = screen.getTerminalSize();
        int lastLine = size.getRows()-1;
        int offset = size.getColumns() / 6;
        invertedColors();
        writer.drawString(offset*col, lastLine-row, key);
        defaultColors();

        writer.drawString(offset*col+key.length(), lastLine-row, name);
    }

    public void redrawAll() {
        redrawCanvas();
        redrawImage();
        screen.refresh();
    }
    
    private void defaultColors(){
        writer.setForegroundColor(FG_COLOR_MAP[0]);
        writer.setBackgroundColor(BG_COLOR_MAP[0]);
    }
    
    private void invertedColors(){
        writer.setForegroundColor(FG_COLOR_MAP[1]);
        writer.setBackgroundColor(BG_COLOR_MAP[1]);
    }

    public void panImage(int deltaX, int deltaY) {
        xOffset += deltaX;
        yOffset += deltaY;
    }

    public void incThreshold(int delta) {
        threshold += delta;
    }

}
