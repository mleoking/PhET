// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.drag;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Drag handler for capacitor plate area property.
 * This drag handle is attached to the front-left corner of the capacitor plate, and its 
 * drag axis is the diagonal line from the front-left corner to the back-right corner of the plate.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */class PlateAreaDragHandler extends PDragSequenceEventHandler {

    private final PNode dragNode;
    private final Capacitor capacitor;
    private final CLModelViewTransform3D mvt;
    private final DoubleRange valueRange;

    private double clickXOffset; // x offset of mouse click from dragNode's origin, in parent node's coordinate frame

    public PlateAreaDragHandler( PNode dragNode, Capacitor capacitor, CLModelViewTransform3D mvt, DoubleRange valueRange ) {
        this.dragNode = dragNode;
        this.capacitor = capacitor;
        this.mvt = mvt;
        this.valueRange = new DoubleRange( valueRange );
    }

    @Override
    protected void startDrag( PInputEvent event ) {
        super.startDrag( event );
        Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
        Point2D pOrigin = mvt.modelToViewDelta( -capacitor.getPlateWidth() / 2, 0, -capacitor.getPlateWidth() / 2 );
        clickXOffset = pMouse.getX() - pOrigin.getX();
    }

    @Override
    protected void drag( PInputEvent event ) {
        super.drag( event );
        Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
        double plateWidth = getPlateWidth( pMouse );
        capacitor.setPlateWidth( plateWidth );
    }

    /*
     * Determines the plateWidth for a specific mouse position.
     * This effectively accounts for the z-axis dimension.
     */
    private double getPlateWidth( Point2D pMouse ) {
        
        // pick any 2 view values
        final double xView1 = 0; 
        final double xView2 = 1;
        
        // compute corresponding model values
        final double xModel1 = getModelX( pMouse, xView1 );
        final double xModel2 = getModelX( pMouse, xView2 );
        
        Function.LinearFunction function = new Function.LinearFunction( xView1, xView2, xModel1, xModel2 );
        double plateWidth = function.createInverse().evaluate( 0 );
        
        // constrain to the plate width bounds
        if ( plateWidth > valueRange.getMax() ) {
            plateWidth = valueRange.getMax();
        }
        else if ( plateWidth < valueRange.getMin() ) {
            plateWidth = valueRange.getMin();
        }
        
        return plateWidth;
    }

    /*
     * Determines how far the mouse is from where we grabbed the arrow, 
     * for a hypothetical capacitor plate width.
     */
    private double getModelX( Point2D pMouse, double samplePlateWidth ) {
        Point2D pFrontLeftCorner = mvt.modelToView( -samplePlateWidth / 2, 0, -samplePlateWidth / 2 );
        return pMouse.getX() - pFrontLeftCorner.getX() - clickXOffset;
    }
}