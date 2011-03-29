// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.drag;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.util.UnitsUtils;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPNode;

/**
 * Drag handle for changing the dielectric offset.
 * Origin is at the end of the dashed line that is farthest from the arrow.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricOffsetDragHandleNode extends PhetPNode {

    private static final Point2D ARROW_TIP_LOCATION = new Point2D.Double( 0, 0 );
    private static final Point2D ARROW_TAIL_LOCATION = new Point2D.Double( CLConstants.DRAG_HANDLE_ARROW_LENGTH, 0 );
    
    private static final double LINE_LENGTH = 60;
    private static final Point2D LINE_START_LOCATION = new Point2D.Double( 0, 0 );
    private static final Point2D LINE_END_LOCATION = new Point2D.Double( LINE_LENGTH, 0 );
    
    private final Capacitor capacitor;
    private final CLModelViewTransform3D mvt;
    private final DragHandleValueNode valueNode;
    
    public DielectricOffsetDragHandleNode( final Capacitor capacitor, CLModelViewTransform3D mvt, DoubleRange valueRange ) {
        
        this.capacitor = capacitor;
        this.mvt = mvt;
        
        // arrow
        DragHandleArrowNode arrowNode = new DragHandleArrowNode( ARROW_TIP_LOCATION, ARROW_TAIL_LOCATION );
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
        
        // layout
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
    
    private void updateValueDisplay() {
        double millimeters = UnitsUtils.metersToMillimeters( capacitor.getDielectricOffset() );
        valueNode.setValue( millimeters );
    }
    
    private void updateOffset() {
        double x = capacitor.getLocationReference().getX() + ( capacitor.getPlateWidth() / 2 ) + capacitor.getDielectricOffset();
        double y = capacitor.getLocationReference().getY();
        double z = 0;
        Point2D handleLocation = mvt.modelToView( x, y, z );
        setOffset( handleLocation );
    }
}
