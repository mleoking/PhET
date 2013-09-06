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
 * Drag handler for the point (x2,y2) manipulator of a line in point-point form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class X2Y2DragHandler extends LineManipulatorDragHandler {

    private final Property<DoubleRange> x2Range, y2Range;
    private double clickXOffset, clickYOffset; // offset of mouse click from dragNode's origin, in parent's coordinate frame

    /**
     * Constructor
     *
     * @param userComponent   sim-sharing component identifier
     * @param componentType   sim-sharing component type
     * @param manipulatorNode the node being manipulated by the user
     * @param mvt             transform between model and view coordinate frames
     * @param line            the line being manipulated
     * @param x2Range         range of the x coordinate
     * @param y2Range         range of the y coordinate
     */
    public X2Y2DragHandler( IUserComponent userComponent, IUserComponentType componentType,
                            LineManipulatorNode manipulatorNode, ModelViewTransform mvt, Property<Line> line,
                            Property<DoubleRange> x2Range, Property<DoubleRange> y2Range ) {
        super( userComponent, componentType, manipulatorNode, mvt, line );
        this.x2Range = x2Range;
        this.y2Range = y2Range;
    }

    @Override protected void startDrag( PInputEvent event ) {
        super.startDrag( event );
        Point2D pMouse = event.getPositionRelativeTo( manipulatorNode.getParent() );
        clickXOffset = pMouse.getX() - mvt.modelToViewDeltaX( line.get().x2 );
        clickYOffset = pMouse.getY() - mvt.modelToViewDeltaY( line.get().y2 );
    }

    @Override protected void drag( PInputEvent event ) {
        super.drag( event );
        Point2D pMouse = event.getPositionRelativeTo( manipulatorNode.getParent() );
        // constrain to range, snap to grid
        double x2 = MathUtil.roundHalfUp( MathUtil.clamp( mvt.viewToModelDeltaX( pMouse.getX() - clickXOffset ), x2Range.get() ) );
        double y2 = MathUtil.roundHalfUp( MathUtil.clamp( mvt.viewToModelDeltaY( pMouse.getY() - clickYOffset ), y2Range.get() ) );
        // Don't allow points to be the same, this would result in slope=0/0 (undefined line.)
        if ( !( x2 == line.get().x1 && y2 == line.get().y1 ) ) {
            // Keep (x1,y1) constant, change (x2,y2) and slope.
            line.set( new Line( line.get().x1, line.get().y1, x2, y2, line.get().color ) );
        }
    }
}
