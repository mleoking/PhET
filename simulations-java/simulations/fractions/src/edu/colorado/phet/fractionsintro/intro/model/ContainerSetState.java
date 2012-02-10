// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.colorado.phet.common.phetcommon.util.FunctionalUtils;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

/**
 * The entire state (immutable) of what is filled and empty for multiple containers.
 *
 * @author Sam Reid
 */
public class ContainerSetState {
    public final List<Container> containers;
    public final int denominator;
    public final int numContainers;  //Number of containers to show
    public final int numerator;

    public ContainerSetState( int denominator, Container[] containers ) {
        this( denominator, Arrays.asList( containers ) );
    }

    public ContainerSetState( int denominator, Collection<Container> containers ) {
        this.containers = new ArrayList<Container>( containers );
        this.denominator = denominator;
        this.numContainers = containers.size();
        int count = 0;
        for ( Container container : containers ) {
            count += container.numFilledCells;
        }
        this.numerator = count;
    }

    @Override public String toString() {
        return containers.toString();
    }

    public ContainerSetState addPieces( int delta ) {
        if ( delta == 0 ) {
            return this;
        }
        else if ( delta > 0 ) {
            ContainerSetState cs = isFull() ? addEmptyContainer() : this;
            return cs.toggle( cs.getFirstEmptyCell() ).addPieces( delta - 1 ).padAndTrim();
        }
        else {
            return toggle( getLastFullCell() ).addPieces( delta + 1 ).padAndTrim();
        }
    }

    //Add an empty container if this one is all full, but don't go past 6 (would be off the screen)
    public ContainerSetState padAndTrim() {
        ContainerSetState cs = trim();
        while ( cs.numContainers < 6 ) {
            cs = cs.addEmptyContainer();
        }
        return cs;
    }

    //Remove any trailing containers that are completely empty
    public ContainerSetState trim() {
        final ArrayList<Container> reversed = new ArrayList<Container>( containers );
        Collections.reverse( reversed );
        final boolean[] foundNonEmpty = { false };

        final ArrayList<Container> all = new ArrayList<Container>() {{
            for ( Container container : reversed ) {
                if ( container.isEmpty() ) {

                }
                else {
                    //as soon as finding a non-empty container, add all after that
                    foundNonEmpty[0] = true;
                }
                if ( foundNonEmpty[0] ) {
                    add( container );
                }
            }
        }};
        Collections.reverse( all );
        return new ContainerSetState( denominator, all );
    }

    public ContainerSetState addEmptyContainer() {
        return new ContainerSetState( denominator, new ArrayList<Container>( containers ) {{
            add( new Container( denominator, new int[0] ) );
        }} );
    }

    private boolean isFull() {
        for ( Container container : containers ) {
            if ( !container.isFull() ) { return false; }
        }
        return true;
    }

    public ContainerSetState toggle( final CellPointer pointer ) {
        return new ContainerSetState( denominator, FunctionalUtils.map( containers, new Function1<Container, Container>() {
            public Container apply( Container container ) {
                int containerIndex = containers.indexOf( container );
                if ( pointer.container == containerIndex ) {
                    return container.toggle( pointer.cell );
                }
                else {
                    return container;
                }
            }
        } ) );
    }

    private CellPointer getLastFullCell() {
        return FunctionalUtils.last( getAllCellPointers(), new Function1<CellPointer, Boolean>() {
            public Boolean apply( CellPointer p ) {
                return !isEmpty( p );
            }
        } ).get();
    }

    public ArrayList<CellPointer> getAllCellPointers() {
        return new ArrayList<CellPointer>() {{
            for ( int i = 0; i < numContainers; i++ ) {
                for ( int k = 0; k < containers.get( i ).numCells; k++ ) {
                    add( new CellPointer( i, k ) );
                }
            }
        }};
    }

    private CellPointer getFirstEmptyCell() {
        return FunctionalUtils.first( getAllCellPointers(), new Function1<CellPointer, Boolean>() {
            public Boolean apply( CellPointer p ) {
                return isEmpty( p );
            }
        } ).get();
    }

    public Boolean isEmpty( CellPointer cellPointer ) {
        return containers.get( cellPointer.container ).isEmpty( cellPointer.cell );
    }

    public boolean isFilled( CellPointer cp ) {
        return !isEmpty( cp );
    }

    public Container getContainer( int container ) {
        return containers.get( container );
    }

    public ContainerSetState removeContainer( final int container ) {
        return new ContainerSetState( denominator, new ArrayList<Container>() {{
            for ( int i = 0; i < numContainers; i++ ) {
                if ( i != container ) {
                    add( getContainer( i ) );
                }
            }
        }} );
    }

    //When converting denominator, try to keep pieces close to where they were.  This requires computing the closest unoccu
    public CellPointer getClosestUnoccupiedLocation( final CellPointer cellPointer ) {
        List<CellPointer> emptyCells = getEmptyCells();
        if ( emptyCells.isEmpty() ) {
            return null;
        }
        return Collections.min( emptyCells, new Comparator<CellPointer>() {
            public int compare( CellPointer o1, CellPointer o2 ) {
                double distance1 = cellPointer.distance( o1 );
                double distance2 = cellPointer.distance( o2 );
                return Double.compare( distance1, distance2 );
            }
        } );
    }

    public List<CellPointer> getFilledCells() {
        return FunctionalUtils.filter( getAllCellPointers(), new Function1<CellPointer, Boolean>() {
            public Boolean apply( CellPointer cp ) {
                return isFilled( cp );
            }
        } );
    }

    public List<CellPointer> getEmptyCells() {
        return FunctionalUtils.filter( getAllCellPointers(), new Function1<CellPointer, Boolean>() {
            public Boolean apply( CellPointer cp ) {
                return !isFilled( cp );
            }
        } );
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }

        ContainerSetState that = (ContainerSetState) o;

        if ( denominator != that.denominator ) { return false; }
        if ( numContainers != that.numContainers ) { return false; }
        if ( numerator != that.numerator ) { return false; }
        if ( containers != null ? !containers.equals( that.containers ) : that.containers != null ) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = containers != null ? containers.hashCode() : 0;
        result = 31 * result + denominator;
        result = 31 * result + numContainers;
        result = 31 * result + numerator;
        return result;
    }
}