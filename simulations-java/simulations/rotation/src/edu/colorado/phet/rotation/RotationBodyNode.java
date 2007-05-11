package edu.colorado.phet.rotation;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Author: Sam Reid
 * May 11, 2007, 12:12:50 AM
 */
public class RotationBodyNode extends PhetPNode {
    private RotationBody rotationBody;

    public RotationBodyNode( RotationBody rotationBody ) {
        this.rotationBody = rotationBody;
        addChild( new PText( "body" ) );
    }
}
