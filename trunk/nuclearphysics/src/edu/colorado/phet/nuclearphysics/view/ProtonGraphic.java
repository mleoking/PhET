/**
 * Class: ProtonGraphic
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.nuclearphysics.model.NuclearParticle;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;

public class ProtonGraphic extends ParticleGraphic implements ImageObserver {

    private static Color color = Color.red;
    private static AffineTransform atx = new AffineTransform();
    private static BufferedImage protonImage;

    static {
        ImageLoader imgLoader = new ImageLoader();
        try {
            protonImage = imgLoader.loadImage( "images/red-xsml.gif" );
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
    }

    public boolean imageUpdate( Image img, int infoflags, int x, int y, int width, int height ) {
        return false;
    }
}
