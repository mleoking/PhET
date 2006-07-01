/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Jul 1, 2006
 * Time: 1:07:31 AM
 * Copyright (c) Jul 1, 2006 by Sam Reid
 */

public class ElectronNode extends PNode {
    public ElectronNode() {
        addChild( PImageFactory.create( "images/Electron3.GIF" ) );
    }
}
