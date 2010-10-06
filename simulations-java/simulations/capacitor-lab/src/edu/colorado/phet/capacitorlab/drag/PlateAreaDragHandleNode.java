/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.drag;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeAdapter;
import edu.colorado.phet.capacitorlab.util.UnitsUtils;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.piccolophet.PhetPNode;

/**
 * Drag handle for changing the plate area.
 * Origin is at the end of the dashed line that is farthest from the arrow.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PlateAreaDragHandleNode extends PhetPNode {
    
    private static final Point2D ARROW_TIP_LOCATION = new Point2D.Double( 0, 0 );
    private static final Point2D ARROW_TAIL_LOCATION = new Point2D.Double( 0, CLConstants.DRAG_HANDLE_ARROW_LENGTH );
    
    private static final double LINE_LENGTH = 20;
    private static final Point2D LINE_START_LOCATION = new Point2D.Double( 0, 0 );
    private static final Point2D LINE_END_LOCATION = new Point2D.Double( 0, LINE_LENGTH );
    
    private final Capacitor capacitor;
    private final ModelViewTransform mvt;
    private final DragHandleArrowNode arrowNode;
    private final DragHandleLineNode lineNode;
    private final DragHandleValueNode valueNode;
    private final PlateAreaDragHandler dragHandler;
    
    public PlateAreaDragHandleNode( final Capacitor capacitor, ModelViewTransform mvt, DoubleRange valueRange ) {
        
        this.capacitor = capacitor;
        this.mvt = mvt;
        
        // arrow
        arrowNode = new DragHandleArrowNode( ARROW_TIP_LOCATION, ARROW_TAIL_LOCATION );
        dragHandler = new PlateAreaDragHandler( this, capacitor, mvt, valueRange );
        arrowNode.addInputEventListener( dragHandler );
        
        // line
        lineNode = new DragHandleLineNode( LINE_START_LOCATION, LINE_END_LOCATION );
        
        // value
        double millimetersSquared = UnitsUtils.metersSquaredToMillimetersSquared( capacitor.getPlateArea() );
        valueNode = new DragHandleValueNode( CLStrings.PATTERN_VALUE_UNITS, CLStrings.LABEL_PLATE_AREA, millimetersSquared, CLStrings.UNITS_MILLIMETERS_SQUARED );
        
        // watch for model changes
        capacitor.addCapacitorChangeListener( new CapacitorChangeAdapter() {
            
            @Override
            public void plateSizeChanged() {
                updateDisplay();
                updateOffset();
            }
            
            @Override
            public void plateSeparationChanged() {
                updateOffset();
            }
        });

        // rendering order
        addChild( lineNode );
        addChild( arrowNode );
        addChild( valueNode );
        
        // layout
        double x = 0;
        double y = 0;
        double angle = ( ( Math.PI / 2 ) + mvt.getYaw() ) - ( mvt.getYaw() / 2 ); // aligned with diagonal of plate surface
        lineNode.setOffset( x, y );
        lineNode.setRotation( angle );
        x = lineNode.getFullBoundsReference().getMinX();
        y = lineNode.getFullBoundsReference().getMaxY() + 2;
        arrowNode.setOffset( x, y );
        arrowNode.setRotation( angle );
        x = lineNode.getFullBoundsReference().getMaxX() - valueNode.getFullBoundsReference().getWidth();
        y = lineNode.getFullBoundsReference().getMinY() - valueNode.getFullBoundsReference().getHeight();
        valueNode.setOffset( x, y );
        
        // initial state
        updateDisplay();
        updateOffset();
    }
    
    private void updateDisplay() {
        double millimetersSquared = UnitsUtils.metersSquaredToMillimetersSquared( capacitor.getPlateArea() );
        valueNode.setValue( millimetersSquared );
    }
    
    private void updateOffset() {
        double x = capacitor.getLocationReference().getX() - ( capacitor.getPlateSideLength() / 2 );
        double y = capacitor.getLocationReference().getY() - ( capacitor.getPlateSeparation() / 2 ) - capacitor.getPlateThickness();
        double z = capacitor.getLocationReference().getZ() - ( capacitor.getPlateSideLength() / 2 );
        Point2D handleLocation = mvt.modelToView( x, y, z );
        setOffset( handleLocation );
    }
}
