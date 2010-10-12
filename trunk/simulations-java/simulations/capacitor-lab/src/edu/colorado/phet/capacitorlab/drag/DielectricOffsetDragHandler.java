/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.drag;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Drag handler for dielectric offset property.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricOffsetDragHandler extends PDragSequenceEventHandler {
    
    private final PNode dragNode;
    private final Capacitor capacitor;
    private final ModelViewTransform mvt;
    private final DoubleRange valueRange;
    
    private double clickXOffset; // x-offset of mouse click from dragNode's origin, in parent node's coordinate frame
    
    public DielectricOffsetDragHandler( PNode dragNode, Capacitor capacitor, ModelViewTransform mvt, DoubleRange valueRange ) {
        this.dragNode = dragNode;
        this.capacitor = capacitor;
        this.mvt = mvt;
        this.valueRange = new DoubleRange( valueRange );
    }
    
    @Override
    protected void startDrag( PInputEvent event ) {
        super.startDrag( event );
        double xMouse = event.getPositionRelativeTo( dragNode.getParent() ).getX();
        double xOrigin = mvt.modelToView( capacitor.getDielectricOffset() );
        clickXOffset = xMouse - xOrigin;
    }

    @Override
    protected void drag( final PInputEvent event ) {
        super.drag( event );
        Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
        double xView = pMouse.getX() - clickXOffset;
        double xModel = mvt.viewToModel( xView );
        if ( xModel > valueRange.getMax() ) {
            xModel = valueRange.getMax();
        }
        else if ( xModel < valueRange.getMin() ) {
            xModel = valueRange.getMin();
        }
        capacitor.setDielectricOffset( xModel );
    }
}