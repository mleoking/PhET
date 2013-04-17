// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.view;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.fluidpressureandflow.flow.model.FluxMeter;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.common.phetcommon.math.MathUtil.clamp;

/**
 * Mouse listener that provides dragging for the hoop and control panel of the flux meter.
 *
 * @author Sam Reid
 */
public class FluxMeterDragHandler extends SimSharingDragHandler {

    private final ModelViewTransform transform;
    private final FluxMeter fluxMeter;
    private final PNode node;

    //Use composition to also implement cursor handler so it doesn't need to be added as a separate listener
    //The cursor handler doesn't implement dragging, it just shows a cursor hand so that this listener can be used like this:
    //node.addInputEventListener(new FluxMeterDragHandler(...))
    //instead of like this:
    //node.addInputEventListener(new CursorHandler())
    //node.addInputEventListener(new FluxMeterDragHandler(...))
    //This is only important since FluxMeterDragHandler is used in more than one place
    private final CursorHandler cursorHandler = new CursorHandler();

    public FluxMeterDragHandler( IUserComponent component, ModelViewTransform transform, FluxMeter fluxMeter, PNode node ) {
        super( component, true );
        this.transform = transform;
        this.fluxMeter = fluxMeter;
        this.node = node;
    }

    @Override protected void drag( final PInputEvent event ) {
        super.drag( event );
        cursorHandler.mouseDragged( event );
        final double targetX = fluxMeter.x.get() + transform.viewToModelDeltaX( event.getDeltaRelativeTo( node.getParent() ).getWidth() );

        //Clamping region selected by experiment while looking at the rendered canvas, to choose a view location that doesn't cause the hoop to intersect with the pipe graphics
        //Cannot compute this in the model because it has no notion of the angle of perspective or size of the pipe graphics
        final double target = clamp( -6, targetX, 5.38 );
        fluxMeter.x.set( target );
    }

    //Override methods that CursorHandler must receive to update the cursor
    @Override public void mouseEntered( PInputEvent event ) {
        super.mouseEntered( event );
        cursorHandler.mouseEntered( event );
    }

    @Override public void mousePressed( PInputEvent event ) {
        super.mousePressed( event );
        cursorHandler.mousePressed( event );
    }

    @Override public void mouseReleased( PInputEvent event ) {
        super.mouseReleased( event );
        cursorHandler.mouseReleased( event );
    }

    @Override public void mouseExited( PInputEvent event ) {
        super.mouseExited( event );
        cursorHandler.mouseExited( event );
    }
}