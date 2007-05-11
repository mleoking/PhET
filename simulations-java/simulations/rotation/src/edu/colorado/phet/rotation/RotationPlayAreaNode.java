package edu.colorado.phet.rotation;

import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.view.PlatformNode;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Jan 9, 2007
 * Time: 7:52:02 AM
 */

public class RotationPlayAreaNode extends PNode {
    private PlatformNode platformNode;
    private PNode rotationBodyLayer = new PNode();

    public RotationPlayAreaNode( final RotationModel rotationModel ) {
        platformNode = new PlatformNode();
        platformNode.setOffset( 5, 5 );

        addChild( platformNode );
        addChild( rotationBodyLayer );

        rotationModel.addListener( new RotationModel.Listener() {
            public void steppedInTime() {
                platformNode.setAngle( rotationModel.getLastState().getAngle() );
            }
        } );
        platformNode.addListener( new PlatformNode.Listener() {
            public void angleChanged() {
                rotationModel.setPositionDriven();
                rotationModel.setAngle( platformNode.getAngle() );
                //todo: this is a hack, to ensure time goes forward during a drag. When the simulation is based on time series, this should be removed
                rotationModel.stepInTime( 1.0 );
            }
        } );
        for( int i = 0; i < rotationModel.getNumRotationBodies(); i++ ) {
            RotationBody rotationBody = rotationModel.getRotationBody( i );
            addRotationBodyNode( rotationBody );
        }
    }

    private void addRotationBodyNode( RotationBody rotationBody ) {
        RotationBodyNode rotationBodyNode = new RotationBodyNode( rotationBody );
        rotationBodyLayer.addChild( rotationBodyNode );
    }

}
