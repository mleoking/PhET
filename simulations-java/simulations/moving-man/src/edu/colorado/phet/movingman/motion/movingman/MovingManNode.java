package edu.colorado.phet.movingman.motion.movingman;

import java.io.IOException;

import edu.colorado.phet.common.motion.model.IVariable;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Author: Sam Reid
 * May 22, 2007, 2:37:54 PM
 */
public class MovingManNode extends AbstractMovingManNode {
    public MovingManNode( final IMovingManModel motionModel ) throws IOException {
        final PImage manImage = super.getManImage();
        manImage.addInputEventListener( new CursorHandler() );
        manImage.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                motionModel.unpause();
                motionModel.setPositionDriven();
                motionModel.setPosition( event.getPositionRelativeTo( getManImage().getParent() ).getX() );
            }
        } );

        motionModel.getXVariable().addListener( new IVariable.Listener() {
            public void valueChanged() {
                updateObject( getManImage(), motionModel );
            }
        } );
        motionModel.getVVariable().addListener( new IVariable.Listener() {
            public void valueChanged() {
                MovingManNode.this.setDirection( motionModel.getVVariable().getValue() );
                updateObject( manImage, motionModel );
            }
        } );
        updateObject( manImage, motionModel );
        motionModel.addListener( new MovingManMotionModel.Adapter() {
            public void boundaryChanged() {
                MovingManNode.this.setWallsVisible( motionModel.isBoundaryOpen() );
            }
        } );
    }

    private void updateObject( PNode object, IMovingManModel model ) {
//        object.setOffset( rotationModel.getPosition() - object.getFullBounds().getWidth() / 2/object.getScale(), 2.0 - object.getFullBounds().getHeight()/object.getScale() );
        object.setOffset( model.getPosition() - object.getFullBounds().getWidth() / 2, 2.0 - object.getFullBounds().getHeight() );
    }

}
