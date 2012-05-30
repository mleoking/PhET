// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.view.fractions;

import java.awt.Color;
import java.awt.Rectangle;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.fractionsintro.intro.model.containerset.CellPointer;
import edu.colorado.phet.fractionsintro.intro.model.containerset.ContainerSet;
import edu.colorado.phet.fractionsintro.intro.view.CircularPieSliceNode;
import edu.colorado.phet.fractionsintro.intro.view.SpacedHBox;
import edu.colorado.phet.fractionsintro.intro.view.VisibilityNode;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas.INSET;
import static edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas.STAGE_SIZE;

/**
 * Shows a fraction as a set of circular pies.
 *
 * @author Sam Reid
 */
public class PieSetFractionNode extends VisibilityNode {

    //6 pies fit on the screen
    public static final int INSET_BETWEEN_PIES = 10;
    public static final double SPACE_FOR_PIES = ( STAGE_SIZE.getWidth() - INSET * 2 ) - INSET_BETWEEN_PIES * 5;
    public static final double DIAMETER = SPACE_FOR_PIES / 6;

    public PieSetFractionNode( final ContainerSet containerSet, final Color color ) {
        super( new Property<Boolean>( true ) );

        final Rectangle PIE_SIZE = new Rectangle( 0, 0, (int) DIAMETER, (int) DIAMETER );

        SpacedHBox box = new SpacedHBox( DIAMETER + INSET_BETWEEN_PIES );

        for ( int i = 0; i < containerSet.containers.length(); i++ ) {
            int numSlices = containerSet.denominator;
            PNode pie = new PNode();
            for ( int j = 0; j < numSlices; j++ ) {
                final CellPointer cp = new CellPointer( i, j );
                double degreesPerSlice = 360.0 / numSlices;
                boolean empty = containerSet.getContainer( cp.container ).isEmpty();
                final CircularPieSliceNode pieSliceNode = new CircularPieSliceNode( degreesPerSlice * j, degreesPerSlice, PIE_SIZE, containerSet.isFilled( cp ) ? color : Color.white,
                                                                                    empty ? Color.lightGray : Color.black,
                                                                                    empty ? 1 : 2 );

                if ( !empty ) {
                    pie.addChild( new CircularPieSliceNode( degreesPerSlice * j, degreesPerSlice, PIE_SIZE, containerSet.isFilled( cp ) ? color : Color.white,
                                                            !empty ? Color.lightGray : Color.black,
                                                            !empty ? 1 : 2 ) );
                }
                pie.addChild( pieSliceNode );
            }
            box.addChild( pie );
        }
        addChild( box );
    }
}