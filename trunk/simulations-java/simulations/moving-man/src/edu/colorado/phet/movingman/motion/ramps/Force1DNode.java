package edu.colorado.phet.movingman.motion.ramps;

import java.awt.geom.Point2D;
import java.io.IOException;

import edu.colorado.phet.common.motion.model.ITemporalVariable;
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
    private IForceNodeModel forceModel;

    static interface IForceNodeModel {
        void setAppliedForce( double appliedForce );

        ITemporalVariable getXVariable();

        void addListener( Force1DMotionModel.Listener adapter );

        Force1DObjectConfig getCurrentObject();

        double getPosition();
    }

    public Force1DNode( final IForceNodeModel forceModel ) throws IOException {
        this.forceModel = forceModel;
        final PImage manImage = super.getManImage();
        manImage.addInputEventListener( new CursorHandler() );
        manImage.addInputEventListener( new PBasicInputEventHandler() {
            public Point2D pressPoint = null;

            public void mouseDragged( PInputEvent event ) {
                Point2D p2 = event.getPositionRelativeTo( manImage.getParent() );
//                forceModel.setUpdateStrategy( forceModel.getAppliedForceStrategy() );
                double dx = p2.getX() - pressPoint.getX();

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
                updateLocation();
            }
        } );
        updateLocation();

        forceModel.addListener( new Force1DMotionModel.Adapter() {
            public void objectChanged() {
                updateObject();
            }
        } );
        updateObject();
    }

    private void updateObject() {
        try {
            getManImage().setImage( forceModel.getCurrentObject().getImage() );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        updateLocation();
    }

    private void updateLocation() {
        PNode object = super.getManImage();
        object.setOffset( forceModel.getPosition() - object.getFullBounds().getWidth() / 2, 2.0 - object.getFullBounds().getHeight() );
    }

}