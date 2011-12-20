// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.Color;
import java.awt.Rectangle;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.fractions.intro.intro.model.CellPointer;
import edu.colorado.phet.fractions.intro.intro.model.ContainerState;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Shows a fraction as a set of pies.
 *
 * @author Sam Reid
 */
public class PieSetFractionNode extends VisibilityNode {

    public PieSetFractionNode( final Property<ContainerState> containerState, ObservableProperty<Boolean> enabled ) {
        super( enabled );
        new RichSimpleObserver() {
            public void update() {

                //6 pies fit on the screen
                int distanceBetweenPies = 10;
                double spaceForPies = FractionsIntroCanvas.WIDTH_FOR_REPRESENTATION - distanceBetweenPies * 5;
                final double DIAMETER = spaceForPies / 6;
                final Rectangle PIE_SIZE = new Rectangle( 0, 0, (int) DIAMETER, (int) DIAMETER );

                removeAllChildren();
                SpacedHBox box = new SpacedHBox( DIAMETER + distanceBetweenPies );

                final ContainerState state = containerState.get();
                for ( int i = 0; i < state.size; i++ ) {
                    int numSlices = state.denominator;
                    PNode pie = new PNode();
                    for ( int j = 0; j < numSlices; j++ ) {
                        final CellPointer cp = new CellPointer( i, j );
                        double degreesPerSlice = 360.0 / numSlices;
                        pie.addChild( new PieSliceNode( degreesPerSlice * j, degreesPerSlice, PIE_SIZE, state.isFilled( cp ) ? FractionsIntroCanvas.FILL_COLOR : Color.white ) {{

                            //When clicking, toggle the slice
                            addInputEventListener( new CursorHandler() );
                            addInputEventListener( new PBasicInputEventHandler() {
                                @Override public void mouseReleased( PInputEvent event ) {
                                    containerState.set( containerState.get().toggle( cp ) );
                                }
                            } );
                        }} );
                    }
                    box.addChild( pie );
                }

                addChild( box );
            }
        }.observe( containerState );
    }
}