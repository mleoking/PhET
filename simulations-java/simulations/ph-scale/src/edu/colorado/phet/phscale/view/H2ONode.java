/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.view;

import edu.colorado.phet.phscale.PHScaleImages;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * H2ONode is the visual representation of an water molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class H2ONode extends PComposite {

    protected H2ONode( boolean bigImage ) {
        super();
        if ( bigImage ) {
            PImage imageNode = new PImage( PHScaleImages.H2O_BIG );
            addChild( imageNode );
        }
        else {
            PImage imageNode = new PImage( PHScaleImages.H2O_SMALL );
            addChild( imageNode );
        }
    }
    
    public static class Big extends H2ONode {
        public Big() {
            super( true );
        }
    }
    
    public static class Small extends H2ONode {
        public Small() {
            super( false );
        }
    }
}
