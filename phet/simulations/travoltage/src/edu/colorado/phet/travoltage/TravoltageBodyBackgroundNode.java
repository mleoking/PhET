/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * User: Sam Reid
 * Date: Jul 1, 2006
 * Time: 12:03:28 AM
 * Copyright (c) Jul 1, 2006 by Sam Reid
 */

public class TravoltageBodyBackgroundNode extends PNode {
    public TravoltageBodyBackgroundNode() {
        PImage image = PImageFactory.create( "images/travolta-hinges.gif" );
        addChild( image );
    }
}
