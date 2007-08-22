/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * PushpinNode represents the pushpin used to "pin" the DNA strand or enzyme in place.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PushpinNode extends PComposite {

    public PushpinNode() {
        super();
        
        PImage imageNode = new PImage( OTResources.getImage( OTConstants.IMAGE_PUSHPIN ) );
        // move origin to the point of the pin, at lower right
        PBounds b = imageNode.getFullBoundsReference();
        imageNode.setOffset( -b.getWidth(), -b.getHeight() );
        
        addChild( imageNode );
    }
}
