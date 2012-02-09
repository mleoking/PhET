// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.FunctionalUtils;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.fractions.intro.intro.model.CellPointer;
import edu.colorado.phet.fractions.intro.intro.model.ContainerState;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Shows a fraction as a set of pies.
 *
 * @author Sam Reid
 */
public class PieSetFractionNode extends VisibilityNode {
    private final HashMap<CellPointer, PieSliceNode> map = new HashMap<CellPointer, PieSliceNode>();
    private final Property<ContainerState> containerState;

    //6 pies fit on the screen
    public static final int INSET_BETWEEN_PIES = 10;
    public static final double SPACE_FOR_PIES = FractionsIntroCanvas.WIDTH_FOR_REPRESENTATION - INSET_BETWEEN_PIES * 5;
    public static final double DIAMETER = SPACE_FOR_PIES / 6;

    public PieSetFractionNode( final Property<ContainerState> containerState, ObservableProperty<Boolean> enabled ) {
        super( enabled );
        this.containerState = containerState;
        new RichSimpleObserver() {
            public void update() {

                final Rectangle PIE_SIZE = new Rectangle( 0, 0, (int) DIAMETER, (int) DIAMETER );

                removeAllChildren();
                SpacedHBox box = new SpacedHBox( DIAMETER + INSET_BETWEEN_PIES );

                map.clear();
                final ContainerState state = containerState.get();
                for ( int i = 0; i < state.numContainers; i++ ) {
                    int numSlices = state.denominator;
                    PNode pie = new PNode();
                    for ( int j = 0; j < numSlices; j++ ) {
                        final CellPointer cp = new CellPointer( i, j );
                        double degreesPerSlice = 360.0 / numSlices;
                        boolean empty = state.getContainer( cp.container ).isEmpty();
                        final PieSliceNode pieSliceNode = new PieSliceNode( degreesPerSlice * j, degreesPerSlice, PIE_SIZE, state.isFilled( cp ) ? FractionsIntroCanvas.FILL_COLOR : Color.white,
                                                                            empty ? Color.lightGray : Color.black,
                                                                            empty ? 1 : 2 );

                        if ( !empty ) {
                            pie.addChild( new PieSliceNode( degreesPerSlice * j, degreesPerSlice, PIE_SIZE, state.isFilled( cp ) ? FractionsIntroCanvas.FILL_COLOR : Color.white,
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
                        map.put( cp, pieSliceNode );
                    }
                    box.addChild( pie );
                }
                addChild( box );
            }
        }.observe( containerState );
    }

//    public static void addListener( PNode node, final Property<ContainerState> containerState, final CellPointer cp ) {
//        node.addInputEventListener( new CursorHandler() );
//        node.addInputEventListener( new PBasicInputEventHandler() {
//            @Override public void mouseReleased( PInputEvent event ) {
//                FractionsIntroModel.setUserToggled( true );
//                containerState.set( containerState.get().toggle( cp ) );
//                FractionsIntroModel.setUserToggled( false );
//            }
//        } );
//    }

    @Override public CellPointer getClosestOpenCell( final Shape globalShape, final Point2D center2D ) {
        final List<CellPointer> emptyCells = FunctionalUtils.filter( new ArrayList<CellPointer>( containerState.get().getEmptyCells() ), new Function1<CellPointer, Boolean>() {
            public Boolean apply( CellPointer cellPointer ) {
                final PieSliceNode pieceNode = map.get( cellPointer );
                final Shape pieceShape = pieceNode.getLocalToGlobalTransform( null ).createTransformedShape( pieceNode.getShape() );
                final boolean intersects = !( new Area( pieceShape ) {{
                    intersect( new Area( globalShape ) );
                }}.isEmpty() );
//                System.out.println( "cellPointer = " + cellPointer + ", pieceShape = " + pieceShape.getBounds2D() + ", globalShape = " + globalShape.getBounds2D() + ", intersects = " + intersects );
                //Only find pieces that overlap
                return intersects;
            }
        } );
        if ( emptyCells.isEmpty() ) {
            return null;
        }
        return Collections.min( emptyCells, new Comparator<CellPointer>() {
            public int compare( CellPointer o1, CellPointer o2 ) {
                return Double.compare( map.get( o1 ).getGlobalFullBounds().getCenter2D().distance( center2D ), map.get( o2 ).getGlobalFullBounds().getCenter2D().distance( center2D ) );
            }
        } );
    }
}