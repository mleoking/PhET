/*
 * Class: GasMolecule
 * Package: edu.colorado.phet.physics.idealgas
 *
 * Created by: Ron LeMaster
 * Date: Nov 4, 2002
 */
package edu.colorado.phet.idealgas.physics;

import edu.colorado.phet.physics.collision.Box2D;
import edu.colorado.phet.physics.collision.SphericalBody;
import edu.colorado.phet.idealgas.physics.body.IdealGasParticle;
import edu.colorado.phet.common.math.Vector2D;

import java.util.List;
import java.awt.geom.Point2D;

/**
 *
 */
public class GasMolecule extends SphericalBody {
//public class GasMolecule extends IdealGasParticle {

    /**
     * Constructor
     */
    public GasMolecule( Point2D position, Vector2D velocity, Vector2D acceleration, double mass ) {
//    public GasMolecule( Vector2D position, Vector2D velocity, Vector2D acceleration, float mass ) {
        super( position, velocity, acceleration, s_moleculeRadius, mass );
    }

//    /**
//     * Constructor
//     */
//    public GasMolecule( Point2D position, Vector2D velocity, Vector2D acceleration,
////    public GasMolecule( Vector2D position, Vector2D velocity, Vector2D acceleration,
//                        double mass, double charge ) {
////                        float mass, float charge ) {
//        super( position, velocity, acceleration, mass, charge );
//    }

    public Point2D.Double getCM() {
        return this.getPosition();
    }

    public double getMomentOfInertia() {
        return 0;
    }

    public void setVelocity( Vector2D velocity ) {
        super.setVelocity( velocity );
    }

    public void setVelocity( float vX, float vY ) {
        super.setVelocity( vX, vY );
    }

    public void setVelocityX( float vX ) {
        super.setVelocity( vX, this.getVelocity().getY() );
    }

    public void setVelocityY( float vY ) {
        super.setVelocity( this.getVelocity().getX(), vY );
    }

    public double getRadius() {
        return s_moleculeRadius;
    }
    //
    // Static fields and methods
    //
    private static float s_moleculeRadius = 5.0f;

    public static void enableParticleParticleInteractions( boolean interactionsEnabled ) {
        if( interactionsEnabled ) {
            s_moleculeRadius = 5.0f;
        }
        else {
            s_moleculeRadius = 0.0f;
        }
    }

    /**
     *
     */
    public static Double computeTemperature( List molecules ) {
        double kineticEnergy = 0;
        boolean init = false;

        if( true ) {
            throw new RuntimeException( "should not be static. behavior should belong to a Species class" );
        }

        Double result = null;
        if( molecules.isEmpty() ) {
            result = new Double( 0.0 );
        }
        else {
            for( int i = 0; i < molecules.size(); i++ ) {
                GasMolecule body = (GasMolecule)molecules.get( i );

                // Get a reference to the box and gravity.
//                if( !init ) {
//                    init = true;
//                    if( body.getPhysicalSystem() == null ) {
//                        System.out.println( "body has no physical system assigned: " + body.getClass() );
//                    }
//                }
                kineticEnergy += body.getKineticEnergy();
            }
            result = new Double( kineticEnergy / molecules.size() );
        }
        return result;
    }

    /**
     *
     */
    public static Double getTotalEnergy( List molecules ) {
        Box2D box = null;
        double kineticEnergy = 0;
        double potentialEnergy = 0;
        Gravity gravity = null;
        boolean init = false;
        boolean isGravityActing = false;

        if( true ) {
            throw new RuntimeException( "should not be static. behavior should belong to a Species class" );
        }

        Double result = null;
        if( molecules.isEmpty() ) {
            result = new Double( 0.0 );
        }
        else {
            synchronized( molecules ) {

                // Get a reference to the box and gravity.
//                box = ( (IdealGasSystem)PhysicalSystem.instance() ).getBox();
//                gravity = ( (IdealGasSystem)PhysicalSystem.instance() ).getGravity();
//                isGravityActing = ( gravity != null ) && ( gravity.getY() != 0 );
//
//                for( int i = 0; i < molecules.size(); i++ ) {
//                    GasMolecule body = (GasMolecule)molecules.get( i );
//
//                    potentialEnergy += PhysicalSystem.instance().getBodyEnergy( body );
///*
//                    kineticEnergy += body.getKineticEnergy();
//                    if( isGravityActing ) {
//                        potentialEnergy += body.getMass() * gravity.getY() * ( box.getMaxY() - body.getPosition().getY() );
//                    }
//*/
//                }
            }
            result = new Double( kineticEnergy + potentialEnergy );
        }
        return result;
    }

    /**
     * TODO: put this and getTemperature in stepInTime(), along with the pressure determination, so
     * that we only have to iterate the list of bodies once
     */
    protected static Double computeAveSpeed( List molecules ) {
        double totalSpeed = 0;
        int denominator = 0;
        if( molecules.isEmpty() ) {
            totalSpeed = 0.0;
        }
        else {
            synchronized( molecules ) {
                for( int i = 0; i < molecules.size(); i++ ) {
                    GasMolecule body = (GasMolecule)molecules.get( i );
                    totalSpeed += body.getVelocity().getMagnitude();
//                    totalSpeed += body.getSpeed();
                }
            }
            denominator = ( molecules.isEmpty() ? 1 : molecules.size() );
        }
        return new Double( totalSpeed / denominator );
    }
}
