// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.tests;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 2:49:22 PM
 *
 */

import edu.colorado.phet.rotation.model.RotationBody;
import edu.colorado.phet.rotation.view.RotationBodyNode;

public class TestRotationPlatformNodeWithBody extends TestRotationPlatformNode implements RotationBodyNode.RotationBodyEnvironment {
    public TestRotationPlatformNodeWithBody() {
        RotationBody body = new RotationBody();
        RotationBodyNode rotationBodyNode = new RotationBodyNode( this, body );
        getPhetPCanvas().addScreenChild( rotationBodyNode );
    }

    public static void main( String[] args ) {
        new TestRotationPlatformNodeWithBody().start();
    }

    public void dropBody( RotationBody rotationBody ) {
        if ( super.getRotationPlatform().containsPosition( rotationBody.getPosition() ) ) {
            rotationBody.setOnPlatform( super.getRotationPlatform() );
        }
        else {
            rotationBody.setOffPlatform();
        }
    }

    public boolean platformContains( double x, double y ) {
        return true;
    }
}