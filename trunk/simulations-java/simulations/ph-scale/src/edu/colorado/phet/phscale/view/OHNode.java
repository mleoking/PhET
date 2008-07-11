/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.view;

import edu.colorado.phet.phscale.PHScaleImages;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * OHNode is the visual representation of a hydroxide molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OHNode extends PComposite {

    protected OHNode( boolean bigImage ) {
        super();
        if ( bigImage ) {
            PImage imageNode = new PImage( PHScaleImages.OH_BIG );
            addChild( imageNode );
        }
        else {
            PImage imageNode = new PImage( PHScaleImages.OH_SMALL );
            addChild( imageNode );      
        }
    }
    
    public static class Big extends OHNode {
        public Big() {
            super( true );
        }
    }
    
    public static class Small extends OHNode {
        public Small() {
            super( false );
        }
    }
}
