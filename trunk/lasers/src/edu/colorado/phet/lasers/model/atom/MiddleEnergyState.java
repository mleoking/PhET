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

import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;

public class MiddleEnergyState extends SpontaneouslyEmittingState {

    /**
     *
     * @param atom
     */
    protected MiddleEnergyState( Atom atom ) {
        super( atom );
        s_numInstances++;
    }

    /**
     *
     * @param photon
     */
    public void collideWithPhoton( Photon photon ) {

        // If the photon has the same energy as the difference
        // between this level and the ground state, then emit
        // a photon of that energy
        if( photon.getWavelength() == Photon.RED && Math.random() < s_collisionLikelihood ) {

//            Photon emittedPhoton = Photon.createStimulated( photon );
//            getAtom().photonEmitted( emittedPhoton );
//
            // Remove the original photon and replace it with another. this
            // makes getting the position and the graphic correct much easier.
//            Photon replacementPhoton = Photon.create( photon );

//            replacementPhoton.setVelocity( new Vector2D( photon.getVelocity() ));
//            replacementPhoton.setWavelength( getEmittedPhotonWavelength() );

            // Place the replacement photon beyond the atom, so it doesn't collide again
            // right away
            Vector2D vHat = new Vector2D.Double( photon.getVelocity() ).normalize();
//            Vector2D position = new Vector2D.Double( getAtom().getPosition() );
//            position.add( vHat.scale( getAtom().getRadius() + 10 ));
//            replacementPhoton.setPosition( position );
            vHat.scale( getAtom().getRadius() + 10 );
            Point2D position = new Point2D.Double( getAtom().getPosition().getX() + vHat.getX(),
                                                   getAtom().getPosition().getY() + vHat.getY());
            photon.setPosition( position );

//            replacementPhoton.setPosition( getAtom().getPosition().getX(),
//                                getAtom().getPosition().getY() + photon.getRadius() + 1);
//            replacementPhoton.collideWithAtom( getAtom() );

            Photon emittedPhoton = Photon.createStimulated( photon );
//            Photon emittedPhoton = Photon.createStimulated( replacementPhoton );
            getAtom().emitPhoton( emittedPhoton );

//            new edu.colorado.phet.controller.command.AddParticleCmd( replacementPhoton ).doIt();
//            new RemoveParticleCmd( photon ).doIt();


            // Change state
            decrementNumInState();
            getAtom().setState( new GroundState( getAtom() ) );
        }

        // If the photon has the same energy level as the difference between
        // this state and the high energy one, then we go to that state
        if( photon.getWavelength() == Photon.DEEP_RED ) {

            // Absorb the photon
            photon.removeFromSystem();

            // Change state
            decrementNumInState();
            getAtom().setState( new HighEnergyState( getAtom() ) );
        }
    }

    //
    // Abstract methods implemented
    //
    protected double getSpontaneousEmmisionHalfLife() {
        return s_spontaneousEmmisionHalfLife;
    }

    protected AtomicState nextLowerEnergyState() {
        return new GroundState( getAtom() );
    }

    void decrementNumInState() {
        s_numInstances--;
    }

    protected double getEmittedPhotonWavelength() {
        return s_wavelength;
    }

    //
    // Static fields and methods
    //
    static private int s_numInstances = 0;
    static public int s_wavelength = Photon.RED;
    static private double s_spontaneousEmmisionHalfLife = LaserConfig.DEFAULT_SPONTANEOUS_EMISSION_TIME / 1000;

    public static void setSpontaneousEmmisionHalfLife( double spontaneousEmmisionHalfLife ) {
        s_spontaneousEmmisionHalfLife = spontaneousEmmisionHalfLife;
    }


    static public int getNumInstances() {
        return s_numInstances;
    }
}
