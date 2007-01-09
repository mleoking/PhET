package edu.colorado.phet.rotation;

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

    public RotationPlayArea() {
        platformNode = new PlatformNode();
        platformNode.setOffset( 5, 5 );

        addChild( platformNode );
    }
}
