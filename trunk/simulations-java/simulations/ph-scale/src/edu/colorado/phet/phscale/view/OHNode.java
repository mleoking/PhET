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

    public OHNode() {
        super();
        PImage imageNode = new PImage( PHScaleImages.OH );
        addChild( imageNode );
    }
}
