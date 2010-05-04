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
    
    private static final double LINE_LENGTH = 60;
    private static final Point2D LINE_START_LOCATION = new Point2D.Double( 0, 0 );
    private static final Point2D LINE_END_LOCATION = new Point2D.Double( 0, LINE_LENGTH );
    
    public PlateAreaDragHandleNode( final Capacitor capacitor ) {
        
        // arrow
        DragHandleArrowNode arrowNode = new DragHandleArrowNode( ARROW_TIP_LOCATION, ARROW_TAIL_LOCATION );
        
        // line
        DragHandleLineNode lineNode = new DragHandleLineNode( LINE_START_LOCATION, LINE_END_LOCATION );
        
        // value
        final DragHandleValueNode valueNode = new DragHandleValueNode( CLStrings.PATTERN_PLATE_AREA, capacitor.getPlateArea(), CLStrings.UNITS_MILLIMETERS_SQUARED );
        
        // update value when offset changes
        capacitor.addCapacitorChangeListener( new CapacitorChangeAdapter() {
            @Override
            public void plateSizeChanged() {
                valueNode.setValue( capacitor.getPlateArea() );
            }
        });
        
        // rendering order
        addChild( lineNode );
        addChild( arrowNode );
        addChild( valueNode );
        
        // layout
        double x = 0;
        double y = 0;
        lineNode.setOffset( x, y );
        x = 0;
        y = lineNode.getFullBoundsReference().getMaxY() + 2;
        arrowNode.setOffset( x, y );
        x = arrowNode.getFullBoundsReference().getMaxX() - valueNode.getFullBoundsReference().getWidth();
        y = arrowNode.getFullBoundsReference().getMaxY() + 2;
        valueNode.setOffset( x, y );
    }
}
