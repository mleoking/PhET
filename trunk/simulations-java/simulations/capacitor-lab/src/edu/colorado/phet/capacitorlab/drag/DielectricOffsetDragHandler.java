// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.drag;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Drag handler for dielectric offset property.
 * This is a horizontal offset, so we're dragged parallel to the x axis.
 * Offsets relative to other axes remain constant.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricOffsetDragHandler extends PDragSequenceEventHandler {

    private final PNode dragNode;
    private final Capacitor capacitor;
    private final CLModelViewTransform3D mvt;
    private final DoubleRange valueRange;

    private double clickXOffset; // x-offset of mouse click from dragNode's origin, in parent node's coordinate frame

    public DielectricOffsetDragHandler( PNode dragNode, Capacitor capacitor, CLModelViewTransform3D mvt, DoubleRange valueRange ) {
        this.dragNode = dragNode;
        this.capacitor = capacitor;
        this.mvt = mvt;
        this.valueRange = new DoubleRange( valueRange );
    }

    // When we start the drag, compute offset from dragNode's origin.
    @Override
    protected void startDrag( PInputEvent event ) {
        super.startDrag( event );
        Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
        Point2D pOrigin = mvt.modelToViewDelta( capacitor.getDielectricOffset(), 0, 0 );
        clickXOffset = pMouse.getX() - pOrigin.getX();
    }

    // As we drag, compute the new offset (subject to range constraint) and update the model.
    @Override
    protected void drag( final PInputEvent event ) {
        super.drag( event );
        Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
        double xView = pMouse.getX() - clickXOffset;
        double xModel = MathUtil.clamp( mvt.viewToModelDelta( xView, 0 ).getX(), valueRange );
        capacitor.setDielectricOffset( xModel );
    }
}