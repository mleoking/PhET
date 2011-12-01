// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.piccolo;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Author: Sam Reid
 * Mar 28, 2007, 12:08:06 AM
 */
public class SurfaceObjectNode extends PhetPNode {
    public static final String HOUSE_RESOURCE_NAME = "espb_house.png";
    public static final String MOUNTAIN_RESOURCE_NAME = "espb_mountains.png";

    public SurfaceObjectNode( String resourceName, double sy, double x ) {
        this( EnergySkateParkResources.getImage( resourceName ), sy, x );
    }

    private SurfaceObjectNode( BufferedImage image, double sy, double x ) {
        PNode imageNode = new PImage( image );
        double scale = sy / image.getHeight();
        imageNode.transformBy( AffineTransform.getScaleInstance( scale, -scale ) );
        double dy = -imageNode.getFullBounds().getHeight() / scale;
        imageNode.translate( x / scale, dy );//10 meters east
        addChild( imageNode );
    }
}
