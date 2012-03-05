// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.view.fractions;

import java.awt.Color;
import java.awt.Rectangle;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.fractionsintro.intro.model.CellPointer;
import edu.colorado.phet.fractionsintro.intro.model.ContainerSet;
import edu.colorado.phet.fractionsintro.intro.view.PieSliceNode;
import edu.colorado.phet.fractionsintro.intro.view.SpacedHBox;
import edu.colorado.phet.fractionsintro.intro.view.VisibilityNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas.INSET;
import static edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas.STAGE_SIZE;

/**
 * Shows a fraction as a set of pies.
 *
 * @author Sam Reid
 */
public class PieSetFractionNode extends VisibilityNode {

    //6 pies fit on the screen
    public static final int INSET_BETWEEN_PIES = 10;
    public static final double SPACE_FOR_PIES = ( STAGE_SIZE.getWidth() - INSET * 2 ) - INSET_BETWEEN_PIES * 5;
    public static final double DIAMETER = SPACE_FOR_PIES / 6;

    public PieSetFractionNode( final Property<ContainerSet> containerSet, ObservableProperty<Boolean> enabled, final Color color ) {
        super( enabled );
        new RichSimpleObserver() {
            public void update() {

                final Rectangle PIE_SIZE = new Rectangle( 0, 0, (int) DIAMETER, (int) DIAMETER );

                removeAllChildren();
                SpacedHBox box = new SpacedHBox( DIAMETER + INSET_BETWEEN_PIES );

                final ContainerSet state = containerSet.get();
                for ( int i = 0; i < state.containers.length(); i++ ) {
                    int numSlices = state.denominator;
                    PNode pie = new PNode();
                    for ( int j = 0; j < numSlices; j++ ) {
                        final CellPointer cp = new CellPointer( i, j );
                        double degreesPerSlice = 360.0 / numSlices;
                        boolean empty = state.getContainer( cp.container ).isEmpty();
                        final PieSliceNode pieSliceNode = new PieSliceNode( degreesPerSlice * j, degreesPerSlice, PIE_SIZE, state.isFilled( cp ) ? color : Color.white,
                                                                            empty ? Color.lightGray : Color.black,
                                                                            empty ? 1 : 2 );

                        if ( !empty ) {
                            pie.addChild( new PieSliceNode( degreesPerSlice * j, degreesPerSlice, PIE_SIZE, state.isFilled( cp ) ? color : Color.white,
                                                            !empty ? Color.lightGray : Color.black,
                                                            !empty ? 1 : 2 ) );

                            pieSliceNode.addInputEventListener( new CursorHandler() );
                            pieSliceNode.addInputEventListener( new PBasicInputEventHandler() {
                                @Override public void mouseDragged( PInputEvent event ) {
                                    super.mouseDragged( event );
                                    final PDimension delta = event.getDeltaRelativeTo( pieSliceNode.getParent() );
                                    pieSliceNode.translate( delta.getWidth(), delta.getHeight() );
                                }
                            } );
                        }
                        pie.addChild( pieSliceNode );
                    }
                    box.addChild( pie );
                }
                addChild( box );
            }
        }.observe( containerSet );
    }
}