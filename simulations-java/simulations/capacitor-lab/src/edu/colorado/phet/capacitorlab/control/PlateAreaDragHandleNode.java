/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeAdapter;
import edu.colorado.phet.common.piccolophet.PhetPNode;

/**
 * Drag handle for changing the plate area.
 * Origin is at the far end of the dashed line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PlateAreaDragHandleNode extends PhetPNode {

    private static final Point2D ARROW_TIP_LOCATION = new Point2D.Double( 0, 0 );
    private static final Point2D ARROW_TAIL_LOCATION = new Point2D.Double( 0, CLConstants.DRAG_HANDLE_ARROW_LENGTH );
    
    private static final double LINE_LENGTH = 20;
    private static final Point2D LINE_START_LOCATION = new Point2D.Double( 0, 0 );
    private static final Point2D LINE_END_LOCATION = new Point2D.Double( 0, LINE_LENGTH );
    
    private final DragHandleArrowNode arrowNode;
    private final DragHandleLineNode lineNode;
    private final DragHandleValueNode valueNode;
    
    public PlateAreaDragHandleNode( final Capacitor capacitor ) {
        
        // arrow
        arrowNode = new DragHandleArrowNode( ARROW_TIP_LOCATION, ARROW_TAIL_LOCATION );
        
        // line
        lineNode = new DragHandleLineNode( LINE_START_LOCATION, LINE_END_LOCATION );
        
        // value
        valueNode = new DragHandleValueNode( CLStrings.PATTERN_PLATE_AREA, capacitor.getPlateArea(), CLStrings.UNITS_MILLIMETERS_SQUARED );
        
        // update value and layout when plate size changes
        capacitor.addCapacitorChangeListener( new CapacitorChangeAdapter() {
            @Override
            public void plateSizeChanged() {
                valueNode.setValue( capacitor.getPlateArea() );
                updateLayout();
            }
        });
        
        // rendering order
        addChild( lineNode );
        addChild( arrowNode );
        addChild( valueNode );
        
        // layout
        updateLayout();
    }
    
    private void updateLayout() {
        double angle = ( ( Math.PI / 2) - CLConstants.VIEWING_ANGLE ) + ( CLConstants.VIEWING_ANGLE / 2 ); // aligned with diagonal of plate surface
        double x = 0;
        double y = 0;
        lineNode.setOffset( x, y );
        lineNode.setRotation( angle );
        x = lineNode.getFullBoundsReference().getMinX();
        y = lineNode.getFullBoundsReference().getMaxY() + 2;
        arrowNode.setOffset( x, y );
        arrowNode.setRotation( angle );
        x = lineNode.getFullBoundsReference().getMaxX() - valueNode.getFullBoundsReference().getWidth();
        y = lineNode.getFullBoundsReference().getMinY() - valueNode.getFullBoundsReference().getHeight();
        valueNode.setOffset( x, y );
    }
}
