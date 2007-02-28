package edu.colorado.phet.bernoulli;

import edu.colorado.phet.bernoulli.common.RectangleImageGraphic2WithBuffer;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;
import edu.colorado.phet.coreadditions.simpleobserver.SimpleObserver;

import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * User: Sam Reid
 * Date: Sep 30, 2003
 * Time: 1:26:13 AM
 * Copyright (c) Sep 30, 2003 by Sam Reid
 */
public class FlameGraphic implements Graphic {
    Flame flame;
    ModelViewTransform2d transform;
    RectangleImageGraphic2WithBuffer image;

    public FlameGraphic( Flame flame, ModelViewTransform2d transform, ImageObserver observer ) {
        this.flame = flame;
        this.transform = transform;
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2d mvt ) {
                update();
            }
        } );
        image = new RectangleImageGraphic2WithBuffer( new ImageLoader().loadBufferedImage( "images/flame.gif" ), observer );
        update();
        flame.addObserver( new SimpleObserver() {
            public void update() {
                FlameGraphic.this.update();
            }
        } );
    }

    private void update() {
        Rectangle viewRect = transform.modelToView( flame.getRectangle() );
        if( viewRect.getWidth() > 1 && viewRect.getHeight() > 1 ) {
            image.setOutputRect( viewRect );
        }
    }

    public void paint( Graphics2D g ) {
        if( image != null ) {
            image.paint( g );
        }
    }
}
