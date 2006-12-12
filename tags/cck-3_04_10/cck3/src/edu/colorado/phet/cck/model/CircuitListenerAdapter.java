/** Sam Reid*/
package edu.colorado.phet.cck.model;

import edu.colorado.phet.cck.model.components.Branch;

/**
 * User: Sam Reid
 * Date: Jun 8, 2004
 * Time: 1:47:25 PM
 * Copyright (c) Jun 8, 2004 by Sam Reid
 */
public class CircuitListenerAdapter implements CircuitListener {
    public void junctionRemoved( Junction junction ) {
    }

    public void branchRemoved( Branch branch ) {
    }

    public void junctionsMoved() {
    }

    public void branchesMoved( Branch[] branches ) {
    }

    public void junctionAdded( Junction junction ) {
    }

    public void junctionsConnected( Junction a, Junction b, Junction newTarget ) {
    }

    public void junctionsSplit( Junction old, Junction[] j ) {
    }

    public void branchAdded( Branch branch ) {
    }

    public void selectionChanged() {
    }
}
