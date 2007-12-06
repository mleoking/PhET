package edu.colorado.phet.movingman.motion.force1d;

import java.awt.geom.Point2D;
import java.io.IOException;

import edu.colorado.phet.common.motion.model.IVariable;
import edu.colorado.phet.common.motion.model.SingleBodyMotionModel;
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
public class Force1DPlayAreaNode extends AbstractMovingManNode {
    public Force1DPlayAreaNode( final SingleBodyMotionModel motionModel, final ForceModel forceModel ) throws IOException {
        final PImage manImage = super.getManImage();
        manImage.addInputEventListener( new CursorHandler() );
        manImage.addInputEventListener( new PBasicInputEventHandler() {
            public Point2D pressPoint = null;

            public void mouseDragged( PInputEvent event ) {
                Point2D p2 = event.getPositionRelativeTo( manImage.getParent() );
                motionModel.setUpdateStrategy( forceModel );
                double dx = p2.getX() - pressPoint.getX();

                final double appliedForce = dx * 0.001;
                forceModel.setAppliedForce( appliedForce );
            }

            public void mouseReleased( PInputEvent event ) {
                forceModel.setAppliedForce( 0.0 );
            }

            public void mousePressed( PInputEvent event ) {
                pressPoint = event.getPositionRelativeTo( manImage.getParent() );
            }
        } );

        motionModel.getXVariable().addListener( new IVariable.Listener() {
            public void valueChanged() {
                updateObject( getManImage(), motionModel );
            }
        } );
        updateObject( manImage, motionModel );
    }

    private void updateObject( PNode object, SingleBodyMotionModel model ) {
//        object.setOffset( rotationModel.getPosition() - object.getFullBounds().getWidth() / 2/object.getScale(), 2.0 - object.getFullBounds().getHeight()/object.getScale() );
        object.setOffset( model.getMotionBody().getPosition() - object.getFullBounds().getWidth() / 2, 2.0 - object.getFullBounds().getHeight() );
    }

}