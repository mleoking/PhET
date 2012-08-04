// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.intro.model.containerset;

import fj.Equal;
import fj.F;
import fj.F2;
import fj.data.List;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import static edu.colorado.phet.fractions.common.util.FJUtils.ord;
import static fj.Function.curry;

/**
 * The entire state (immutable) of what is filled and empty for multiple containers.
 *
 * @author Sam Reid
 */
public @Data class ContainerSet {
    public final List<Container> containers;
    public final int denominator;
    public final int numerator;

    public static <T> Equal<T> refEqual() {
        return Equal.equal( curry( new F2<T, T, Boolean>() {
            public Boolean f( final T a1, final T a2 ) {
                return a1 == a2;
            }
        } ) );
    }

    public ContainerSet( int denominator, Container[] containers ) {
        this( denominator, Arrays.asList( containers ) );
    }

    public ContainerSet( int denominator, List<Container> containers ) {
        this( denominator, containers.toCollection() );
    }

    public ContainerSet( int denominator, Collection<Container> containers ) {
        this.containers = List.iterableList( containers );
        this.denominator = denominator;
        int count = 0;
        for ( Container container : containers ) {
            count += container.filledCells.length();
        }
        this.numerator = count;
    }

    @Override public String toString() {
        return containers.toString();
    }

    //Add an empty container if this one is all full, but don't go past 6 (would be off the screen)
    public ContainerSet padAndTrim() {
        ContainerSet cs = trim();
        while ( cs.containers.length() < 6 ) {
            cs = cs.addEmptyContainer();
        }
        return cs;
    }

    //Remove any trailing containers that are completely empty
    public ContainerSet trim() {
        final List<Container> reversed = containers.reverse();
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
        return new ContainerSet( denominator, all );
    }

    public ContainerSet addEmptyContainer() {
        return new ContainerSet( denominator, new ArrayList<Container>( containers.toCollection() ) {{
            add( new Container( denominator, new int[0] ) );
        }} );
    }

    public ContainerSet toggle( final CellPointer pointer ) {
        return new ContainerSet( denominator, containers.map( new F<Container, Container>() {
            @Override public Container f( Container container ) {
                int containerIndex = containers.elementIndex( ContainerSet.<Container>refEqual(), container ).some();
                if ( pointer.container == containerIndex ) {
                    return container.toggle( pointer.cell );
                }
                else {
                    return container;
                }
            }
        } ) );
    }

    public CellPointer getLastFullCell() {
        return getAllCellPointers().reverse().find( new F<CellPointer, Boolean>() {
            @Override public Boolean f( CellPointer cellPointer ) {
                return !isEmpty( cellPointer );
            }
        } ).some();
    }

    public List<CellPointer> getAllCellPointers() {
        return List.iterableList( new ArrayList<CellPointer>() {{
            for ( int i = 0; i < containers.length(); i++ ) {
                for ( int k = 0; k < containers.index( i ).numCells; k++ ) {
                    add( new CellPointer( i, k ) );
                }
            }
        }} );
    }

    public CellPointer getFirstEmptyCell() {
        return getAllCellPointers().find( new F<CellPointer, Boolean>() {
            @Override public Boolean f( CellPointer cellPointer ) {
                return isEmpty( cellPointer );
            }
        } ).some();
    }

    public Boolean isEmpty( CellPointer cellPointer ) {
        return containers.index( cellPointer.container ).isEmpty( cellPointer.cell );
    }

    public boolean isFilled( CellPointer cp ) {
        return !isEmpty( cp );
    }

    public Container getContainer( int container ) {
        return containers.index( container );
    }

    //When converting denominator, try to keep pieces close to where they were.  This requires computing the closest unoccupied space
    public CellPointer getClosestUnoccupiedLocation( final CellPointer cellPointer ) {
        List<CellPointer> emptyCells = getEmptyCells();
        if ( emptyCells.isEmpty() ) {
            return null;
        }
        return emptyCells.minimum( ord( new F<CellPointer, Double>() {
            @Override public Double f( final CellPointer u ) {
                return cellPointer.distance( u );
            }
        } ) );
    }

    public List<CellPointer> getFilledCells() {
        return getAllCellPointers().filter( new F<CellPointer, Boolean>() {
            @Override public Boolean f( CellPointer c ) {
                return isFilled( c );
            }
        } );
    }

    public List<CellPointer> getEmptyCells() {
        return getAllCellPointers().filter( new F<CellPointer, Boolean>() {
            @Override public Boolean f( CellPointer c ) {
                return !isFilled( c );
            }
        } );
    }

    //Handle a change in the number of containers as well as the denominator
    public ContainerSet update( final int numContainers, final Integer denominator ) {
        ContainerSet newState = new ContainerSet( denominator, List.iterableList( new ArrayList<Container>() {{
            for ( int i = 0; i < numContainers; i++ ) {
                add( new Container( denominator, new int[0] ) );
            }
        }} ) );

        //for each piece in oldState, find the closest unoccupied location in newState and add it there
        for ( CellPointer cellPointer : getFilledCells() ) {

            //Find closest unoccupied location
            CellPointer cp = newState.getClosestUnoccupiedLocation( cellPointer );
            if ( cp != null ) {
                newState = newState.toggle( cp );
            }
            else {
                //No unoccupied location, so don't modify the state
            }
        }
        return newState;
    }

    public ContainerSet maximum( Integer maximum ) { return update( maximum, denominator ); }

    //For the equality lab tab, create an equivalent representation where the numerator and denominator are scaled by the same value
    public ContainerSet scale( final int scale ) {
        return new ContainerSet( denominator * scale, containers.map( new F<Container, Container>() {
            @Override public Container f( final Container container ) {
                return container.scale( scale );
            }
        } ) );
    }

    public CellPointer getRandomEmptyCell( final Random random ) {
        final List<CellPointer> emptyCells = getEmptyCells();
        return emptyCells.index( random.nextInt( emptyCells.length() ) );
    }
}