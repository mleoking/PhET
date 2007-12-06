package edu.colorado.phet.movingman.motion.force1d;

import java.awt.geom.Point2D;
import java.io.IOException;

import edu.colorado.phet.common.motion.model.IVariable;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.movingman.motion.movingman.AbstractMovingManNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Author: Sam Reid
 * May 22, 2007, 2:37:54 PM
 */
public class Force1DNode extends AbstractMovingManNode {
    public Force1DNode( final ForceModel forceModel ) throws IOException {
        final PImage manImage = super.getManImage();
        manImage.addInputEventListener( new CursorHandler() );
        manImage.addInputEventListener( new PBasicInputEventHandler() {
            public Point2D pressPoint = null;

            public void mouseDragged( PInputEvent event ) {
                Point2D p2 = event.getPositionRelativeTo( manImage.getParent() );
                forceModel.setUpdateStrategy( forceModel );
                double dx = p2.getX() - pressPoint.getX();

//                final double appliedForce = dx * 0.2;
                final double appliedForce = dx * 30;
                forceModel.setAppliedForce( appliedForce );
            }

            public void mouseReleased( PInputEvent event ) {
                forceModel.setAppliedForce( 0.0 );
            }

            public void mousePressed( PInputEvent event ) {
                pressPoint = event.getPositionRelativeTo( manImage.getParent() );
            }
        } );

        forceModel.getXVariable().addListener( new IVariable.Listener() {
            public void valueChanged() {
                updateObject( getManImage(), forceModel );
            }
        } );
        updateObject( manImage, forceModel );
    }

    private void updateObject( PNode object, ForceModel model ) {
        object.setOffset( model.getPosition() - object.getFullBounds().getWidth() / 2, 2.0 - object.getFullBounds().getHeight() );
    }

}