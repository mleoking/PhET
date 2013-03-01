// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.event;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponents;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Drag handler for a slider's thumb.
 * The slider's orientation can be horizontal or vertical.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SliderThumbDragHandler extends SimSharingDragHandler {

    public static enum Orientation {HORIZONTAL, VERTICAL}

    private final Orientation orientation;
    private final PNode relativeNode, trackNode, thumbNode;
    private final DoubleRange range;
    private final VoidFunction1<Double> updateFunction;
    private final LinearFunction valueFunction;
    private double globalClickXOffset; // x offset of mouse click from knob's origin, in global coordinates
    private double globalClickYOffset; // y offset of mouse click from knob's origin, in global coordinates

    /**
     * Constructor
     *
     * @param userComponent  user component for sim-sharing message
     * @param sendDragMessages whether to send data-collection messages during dragging
     * @param orientation    orientation of the slider, horizontal or vertical
     * @param relativeNode   dragging is relative to this node, typically the sliders' parent
     * @param trackNode      the track that the knob moves in
     * @param thumbNode      the thumb (aka knob) that the user drags
     * @param range          range of model values
     * @param updateFunction called with model value while dragging
     */
    public SliderThumbDragHandler( IUserComponent userComponent, boolean sendDragMessages,
                                   Orientation orientation, PNode relativeNode, PNode trackNode, PNode thumbNode, DoubleRange range, VoidFunction1<Double> updateFunction ) {
        super( userComponent, UserComponentTypes.slider, sendDragMessages );
        this.orientation = orientation;
        this.relativeNode = relativeNode;
        this.trackNode = trackNode;
        this.thumbNode = thumbNode;
        this.range = range;
        this.updateFunction = updateFunction;
        this.valueFunction = ( orientation == Orientation.HORIZONTAL ?
                               new LinearFunction( 0, trackNode.getFullBoundsReference().getWidth(), range.getMin(), range.getMax() ) :
                               new LinearFunction( 0, trackNode.getFullBoundsReference().getHeight(), range.getMax(), range.getMin() ) );
    }

    @Override protected void startDrag( PInputEvent event ) {
        super.startDrag( event );
        // note the offset between the mouse click and the knob's origin
        Point2D pMouseLocal = event.getPositionRelativeTo( relativeNode );
        Point2D pMouseGlobal = relativeNode.localToGlobal( pMouseLocal );
        Point2D pKnobGlobal = relativeNode.localToGlobal( thumbNode.getOffset() );
        globalClickXOffset = pMouseGlobal.getX() - pKnobGlobal.getX() + trackNode.getXOffset();
        globalClickYOffset = pMouseGlobal.getY() - pKnobGlobal.getY() + trackNode.getYOffset();
    }

    @Override protected void drag( PInputEvent event ) {
        super.drag( event );
        updateValue( event, true /* isDragging */ );
    }

    @Override protected void endDrag( PInputEvent event ) {
        updateValue( event, false /* isDragging */ );
        super.endDrag( event );
    }

    /*
     * This is called just before the updateFunction is applied when dragging ends.
     * Subclasses can override this to implement snapping, etc.
     * The default implementation returns the value unchanged.
     */
    protected double adjustValue( double value ) {
        return value;
    }

    /*
     * Maps knob movement to model value and calls the update function.
     * When dragging ends, calls the adjustValue method.
     */
    private void updateValue( PInputEvent event, boolean isDragging ) {

        // determine the knob's new offset
        Point2D pMouseLocal = event.getPositionRelativeTo( relativeNode );
        Point2D pMouseGlobal = relativeNode.localToGlobal( pMouseLocal );
        Point2D pKnobGlobal = new Point2D.Double( pMouseGlobal.getX() - globalClickXOffset, pMouseGlobal.getY() - globalClickYOffset );
        Point2D pKnobLocal = relativeNode.globalToLocal( pKnobGlobal );

        // convert the offset to a value
        double value = valueFunction.evaluate( orientation == Orientation.HORIZONTAL ? pKnobLocal.getX() : pKnobLocal.getY() );
        value = MathUtil.clamp( value, range );

        // hook for adjusting value (for subclasses to implement snapping, etc.)
        if ( !isDragging ) {
            value = adjustValue( value );
        }

        updateFunction.apply( value );
    }
}
