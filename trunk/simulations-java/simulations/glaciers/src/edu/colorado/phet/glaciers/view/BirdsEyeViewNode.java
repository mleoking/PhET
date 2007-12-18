/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.view;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.glaciers.GlaciersImages;
import edu.umd.cs.piccolo.nodes.PImage;


public class BirdsEyeViewNode extends PhetPNode {

    public BirdsEyeViewNode() {
        super();
        PImage imageNode = new PImage( GlaciersImages.VALLEY );
        addChild( imageNode );
    }
}
