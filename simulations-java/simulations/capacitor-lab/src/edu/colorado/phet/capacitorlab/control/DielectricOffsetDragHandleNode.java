/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeAdapter;
import edu.colorado.phet.capacitorlab.util.UnitsUtils;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Drag handle for changing the dielectric offset.
 * Origin is at the far end of the dashed line.
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
    private final ModelViewTransform mvt;
    private final DragHandleValueNode valueNode;
    
    public DielectricOffsetDragHandleNode( final Capacitor capacitor, ModelViewTransform mvt, DoubleRange valueRange ) {
        
        this.capacitor = capacitor;
        this.mvt = mvt;
        
        // arrow
        DragHandleArrowNode arrowNode = new DragHandleArrowNode( ARROW_TIP_LOCATION, ARROW_TAIL_LOCATION );
        arrowNode.addInputEventListener( new DielectricOffsetDragHandler( this, capacitor, mvt, valueRange ) );
        
        // line
        DragHandleLineNode lineNode = new DragHandleLineNode( LINE_START_LOCATION, LINE_END_LOCATION );
        
        // value
        double millimeters = UnitsUtils.metersToMillimeters( capacitor.getDielectricOffset() );
        valueNode = new DragHandleValueNode( CLStrings.PATTERN_DIELECTRIC_OFFSET, millimeters, CLStrings.UNITS_MILLIMETERS );
        
        // update when related model properties change
        capacitor.addCapacitorChangeListener( new CapacitorChangeAdapter() {
            
            @Override
            public void dielectricOffsetChanged() {
                updateValueDisplay();
                updateOffset();
            }
            
            @Override
            public void plateSizeChanged() {
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
        lineNode.setOffset( x, y );
        x = lineNode.getFullBoundsReference().getMaxX() + 2;
        y = 0;
        arrowNode.setOffset( x, y );
        x = arrowNode.getXOffset();
        y = arrowNode.getFullBoundsReference().getMaxY();
        valueNode.setOffset( x, y );
        
        updateValueDisplay();
        updateOffset();
    }
    
    private void updateValueDisplay() {
        double millimeters = UnitsUtils.metersToMillimeters( capacitor.getDielectricOffset() );
        valueNode.setValue( millimeters );
    }
    
    private void updateOffset() {
        Point2D capacitorLocation = mvt.modelToView( capacitor.getLocationReference() );
        double plateSize = mvt.modelToView( capacitor.getPlateSideLength() );
        double dielectricOffset = mvt.modelToView( capacitor.getDielectricOffset() );
        double x = capacitorLocation.getX() + ( plateSize / 2 ) + dielectricOffset;
        double y = capacitorLocation.getY();
        setOffset( x, y );
    }
    
    private static class DielectricOffsetDragHandler extends PDragSequenceEventHandler {
        
        private final PNode dragNode;
        private final Capacitor capacitor;
        private final ModelViewTransform mvt;
        private final DoubleRange valueRange;
        private double clickXOffset; // x-offset of mouse click from node's origin, in parent node's coordinate frame
        
        public DielectricOffsetDragHandler( PNode dragNode, Capacitor capacitor, ModelViewTransform mvt, DoubleRange valueRange ) {
            this.dragNode = dragNode;
            this.capacitor = capacitor;
            this.mvt = mvt;
            this.valueRange = valueRange;
        }
        
        @Override
        protected void startDrag( PInputEvent event ) {
            super.startDrag( event );
            Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
            double xView = mvt.modelToView( capacitor.getDielectricOffset() );
            clickXOffset = pMouse.getX() - xView;
        }

        @Override
        protected void drag( final PInputEvent event ) {
            super.drag( event );
            Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
            double xView = pMouse.getX() - clickXOffset;
            double xModel = mvt.viewToModel( xView );
            if ( xModel > valueRange.getMax() ) {
                xModel = valueRange.getMax();
            }
            else if ( xModel < valueRange.getMin() ) {
                xModel = valueRange.getMin();
            }
            capacitor.setDielectricOffset( xModel );
        }
    }
}
