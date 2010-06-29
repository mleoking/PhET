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
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

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
    private final PlateAreaDragHandler dragHandler;
    
    public PlateAreaDragHandleNode( final Capacitor capacitor, ModelViewTransform mvt, DoubleRange valueRange ) {
        
        // arrow
        arrowNode = new DragHandleArrowNode( ARROW_TIP_LOCATION, ARROW_TAIL_LOCATION );
        dragHandler = new PlateAreaDragHandler( capacitor, mvt, valueRange );
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
                double millimetersSquared = UnitsUtils.metersSquaredToMillimetersSquared( capacitor.getPlateArea() );
                valueNode.setValue( millimetersSquared );
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
        
        double angle = ( ( Math.PI / 2) - CLConstants.YAW_VIEWING_ANGLE ) + ( CLConstants.YAW_VIEWING_ANGLE / 2 ); // aligned with diagonal of plate surface
        dragHandler.setAngle( angle );
        
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
    
    private static class PlateAreaDragHandler extends PDragEventHandler {
        
        private final Capacitor capacitor;
        private final ModelViewTransform mvt;
        private final DoubleRange valueRange;
        private double angle;
        
        private double startDragValue; // the model value when the drag started
        private final Point2D startDragPosition; // mouse location when the drag started, in global coordinates
        
        public PlateAreaDragHandler( Capacitor capacitor, ModelViewTransform mvt, DoubleRange valueRange ) {
            this.capacitor = capacitor;
            this.mvt = mvt;
            this.valueRange = new DoubleRange( valueRange );
            startDragPosition = new Point2D.Double();
        }
        
        public void setAngle( double angle ) {
            this.angle = angle;
        }
        
        @Override
        protected void startDrag(PInputEvent event) {
            super.startDrag( event );
            startDragPosition.setLocation( getGlobalMousePosition( event ) );
            startDragValue = capacitor.getPlateSideLength();
        }
        
        @Override
        protected void drag( PInputEvent event ) {
            
            // calculate the new model value, clamped to the range
            Point2D mousePosition = getGlobalMousePosition( event );
            double deltaX = mousePosition.getX() - startDragPosition.getX();
            double deltaY = mousePosition.getY() - startDragPosition.getY();
            
            // only allow dragging down to the left, or up to the right
            if ( ( deltaX < 0 && deltaY > 0 ) || ( deltaX > 0 && deltaY < 0 ) ) {
                
                double deltaView = 0;
                if ( Math.abs( deltaX ) > Math.abs( deltaY ) ) {
                    deltaView = -deltaX / Math.cos( angle );
                }
                else {
                    deltaView = deltaY / Math.sin( angle );
                }
                
                double deltaValue = mvt.viewToModel( deltaView );
                double newValue =  startDragValue + deltaValue;
                if ( newValue > valueRange.getMax() ) {
                    newValue = valueRange.getMax();
                }
                else if ( newValue < valueRange.getMin() ) {
                    newValue = valueRange.getMin();
                }
                
                // update the model
                capacitor.setPlateSideLength( newValue );
            }
        }
        
        /*
         * Gets the mouse position in the global coordinate frame.
         */
        private Point2D getGlobalMousePosition( PInputEvent event ) {
            Point2D pLocal = event.getPositionRelativeTo( event.getPickedNode() );
            return event.getPickedNode().localToGlobal( pLocal );
        }
    }
}
