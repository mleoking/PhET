/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.drag;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Drag handler for capacitor plate area property.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */  class PlateAreaDragHandler extends PDragEventHandler {
    
    private final PNode dragNode;
    private final Capacitor capacitor;
    private final ModelViewTransform mvt;
    private final DoubleRange valueRange;
    private PDimension clickOffset; // xy-offset of mouse click from node's origin, in parent node's coordinate frame
    
    public PlateAreaDragHandler( PNode dragNode, Capacitor capacitor, ModelViewTransform mvt, DoubleRange valueRange ) {
        this.dragNode = dragNode;
        this.capacitor = capacitor;
        this.mvt = mvt;
        this.valueRange = new DoubleRange( valueRange );
        clickOffset = new PDimension();
    }
    
    @Override
    protected void startDrag(PInputEvent event) {
        super.startDrag( event );
        Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
        double xView = mvt.modelToView( capacitor.getLocationReference().getX() - ( capacitor.getPlateSideLength() / 2 ) );
        double yView = mvt.modelToView( capacitor.getLocationReference().getY() - ( capacitor.getPlateThickness() / 2 ) );
        clickOffset.setSize( pMouse.getX() - xView, pMouse.getY() - yView );
    }
    
    @Override
    protected void drag( PInputEvent event ) {
        PDimension delta = event.getDeltaRelativeTo( dragNode.getParent() );
        double deltaX = delta.getWidth();
        double deltaY = delta.getHeight();
        // only allow dragging down to the left, or up to the right
        if ( ( deltaX < 0 && deltaY > 0 ) || ( deltaX > 0 && deltaY < 0 ) ) {
            double deltaSideLength = mvt.viewToModel( -deltaX );
            double newValue = capacitor.getPlateSideLength() + deltaSideLength;
            if ( newValue > valueRange.getMax() ) {
                newValue = valueRange.getMax();
            }
            else if ( newValue < valueRange.getMin() ) {
                newValue = valueRange.getMin();
            }
            capacitor.setPlateSideLength( newValue );
        }
    }
}