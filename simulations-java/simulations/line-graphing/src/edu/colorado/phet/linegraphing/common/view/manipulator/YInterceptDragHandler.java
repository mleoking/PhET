// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view.manipulator;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Drag handler for the y-intercept manipulator of a line in slope-intercept form.
 * This manipulates (x1,y1), with x1=0 and dragging is constrained to the y axis.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class YInterceptDragHandler extends LineManipulatorDragHandler {

    private final Property<DoubleRange> y1Range;
    private double clickYOffset; // offset of mouse click from dragNode's origin, in parent's coordinate frame

    /**
     * Constructor
     *
     * @param userComponent   sim-sharing component identifier
     * @param componentType   sim-sharing component type
     * @param manipulatorNode the node being manipulated by the user
     * @param mvt             transform between model and view coordinate frames
     * @param line            the line being manipulated
     * @param y1Range         range of the y-intercept
     */
    public YInterceptDragHandler( IUserComponent userComponent, IUserComponentType componentType,
                                  LineManipulatorNode manipulatorNode, ModelViewTransform mvt, Property<Line> line,
                                  Property<DoubleRange> y1Range ) {
        super( userComponent, componentType, manipulatorNode, mvt, line );
        this.y1Range = y1Range;
    }

    @Override protected void startDrag( PInputEvent event ) {
        super.startDrag( event );
        Point2D pMouse = event.getPositionRelativeTo( manipulatorNode.getParent() );
        clickYOffset = pMouse.getY() - mvt.modelToViewDeltaY( line.get().y1 );
    }

    @Override protected void drag( PInputEvent event ) {
        super.drag( event );
        Point2D pMouse = event.getPositionRelativeTo( manipulatorNode.getParent() );
        // constrain to range, snap to grid
        double y1 = MathUtil.roundHalfUp( MathUtil.clamp( mvt.viewToModelDeltaY( pMouse.getY() - clickYOffset ), y1Range.get() ) );
        // Keep slope constant, change y1.
        line.set( Line.createSlopeIntercept( line.get().rise, line.get().run, y1, line.get().color ) );
    }
}
