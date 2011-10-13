// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.ResetModel;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.CanvasBoundedDragHandler;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.DragEvent;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Base class for draggable english/metric rules
 *
 * @author Sam Reid
 */
public class FluidPressureAndFlowRuler extends PNode {
    protected FluidPressureAndFlowRuler( final ModelViewTransform transform,

                                         //getter
                                         final ObservableProperty<Boolean> visible,

                                         //setter, separate from getter since has to be and-ed with units property in FluidPressureCanvas
                                         final Property<Boolean> setVisible,
                                         double length,
                                         String[] majorTicks,
                                         String units,
                                         final Point2D rulerModelOrigin,
                                         ResetModel resetModel ) {
        visible.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( visible.get() );
            }
        } );

        //Create the ruler node, and put 0 exactly at the edge so it can be placed at the bottom of the pool and give a good reading
        final RulerNode rulerNode = new RulerNode( length, 50, majorTicks, units, 4, 15 ) {{
            setInsetWidth( 0 );
        }};

        //Make it vertical
        rulerNode.rotate( -Math.PI / 2 );
        rulerNode.setOffset( transform.modelToViewX( rulerModelOrigin.getX() ),
                             transform.modelToViewY( rulerModelOrigin.getY() ) + rulerNode.getInsetWidth() );

        //Was leaving "ghosting" lines on the play area, so wrap in a BiggerPNode
        addChild( new PaddedNode( rulerNode ) );

        //Allow to drag, but not to leave the canvas
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new CanvasBoundedDragHandler( this ) {
            @Override protected void dragNode( DragEvent event ) {
                translate( event.delta.getWidth(), event.delta.getHeight() );
            }
        } );

        //Show a button that hides the ruler
        addChild( new PImage( PhetCommonResources.getImage( PhetCommonResources.IMAGE_CLOSE_BUTTON ) ) {{
            addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    setVisible.set( false );
                }
            } );
            setOffset( rulerNode.getFullBounds().getOrigin().getX(), rulerNode.getFullBounds().getOrigin().getY() - getFullBounds().getHeight() );
        }} );

        //just undo the part modified by user translation of the ruler
        resetModel.addResetListener( new VoidFunction0() {
            public void apply() {
                setOffset( 0, 0 );
            }
        } );
    }
}