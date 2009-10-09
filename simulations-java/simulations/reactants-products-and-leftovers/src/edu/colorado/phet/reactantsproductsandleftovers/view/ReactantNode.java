package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.Image;

import edu.umd.cs.piccolo.nodes.PImage;


public abstract class ReactantNode extends PImage {

    public ReactantNode( Image image ) {
        super( image );
        scale( 0.5 ); //XXX remove this, scale image files
    }
    
}
