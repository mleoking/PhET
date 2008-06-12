package edu.colorado.phet.phscale.view;

import java.awt.image.BufferedImage;

import javax.swing.SwingConstants;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.phscale.PHScaleImages;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;


public class FaucetNode extends PNode {
    
    public static final int ORIENTATION_LEFT = SwingConstants.LEFT;
    public static final int ORIENTATION_RIGHT = SwingConstants.RIGHT;
    
    public FaucetNode( int orientation ) {
        BufferedImage image = null;
        if ( orientation == ORIENTATION_RIGHT ) {
            image = PHScaleImages.FAUCET;
        }
        else {
            image = BufferedImageUtils.flipX( PHScaleImages.FAUCET );
        }
        PImage faucetImage = new PImage( image );
        faucetImage.scale( 0.05 );//XXX
        addChild( faucetImage );
    }

}
