/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.common;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.coreadditions.graphics.GraphicsUtilities;
import edu.colorado.phet.ec2.EC2Module;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Aug 2, 2003
 * Time: 1:28:47 PM
 * Copyright (c) Aug 2, 2003 by Sam Reid
 */
public class RotatableImageGraphic implements Graphic {
    double angle;
    BufferedImage image;
    int x;
    int y;
    AffineTransform transform;

    public RotatableImageGraphic( double angle, BufferedImage image, int x, int y ) {
        this.angle = angle;
        this.image = image;
        this.x = x;
        this.y = y;
        transform = GraphicsUtilities.getImageTransform( image, angle, x, y );
    }

    public void setState( double angle, int x, int y ) {
        Rectangle pre=getRect();
        if( image == null ) {
            return;
        }
        this.angle = angle;
        this.x = x;
        this.y = y;
        this.transform = GraphicsUtilities.getImageTransform( image, angle, x, y );
        Rectangle post=getRect();
        EC2Module.repaint(pre,post);
    }

    private Rectangle getRect() {
        Rectangle orig=new Rectangle( 0,0,image.getWidth( ),image.getHeight( ) );
        Rectangle view=transform.createTransformedShape( orig ).getBounds();
        return view;
    }

    public void paint( Graphics2D graphics2D ) {
        if( image != null && transform != null ) {
            graphics2D.drawRenderedImage( image, transform );
        }
    }

    public void setBufferedImage( BufferedImage image ) {
        this.image = image;
    }

}
