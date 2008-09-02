/**
 * Class: PhotonCloudCollisionModel
 * Class: edu.colorado.phet.greenhouse
 * User: Ron LeMaster
 * Date: Oct 12, 2003
 * Time: 5:14:53 PM
 */
package edu.colorado.phet.greenhouse;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.greenhouse.coreadditions.Body;
import edu.colorado.phet.greenhouse.coreadditions.Vector3D;
import edu.colorado.phet.greenhouse.filter.BandpassFilter;
import edu.colorado.phet.greenhouse.filter.Filter1D;
import edu.colorado.phet.greenhouse.filter.ProbablisticPassFilter;
import edu.colorado.phet.greenhouse.phetcommon.math.Vector2D;

public class PhotonCloudCollisionModel {

    private static Vector2D n = new Vector2D();
    private static Vector2D vRel = new Vector2D();
    private static Vector2D r1 = new Vector2D();
    private static Vector3D nj = new Vector3D();
    private static Vector2D result = new Vector2D();
    private static Filter1D filter = new PhotonPassFilter();
    private static Filter1D visibleLightFilter = new BandpassFilter( 300E-9, 700E-9 );
    private static Filter1D irFilter = new IrFilter();

    public static void handle( Photon photon, Cloud cloud ) {

        // Do bounding box check
        boolean boundingBoxesOverlap = cloud.getBounds().contains( photon.getLocation() );
        if ( boundingBoxesOverlap && filter.passes( photon.getWavelength() ) ) {
            Vector2D loa = getNormalAtPoint( photon.getLocation(), cloud );
            if ( visibleLightFilter.passes( photon.getWavelength() ) ) {
                doCollision( photon, cloud, loa,
                             photon.getLocation() );
            }
            if ( irFilter.absorbs( photon.getWavelength() ) ) {
                doScatter( photon );
            }
        }
    }

    private static void doScatter( Photon photon ) {
        // Scatter the photon in a random direction
        double dispersionAngle = Math.PI / 4;
        double theta = Math.random() * dispersionAngle + ( Math.PI * 3 / 2 ) - ( dispersionAngle / 2 );
        theta += Math.random() < 0.5 ? 0 : Math.PI;
        float vBar = photon.getVelocity().getMagnitude();

        photon.setVelocity( vBar * (float) Math.cos( theta ),
                            vBar * (float) Math.sin( theta ) );

    }

    private static void doCollision( Body bodyA, Body bodyB, Vector2D loa, Point2D.Double collisionPt ) {

        // Check to see that the bodies are moving toward each other. Otherwise, there is no collision
        vRel.setComponents( bodyA.getVelocity() );
        vRel.subtract( bodyB.getVelocity() );
        if ( vRel.dot( loa ) <= 0 ) {

            // Get the vectors from the bodies' CMs to the point of contact
            r1.setComponents( (float) ( collisionPt.getX() - bodyA.getLocation().getX() ),
                              (float) ( collisionPt.getY() - bodyA.getLocation().getY() ) );

            // Get the unit vector along the line of action
            n.setComponents( loa ).normalize();

            // Get the magnitude along the line of action of the bodies' relative velocities at the
            // point of contact
            Vector3D omega = new Vector3D( 0, 0, (float) bodyA.getOmega() );
            Vector3D ot = omega.crossProduct( new Vector3D( r1 ) ).add( new Vector3D( bodyA.getVelocity() ) );
            float vr = ot.dot( new Vector3D( n ) );

            // Assume the coefficient of restitution is 1
            float e = 1;

            // Compute the impulse, j
            float numerator = -vr * ( 1 + e );
            Vector3D n3D = new Vector3D( n );
            Vector3D r13D = new Vector3D( r1 );
            Vector3D t1 = r13D.crossProduct( n3D ).multiply( (float) ( 1 / bodyA.getMomentOfInertia() ) );
            Vector3D t1A = t1.crossProduct( t1 );
            float t1B = n3D.dot( t1A );
            double denominator = ( 1 / bodyA.getMass() ) + t1B;
            denominator = ( 1 / bodyA.getMass() ) +
                          ( n3D.dot( r13D.crossProduct( n3D ).multiply( 1 / (float) bodyA.getMomentOfInertia() ).crossProduct( r13D ) ) );
            double j = numerator / denominator;

            // Compute the new linear and angular velocities, based on the impulse
            bodyA.getVelocity().add( new Vector2D( n ).multiply( (float) ( j / bodyA.getMass() ) ) );

            nj.setComponents( n ).multiply( (float) j );
            double omegaB = bodyA.getOmega() + ( r13D.crossProduct( nj ).getZ() / bodyA.getMomentOfInertia() );
            bodyA.setOmega( omegaB );
        }
    }

    private static Vector2D getNormalAtPoint( Point2D p, Cloud cloud ) {

        double x = p.getX();
        double y = p.getY();
        double a = cloud.getLocation().getX();
        double b = cloud.getLocation().getY();
        double c = cloud.getWidth() / 2;
        double d = cloud.getHeight() / 2;

        double t = Math.acos( ( x - a ) / c );

        double cos2t = Math.cos( t );
        double sin2t = Math.sin( t );
        double denominator = Math.sqrt( ( d * d * cos2t * cos2t ) + ( c * c * sin2t * sin2t ) );
        double xt = -( c * Math.sin( t ) / denominator );
        double yt = d * Math.cos( t ) / denominator;

        // If we are hitting the cloud from bellow, we need to flip the
        // vector.
        if ( y < b ) {
            xt *= -1;
        }

        result.setComponents( (float) xt, (float) yt );
        return result.normalVector();
    }


    /**
     * A filter model for the clouds likelihood of reflecting light of a specified wavelength
     * If the filter *passes* the wavelength, that means the cloud will reflect the photon
     */

    static int passed;
    static int reflected;

    private static class PhotonPassFilter extends Filter1D {

        private ProbablisticPassFilter visibleLightFilter = new ProbablisticPassFilter( .4 );

        public boolean passes( double wavelength ) {

            // If wavelength is in the IR, it never passes
            if ( wavelength >= 800E-9 ) {
                return true;
            }
            else {
                return visibleLightFilter.passes( wavelength );
            }
        }
    }


    public static void main( String[] args ) {
        Cloud cloud = new Cloud( new Ellipse2D.Double( -2, -1, 4, 2 ) );
        Vector2D v = getNormalAtPoint( new Point2D.Double( Math.cos( Math.PI / 4 ), Math.cos( Math.PI / 4 ) ), cloud );
        System.out.println( v );
    }


}
