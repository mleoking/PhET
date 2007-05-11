package edu.colorado.phet.rotation.tests;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 2:49:22 PM
 *
 */

import edu.colorado.phet.rotation.RotationBody;
import edu.colorado.phet.rotation.RotationBodyNode;

public class TestPlatformNodeWithBody extends TestPlatformNode {
    public TestPlatformNodeWithBody() {
        RotationBody body = new RotationBody();
        RotationBodyNode rotationBodyNode = new RotationBodyNode( body );
        getPhetPCanvas().addScreenChild( rotationBodyNode );
    }

    public static void main( String[] args ) {
        new TestPlatformNodeWithBody().start();
    }

}