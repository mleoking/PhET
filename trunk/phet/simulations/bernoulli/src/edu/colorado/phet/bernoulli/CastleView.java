package edu.colorado.phet.bernoulli;

import edu.colorado.phet.bernoulli.common.RectangleImageGraphic2WithBuffer;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;

import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * User: Sam Reid
 * Date: Sep 30, 2003
 * Time: 12:58:36 AM
 * Copyright (c) Sep 30, 2003 by Sam Reid
 */
public class CastleView implements Graphic {
    private Castle castle;
    private ModelViewTransform2d transform;
    private ImageObserver observer;
    RectangleImageGraphic2WithBuffer image;

    public CastleView( Castle castle, ModelViewTransform2d transform, ImageObserver observer ) {
        this.castle = castle;
        this.transform = transform;
        this.observer = observer;
        image = new RectangleImageGraphic2WithBuffer( new ImageLoader().loadBufferedImage( "images/castle300.gif" ), observer );
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2d mvt ) {
                update();
            }
        } );
        update();
    }

    private void update() {
        Rectangle outRect = transform.modelToView( castle.getRectangle() );
        image.setOutputRect( outRect );
    }

    public void paint( Graphics2D g ) {
        image.paint( g );
    }
}
