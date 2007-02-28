package edu.colorado.phet.rotation;

import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.view.PlatformNode;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Jan 9, 2007
 * Time: 7:52:02 AM
 * Copyright (c) Jan 9, 2007 by Sam Reid
 */

public class RotationPlayArea extends PNode {
    private PlatformNode platformNode;

    public RotationPlayArea( final RotationModule rotationModule ) {
        platformNode = new PlatformNode();
        platformNode.setOffset( 5, 5 );

        addChild( platformNode );

        rotationModule.getRotationModel().addListener( new RotationModel.Listener() {
            public void steppedInTime() {
                platformNode.setAngle( rotationModule.getRotationModel().getLastState().getAngle() );
            }
        } );
        platformNode.addListener( new PlatformNode.Listener() {
            public void angleChanged() {
                rotationModule.setAngleUpdateStrategy();
                rotationModule.setAngle( platformNode.getAngle() );
                //todo: this is a hack, to ensure time goes forward during a drag. When the simulation is based on time series, this should be deprecated.
                rotationModule.getRotationModel().stepInTime( 1.0 );
            }
        } );
    }
}
