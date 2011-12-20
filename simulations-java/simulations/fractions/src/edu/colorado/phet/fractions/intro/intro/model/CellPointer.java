// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.model;

/**
 * Points to a cell within a container state, used for toggling a specific cell.
 *
 * @author Sam Reid
 */
public class CellPointer {
    public final Container container;
    public final int cell;

    public CellPointer( Container container, int cell ) {
        this.container = container;
        this.cell = cell;
    }
}