// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.energyformsandchanges.common.model.Thermometer;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * PNode that is used to represent thermometers in the tool box and that
 * controls the initial movement of thermometers in and out of the tool
 * box.
 *
 * @author John Blanco
 */
public class ThermometerToolBoxNode extends ThermometerNode {

    private final PhetPCanvas canvas;
    private final ModelViewTransform mvt;
    private Rectangle2D returnRect = null;

    public ThermometerToolBoxNode( final MovableThermometerNode thermometerNode, ModelViewTransform mvt, PhetPCanvas canvas ) {
        this.canvas = canvas;
        this.mvt = mvt;
        final Thermometer thermometer = thermometerNode.getThermometer();
//        final Vector2D positioningOffset = mvt.viewToModel( thermometerNode.getOffsetCenterShaftToTriangleTip() );
        final Vector2D positioningOffset = new Vector2D( -0.015, -0.03 ); // TODO: Hack to get this working, should be based on thermometer node.

        // This node's visibility is the inverse of the thermometer's.
        thermometer.active.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean active ) {
                setVisible( !active );
            }
        } );

        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( PInputEvent event ) {
                thermometer.active.set( true );
                thermometer.userControlled.set( true );
                thermometer.position.set( new Vector2D( getModelPosition( event.getCanvasPosition() ) ).plus( positioningOffset ) );
            }

            @Override public void mouseDragged( PInputEvent event ) {
                thermometer.position.set( new Vector2D( getModelPosition( event.getCanvasPosition() ) ).plus( positioningOffset ) );
            }

            @Override public void mouseReleased( PInputEvent event ) {
                thermometer.userControlled.set( false );
                if ( returnRect != null && thermometerNode.getFullBoundsReference().intersects( returnRect ) ){
                    // Released over tool box, so return it.
                    thermometer.active.set( false );
                }
            }
        } );
    }

    /**
     * Convert the canvas position to the corresponding location in the model.
     */
    private Point2D getModelPosition( Point2D canvasPos ) {
        Point2D worldPos = new Point2D.Double( canvasPos.getX(), canvasPos.getY() );
        canvas.getPhetRootNode().screenToWorld( worldPos );
        Point2D adjustedWorldPos = new Point2D.Double( worldPos.getX(), worldPos.getY() );
        return mvt.viewToModel( adjustedWorldPos );
    }

    public void setReturnRect( Rectangle2D returnRect ) {
        this.returnRect = returnRect;
    }
}
