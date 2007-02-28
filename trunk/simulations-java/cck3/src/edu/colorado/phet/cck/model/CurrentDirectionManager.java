package edu.colorado.phet.cck.model;

import edu.colorado.phet.cck.model.components.Branch;

/**
 * User: Sam Reid
 * Date: Dec 16, 2006
 * Time: 11:38:12 AM
 * Copyright (c) Dec 16, 2006 by Sam Reid
 */

public class CurrentDirectionManager implements CircuitListener {
    Circuit circuit;

    public CurrentDirectionManager( Circuit circuit ) {
        this.circuit = circuit;
        update();
    }

    public void junctionRemoved( Junction junction ) {
        update();
    }

    public void branchRemoved( Branch branch ) {
        update();
    }

    public void junctionsMoved() {
    }

    public void branchesMoved( Branch[] branches ) {
    }

    public void junctionAdded( Junction junction ) {
        update();
    }

    public void junctionsConnected( Junction a, Junction b, Junction newTarget ) {
        update();
    }

    public void junctionsSplit( Junction old, Junction[] j ) {
        update();
    }

    public void branchAdded( Branch branch ) {
        update();
    }

    public void selectionChanged() {
    }

    public void editingChanged() {
    }

    private void update() {
        //arbitrarily choose the current of the 1st component in each connected component to be forward.

    }

}
