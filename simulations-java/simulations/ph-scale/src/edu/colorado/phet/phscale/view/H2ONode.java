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

    public H2ONode() {
        super();
        PImage imageNode = new PImage( PHScaleImages.H2O_SMALL );
        addChild( imageNode );
    }
}
