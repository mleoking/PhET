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
 * Drag handle for changing the plate separation.
 * Origin is at the far end of the dashed line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PlateSeparationDragHandleNode extends PhetPNode {

    private static final Point2D ARROW_TIP_LOCATION = new Point2D.Double( 0, 0 );
    private static final Point2D ARROW_TAIL_LOCATION = new Point2D.Double( 0, -CLConstants.DRAG_HANDLE_ARROW_LENGTH );
    
    private static final double LINE_LENGTH = 60;
    private static final Point2D LINE_START_LOCATION = new Point2D.Double( 0, 0 );
    private static final Point2D LINE_END_LOCATION = new Point2D.Double( 0, -LINE_LENGTH );
    
    public PlateSeparationDragHandleNode( final Capacitor capacitor, ModelViewTransform mvt, DoubleRange valueRange ) {
        
        // arrow
        DragHandleArrowNode arrowNode = new DragHandleArrowNode( ARROW_TIP_LOCATION, ARROW_TAIL_LOCATION );
        arrowNode.addInputEventListener( new PlateSeparationDragHandler( capacitor, mvt, valueRange ) );
        
        // line
        DragHandleLineNode lineNode = new DragHandleLineNode( LINE_START_LOCATION, LINE_END_LOCATION );
        
        // value
        double millimeters = UnitsUtils.metersToMillimeters( capacitor.getPlateSeparation() );
        final DragHandleValueNode valueNode = new DragHandleValueNode( CLStrings.PATTERN_PLATE_SEPARATION, millimeters, CLStrings.UNITS_MILLIMETERS );
        
        // update value when plate separation changes
        capacitor.addCapacitorChangeListener( new CapacitorChangeAdapter() {
            @Override
            public void plateSeparationChanged() {
                double millimeters = UnitsUtils.metersToMillimeters( capacitor.getPlateSeparation() );
                valueNode.setValue( millimeters );
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
        y = lineNode.getFullBoundsReference().getMinY() - 2;
        arrowNode.setOffset( x, y );
        x = arrowNode.getFullBoundsReference().getMaxX() - valueNode.getFullBoundsReference().getWidth();
        y = arrowNode.getFullBoundsReference().getMinY() - valueNode.getFullBoundsReference().getHeight();
        valueNode.setOffset( x, y );
    }
    
    private static class PlateSeparationDragHandler extends PDragEventHandler {
        
        private final Capacitor capacitor;
        private final ModelViewTransform mvt;
        private final DoubleRange valueRange;
        
        private double startDragValue; // the model value when the drag started
        private final Point2D startDragPosition; // mouse location when the drag started, in global coordinates
        
        public PlateSeparationDragHandler( Capacitor capacitor, ModelViewTransform mvt, DoubleRange valueRange ) {
            this.capacitor = capacitor;
            this.mvt = mvt;
            this.valueRange = new DoubleRange( valueRange );
            startDragPosition = new Point2D.Double();
        }
        
        @Override
        protected void startDrag(PInputEvent event) {
            super.startDrag( event );
            startDragPosition.setLocation( getGlobalMousePosition( event ) );
            startDragValue = capacitor.getPlateSeparation();
        }
        
        @Override
        protected void drag( PInputEvent event ) {
            
            // calculate the new model value, clamped to the range
            Point2D mousePosition = getGlobalMousePosition( event );
            double deltaY = mousePosition.getY() - startDragPosition.getY();
            double deltaValue = 2 * mvt.viewToModel( -deltaY );
            double newValue =  startDragValue + deltaValue;
            if ( newValue > valueRange.getMax() ) {
                newValue = valueRange.getMax();
            }
            else if ( newValue < valueRange.getMin() ) {
                newValue = valueRange.getMin();
            }
            
            // update the model
            capacitor.setPlateSeparation( newValue );
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
