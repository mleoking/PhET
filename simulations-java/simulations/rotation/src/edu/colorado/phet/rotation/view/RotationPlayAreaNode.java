package edu.colorado.phet.rotation.view;

import edu.colorado.phet.rotation.controls.VectorViewModel;
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
    private RotationOriginNode originNode;

    public RotationPlayAreaNode( final RotationModel rotationModel, VectorViewModel vectiorViewModel ) {
        this.rotationModel = rotationModel;
        platformNode = new PlatformNode( rotationModel, rotationModel.getRotationPlatform() );
        originNode = new RotationOriginNode( rotationModel.getRotationPlatform() );

        addChild( platformNode );
        addChild( rotationBodyLayer );
        addChild( vectorLayer );
        addChild( originNode );

        for( int i = 0; i < rotationModel.getNumRotationBodies(); i++ ) {
            addRotationBodyNode( rotationModel.getRotationBody( i ) );
        }
        for( int i = 0; i < rotationModel.getNumRotationBodies(); i++ ) {
            addVectorNode( rotationModel.getRotationBody( i ), vectiorViewModel );
        }
    }

    private void addVectorNode( RotationBody rotationBody, VectorViewModel vectorViewModel ) {
        vectorLayer.addChild( new BodyVectorLayer( rotationModel, rotationBody, vectorViewModel ) );
    }

    public PlatformNode getPlatformNode() {
        return platformNode;
    }

    private void addRotationBodyNode( RotationBody rotationBody ) {
        rotationBodyLayer.addChild( new RotationBodyNode( rotationModel, rotationBody ) );
    }

}
