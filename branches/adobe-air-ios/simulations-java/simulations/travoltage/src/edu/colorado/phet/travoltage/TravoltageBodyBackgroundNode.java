// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.piccolophet.util.PImageFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * User: Sam Reid
 * Date: Jul 1, 2006
 * Time: 12:03:28 AM
 */

public class TravoltageBodyBackgroundNode extends PNode {
    public TravoltageBodyBackgroundNode() {
        PImage image = PImageFactory.create( "travoltage/images/travolta-hinges.gif" );
        addChild( image );
    }
}
