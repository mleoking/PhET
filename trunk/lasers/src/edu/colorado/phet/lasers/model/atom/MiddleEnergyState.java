/**
 * Class: MiddleEnergyState
 * Package: edu.colorado.phet.lasers.model
 * Author: Another Guy
 * Date: Mar 21, 2003
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.model.atom;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.geom.Point2D;

public class MiddleEnergyState extends SpontaneouslyEmittingState {

    //
    // Static fields and methods
    //
    private static MiddleEnergyState instance = new MiddleEnergyState();
    public static MiddleEnergyState instance() {
        return instance;
    }

    static private int s_numInstances = 0;
    static public int s_wavelength = Photon.RED;
    static private double s_spontaneousEmmisionHalfLife = LaserConfig.DEFAULT_SPONTANEOUS_EMISSION_TIME / 1000;

    public static void setSpontaneousEmmisionHalfLife( double spontaneousEmmisionHalfLife ) {
        s_spontaneousEmmisionHalfLife = spontaneousEmmisionHalfLife;
    }

    static public int getNumInstances() {
        return s_numInstances;
    }

    //
    // Instance
    //
    private MiddleEnergyState() {
        setEnergyLevel( 40 );
    }

    /**
     * @param photon
     */
    public void collideWithPhoton( Atom atom, Photon photon ) {

        // If the photon has the same energy as the difference
        // between this level and the ground state, then emit
        // a photon of that energy
        if( photon.getWavelength() == Photon.RED && Math.random() < s_collisionLikelihood ) {

            // Place the replacement photon beyond the atom, so it doesn't collide again
            // right away
            Vector2D vHat = new Vector2D.Double( photon.getVelocity() ).normalize();
            vHat.scale( atom.getRadius() + 10 );
            Point2D position = new Point2D.Double( atom.getPosition().getX() + vHat.getX(),
                                                   atom.getPosition().getY() + vHat.getY() );
            photon.setPosition( position );
            Photon emittedPhoton = Photon.createStimulated( photon );
            atom.emitPhoton( emittedPhoton );

            // Change state
            atom.setState( GroundState.instance() );
        }

        // If the photon has the same energy level as the difference between
        // this state and the high energy one, then we go to that state
        if( photon.getWavelength() == Photon.DEEP_RED ) {

            // Absorb the photon and change state
            photon.removeFromSystem();

            // Change state
            atom.setState( HighEnergyState.instance() );
        }
    }

    protected double getSpontaneousEmmisionHalfLife() {
        return s_spontaneousEmmisionHalfLife;
    }

    protected AtomicState nextLowerEnergyState() {
        return GroundState.instance();
    }

    void incrNumInState() {
        s_numInstances++;
    }

    void decrementNumInState() {
        s_numInstances--;
    }

    protected double getEmittedPhotonWavelength() {
        return s_wavelength;
    }
}
