/**
 * Class: PotentialProfileGraphic
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.model.simpleobservable.SimpleObserver;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.nuclearphysics.model.Nucleus;
import edu.colorado.phet.nuclearphysics.model.PotentialProfile;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class PotentialProfileGraphic implements Graphic, SimpleObserver {

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
    private AffineTransform profileTx = new AffineTransform();
    private Image image;
    private Nucleus nucleus;

    public PotentialProfileGraphic( Nucleus nucleus ) {
        this.nucleus = nucleus;
        this.profile = nucleus.getPotentialProfile();
        this.profile.addObserver( this );
        image = buildImage();
    }

    public void setColor( Color color ) {
        this.color = color;
        image = buildImage();
    }

    public void setOrigin( Point2D.Double origin ) {
        this.origin = origin;
    }

    public PotentialProfile getProfile() {
        return profile;
    }

    public void paint( Graphics2D g ) {
        profileTx.setToIdentity();
        profileTx.translate( nucleus.getLocation().getX(), 0 );
        AffineTransform orgTx = g.getTransform();
        g.transform( profileTx );
        g.drawImage( image, -image.getWidth( imgObs ) / 2,
                     -image.getHeight( imgObs ), imgObs );
        GraphicsUtil.setAlpha( g, 1 );

        g.setTransform( orgTx );
    }

    private Image buildImage() {
        AffineTransform atx = new AffineTransform();
        int imageHeight = (int)Math.max( profile.getMaxPotential(), profile.getWellPotential() );
        BufferedImage bi = new BufferedImage( (int)( profile.getWidth() ),
                                              imageHeight,
                                              BufferedImage.TYPE_INT_ARGB );
        Graphics2D g = (Graphics2D)bi.getGraphics();
        GraphicsUtil.setAntiAliasingOn( g );

        // Note that the profile path is centered on the y axis, so half of it
        // has negative x coordinates. That's why is has to be translated
        g.setColor( backgroundColor );
        GraphicsUtil.setAlpha( g, 1 );
        g.fill( atx.createTransformedShape( profile.getBackgroundPath() ) );
        GraphicsUtil.setAlpha( g, 1 );
        g.setColor( color );
        g.setStroke( stroke );
        atx.setToIdentity();
        atx.translate( profile.getWidth() / 2, imageHeight );
        g.draw( atx.createTransformedShape( profile.getPath() ) );

        return bi;
    }

    public void update() {
        image = buildImage();
    }
}
