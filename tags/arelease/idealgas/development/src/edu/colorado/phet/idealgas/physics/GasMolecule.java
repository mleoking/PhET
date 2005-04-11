/*
 * Class: GasMolecule
 * Package: edu.colorado.phet.physics.idealgas
 *
 * Created by: Ron LeMaster
 * Date: Nov 4, 2002
 */
package edu.colorado.phet.idealgas.physics;

import edu.colorado.phet.physics.collision.Box2D;
import edu.colorado.phet.idealgas.physics.body.Particle;
import edu.colorado.phet.physics.Gravity;
import edu.colorado.phet.physics.PhysicalSystem;
import edu.colorado.phet.physics.Vector2D;

import java.util.List;

/**
 *
 */
public class GasMolecule extends Particle {

    /**
     * Constructor
     */
    public GasMolecule( Vector2D position, Vector2D velocity, Vector2D acceleration, float mass ) {
        super( position, velocity, acceleration, mass );
    }

    /**
     * Constructor
     */
    public GasMolecule( Vector2D position, Vector2D velocity, Vector2D acceleration,
                        float mass, float charge ) {
        super( position, velocity, acceleration, mass, charge );
    }

    public void setVelocity( Vector2D velocity ) {
        super.setVelocity( velocity );
    }

    public void setVelocity( float vX, float vY ) {
        super.setVelocity( vX, vY );
    }

    public void setVelocityX( float vX ) {
        super.setVelocityX( vX );
    }

    public void setVelocityY( float vY ) {
        super.setVelocityY( vY );
    }

    public float getRadius() {
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

        Double result = null;
        if( molecules.isEmpty() ) {
            result = new Double( 0.0 );
        }
        else {
            for( int i = 0; i < molecules.size(); i++ ) {
                GasMolecule body = (GasMolecule)molecules.get( i );

                // Get a reference to the box and gravity.
                if( !init ) {
                    init = true;
                    if( body.getPhysicalSystem() == null ) {
                        System.out.println( "body has no physical system assigned: " + body.getClass() );
                    }
                }
                if( Double.isNaN( body.getKineticEnergy() ) ) {
//                        System.out.println( "!!!" );
                }
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

        Double result = null;
        if( molecules.isEmpty() ) {
            result = new Double( 0.0 );
        }
        else {
            synchronized( molecules ) {

                // Get a reference to the box and gravity.
                box = ( (IdealGasSystem)PhysicalSystem.instance() ).getBox();
                gravity = ( (IdealGasSystem)PhysicalSystem.instance() ).getGravity();
                isGravityActing = ( gravity != null ) && ( gravity.getY() != 0 );

                for( int i = 0; i < molecules.size(); i++ ) {
                    GasMolecule body = (GasMolecule)molecules.get( i );

                    potentialEnergy += PhysicalSystem.instance().getBodyEnergy( body );
/*
                    kineticEnergy += body.getKineticEnergy();
                    if( isGravityActing ) {
                        potentialEnergy += body.getMass() * gravity.getY() * ( box.getMaxY() - body.getPosition().getY() );
                    }
*/
                }
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
                    totalSpeed += body.getSpeed();
                }
            }
            denominator = ( molecules.isEmpty() ? 1 : molecules.size() );
        }
        return new Double( totalSpeed / denominator );
    }
}
