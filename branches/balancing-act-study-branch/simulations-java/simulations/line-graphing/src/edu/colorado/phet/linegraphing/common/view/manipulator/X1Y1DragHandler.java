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
 * Drag handler for the point (x1,y1) manipulator of a line in point-slope form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class X1Y1DragHandler extends LineManipulatorDragHandler {

    private final Property<DoubleRange> x1Range, y1Range;
    private double clickXOffset, clickYOffset; // offset of mouse click from dragNode's origin, in parent's coordinate frame
    private final boolean constantSlope;

    /**
     * Constructor
     *
     * @param userComponent   sim-sharing component identifier
     * @param componentType   sim-sharing component type
     * @param manipulatorNode the node being manipulated by the user
     * @param mvt             transform between model and view coordinate frames
     * @param line            the line being manipulated
     * @param x1Range         range of the x coordinate
     * @param y1Range         range of the y coordinate
     * @param constantSlope   true: slope is constant, false: (x2,y2) is constant
     */
    public X1Y1DragHandler( IUserComponent userComponent, IUserComponentType componentType,
                            LineManipulatorNode manipulatorNode, ModelViewTransform mvt, Property<Line> line,
                            Property<DoubleRange> x1Range, Property<DoubleRange> y1Range,
                            boolean constantSlope ) {
        super( userComponent, componentType, manipulatorNode, mvt, line );
        this.x1Range = x1Range;
        this.y1Range = y1Range;
        this.constantSlope = constantSlope;
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
        if ( constantSlope ) {
            // Keep slope constant, change (x1,y1) and (x2,y2).
            line.set( Line.createPointSlope( x1, y1, line.get().rise, line.get().run, line.get().color ) );
        }
        // Don't allow points to be the same, this would result in slope=0/0 (undefined line.)
        else if ( !( x1 == line.get().x2 && y1 == line.get().y2 ) ) {
            // Keep (x2,y2) constant, change (x1,y1) and slope.
            line.set( new Line( x1, y1, line.get().x2, line.get().y2, line.get().color ) );
        }
    }
}
