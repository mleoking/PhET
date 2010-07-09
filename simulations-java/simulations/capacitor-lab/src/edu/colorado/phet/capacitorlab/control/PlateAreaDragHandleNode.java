/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeAdapter;
import edu.colorado.phet.capacitorlab.util.UnitsUtils;
import edu.colorado.phet.capacitorlab.view.CapacitorNode;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Drag handle for changing the plate area.
 * Origin is at the far end of the dashed line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PlateAreaDragHandleNode extends PhetPNode {
    
    private static final double DRAG_HANDLE_ANGLE = ( ( Math.PI / 2) - CLConstants.YAW_VIEWING_ANGLE ) + ( CLConstants.YAW_VIEWING_ANGLE / 2 ); // aligned with diagonal of plate surface

    private static final Point2D ARROW_TIP_LOCATION = new Point2D.Double( 0, 0 );
    private static final Point2D ARROW_TAIL_LOCATION = new Point2D.Double( 0, CLConstants.DRAG_HANDLE_ARROW_LENGTH );
    
    private static final double LINE_LENGTH = 20;
    private static final Point2D LINE_START_LOCATION = new Point2D.Double( 0, 0 );
    private static final Point2D LINE_END_LOCATION = new Point2D.Double( 0, LINE_LENGTH );
    
    private final Capacitor capacitor;
    private final CapacitorNode capacitorNode;
    private final ModelViewTransform mvt;
    private final DragHandleArrowNode arrowNode;
    private final DragHandleLineNode lineNode;
    private final DragHandleValueNode valueNode;
    private final PlateAreaDragHandler dragHandler;
    
    public PlateAreaDragHandleNode( final Capacitor capacitor, CapacitorNode capacitorNode, ModelViewTransform mvt, DoubleRange valueRange ) {
        
        this.capacitor = capacitor;
        this.capacitorNode = capacitorNode;
        this.mvt = mvt;
        
        // arrow
        arrowNode = new DragHandleArrowNode( ARROW_TIP_LOCATION, ARROW_TAIL_LOCATION );
        dragHandler = new PlateAreaDragHandler( this, capacitor, mvt, valueRange );
        arrowNode.addInputEventListener( dragHandler );
        
        // line
        lineNode = new DragHandleLineNode( LINE_START_LOCATION, LINE_END_LOCATION );
        
        // value
        double millimetersSquared = UnitsUtils.metersSquaredToMillimetersSquared( capacitor.getPlateArea() );
        valueNode = new DragHandleValueNode( CLStrings.PATTERN_PLATE_AREA, millimetersSquared, CLStrings.UNITS_MILLIMETERS_SQUARED );
        
        // update value and layout when plate size changes
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
        lineNode.setOffset( x, y );
        lineNode.setRotation( DRAG_HANDLE_ANGLE );
        x = lineNode.getFullBoundsReference().getMinX();
        y = lineNode.getFullBoundsReference().getMaxY() + 2;
        arrowNode.setOffset( x, y );
        arrowNode.setRotation( DRAG_HANDLE_ANGLE );
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
      Point2D capacitorLocation = mvt.modelToView( capacitor.getLocationReference() );
      Point2D dragPointOffset = capacitorNode.getPlateSizeDragPointOffsetReference();
      double x = capacitorLocation.getX() + dragPointOffset.getX();
      double y = capacitorLocation.getY() + dragPointOffset.getY();
      setOffset( x, y );
    }
    
    private static class PlateAreaDragHandler extends PDragEventHandler {
        
        private final PNode dragNode;
        private final Capacitor capacitor;
        private final ModelViewTransform mvt;
        private final DoubleRange valueRange;
        private PDimension clickOffset; // xy-offset of mouse click from node's origin, in parent node's coordinate frame
        
        public PlateAreaDragHandler( PNode dragNode, Capacitor capacitor, ModelViewTransform mvt, DoubleRange valueRange ) {
            this.dragNode = dragNode;
            this.capacitor = capacitor;
            this.mvt = mvt;
            this.valueRange = new DoubleRange( valueRange );
            clickOffset = new PDimension();
        }
        
        @Override
        protected void startDrag(PInputEvent event) {
            super.startDrag( event );
            Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
            double xView = mvt.modelToView( capacitor.getLocationReference().getX() - ( capacitor.getPlateSideLength() / 2 ) );
            double yView = mvt.modelToView( capacitor.getLocationReference().getY() - ( capacitor.getPlateThickness() / 2 ) );
            clickOffset.setSize( pMouse.getX() - xView, pMouse.getY() - yView );
        }
        
        @Override
        protected void drag( PInputEvent event ) {
            PDimension delta = event.getDeltaRelativeTo( dragNode.getParent() );
            double deltaX = delta.getWidth();
            double deltaY = delta.getHeight();
            // only allow dragging down to the left, or up to the right
            if ( ( deltaX < 0 && deltaY > 0 ) || ( deltaX > 0 && deltaY < 0 ) ) {
                double deltaSideLength = mvt.viewToModel( -deltaX );
                double newValue = capacitor.getPlateSideLength() + deltaSideLength;
                if ( newValue > valueRange.getMax() ) {
                    newValue = valueRange.getMax();
                }
                else if ( newValue < valueRange.getMin() ) {
                    newValue = valueRange.getMin();
                }
                capacitor.setPlateSideLength( newValue );
            }
        }
    }
}
