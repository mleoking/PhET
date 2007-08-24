package edu.colorado.phet.bernoulli;

import edu.colorado.phet.bernoulli.common.RectangleImageGraphic2;
import edu.colorado.phet.common.bernoulli.bernoulli.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.common.bernoulli.bernoulli.graphics.transform.TransformListener;
import edu.colorado.phet.common.bernoulli.view.graphics.Graphic;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Sep 29, 2003
 * Time: 11:56:29 PM
 */
public class FireDog implements Graphic {
    RectangleImageGraphic2 rig2;
    private ModelViewTransform2d transform;
    private Rectangle2D.Double dogRect;

    public FireDog( double x, double y, ModelViewTransform2d transform ) {
        this.transform = transform;
        BufferedImage image = BernoulliResources.getImage( "firedog2.gif" );
//        double aspectRatio=image.getHeight()/((double)image.getWidth());
        rig2 = new RectangleImageGraphic2( image );
        double dogWidth = 1.8;
        double dogHeight = 2.4;
        double dogX = 8.6;
        double dogY = 0;
        dogRect = new Rectangle2D.Double( dogX, dogY, dogWidth, dogHeight );
        update();
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2d mvt ) {
                update();
            }
        } );
    }

    private void update() {
        Rectangle r = transform.modelToView( dogRect );
        rig2.setOutputRect( r );
    }

    public void paint( Graphics2D g ) {
        g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
//        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        rig2.paint( g );
    }
}
