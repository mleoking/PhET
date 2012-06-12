// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.model.StraightLine;
import edu.colorado.phet.linegraphing.common.view.LineManipulatorDragHandler;
import edu.colorado.phet.linegraphing.common.view.LineManipulatorNode;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Drag handler for the (x1,y1) point manipulator of a line in point-slope form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class X1Y1DragHandler extends LineManipulatorDragHandler {

    private final Property<DoubleRange> x1Range, y1Range;
    private double clickXOffset, clickYOffset; // offset of mouse click from dragNode's origin, in parent's coordinate frame

    public X1Y1DragHandler( IUserComponent userComponent, IUserComponentType componentType,
                            LineManipulatorNode manipulatorNode, ModelViewTransform mvt, Property<StraightLine> line,
                            Property<DoubleRange> x1Range, Property<DoubleRange> y1Range ) {
        super( userComponent, componentType, manipulatorNode, mvt, line );
        this.x1Range = x1Range;
        this.y1Range = y1Range;
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
        double x1 = MathUtil.clamp( mvt.viewToModelDeltaX( pMouse.getX() - clickXOffset ), x1Range.get() );
        double y1 = MathUtil.clamp( mvt.viewToModelDeltaY( pMouse.getY() - clickYOffset ), y1Range.get() );
        updateLine( line.get().rise, line.get().run, x1, y1 );
    }
}
