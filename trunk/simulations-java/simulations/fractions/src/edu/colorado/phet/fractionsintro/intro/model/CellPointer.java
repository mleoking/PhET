// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

/**
 * Points to a cell within a container state, used for toggling a specific cell.
 *
 * @author Sam Reid
 */
public class CellPointer {
    public final int container;
    public final int cell;

    public CellPointer( int container, int cell ) {
        this.container = container;
        this.cell = cell;
    }

    @Override public String toString() {
        return "CellPointer{" +
               "container=" + container +
               ", cell=" + cell +
               '}';
    }

    //Using the denominator for this CellPointer, find a distance metric to the other cell pointer
    public double distance( CellPointer o1 ) {

        //Note this comparator doesn't "wrap" so that cell 9 is really far from cell 0 in a 10-cell container
        return Math.abs( o1.container - container ) * 10 + Math.abs( o1.cell - cell );
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }

        CellPointer that = (CellPointer) o;

        if ( cell != that.cell ) { return false; }
        if ( container != that.container ) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = container;
        result = 31 * result + cell;
        return result;
    }
}