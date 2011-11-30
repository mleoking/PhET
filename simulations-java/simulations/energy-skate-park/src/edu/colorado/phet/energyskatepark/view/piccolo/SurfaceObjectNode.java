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
    public static final String HOUSE_RESOURCE_NAME = "house.png";
    public static final String MOUNTAIN_RESOURCE_NAME = "mountains.gif";

    public SurfaceObjectNode( String resourceName, double sy, double x ) {
        this( EnergySkateParkResources.getImage( resourceName ), sy, x );
    }

    public SurfaceObjectNode( BufferedImage houseImage, double sy, double x ) {
        PNode houseImageNode = new PImage( houseImage );
        double scale = sy / houseImage.getHeight();
        houseImageNode.transformBy( AffineTransform.getScaleInstance( scale, -scale ) );
        double dy = -houseImageNode.getFullBounds().getHeight() / scale;
        houseImageNode.translate( x / scale, dy );//10 meters east
        addChild( houseImageNode );
    }
}
