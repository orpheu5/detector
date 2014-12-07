package com.servicenow.detector.terminalgui;

import java.util.Collections;
import java.util.List;

import com.googlecode.lanterna.terminal.Terminal.Color;
import com.servicenow.detector.BinaryCharImage;
import com.servicenow.detector.MatchResult;

public class Model{
    private static final String Name = "Bliffoscope Detector";
    private static final String version = "v1";
    
    static final Color[] FG_COLOR_MAP = new Color[]{Color.GREEN, Color.BLACK};
    static final Color[] BG_COLOR_MAP = new Color[]{Color.BLACK, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.RED, Color.YELLOW, Color.WHITE};
    
    static final char 
        C_THRESH_UP = 'U',
        C_THRESH_DOWN = 'D',
        C_NEXT_IMAGE= 'N',
        C_PREV_IMAGE= 'P',
        C_TOGGLE_OVERLAP= 'O',
        C_EXIT= 'X';
    
    private final List<MatchResult> matches;
    private final BinaryCharImage image;
    
    private int threshold;
    private int xOffset;
    private int yOffset;
    private boolean overlap;

    public Model(BinaryCharImage image, List<MatchResult> matches) {
        xOffset = 0;
        yOffset = 1;
        overlap = true;
        
        this.image = image;
        this.matches = matches;
        
        Collections.sort(this.matches, (r1, r2) -> Integer.compare(r1.getPercentage(), r2.getPercentage()));
        Collections.reverse(this.matches);
        
        this.threshold = this.matches.stream().mapToInt(r -> r.getPercentage()).min().orElse(-1);
    }

    private void redrawImage(ScreenBuffer buffer){
        //Add image at offset
        final char[][] chars = image.getChars();
        for (int i = 0; i < chars.length; i++) {
            for (int j = 0; j < chars[i].length; j++) {
                buffer.setChar(xOffset + j, yOffset + i, chars[i][j]);
            }
        }
        
        //Add all matches that meet threshold
        for (MatchResult match : matches) {
            //Only add matches that meet threshold
            if (match.getPercentage() < threshold) continue;
            
            final int x = match.getOffsetX() + xOffset;
            final int y = match.getOffsetY() + yOffset;
            final char[][] pattern = match.getPattern().getChars();
            
            //If overlap disabled, check if should skip
            if (!overlap) {
                boolean isOverlapping = false;
                
                nestedLoop:
                for (int i = 0; i < pattern.length; i++) {
                    for (int j = 0; j < pattern[i].length; j++) {
                        if (pattern[i][j] == match.getPattern().getOneChar()) {
                            //See if intensity already incremented for this pattern one bit
                            if (buffer.getIntensity(x + j, y + i) > 0) {
                                isOverlapping = true;
                                break nestedLoop;
                            }
                        }
                    }
                }
                
                if (isOverlapping) continue;
            }
            
            //Add the title of the pattern with hamDist
            final char[] title = (match.getPattern().getName() + ":" + match.getPercentage()).toCharArray();
            buffer.setChars(x, y, title);
            
            
            //increment color intensities for each 'one' in pattern that matches
            for (int i = 0; i < pattern.length; i++) {
                for (int j = 0; j < pattern[i].length; j++) {
                    if (pattern[i][j] == match.getPattern().getOneChar()) {
                        buffer.incIntensity(x + j, y + i);
                    }
                }
            }
        }
    }

    private void redrawCanvas(ScreenBuffer buffer){
        //Draw header set background green
        for (int i = 0; i < buffer.getColumns(); i++) {
            buffer.setIntensity(i, 0, 1);
            buffer.setChar(i, 0, ' ');
        }
        //Draw header text
        final String header = "   " + Name +" " + version + "      " + image.getName() + "[matches=" + matches.size() + ", showing > " + threshold +"%][Overlap=" + (overlap ? "on":"off") + "]";
        buffer.setChars(0, 0, header.toCharArray());

        //Draw footer, first empty line
        for (int i = 0; i < buffer.getColumns(); i++) {
            buffer.setIntensity(i, buffer.getRows()-1, 0);
            buffer.setChar(i, buffer.getRows()-1, ' ');
        }
        
        //Draw options
        drawOptions(buffer, new String[]{
              "^"+ C_EXIT  +":Exit",
              C_NEXT_IMAGE +":Next Image",
              C_PREV_IMAGE +":Prev Image",
              C_THRESH_UP  +":Inc Thresh",
              C_THRESH_DOWN+":Dec Thresh",
              C_TOGGLE_OVERLAP +":Overlap " + (overlap ? "off":"on"), 
              "Arrows:to pan"});
    }

    private static void drawOptions(ScreenBuffer buffer, String[] options){
        int lastLine = buffer.getRows()-1;
        int offset = buffer.getColumns() / options.length;
        
        for (int i = 0; i < options.length; i++) {
            buffer.setChars(i * offset, lastLine, options[i].replace(":", " ").toCharArray());
            int highlightLength = options[i].split(":")[0].length();
            for (int j = 0; j < highlightLength; j++) {
                buffer.setIntensity(i * offset + j, lastLine, 1);
            }
        }
        
    }

    public ScreenBuffer redrawAll(int cols, int rows) {
        final ScreenBuffer buffer = new ScreenBuffer(cols, rows , 'X', 0);
        redrawImage(buffer);
        redrawCanvas(buffer);
        return buffer;
    }

    public void panImage(int deltaX, int deltaY) {
        xOffset += deltaX;
        yOffset += deltaY;
    }

    public void incThreshold(int delta) {
        threshold += delta;
        if (threshold < 0) threshold = 0;
        if (threshold > 100) threshold = 100;
    }

    public void toggleOverlap() {
        overlap = !overlap;
    }
}
