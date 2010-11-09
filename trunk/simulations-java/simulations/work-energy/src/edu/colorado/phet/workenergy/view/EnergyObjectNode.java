package edu.colorado.phet.workenergy.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.workenergy.model.WorkEnergyObject;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Sam Reid
 */
public class EnergyObjectNode extends WorkEnergyObjectNode {

    public EnergyObjectNode( final WorkEnergyObject workEnergyObject, final ModelViewTransform2D transform, Property<Boolean> originLineVisible ) {
        super( workEnergyObject, transform, originLineVisible );
        addInputEventListener( new PBasicInputEventHandler() {
            private Point2D grabPoint;

            public void mousePressed( PInputEvent event ) {
                updateGrabPoint( event );
            }

            private void updateGrabPoint( PInputEvent event ) {
                grabPoint = transform.viewToModel( event.getPositionRelativeTo( getParent() ) );
            }

            public void mouseDragged( PInputEvent event ) {
                if ( grabPoint == null ) {
                    updateGrabPoint( event );
                }
                Point2D modelLocation = transform.viewToModel( event.getPositionRelativeTo( getParent() ) );
                workEnergyObject.setPosition( modelLocation.getX() - grabPoint.getX(), Math.max( modelLocation.getY() - grabPoint.getY(), 0 ) );//not allowed to go to negative Potential Energy
            }
        } );
    }
}
