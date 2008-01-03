/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.view;

import edu.colorado.phet.glaciers.GlaciersImages;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.nodes.PComposite;


public class ValleyNode extends PComposite {

    public ValleyNode() {
        super();
        PImage imageNode = new PImage( GlaciersImages.VALLEY );
        addChild( imageNode );
        setPickable( false );
    }
    
    public void cleanup() {}
}
