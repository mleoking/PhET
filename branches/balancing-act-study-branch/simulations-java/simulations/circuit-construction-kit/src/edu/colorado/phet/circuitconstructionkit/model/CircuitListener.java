// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model;

import edu.colorado.phet.circuitconstructionkit.model.components.Branch;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 10:43:05 AM
 */
public interface CircuitListener {
    void junctionRemoved( Junction junction );

    void branchRemoved( Branch branch );

    void junctionsMoved();

    void branchesMoved( Branch[] branches );

    void junctionAdded( Junction junction );

    void junctionsConnected( Junction a, Junction b, Junction newTarget );

    void junctionsSplit( Junction old, Junction[] j );

    void branchAdded( Branch branch );

    void selectionChanged();

    void editingChanged();
}
