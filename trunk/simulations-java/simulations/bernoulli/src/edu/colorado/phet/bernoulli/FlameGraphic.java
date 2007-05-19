package edu.colorado.phet.bernoulli;

import edu.colorado.phet.bernoulli.common.RectangleImageGraphic2WithBuffer;
import edu.colorado.phet.common.bernoulli.view.graphics.Graphic;
import edu.colorado.phet.common.bernoulli.bernoulli.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.common.bernoulli.bernoulli.graphics.transform.TransformListener;
import edu.colorado.phet.common.bernoulli.bernoulli.simpleobserver.SimpleObserver;

import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * User: Sam Reid
 * Date: Sep 30, 2003
 * Time: 1:26:13 AM
 *
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
        image = new RectangleImageGraphic2WithBuffer( BernoulliResources.getImage( "flame.gif" ), observer );
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
