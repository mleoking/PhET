// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.drag;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.ComplexCapacitor;
import edu.colorado.phet.capacitorlab.util.UnitsUtils;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPNode;

/**
 * Drag handle for changing the plate separation.
 * Origin is at the end of the dashed line that is farthest from the arrow.
 * Attached to the top capacitor plate, in the center of the plate's top face.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PlateSeparationDragHandleNode extends PhetPNode {

    // endpoints for vertical double-headed arrow
    private static final Point2D ARROW_START_LOCATION = new Point2D.Double( 0, 0 );
    private static final Point2D ARROW_END_LOCATION = new Point2D.Double( 0, -CLConstants.DRAG_HANDLE_ARROW_LENGTH );

    // endpoints for vertical line
    private static final double LINE_LENGTH = 60;
    private static final Point2D LINE_START_LOCATION = new Point2D.Double( 0, 0 );
    private static final Point2D LINE_END_LOCATION = new Point2D.Double( 0, -LINE_LENGTH );

    private final ComplexCapacitor capacitor;
    private final CLModelViewTransform3D mvt;
    private final DragHandleValueNode valueNode;

    public PlateSeparationDragHandleNode( final ComplexCapacitor capacitor, CLModelViewTransform3D mvt, DoubleRange valueRange ) {

        this.capacitor = capacitor;
        this.mvt = mvt;

        // arrow
        DragHandleArrowNode arrowNode = new DragHandleArrowNode( ARROW_START_LOCATION, ARROW_END_LOCATION );
        arrowNode.addInputEventListener( new PlateSeparationDragHandler( this, capacitor, mvt, valueRange ) );

        // line
        DragHandleLineNode lineNode = new DragHandleLineNode( LINE_START_LOCATION, LINE_END_LOCATION );

        // value
        double millimeters = UnitsUtils.metersToMillimeters( capacitor.getPlateSeparation() );
        valueNode = new DragHandleValueNode( CLStrings.PATTERN_VALUE_UNITS, CLStrings.SEPARATION, millimeters, CLStrings.MILLIMETERS );

        // rendering order
        addChild( lineNode );
        addChild( arrowNode );
        addChild( valueNode );

        // layout: arrow about line, horizontally centered
        double x = 0;
        double y = 0;
        lineNode.setOffset( x, y );
        x = 0;
        y = lineNode.getFullBoundsReference().getMinY() - 2;
        arrowNode.setOffset( x, y );
        x = arrowNode.getFullBoundsReference().getMaxX() - valueNode.getFullBoundsReference().getWidth();
        y = arrowNode.getFullBoundsReference().getMinY() - valueNode.getFullBoundsReference().getHeight();
        valueNode.setOffset( x, y );

        // update when related model properties change
        capacitor.addPlateSeparationObserver( new SimpleObserver() {
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
        double millimeters = UnitsUtils.metersToMillimeters( capacitor.getPlateSeparation() );
        valueNode.setValue( millimeters );
    }

    // Attach drag handle top capacitor plate, in center the plate's top face.
    private void updateOffset() {
        double x = capacitor.getX() - ( 0.3 * capacitor.getPlateWidth() );
        double y = capacitor.getY() - ( capacitor.getPlateSeparation() / 2 ) - capacitor.getPlateHeight();
        double z = 0;
        Point2D handleLocation = mvt.modelToView( x, y, z );
        setOffset( handleLocation );
    }
}
