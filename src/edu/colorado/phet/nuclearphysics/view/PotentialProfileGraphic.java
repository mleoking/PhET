/**
 * Class: PotentialProfileGraphic
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.coreadditions.AlphaSetter;
import edu.colorado.phet.nuclearphysics.model.PotentialProfile;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class PotentialProfileGraphic implements Graphic {

    //
    // Statics
    //
    private static ImageObserver imgObs = new ImageObserver() {
        public boolean imageUpdate( Image img, int infoflags, int x, int y, int width, int height ) {
            return false;
        }
    };

    //
    // Instance fields and methods
    //
    private Color color = Color.blue;
    private Color backgroundColor = new Color( 200, 200, 255 );
    private Stroke stroke = new BasicStroke( 2f );

    private PotentialProfile profile;
    private Point2D.Double origin;
    AffineTransform profileTx = new AffineTransform();
    private Image image;

    public PotentialProfileGraphic( PotentialProfile profile ) {
        this.profile = profile;
        image = buildImage();
    }

    public void setOrigin( Point2D.Double origin ) {
        this.origin = origin;
    }

    public PotentialProfile getProfile() {
        return profile;
    }

    public void paint( Graphics2D g ) {
        g.drawImage( image, (int)origin.getX() - image.getWidth( imgObs ) / 2,
                     (int)origin.getY() - image.getHeight( imgObs ), imgObs );

//        GraphicsUtil.setAntiAliasingOn( g );
//        g.setColor( color );
//        g.setStroke( stroke );
//        profileTx.setToIdentity();
//        profileTx.translate( origin.getX(), origin.getY() );
//        g.draw( profileTx.createTransformedShape( profile.getPath() ) );
//        g.setColor( backgroundColor );
//        AlphaSetter.set( g, .5 );
//        g.fill( profileTx.createTransformedShape( profile.getBackgroundPath() ) );
//        AlphaSetter.set( g, 1 );
    }

    private Image buildImage() {
        BufferedImage bi = new BufferedImage( (int)( profile.getWidth() ),
                                              (int)( profile.getMaxPotential() ),
                                              BufferedImage.TYPE_INT_ARGB );
        Graphics2D g = (Graphics2D)bi.getGraphics();
        GraphicsUtil.setAntiAliasingOn( g );
        g.setColor( color );
        g.setStroke( stroke );
        profileTx.setToIdentity();

        // Note that the profile path is centered on the y axis, so half of it
        // has negative x coordinates. That's why is has to be translated
        profileTx.translate( profile.getWidth() / 2, profile.getMaxPotential() );
        g.draw( profileTx.createTransformedShape( profile.getPath() ) );
        g.setColor( backgroundColor );
        AlphaSetter.set( g, .2 );
        g.fill( profileTx.createTransformedShape( profile.getBackgroundPath() ) );
        AlphaSetter.set( g, 1 );
        return bi;
    }

}
