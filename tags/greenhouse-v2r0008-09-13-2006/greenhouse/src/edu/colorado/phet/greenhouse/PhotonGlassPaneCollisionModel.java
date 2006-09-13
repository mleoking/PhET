/**
 * Class: PhotonGlassPaneCollisionModel
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 30, 2003
 */
package edu.colorado.phet.greenhouse;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class PhotonGlassPaneCollisionModel {
    private static IrFilter irPassFilter = new IrFilter();

    public static void handle( Photon photon, GlassPane glassPane ) {

        if( irPassFilter.absorbs( photon.getWavelength() ) ) {
            double dt = GreenhouseApplication.getClock().getRequestedDT();

            Point2D p0 = new Point2D.Double( photon.getLocation().getX() - photon.getVelocity().getX() * dt,
                                             photon.getLocation().getY() - photon.getVelocity().getY() * dt );
            Point2D p1 = photon.getLocation();
            Line2D l = new Line2D.Double( glassPane.getBounds().getX(),
                                          glassPane.getBounds().getY() + glassPane.getBounds().getHeight() / 2,
                                          glassPane.getBounds().getX() + glassPane.getBounds().getWidth(),
                                          glassPane.getBounds().getY() + glassPane.getBounds().getHeight() / 2 );
            boolean photoCrossedGlassPaneCenterline = l.intersectsLine( p0.getX(), p0.getY(), p1.getX(), p1.getY() );

            if( photoCrossedGlassPaneCenterline ) {
                doScatter( photon, glassPane );
            }
        }
    }

    private static void doScatter( Photon photon, GlassPane glassPane ) {

        // Scatter the photon in a random direction
        double dispersionAngle = Math.PI / 2;
        double theta = Math.random() * dispersionAngle + ( Math.PI * 3 / 2 ) - ( dispersionAngle / 2 );
        theta += Math.random() < 0.5 ? 0 : Math.PI;
        float vBar = photon.getVelocity().getMagnitude();
        Photon newPhoton = new Photon( photon.getWavelength(), glassPane );
        newPhoton.setVelocity( vBar * (float)Math.cos( theta ),
                               vBar * (float)Math.sin( theta ) );

        double y = glassPane.getBounds().getY();
        if( theta % ( 2 * Math.PI ) < Math.PI ) {
            y = glassPane.getBounds().getMaxY();
        }
        else {
            y = glassPane.getBounds().getMinY();
        }
        newPhoton.setLocation( Math.random() * glassPane.getWidth() + glassPane.getBounds().getX(), y );
        photon.setVelocity( 0,0);
        glassPane.absorbPhoton( photon );
        glassPane.emitPhoton( newPhoton );
    }
}
