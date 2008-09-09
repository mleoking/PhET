/* Copyright 2007, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.StatesOfMatterResources;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * PushpinNode represents the pushpin used to "pin" an atom in one location.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PushpinNode extends PComposite {

    public PushpinNode() {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        PImage imageNode = new PImage( StatesOfMatterResources.getImage( StatesOfMatterConstants.PUSH_PIN_IMAGE ) );
        // move origin to the point of the pin, at lower right
        PBounds b = imageNode.getFullBoundsReference();
        imageNode.setOffset( -b.getWidth(), -b.getHeight() );
        
        addChild( imageNode );
    }
}
