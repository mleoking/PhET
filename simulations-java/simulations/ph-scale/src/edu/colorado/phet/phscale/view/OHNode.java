/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.view;

import edu.colorado.phet.phscale.PHScaleImages;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.nodes.PComposite;


public class OHNode extends PComposite {

    public OHNode() {
        super();
        PImage imageNode = new PImage( PHScaleImages.OH );
        addChild( imageNode );
    }
}
