/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.view;

import edu.colorado.phet.phscale.PHScaleImages;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * OHMoleculeNode is the visual representation of a hydroxide molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OHMoleculeNode extends PComposite {

    public OHMoleculeNode() {
        super();
        PImage imageNode = new PImage( PHScaleImages.OH_MOLECULE );
        addChild( imageNode );
    }
}
