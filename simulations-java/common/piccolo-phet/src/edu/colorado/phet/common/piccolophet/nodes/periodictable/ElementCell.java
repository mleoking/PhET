// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.periodictable;

import edu.umd.cs.piccolo.PNode;

/**
 * Abstract base class for cells that comprise the periodic table.
 */
public abstract class ElementCell extends PNode {
    private final int atomicNumber;

    public ElementCell( int atomicNumber ) {
        this.atomicNumber = atomicNumber;
    }

    public int getAtomicNumber() {
        return atomicNumber;
    }

    //Callback that allows nodes to update after the table has been built.  This is used so that highlighted or larger cells can move themselves to the front so they aren't clipped on 2 sides
    public void tableInitComplete() {
    }
}
