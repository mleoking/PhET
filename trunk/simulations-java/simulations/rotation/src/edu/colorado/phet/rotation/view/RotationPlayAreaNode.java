package edu.colorado.phet.rotation.view;

import edu.colorado.phet.rotation.model.RotationBody;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Jan 9, 2007
 * Time: 7:52:02 AM
 */

public class RotationPlayAreaNode extends PNode {
    private PlatformNode platformNode;
    private PNode rotationBodyLayer = new PNode();
    private RotationModel rotationModel;
    private PNode vectorLayer = new PNode();

    public RotationPlayAreaNode( final RotationModel rotationModel ) {
        this.rotationModel = rotationModel;
        platformNode = new PlatformNode( rotationModel, rotationModel.getRotationPlatform() );

        addChild( platformNode );
        addChild( rotationBodyLayer );
        addChild( vectorLayer );

        for( int i = 0; i < rotationModel.getNumRotationBodies(); i++ ) {
            addRotationBodyNode( rotationModel.getRotationBody( i ) );
        }
        for( int i = 0; i < rotationModel.getNumRotationBodies(); i++ ) {
            addVectorNode( rotationModel.getRotationBody( i ) );
        }


    }

    private void addVectorNode( RotationBody rotationBody ) {
        vectorLayer.addChild( new VectorNode(rotationModel,rotationBody));
    }

    public PlatformNode getPlatformNode() {
        return platformNode;
    }

    private void addRotationBodyNode( RotationBody rotationBody ) {
        rotationBodyLayer.addChild( new RotationBodyNode( rotationModel, rotationBody ) );
    }

}
