// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Drag handler for a horizontal slider's knob.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class HorizontalSliderDragHandler extends PDragSequenceEventHandler {

    private final PNode relativeNode, trackNode, knobNode;
    private final DoubleRange range;
    private final VoidFunction1<Double> updateFunction;
    private double globalClickXOffset; // x offset of mouse click from knob's origin, in global coordinates

    /**
     * Constructor
     *
     * @param relativeNode   dragging is relative to this node, typically the sliders' parent
     * @param trackNode      the track that the knob moves in
     * @param knobNode       the knob (aka thumb) that the user drags
     * @param range          range of model values
     * @param updateFunction called with model value while dragging
     */
    public HorizontalSliderDragHandler( PNode relativeNode, PNode trackNode, PNode knobNode, DoubleRange range, VoidFunction1<Double> updateFunction ) {
        this.relativeNode = relativeNode;
        this.trackNode = trackNode;
        this.knobNode = knobNode;
        this.range = range;
        this.updateFunction = updateFunction;
    }

    @Override protected void startDrag( PInputEvent event ) {
        super.startDrag( event );
        // note the offset between the mouse click and the knob's origin
        Point2D pMouseLocal = event.getPositionRelativeTo( relativeNode );
        Point2D pMouseGlobal = relativeNode.localToGlobal( pMouseLocal );
        Point2D pKnobGlobal = relativeNode.localToGlobal( knobNode.getOffset() );
        globalClickXOffset = pMouseGlobal.getX() - pKnobGlobal.getX() + trackNode.getXOffset();
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
        Point2D pKnobGlobal = new Point2D.Double( pMouseGlobal.getX() - globalClickXOffset, pMouseGlobal.getY() );
        Point2D pKnobLocal = relativeNode.globalToLocal( pKnobGlobal );

        // convert the offset to a value
        double xOffset = pKnobLocal.getX();
        double trackLength = trackNode.getFullBoundsReference().getWidth();
        LinearFunction f = new LinearFunction( 0, trackLength, range.getMin(), range.getMax() );
        double value = f.evaluate( xOffset );
        value = MathUtil.clamp( value, range );

        // hook for adjusting value (for subclasses to implement snapping, etc.)
        if ( !isDragging ) {
            value = adjustValue( value );
        }

        updateFunction.apply( value );
    }
}
