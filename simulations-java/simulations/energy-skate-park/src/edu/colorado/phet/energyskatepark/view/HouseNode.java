package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Author: Sam Reid
 * Mar 28, 2007, 12:08:06 AM
 */
public class HouseNode extends PhetPNode {

    public HouseNode() {

        PNode houseImageNode = null;
        try {
            BufferedImage houseImage = ImageLoader.loadBufferedImage( "energy-skate-park/images/house.png" );
            houseImageNode = new PImage( houseImage );
            double scale = 1.5 / houseImage.getHeight();
            houseImageNode.transformBy( AffineTransform.getScaleInstance( scale, -scale ) );
            double dy = -houseImageNode.getFullBounds().getHeight() / scale;
//            System.out.println( "dy = " + dy );
            houseImageNode.translate( 10 / scale, dy );//10 meters east
            addChild( houseImageNode );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
}
