package edu.colorado.phet.cck3.circuit;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 10:43:05 AM
 * Copyright (c) May 24, 2004 by Sam Reid
 */
public interface CircuitListener {
    void junctionRemoved( Junction junction );

    void branchRemoved( Branch branch );

    void junctionsMoved();

    void branchesMoved( Branch[] branches );
}
