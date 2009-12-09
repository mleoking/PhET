package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import edu.colorado.phet.reactantsproductsandleftovers.RPALImages;
import edu.umd.cs.piccolo.nodes.PImage;


public class FaceNode extends PImage {
    
    public FaceNode() {
        super( RPALImages.SMILEY_FACE );
    }
    
    public void smile() {
        setImage( RPALImages.SMILEY_FACE );
    }
    
    public void frown() {
        setImage( RPALImages.FROWNY_FACE );
    }

}
