/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.drag;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Drag handler for capacitor plate area property.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */  class PlateAreaDragHandler extends PDragSequenceEventHandler {
    
    private final PNode dragNode;
    private final Capacitor capacitor;
    private final ModelViewTransform mvt;
    private final DoubleRange valueRange;
    private double clickXOffset; // x-offset of mouse click from node's origin, in parent node's coordinate frame
    
    public PlateAreaDragHandler( PNode dragNode, Capacitor capacitor, ModelViewTransform mvt, DoubleRange valueRange ) {
        this.dragNode = dragNode;
        this.capacitor = capacitor;
        this.mvt = mvt;
        this.valueRange = new DoubleRange( valueRange );
    }
    
    @Override
    protected void startDrag(PInputEvent event) {
        super.startDrag( event );
        Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
        double xView = mvt.modelToView( capacitor.getLocationReference().getX() - ( capacitor.getPlateSideLength() / 2 ) );
        clickXOffset = pMouse.getX() - xView;
    }
    
    @Override
    protected void drag( PInputEvent event ) {
        super.drag( event );
        PDimension delta = event.getDeltaRelativeTo( dragNode.getParent() );
        double dx = delta.getWidth();
        double dy = delta.getHeight();
        // only allow dragging down to the left, or up to the right
        if ( ( dx < 0 && dy > 0 ) || ( dx > 0 && dy < 0 ) ) {
            Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
            double xView = pMouse.getX() - clickXOffset;
            double xModel = 2 * mvt.viewToModel( -xView ); // use x only, y dimension is foreshortened for pseudo-3D perspective
            if ( xModel > valueRange.getMax() ) {
                xModel = valueRange.getMax();
            }
            else if ( xModel < valueRange.getMin() ) {
                xModel = valueRange.getMin();
            }
            capacitor.setPlateSideLength( xModel );
        }
    }
}