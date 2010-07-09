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
    
    private final Capacitor capacitor;
    private final ModelViewTransform mvt;
    private final DragHandleValueNode valueNode;
    
    public PlateSeparationDragHandleNode( final Capacitor capacitor, ModelViewTransform mvt, DoubleRange valueRange ) {
        
        this.capacitor = capacitor;
        this.mvt = mvt;
        
        // arrow
        DragHandleArrowNode arrowNode = new DragHandleArrowNode( ARROW_TIP_LOCATION, ARROW_TAIL_LOCATION );
        arrowNode.addInputEventListener( new PlateSeparationDragHandler( this, capacitor, mvt, valueRange ) );
        
        // line
        DragHandleLineNode lineNode = new DragHandleLineNode( LINE_START_LOCATION, LINE_END_LOCATION );
        
        // value
        double millimeters = UnitsUtils.metersToMillimeters( capacitor.getPlateSeparation() );
        valueNode = new DragHandleValueNode( CLStrings.PATTERN_PLATE_SEPARATION, millimeters, CLStrings.UNITS_MILLIMETERS );
        
        // update when related model properties change
        capacitor.addCapacitorChangeListener( new CapacitorChangeAdapter() {
            
            @Override
            public void plateSeparationChanged() {
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
        x = 0;
        y = lineNode.getFullBoundsReference().getMinY() - 2;
        arrowNode.setOffset( x, y );
        x = arrowNode.getFullBoundsReference().getMaxX() - valueNode.getFullBoundsReference().getWidth();
        y = arrowNode.getFullBoundsReference().getMinY() - valueNode.getFullBoundsReference().getHeight();
        valueNode.setOffset( x, y );
        
        updateValueDisplay();
        updateOffset();
    }
    
    private void updateValueDisplay() {
        double millimeters = UnitsUtils.metersToMillimeters( capacitor.getPlateSeparation() );
        valueNode.setValue( millimeters );
    }
    
    private void updateOffset() {
        Point2D capacitorLocation = mvt.modelToView( capacitor.getLocationReference() );
        double plateSize = mvt.modelToView( capacitor.getPlateSideLength() );
        double plateSeparation = mvt.modelToView( capacitor.getPlateSeparation() );
        double x = capacitorLocation.getX() - ( 0.4 * plateSize );
        double y = capacitorLocation.getY() - ( plateSeparation / 2 );
        setOffset( x, y );
    }
    
    private static class PlateSeparationDragHandler extends PDragSequenceEventHandler {
        
        private final PNode dragNode;
        private final Capacitor capacitor;
        private final ModelViewTransform mvt;
        private final DoubleRange valueRange;
        private double clickYOffset; // y-offset of mouse click from node's origin, in parent node's coordinate frame
        
        public PlateSeparationDragHandler( PNode dragNode, Capacitor capacitor, ModelViewTransform mvt, DoubleRange valueRange ) {
            this.dragNode = dragNode;
            this.capacitor = capacitor;
            this.mvt = mvt;
            this.valueRange = new DoubleRange( valueRange );
        }
        
        @Override
        protected void startDrag( PInputEvent event ) {
            super.startDrag( event );
            Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
            double yView = mvt.modelToView( capacitor.getLocationReference().getY() - ( capacitor.getPlateSeparation() / 2 ) );
            clickYOffset = pMouse.getY() - yView;
        }

        @Override
        protected void drag( final PInputEvent event ) {
            super.drag( event );
            Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
            double yView = pMouse.getY() - clickYOffset;
            double yModel = 2 * mvt.viewToModel( -yView );
            if ( yModel > valueRange.getMax() ) {
                yModel = valueRange.getMax();
            }
            else if ( yModel < valueRange.getMin() ) {
                yModel = valueRange.getMin();
            }
            capacitor.setPlateSeparation( yModel );
        }
    }
}
