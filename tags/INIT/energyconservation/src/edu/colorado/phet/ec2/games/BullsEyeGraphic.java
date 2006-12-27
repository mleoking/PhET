/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.games;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Sep 15, 2003
 * Time: 1:43:12 AM
 * Copyright (c) Sep 15, 2003 by Sam Reid
 */
public class BullsEyeGraphic implements Graphic {
    BufferedImage im;
    double modelX;
    double modelY;
    Point viewPt;
    double modelWidth;
    private ModelViewTransform2d transform;
    private int viewWidth;
    private AffineTransform affineTransform;

    public BullsEyeGraphic( BufferedImage im, double modelX, double modelY, double modelWidth, ModelViewTransform2d transform ) {
        this.im = im;
        this.modelX = modelX;
        this.modelY = modelY;
        this.modelWidth = modelWidth;
        this.transform = transform;
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2d modelViewTransform2d ) {
                update();
            }
        } );
        update();
    }

    private void update() {
        this.viewPt = transform.modelToView( modelX, modelY );
        this.viewWidth = transform.modelToViewDifferentialX( modelWidth );
        this.affineTransform = new AffineTransform();
        double sy = 1;
        double sx = ( (double)viewWidth / im.getWidth() );
        affineTransform.scale( sx, sy );
        affineTransform.translate( viewPt.x / sx, viewPt.y / sy );
    }

    public void paint( Graphics2D g ) {
        if( im != null && affineTransform != null ) {
            g.drawRenderedImage( im, affineTransform );
        }
    }
}
