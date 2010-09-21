package edu.colorado.phet.densityjava.common;

import java.util.ArrayList;

public class SimpleModel implements Model {
    private ArrayList<Unit> listeners = new ArrayList<Unit>();

    public void addListener(Unit unit) {
        listeners.add(unit);
    }

    public void removeListener(Unit unit) {
        listeners.remove(unit);
    }

    public void notifyListeners() {
        for (Unit listener : listeners) listener.update();
    }
}
