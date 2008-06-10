/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.view;

import edu.colorado.phet.phscale.PHScaleImages;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.nodes.PComposite;


public class H3ONode extends PComposite {

    public H3ONode() {
        super();
        PImage imageNode = new PImage( PHScaleImages.H3O );
        addChild( imageNode );
    }
}
