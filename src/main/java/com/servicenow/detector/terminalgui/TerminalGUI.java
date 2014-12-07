package com.servicenow.detector.terminalgui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.Key.Kind;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.servicenow.detector.BinaryCharImage;
import com.servicenow.detector.MatchResult;

public class TerminalGUI {
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final InputKeyHandler keyHandler;
    private final Screen screen;
    private Model currentModel;
    private final List<Model> models = new ArrayList<>();
    private int modelIndex = -1;
    
    private static class InputCharHandler{
        private final Map<Character, Consumer<Key>> consumers = new HashMap<>();
        
        public void addHandler(Consumer<Key> consumer, char c){
            addHandler(consumer, c, true);
        }
        
        public void addHandler(Consumer<Key> consumer, char c, boolean ignoreCase){
            consumers.put(c, consumer);
            if (ignoreCase && Character.isAlphabetic(c)) consumers.put(Character.isUpperCase(c) ? Character.toLowerCase(c) : Character.toUpperCase(c), consumer);
        }
        public void handleChar(Key key){
            final Consumer<Key> consumer = consumers.get(key.getCharacter());
            if (consumer != null) consumer.accept(key);
        }
    }

    private static class InputKeyHandler{
        private final Map<Kind, Consumer<Key>> consumers = new HashMap<>();
        public void addHandler(Consumer<Key> consumer, Kind keyKind){
            consumers.put(keyKind, consumer);
        }
        public void handleKey(Key key){
            final Consumer<Key> consumer = consumers.get(key.getKind());
            if (consumer != null) consumer.accept(key);
        }
    }
    
    public TerminalGUI() {
            screen = TerminalFacade.createScreen();
            screen.startScreen();
            
            keyHandler = new InputKeyHandler();
            final InputCharHandler charHandler = new InputCharHandler();

            keyHandler.addHandler(k->currentModel.panImage(0,1), Kind.ArrowDown);
            keyHandler.addHandler(k->currentModel.panImage(0,-1), Kind.ArrowUp);
            keyHandler.addHandler(k->currentModel.panImage(1,0), Kind.ArrowRight);
            keyHandler.addHandler(k->currentModel.panImage(-1,0), Kind.ArrowLeft);
            keyHandler.addHandler(k->charHandler.handleChar(k), Kind.NormalKey);

            charHandler.addHandler(k->currentModel.incThreshold(1), Model.C_THRESH_UP);
            charHandler.addHandler(k->currentModel.incThreshold(-1), Model.C_THRESH_DOWN);
            charHandler.addHandler(k->rollModel(1), Model.C_NEXT_IMAGE);
            charHandler.addHandler(k->rollModel(-1), Model.C_PREV_IMAGE);
            charHandler.addHandler(k->currentModel.toggleOverlap(), Model.C_TOGGLE_OVERLAP);

            charHandler.addHandler(k->{if (k.isCtrlPressed()) running.set(false);}, Model.C_EXIT);
        }
    
    public void show(Map<BinaryCharImage, List<MatchResult>> matchResults){
        if (matchResults.isEmpty()) return;
        
        int showing = -1;
        
        //Create a model for each image and show model/image with most matches first
        for (Entry<BinaryCharImage, List<MatchResult>> e : matchResults.entrySet()) {
            final Model m = new Model(e.getKey(), e.getValue());
            models.add(m);
            if (showing < e.getValue().size()) {
                currentModel = m;
                showing = e.getValue().size();
            }
        }
        
        refresh();
        
        try{
            while(running.get()) {
                final Key key = screen.readInput();
                if (key != null) {
                    keyHandler.handleKey(key);
                    refresh();
                } else if(screen.resizePending()) {
                    refresh();
                } else {
                    //Do not busy-spin, sleep a bit to allow OS to un-schedule this thread
                    try {Thread.sleep(1);} catch (InterruptedException e) {}
                }
            }
        } finally {
            screen.stopScreen();
        }
    }

    private void rollModel(int step) {
        modelIndex = (models.size() + modelIndex + step) % models.size();
        currentModel = models.get(modelIndex);
    }
    
    private void refresh(){
        screen.updateScreenSize();
        screen.clear();
        final TerminalSize size = screen.getTerminalSize();
        currentModel.redrawAll(size.getColumns(), size.getRows()).flush(screen);
        screen.setCursorPosition(size.getColumns()-1, size.getRows()-1);
        screen.refresh();
    }
}
