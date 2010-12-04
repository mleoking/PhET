/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.drag;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
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
    protected void startDrag(PInputEvent event) {
        super.startDrag( event );
        Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
        Point2D pOrigin = mvt.modelToViewDelta( -( capacitor.getPlateWidth() / 2 ), 0, ( capacitor.getPlateWidth() / 2 ) );
        clickXOffset = pMouse.getX() - pOrigin.getX();
    }
    
    @Override
    protected void drag( PInputEvent event ) {
        super.drag( event );
        PDimension delta = event.getDeltaRelativeTo( dragNode.getParent() );
        double dx = delta.getWidth();
        double dy = delta.getHeight();
        // only allow dragging down to the left, or up to the right
        if ( ( dx < 0 && dy >= 0 ) || ( dx > 0 && dy <= 0 ) ) {
            Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
            double xView = pMouse.getX() - clickXOffset;
            double xModel = 2 * mvt.viewToModelDelta( -xView, 0 ).getX();
            if ( xModel > valueRange.getMax() ) {
                xModel = valueRange.getMax();
            }
            else if ( xModel < valueRange.getMin() ) {
                xModel = valueRange.getMin();
            }
            capacitor.setPlateWidth( xModel );
        }
    }
}