/**
 * Class: ProtonGraphic
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.nuclearphysics.model.NuclearParticle;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;

public class ProtonGraphic extends ParticleGraphic implements ImageObserver {

    private static Stroke outlineStroke = new BasicStroke( 0.35f );
    private static Color color = Color.red;
    private static AffineTransform atx = new AffineTransform();
    private static BufferedImage protonImage;
    private static Ellipse2D.Double circle;

    static {
        ImageLoader imgLoader = new ImageLoader();
        try {
            protonImage = imgLoader.loadImage( "images/red-xsml.gif" );
            circle = new Ellipse2D.Double( 0,
                                           0,
                                           protonImage.getWidth(),
                                           protonImage.getWidth() );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }


    public ProtonGraphic() {
        super( color );
    }

    public ProtonGraphic( NuclearParticle particle ) {
        super( particle, ProtonGraphic.color );
    }

    public void paint( Graphics2D g, double x, double y ) {
        atx.setToTranslation( x, y );
        g.drawImage( protonImage, atx, this );

        g.setColor( Color.black );
        g.setStroke( outlineStroke );
        AffineTransform orgTx = g.getTransform();
        GraphicsUtil.setAntiAliasingOn( g );
        g.transform( atx );
        g.draw( circle );
        g.setTransform( orgTx );
    }

    public boolean imageUpdate( Image img, int infoflags, int x, int y, int width, int height ) {
        return false;
    }
}
