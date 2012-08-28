// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.model.LineFactory;
import edu.colorado.phet.linegraphing.common.model.PointSlopeLine;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Drag handler for the point (x1,y1) manipulator of a line in point-slope form.
 * This can also be used for a line in slope-intercept form, if x1's range is fixed at zero.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointDragHandler<T extends PointSlopeLine> extends LineManipulatorDragHandler<T> {

    private final Property<DoubleRange> x1Range, y1Range;
    private final LineFactory<T> lineFactory;
    private double clickXOffset, clickYOffset; // offset of mouse click from dragNode's origin, in parent's coordinate frame

    /**
     * Constructor
     * @param userComponent sim-sharing component identifier
     * @param componentType sim-sharing component type
     * @param manipulatorNode the node being manipulated by the user
     * @param mvt  transform between model and view coordinate frames
     * @param line the line being manipulated
     * @param x1Range
     * @param y1Range
     */
    public PointDragHandler( IUserComponent userComponent, IUserComponentType componentType,
                             LineManipulatorNode manipulatorNode, ModelViewTransform mvt, Property<T> line,
                             Property<DoubleRange> x1Range, Property<DoubleRange> y1Range,
                             LineFactory<T> lineFactory ) {
        super( userComponent, componentType, manipulatorNode, mvt, line );
        this.x1Range = x1Range;
        this.y1Range = y1Range;
        this.lineFactory = lineFactory;
    }

    @Override protected void startDrag( PInputEvent event ) {
        super.startDrag( event );
        Point2D pMouse = event.getPositionRelativeTo( manipulatorNode.getParent() );
        clickXOffset = pMouse.getX() - mvt.modelToViewDeltaX( line.get().x1 );
        clickYOffset = pMouse.getY() - mvt.modelToViewDeltaY( line.get().y1 );
    }

    @Override protected void drag( PInputEvent event ) {
        super.drag( event );
        Point2D pMouse = event.getPositionRelativeTo( manipulatorNode.getParent() );
        // constrain to range, snap to grid
        double x1 = MathUtil.roundHalfUp( MathUtil.clamp( mvt.viewToModelDeltaX( pMouse.getX() - clickXOffset ), x1Range.get() ) );
        double y1 = MathUtil.roundHalfUp( MathUtil.clamp( mvt.viewToModelDeltaY( pMouse.getY() - clickYOffset ), y1Range.get() ) );
        line.set( lineFactory.withPoint( line.get(), x1, y1 ) );
    }
}
