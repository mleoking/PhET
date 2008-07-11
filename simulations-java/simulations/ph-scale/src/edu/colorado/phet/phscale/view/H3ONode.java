/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.view;

import edu.colorado.phet.phscale.PHScaleImages;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * H3ONode is the visual representation of a hydronium molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class H3ONode extends PComposite {

    protected H3ONode( boolean bigImage ) {
        super();
        if ( bigImage ) {
            PImage imageNode = new PImage( PHScaleImages.H3O_BIG );
            addChild( imageNode );
        }
        else {
            PImage imageNode = new PImage( PHScaleImages.H3O_SMALL );
            addChild( imageNode );
        }
    }
    
    public static class Big extends H3ONode {
        public Big() {
            super( true );
        }
    }
    
    public static class Small extends H3ONode {
        public Small() {
            super( false );
        }
    }
}
