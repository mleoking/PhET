/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Jul 3, 2006
 * Time: 12:35:54 AM
 * Copyright (c) Jul 3, 2006 by Sam Reid
 */

public class TravoltageBackgroundNode extends PNode {
    public TravoltageBackgroundNode() {
        addChild( PImageFactory.create( "images/livingroom.gif" ) );
    }
}
