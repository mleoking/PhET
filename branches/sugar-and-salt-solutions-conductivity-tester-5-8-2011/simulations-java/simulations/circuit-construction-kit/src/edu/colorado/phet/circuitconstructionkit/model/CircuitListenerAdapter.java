// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model;

import edu.colorado.phet.circuitconstructionkit.model.components.Branch;

/**
 * User: Sam Reid
 * Date: Jun 8, 2004
 * Time: 1:47:25 PM
 */
public class CircuitListenerAdapter implements CircuitListener {
    public void junctionRemoved(Junction junction) {
    }

    public void branchRemoved(Branch branch) {
    }

    public void junctionsMoved() {
    }

    public void branchesMoved(Branch[] branches) {
    }

    public void junctionAdded(Junction junction) {
    }

    public void junctionsConnected(Junction a, Junction b, Junction newTarget) {
    }

    public void junctionsSplit(Junction old, Junction[] j) {
    }

    public void branchAdded(Branch branch) {
    }

    public void selectionChanged() {
    }

    public void editingChanged() {
    }
}
