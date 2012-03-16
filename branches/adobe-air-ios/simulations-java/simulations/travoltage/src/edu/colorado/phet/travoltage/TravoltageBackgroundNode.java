// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.piccolophet.util.PImageFactory;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Jul 3, 2006
 * Time: 12:35:54 AM
 */

public class TravoltageBackgroundNode extends PNode {
    public TravoltageBackgroundNode() {
        addChild( PImageFactory.create( "travoltage/images/livingroom.gif" ) );
    }
}
