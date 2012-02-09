// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.fractions.intro.intro.model.CellPointer;
import edu.colorado.phet.fractions.intro.intro.model.ContainerState;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Shows a fraction as a set of 3d cakes.
 *
 * @author Sam Reid
 */
public class CakeSetFractionNode extends VisibilityNode {

    //Order that slices appear in the cake as the user adds them
    HashMap<Integer, int[]> orderToAdd = new HashMap<Integer, int[]>() {{
        put( 1, new int[] { 1 } );
        put( 2, new int[] { 2, 1 } );
        put( 3, new int[] { 1, 2, 3 } );
        put( 4, new int[] { 1, 2, 3, 4 } );
        put( 5, new int[] { 1, 2, 3, 4, 5 } );
        put( 6, new int[] { 1, 2, 3, 4, 5, 6 } );
        put( 7, new int[] { 1, 2, 3, 4, 5, 6, 7 } );
        put( 8, new int[] { 1, 2, 3, 4, 5, 6, 7, 8 } );


//        put( 12, new int[] { 4, 5, 6, 7, 8, 9, 10, 11, 12, 1, 2, 3 } );
    }};

    //Find which should appear before/after others in z-ordering.  Must be back to front.
    HashMap<Integer, int[]> renderOrder = new HashMap<Integer, int[]>() {{
        put( 1, new int[] { 1 } );
        put( 2, new int[] { 2, 1 } );
        put( 3, new int[] { 1, 2, 3 } );
        put( 4, new int[] { 1, 2, 3, 4 } );
        put( 5, new int[] { 2, 1, 3, 5, 4 } );
        put( 6, new int[] { 2, 1, 3, 6, 4, 5 } );
        put( 7, new int[] { 2, 3, 1, 4, 7, 5, 6 } );
        put( 8, new int[] { 2, 3, 1, 4, 5, 8, 6, 7 } );


//        put( 12, new int[] { 3, 4, 5, 2, 6, 1, 7, 12, 8, 11, 9, 10 } );
    }};

    public CakeSetFractionNode( final Property<ContainerState> state, ObservableProperty<Boolean> enabled ) {
        super( enabled );
        new RichSimpleObserver() {
            public void update() {

                int d = state.get().denominator;

                //6 pies fit on the screen
                int distanceBetweenPies = 10;
                double spaceForPies = FractionsIntroCanvas.WIDTH_FOR_REPRESENTATION - distanceBetweenPies * 5;
                final double DIAMETER = spaceForPies / 6;

                removeAllChildren();
                SpacedHBox box = new SpacedHBox( DIAMETER + distanceBetweenPies );

                if ( orderToAdd.containsKey( d ) ) {

                    ContainerState c = state.get();

                    for ( int i = 0; i < c.numContainers; i++ ) {
                        box.addChild( new CakeNode( d, getSliceArray( i, c, d ), state, i, orderToAdd.get( d ) ) );
                    }
                }
                else {
                    box.addChild( new PText( "-----------------> No images for cake for denominator = " + d ) );
                }

                addChild( box );
            }
        }.observe( state );
    }

    private int[] getSliceArray( int container, ContainerState c, int denominator ) {
        final int[] orderToAddThem = orderToAdd.get( denominator );
        final int[] theRenderOrder = renderOrder.get( denominator );

        final ArrayList<Integer> renderOrder = new ArrayList<Integer>();
        for ( int aTheRenderOrder : theRenderOrder ) {
            renderOrder.add( aTheRenderOrder );
        }

        //Find which should appear before/after others.
        ArrayList<Integer> values = new ArrayList<Integer>();
        for ( int i = 0; i < c.denominator; i++ ) {
            if ( !c.isEmpty( new CellPointer( container, i ) ) ) {
                values.add( orderToAddThem[i] );
            }
        }

        Collections.sort( values, new Comparator<Integer>() {
            public int compare( Integer o1, Integer o2 ) {
                return Double.compare( renderOrder.indexOf( o1 ), renderOrder.indexOf( o2 ) );
            }
        } );

        int[] x = new int[values.size()];
        for ( int i = 0; i < x.length; i++ ) {
            x[i] = values.get( i );
        }
        return x;
    }

    @Override public CellPointer getClosestOpenCell( Shape globalShape, Point2D center2D ) {
        return null;
    }
}