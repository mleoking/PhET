// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 9, 2004
 * Time: 2:45:10 PM
 */
public class CompositeCircuitChangeListener implements CircuitChangeListener {
    private ArrayList list = new ArrayList();

    public void addCircuitChangeListener(CircuitChangeListener kl) {
        list.add(kl);
    }

    public void removeKirkhoffListener(CircuitChangeListener kl) {
        list.remove(kl);
    }

    public void circuitChanged() {
        for (int i = 0; i < list.size(); i++) {
            CircuitChangeListener circuitChangeListener = (CircuitChangeListener) list.get(i);
            circuitChangeListener.circuitChanged();
        }
    }
}
