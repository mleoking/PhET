/**
 * Class: PotentialProfileGraphic
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.nuclearphysics.model.PotentialProfile;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;

public class PotentialProfileGraphic implements Graphic {

    private Color color = Color.blue;
    private Stroke stroke = new BasicStroke( 2f );

    private PotentialProfile profile;
    private Point2D.Double origin;
    private Point2D.Double endPt1 = new Point2D.Double();;
    private Point2D.Double endPt2 = new Point2D.Double();
    private Point2D.Double endPt3 = new Point2D.Double();
    private Point2D.Double ctrlPt1 = new Point2D.Double();
    private Point2D.Double ctrlPt2A = new Point2D.Double();
    private Point2D.Double ctrlPt2B = new Point2D.Double();
    private Point2D.Double ctrlPt3 = new Point2D.Double();
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

        // Draw the curve going up the left side of the potential profile
        endPt1.x = origin.getX() - profile.getWidth() / 2;
        endPt1.y = origin.getY();
        endPt2.x = origin.getX() - profile.getWidth() / 10;
        endPt2.y = origin.getY() - profile.getMaxPotential();

        ctrlPt1.x = endPt1.getX() + ( ( endPt2.getX() - endPt1.getX() ) / 3 );
        ctrlPt1.y = endPt1.getY();
        ctrlPt2A.x = endPt2.getX() - ( ( endPt2.getX() - endPt1.getX() ) / 3 );
        ctrlPt2A.y = endPt2.getY();

        CubicCurve2D.Double c1 = new CubicCurve2D.Double( endPt1.x, endPt1.y,
                                                          ctrlPt1.x, ctrlPt1.y,
                                                          ctrlPt2A.x, ctrlPt2A.y,
                                                          endPt2.x, endPt2.y );

        // Draw the curve down into the left side of the potential well
        endPt3.x = origin.getX();
        endPt3.y = origin.getY() - profile.getWellPotential();

        ctrlPt2B.x = endPt2.getX() + ( ( endPt2.getX() - endPt1.getX() ) / 4 );
        ctrlPt2B.y = endPt2.getY();
        ctrlPt3.x = endPt3.getX() - ( ( endPt3.getX() - endPt2.getX() ) / 2 );
        ctrlPt3.y = endPt3.getY();

        CubicCurve2D.Double c2 = new CubicCurve2D.Double( endPt2.x, endPt2.y,
                                                          ctrlPt2B.x, ctrlPt2B.y,
                                                          ctrlPt3.x, ctrlPt3.y,
                                                          endPt3.x, endPt3.y );

        // draw the curve for the right side of the well
        profileTx.setToIdentity();
        profileTx.translate( origin.getX() + 1, origin.getY() );
        profileTx.scale( -1, 1 );
        profileTx.translate( -origin.getX(), -origin.getY() );

        Shape c3 = profileTx.createTransformedShape( c2 );
        Shape c4 = profileTx.createTransformedShape( c1 );

        g.draw( c1 );
        g.draw( c2 );
        g.draw( c3 );
        g.draw( c4 );
    }
}
