// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.workenergy.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
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
            private Point2D.Double relativeGrabPoint;

            public void mousePressed( PInputEvent event ) {
                updateGrabPoint( event );
            }

            private void updateGrabPoint( PInputEvent event ) {
                Point2D viewStartingPoint = event.getPositionRelativeTo( getParent() );
                Point2D viewCoordinateOfObject = transform.modelToView( workEnergyObject.getX(), workEnergyObject.getY() );
                relativeGrabPoint = new Point2D.Double( viewStartingPoint.getX() - viewCoordinateOfObject.getX(), viewStartingPoint.getY() - viewCoordinateOfObject.getY() );
            }

            public void mouseDragged( PInputEvent event ) {
                if ( relativeGrabPoint == null ) {
                    updateGrabPoint( event );
                }
                final Point2D newDragPosition = event.getPositionRelativeTo( getParent() );
                Point2D modelLocation = transform.viewToModel( newDragPosition.getX() - relativeGrabPoint.getX(),
                                                               newDragPosition.getY() - relativeGrabPoint.getY() );
                workEnergyObject.setPosition( modelLocation.getX(), Math.max( modelLocation.getY(), 0 ) );//not allowed to go to negative Potential Energy
            }

            public void mouseReleased( PInputEvent event ) {
                relativeGrabPoint = null;
            }
        } );
    }
}
