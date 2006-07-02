/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Jul 2, 2006
 * Time: 12:01:51 AM
 * Copyright (c) Jul 2, 2006 by Sam Reid
 */

public class DoorknobNode extends PNode {
    public DoorknobNode() {
        addChild( PImageFactory.create( "images/knob.jpg" ) );
    }
}
