// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.colorado.phet.common.phetcommon.util.FunctionalUtils;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

/**
 * The entire state of what is filled and empty.
 *
 * @author Sam Reid
 */
public class ContainerState {
    public final List<Container> containers;
    private final int denominator;
    private final int numerator;
    public final int size;  //Number of containers to show

    public ContainerState( int numerator, int denominator, Container[] containers ) {
        this( numerator, denominator, Arrays.asList( containers ) );
    }

    public ContainerState( int numerator, int denominator, List<Container> containers ) {
        this.containers = containers;
        this.denominator = denominator;
        this.numerator = numerator;
        this.size = containers.size();
    }

    public ContainerState nextRandomState() {
        ArrayList<Container> newContainers = new ArrayList<Container>();
        boolean incrementedCount = false;
        for ( Container container : containers ) {
            if ( container.isFull() ) {
                newContainers.add( container );
            }
            else {

                //Assumes things fill to the right and not randomly
                newContainers.add( container.addRandom() );
                incrementedCount = true;
            }
        }

        //Didn't add one yet, so add a new container now
        if ( !incrementedCount ) {
            newContainers.add( new Container( denominator, new int[] { RandomFill.random.nextInt( denominator ) } ) );
        }
        return new ContainerState( numerator, denominator, newContainers );
    }

    @Override public String toString() {
        return containers.toString();
    }

    public ContainerState addPieces( int delta ) {
        if ( delta == 0 ) {
            return this;
        }
        else if ( delta > 0 ) {
            return toggle( getLowestEmptyCell() ).addPieces( delta - 1 );
        }
        else {
            return toggle( getLowestFilledCell() ).addPieces( delta + 1 );
        }
    }

    private ContainerState toggle( final CellPointer pointer ) {
        return new ContainerState( numerator, denominator, FunctionalUtils.map( containers, new Function1<Container, Container>() {
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

    private CellPointer getLowestFilledCell() {
        return FunctionalUtils.first( getAllCellPointers(), new Function1<CellPointer, Boolean>() {
            public Boolean apply( CellPointer cellPointer ) {
                return !isEmpty( cellPointer );
            }
        } ).get();
    }

    public ArrayList<CellPointer> getAllCellPointers() {
        return new ArrayList<CellPointer>() {{
            for ( int i = 0; i < size; i++ ) {
                for ( int k = 0; k < containers.get( i ).numCells; k++ ) {
                    add( new CellPointer( i, k ) );
                }
            }
        }};
    }

    private CellPointer getLowestEmptyCell() {
        return FunctionalUtils.first( getAllCellPointers(), new Function1<CellPointer, Boolean>() {
            public Boolean apply( CellPointer cellPointer ) {
                return isEmpty( cellPointer );
            }
        } ).get();
    }

    private Boolean isEmpty( CellPointer cellPointer ) {
        return containers.get( cellPointer.container ).isEmpty( cellPointer.cell );
    }
}