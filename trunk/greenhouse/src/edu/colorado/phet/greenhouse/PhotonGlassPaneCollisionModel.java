/**
 * Class: PhotonGlassPaneCollisionModel
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 30, 2003
 */
package edu.colorado.phet.greenhouse;

import edu.colorado.phet.filter.BandpassFilter;
import edu.colorado.phet.filter.Filter1D;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class PhotonGlassPaneCollisionModel {
    static int up = 0;
    static int down = 0;

//    private static Vector2D n = new Vector2D();
//    private static Vector2D vRel = new Vector2D();
//    private static Vector2D r1 = new Vector2D();
//    private static Vector3D nj = new Vector3D();
//    private static Vector2D result = new Vector2D();
    private static Filter1D irPassFilter = new BandpassFilter( 800E-9, 1500E-9 );

    public static void handle( Photon photon, GlassPane glassPane ) {

        if( irPassFilter.passes( photon.getWavelength() )|| photon.getVelocity().getY() > 0 ) {
            double dt = GreenhouseApplication.getClock().getRequestedDT();

            Point2D p0 = new Point2D.Double( photon.getLocation().getX() - photon.getVelocity().getX() * dt,
                                             photon.getLocation().getY() - photon.getVelocity().getY() * dt );
            Point2D p1 = photon.getLocation();
            Line2D l = new Line2D.Double( glassPane.getBounds().getX(),
                                          glassPane.getBounds().getY() + glassPane.getBounds().getHeight() / 2,
                                          glassPane.getBounds().getX() + glassPane.getBounds().getWidth(),
                                          glassPane.getBounds().getY() + glassPane.getBounds().getHeight() / 2 );
            boolean photoCrossedGlassPaneCenterline = l.intersectsLine( p0.getX(), p0.getY(), p1.getX(), p1.getY() );

//            if( photon.getVelocity().getY() > 0 && photon.getLocation().getY() > l.getY1() && p0.getY() > l.getY1() ) {
//                System.out.println( "PhotonGlassPaneCollisionModel.handle" );
//            }

            if( photoCrossedGlassPaneCenterline ) {

                doScatter( photon, glassPane );
                if( photon.getVelocity().getY() > 0 && photon.getLocation().getY() > l.getY1() /* && p0.getY() > l.getY1() */) {
                    System.out.println( "PhotonGlassPaneCollisionModel.handle" );
                }
                if( photon.getVelocity().getY() > 0 ) {
//                    System.out.println( "PhotonGlassPaneCollisionModel.handle" );
                }
                photon.setVelocity( 0,0 );
            }
        }
    }

    private static void doScatter( Photon photon, GlassPane glassPane ) {
        photon.setVelocity( 0,0);

        // Scatter the photon in a random direction
        double dispersionAngle = Math.PI / 2;
        double theta = Math.random() * dispersionAngle + ( Math.PI * 3 / 2 ) - ( dispersionAngle / 2 );
        theta = -Math.PI / 2;
//        theta += Math.random() < 0.5 ? 0 : Math.PI;
        float vBar = photon.getVelocity().getMagnitude();
        Photon newPhoton = new Photon( 400E-9, glassPane );
//        Photon newPhoton = new Photon( photon.getWavelength(), glassPane );
        newPhoton.setVelocity( vBar * (float)Math.cos( theta ),
                               vBar * (float)Math.sin( theta ) );

        if( newPhoton.getVelocity().getY() > 0 ) {
            System.out.println( "PhotonGlassPaneCollisionModel.doScatter" );
        }

        double y = glassPane.getBounds().getY();
        if( theta % ( 2 * Math.PI ) < Math.PI ) {
            y = glassPane.getBounds().getMaxY() - glassPane.getBounds().getHeight() * 1;
//            y = glassPane.getBounds().getMaxY() - glassPane.getBounds().getHeight() * 0.55;
//            y = glassPane.getBounds().getMaxY() + 0.5;
        }
        else {
            y = glassPane.getBounds().getMaxY() + glassPane.getBounds().getHeight() * 1;
//            y = glassPane.getBounds().getMaxY() + glassPane.getBounds().getHeight() * 0.55;
//            y = glassPane.getBounds().getMinY() - 1;
        }
        newPhoton.setLocation( Math.random() * glassPane.getWidth() + glassPane.getBounds().getX(), y );
        photon.setVelocity( 0,0);
//        if(true) return;
        glassPane.absorbPhoton( photon );
        photon.setVelocity( 0,0);
        System.out.println( "photon.getVelocity() = " + photon.getVelocity() );
        glassPane.emitPhoton( newPhoton );
    }
}
