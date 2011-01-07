// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.piccolo;

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
public class SurfaceObjectNode extends PhetPNode {
    public static final String HOUSE_URL = "energy-skate-park/images/house.png";
    public static final String MOUNTAIN_URL = "energy-skate-park/images/mountains.gif";

    public SurfaceObjectNode( String imageURL, double sy, double x ) {
        this( loadBufferedImage( imageURL ), sy, x );
    }

    public SurfaceObjectNode( BufferedImage houseImage, double sy, double x ) {
        PNode houseImageNode = new PImage( houseImage );
        double scale = sy / houseImage.getHeight();
        houseImageNode.transformBy( AffineTransform.getScaleInstance( scale, -scale ) );
        double dy = -houseImageNode.getFullBounds().getHeight() / scale;
        houseImageNode.translate( x / scale, dy );//10 meters east
        addChild( houseImageNode );
    }

    private static BufferedImage loadBufferedImage( String s ) {
        BufferedImage houseImage = null;
        try {
            houseImage = ImageLoader.loadBufferedImage( s );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return houseImage;
    }
}
