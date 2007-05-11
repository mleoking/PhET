package edu.colorado.phet.rotation.tests;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 2:49:22 PM
 *
 */

import edu.colorado.phet.rotation.RotationBody;
import edu.colorado.phet.rotation.RotationBodyNode;

public class TestPlatformNodeWithBody extends TestPlatformNode implements RotationBodyNode.RotationBodyEnvironment {
    public TestPlatformNodeWithBody() {
        RotationBody body = new RotationBody();
        RotationBodyNode rotationBodyNode = new RotationBodyNode( this, body );
        getPhetPCanvas().addScreenChild( rotationBodyNode );
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch( CloneNotSupportedException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    public static void main( String[] args ) {
        new TestPlatformNodeWithBody().start();
    }

    public void dropBody( RotationBody rotationBody ) {
        rotationBody.setOnPlatform( super.getRotationPlatform() );
    }
}