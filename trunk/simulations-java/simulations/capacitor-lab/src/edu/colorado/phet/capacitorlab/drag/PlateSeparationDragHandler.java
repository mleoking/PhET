// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.drag;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Drag handler for capacitor plate separation property.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */  class PlateSeparationDragHandler extends PDragSequenceEventHandler {

    private final PNode dragNode;
    private final Capacitor capacitor;
    private final CLModelViewTransform3D mvt;
    private final DoubleRange valueRange;

    private double clickYOffset; // y-offset of mouse click from dragNode's origin, in parent node's coordinate frame

    public PlateSeparationDragHandler( PNode dragNode, Capacitor capacitor, CLModelViewTransform3D mvt, DoubleRange valueRange ) {
        this.dragNode = dragNode;
        this.capacitor = capacitor;
        this.mvt = mvt;
        this.valueRange = new DoubleRange( valueRange );
    }

    @Override
    protected void startDrag( PInputEvent event ) {
        super.startDrag( event );
        Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
        Point2D pOrigin = mvt.modelToView( 0, -( capacitor.getPlateSeparation() / 2 ), 0 );
        clickYOffset = pMouse.getY() - pOrigin.getY();
    }

    @Override
    protected void drag( final PInputEvent event ) {
        super.drag( event );
        Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
        double yView = pMouse.getY() - clickYOffset;
        double yModel = 2 * mvt.viewToModelDelta( 0, -yView ).getY();
        if ( yModel > valueRange.getMax() ) {
            yModel = valueRange.getMax();
        }
        else if ( yModel < valueRange.getMin() ) {
            yModel = valueRange.getMin();
        }
        capacitor.setPlateSeparation( yModel );
    }
}