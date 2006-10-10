/* Copyright 2004, Sam Reid */
package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Jun 1, 2006
 * Time: 12:25:26 PM
 * Copyright (c) Jun 1, 2006 by Sam Reid
 */

public class BackgroundNode extends PhetPNode {
    private EC3Canvas ec3Canvas;
    private Image backgroundImage;
    private PNode floorGraphic;

    public BackgroundNode( EC3Canvas ec3Canvas, Image backgroundImage, PNode floorGraphic ) {
        this.ec3Canvas = ec3Canvas;
        this.backgroundImage = backgroundImage;
        this.floorGraphic = floorGraphic;
    }

    public void update() {
        if( backgroundImage == null ) {
            removeAllChildren();
        }
        else {
//            System.out.println( "EC3RootNode.updateBackgroundImage@" + System.currentTimeMillis() );
            BufferedImage i2 = BufferedImageUtils.toBufferedImage( backgroundImage );
            if( ec3Canvas.getHeight() > 0 && ec3Canvas.getWidth() > 0 ) {
                i2 = BufferedImageUtils.rescaleYMaintainAspectRatio( i2, ec3Canvas.getHeight() );
//                System.out.println( "i2.getHeight( ) = " + i2.getHeight() + ", canvasHeight=" + ec3Canvas.getHeight() );
            }
            removeAllChildren();
            PImage child = new PImage( i2 );
//        double overshootY = ec3Canvas.getHeight() - child.getFullBounds().getHeight();
            double maxY = floorGraphic.getGlobalFullBounds().getMinY();
            Point2D.Double loc = new Point2D.Double( 0, maxY );
            globalToLocal( loc );
//        child.translate( 0, -loc.y + getEC3Panel().getHeight() );
            double dy = child.getFullBounds().getHeight() - maxY;
            child.translate( 0, -dy );
            addChild( child );
        }
    }

    public void setBackground( Image image ) {
        if( image != null ) {
            BufferedImage im = BufferedImageUtils.toBufferedImage( image );//todo this is a bit of a hack for background color.
            ec3Canvas.setBackground( new Color( im.getRGB( im.getWidth() / 2, 30 ) ) );
        }
        else {
            ec3Canvas.setBackground( EC3RootNode.SKY_COLOR );
        }
        if( this.backgroundImage != image ) {
            this.backgroundImage = image;
            update();
        }
    }
}
