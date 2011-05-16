// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.drag;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.ICapacitor;
import edu.colorado.phet.capacitorlab.util.UnitsUtils;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPNode;

/**
 * Drag handle for changing the dielectric offset.
 * Origin is at the end of the dashed line that is farthest from the arrow.
 * Attached to the center of the dielectric's right face.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricOffsetDragHandleNode extends PhetPNode {

    // endpoints for horizontal double-headed arrow
    private static final Point2D ARROW_START_LOCATION = new Point2D.Double( 0, 0 );
    private static final Point2D ARROW_END_LOCATION = new Point2D.Double( CLConstants.DRAG_HANDLE_ARROW_LENGTH, 0 );

    // endpoints for horizontal line
    private static final double LINE_LENGTH = 60;
    private static final Point2D LINE_START_LOCATION = new Point2D.Double( 0, 0 );
    private static final Point2D LINE_END_LOCATION = new Point2D.Double( LINE_LENGTH, 0 );

    private final ICapacitor capacitor;
    private final CLModelViewTransform3D mvt;
    private final DragHandleValueNode valueNode;

    public DielectricOffsetDragHandleNode( final ICapacitor capacitor, CLModelViewTransform3D mvt, DoubleRange valueRange ) {

        this.capacitor = capacitor;
        this.mvt = mvt;

        // arrow
        DragHandleArrowNode arrowNode = new DragHandleArrowNode( ARROW_START_LOCATION, ARROW_END_LOCATION );
        arrowNode.addInputEventListener( new DielectricOffsetDragHandler( this, capacitor, mvt, valueRange ) );

        // line
        DragHandleLineNode lineNode = new DragHandleLineNode( LINE_START_LOCATION, LINE_END_LOCATION );

        // value
        double millimeters = UnitsUtils.metersToMillimeters( capacitor.getDielectricOffset() );
        valueNode = new DragHandleValueNode( CLStrings.PATTERN_VALUE_UNITS, CLStrings.OFFSET, millimeters, CLStrings.MILLIMETERS );

        // rendering order
        addChild( lineNode );
        addChild( arrowNode );
        addChild( valueNode );

        // layout: arrow to the right of line, vertically centered
        double x = 0;
        double y = 0;
        lineNode.setOffset( x, y );
        x = lineNode.getFullBoundsReference().getMaxX() + 2;
        y = 0;
        arrowNode.setOffset( x, y );
        x = arrowNode.getXOffset();
        y = arrowNode.getFullBoundsReference().getMaxY();
        valueNode.setOffset( x, y );

        // update when related model properties change
        capacitor.addDielectricOffsetObserver( new SimpleObserver() {
            public void update() {
                updateValueDisplay();
                updateOffset();
            }
        } );
        capacitor.addPlateSizeObserver( new SimpleObserver() {
            public void update() {
                updateOffset();
            }
        } );
    }

    // synchronizes the value display with the model
    private void updateValueDisplay() {
        double millimeters = UnitsUtils.metersToMillimeters( capacitor.getDielectricOffset() );
        valueNode.setValue( millimeters );
    }

    // Attach drag handle to center of dielectric's right face.
    private void updateOffset() {
        double x = capacitor.getX() + ( capacitor.getPlateWidth() / 2 ) + capacitor.getDielectricOffset();
        double y = capacitor.getY();
        double z = 0;
        Point2D handleLocation = mvt.modelToView( x, y, z );
        setOffset( handleLocation );
    }
}
