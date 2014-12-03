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
import com.googlecode.lanterna.screen.ScreenWriter;
import com.servicenow.detector.Image;
import com.servicenow.detector.MatchResult;

public class TerminalGUI {
    
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


    private final AtomicBoolean running = new AtomicBoolean(true);
    private final InputKeyHandler keyHandler;
    private final Screen screen;
    private final ScreenWriter writer;
    private Model currentModel;
    private final List<Model> models = new ArrayList<>();
    private int modelIndex = -1;
    
    public TerminalGUI() {
            screen = TerminalFacade.createScreen();
            screen.startScreen();
            writer = new ScreenWriter(screen);
            
            keyHandler = new InputKeyHandler();
            InputCharHandler charHandler = new InputCharHandler();

            keyHandler.addHandler(k->currentModel.panImage(0,1), Kind.ArrowDown);
            keyHandler.addHandler(k->currentModel.panImage(0,-1), Kind.ArrowUp);
            keyHandler.addHandler(k->currentModel.panImage(1,0), Kind.ArrowRight);
            keyHandler.addHandler(k->currentModel.panImage(-1,0), Kind.ArrowLeft);
            keyHandler.addHandler(k->charHandler.handleChar(k), Kind.NormalKey);

            charHandler.addHandler(k->currentModel.incThreshold(1), Model.C_THRESH_UP);
            charHandler.addHandler(k->currentModel.incThreshold(-1), Model.C_THRESH_DOWN);
            charHandler.addHandler(k->rollModel(1), Model.C_NEXT_IMAGE);
            charHandler.addHandler(k->rollModel(-1), Model.C_PREV_IMAGE);

            charHandler.addHandler(k->{if (k.isCtrlPressed()) running.set(false);}, Model.C_EXIT);
        }
    
    public void show(Map<Image, List<MatchResult>> matchResults){
        int showing = -1;
        for (Entry<Image, List<MatchResult>> e : matchResults.entrySet()) {
            Model m = new Model(e.getKey(), e.getValue(), screen, writer);
            models.add(m);
            if (showing < e.getValue().size()) {
                currentModel = m;
                showing = e.getValue().size();
            }
        }
        
        currentModel.redrawAll();
        
        try{
            while(running.get()) {
                Key key = screen.readInput();
                if (key != null) {
                    keyHandler.handleKey(key);
                    currentModel.redrawAll();
                } else if(screen.resizePending()) {
                    currentModel.redrawAll();
                } else {
                    try {Thread.sleep(1);} catch (InterruptedException e) {}
                }
            }
        } finally {
            screen.stopScreen();
        }
    }

    private void rollModel(int step) {
        modelIndex = (modelIndex + step) % models.size();
        currentModel = models.get(modelIndex);
    }

//    private static char[][] getTestPattern() {
//        char[][] c = new char[][]{
//                "+ +++ +".toCharArray(),
//                " + + + ".toCharArray(),
//                "  +++  ".toCharArray(),
//                "   +   ".toCharArray()
//        };
//        return c;
//    }
//
//    private static char[][] getTestPattern2() {
//        char[][] c = new char[][]{
//                "  +++  ".toCharArray(),
//                " +++++ ".toCharArray(),
//                "  +++  ".toCharArray(),
//                " +++++ ".toCharArray()
//        };
//        return c;
//    }

//    private static Match getTestMatch2() {
//        Match m = new Match();
//        m.x = 8;
//        m.y = 10;
//        m.dist = 75;
//        return m;
//    }
//
//
//    private static Match getTestMatch() {
//        Match m = new Match();
//        m.x = 5;
//        m.y = 2;
//        m.dist = 70;
//        return m;
//    }
//
//    private static char[][] getTestImage() {
//        char[][] c = new char[20][25];
//        Random r = new Random();
//        for (int i = 0; i < c.length; i++) {
//            for (int j = 0; j < c[i].length; j++) {
//                c[i][j] = ' ';
//                if (r.nextInt(100) < 20) c[i][j] = '+';
//            }
//        }
//        return c;
//    }
}
