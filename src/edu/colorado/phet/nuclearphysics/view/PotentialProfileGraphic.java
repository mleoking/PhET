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

public class PotentialProfileGraphic implements Graphic {

    private Color color = Color.blue;
    private Color backgroundColor = new Color( 200, 200, 255 );
    private Stroke stroke = new BasicStroke( 2f );

    private PotentialProfile profile;
    private Point2D.Double origin;
    AffineTransform profileTx = new AffineTransform();

    public PotentialProfileGraphic( PotentialProfile profile ) {
        this.profile = profile;
    }

    public void setOrigin( Point2D.Double origin ) {
        this.origin = origin;
    }

    public PotentialProfile getProfile() {
        return profile;
    }

    public void paint( Graphics2D g ) {
        GraphicsUtil.setAntiAliasingOn( g );
        g.setColor( color );
        g.setStroke( stroke );

        Shape[] shape = profile.getShape();
        profileTx.setToIdentity();
        profileTx.translate( origin.getX(), origin.getY() );
        g.draw( profileTx.createTransformedShape( profile.getPath() ) );
        g.setColor( backgroundColor );
        AlphaSetter.set( g, .5 );
        g.fill( profileTx.createTransformedShape( profile.getBackgroundPath() ) );
        AlphaSetter.set( g, 1 );
    }
}
