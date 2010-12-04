/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.drag;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Drag handler for capacitor plate area property.
 * This drag handle is attached to the front-left corner of the capacitor plate, and its 
 * drag axis is the diagonal line from the front-left corner to the back-right corner of the plate.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */  class PlateAreaDragHandler extends PDragSequenceEventHandler {
    
    private final boolean DO_TRIG = false;
    
    private final PNode dragNode;
    private final Capacitor capacitor;
    private final CLModelViewTransform3D mvt;
    private final DoubleRange valueRange;
    
    private double clickXOffset, clickYOffset; // y offset of mouse click from dragNode's origin, in parent node's coordinate frame
    
    public PlateAreaDragHandler( PNode dragNode, Capacitor capacitor, CLModelViewTransform3D mvt, DoubleRange valueRange ) {
        this.dragNode = dragNode;
        this.capacitor = capacitor;
        this.mvt = mvt;
        this.valueRange = new DoubleRange( valueRange );
    }
    
    @Override
    protected void startDrag(PInputEvent event) {
        super.startDrag( event );
        Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
        Point2D pOrigin = mvt.modelToViewDelta( -capacitor.getPlateWidth() / 2, 0, -capacitor.getPlateWidth() / 2 );
        clickXOffset = pMouse.getX() - pOrigin.getX();
        clickYOffset = pMouse.getY() - pOrigin.getY();
    }
    
    @Override
    protected void drag( PInputEvent event ) {
        if ( DO_TRIG ) {
            dragTrig( event );
        }
        else {
            dragFunc( event );
        }
    }
    
    protected void dragTrig( PInputEvent event ) {
        super.drag( event );
        PDimension delta = event.getDeltaRelativeTo( dragNode.getParent() );
        double dx = delta.getWidth();
        double dy = delta.getHeight();
        // only allow dragging down to the left, or up to the right
        if ( ( dx < 0 && dy >= 0 ) || ( dx > 0 && dy <= 0 ) ) {
            Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
            double yView = pMouse.getY() - clickYOffset;
            double zModel = getZModel( yView );
            double plateWidth = Math.abs( 2 * zModel  );
            if ( plateWidth > valueRange.getMax() ) {
                plateWidth = valueRange.getMax();
            }
            else if ( plateWidth < valueRange.getMin() ) {
                plateWidth = valueRange.getMin();
            }
            capacitor.setPlateWidth( plateWidth );
        }
    }
    
    private double getZModel( double yView ) {
        return Math.cos( Math.PI/2 - mvt.getYaw() ) / mvt.viewToModel( 0, yView ).getY();
    }
    
    protected void dragFunc( PInputEvent event ) {
        super.drag( event );
        PDimension delta = event.getDeltaRelativeTo( dragNode.getParent() );
        double dx = delta.getWidth();
        double dy = delta.getHeight();
        // only allow dragging down to the left, or up to the right
        if ( ( dx < 0 && dy >= 0 ) || ( dx > 0 && dy <= 0 ) ) {
            Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
            double xView = pMouse.getX() - clickXOffset;
            double plateWidth = 2 * mvt.viewToModelDelta( -xView, 0 ).getX();
            double adjustedPlateWidth = getAdjustedPlateWidth( pMouse, plateWidth );
            capacitor.setPlateWidth( adjustedPlateWidth );
        }
    }
    
    private double getAdjustedPlateWidth( Point2D pMouse, double plateWidth ) {
        double dx1 = getDX( pMouse, plateWidth );

        final double deltaX = 1E-6;
        double dx2 = getDX( pMouse, plateWidth + deltaX );

        Function.LinearFunction function = new Function.LinearFunction( plateWidth,plateWidth+ deltaX,dx1,dx2 );
        double adjustedPlateWidth = function.createInverse().evaluate( 0 );
        if ( adjustedPlateWidth > valueRange.getMax() ) {
            adjustedPlateWidth = valueRange.getMax();
        }
        else if ( adjustedPlateWidth < valueRange.getMin() ) {
            adjustedPlateWidth = valueRange.getMin();
        }
        return adjustedPlateWidth;
    }

    private double getDX( Point2D pMouse, double plateWidth ) {
        Point2D pFront = mvt.modelToView( -plateWidth/2,0,-plateWidth/2 );
        return pMouse.getX() - pFront.getX()-clickXOffset;
    }
}