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
import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.geom.Point2D;

public class MiddleEnergyState extends SpontaneouslyEmittingState {

    private static MiddleEnergyState instance = new MiddleEnergyState();

    public static MiddleEnergyState instance() {
        return instance;
    }

    private MiddleEnergyState() {
        setEmittedPhotonWavelength( Photon.RED );
    }

    public void collideWithPhoton( Atom atom, Photon photon ) {

        // If the photon has the same energy as the difference
        // between this level and the ground state, then emit
        // a photon of that energy
        if( isStimulatedBy( photon ) ) {

            // Place the replacement photon beyond the atom, so it doesn't collide again
            // right away
            Vector2D vHat = new Vector2D.Double( photon.getVelocity() ).normalize();
            vHat.scale( atom.getRadius() + 10 );
            Point2D position = new Point2D.Double( atom.getPosition().getX() + vHat.getX(),
                                                   atom.getPosition().getY() + vHat.getY() );
            photon.setPosition( position );
            Photon emittedPhoton = Photon.createStimulated( photon, position );
            atom.emitPhoton( emittedPhoton );

            // Change state
            atom.setState( GroundState.instance() );
        }

        // If the photon has the same energy level as the difference between
        // this state and the high energy one, then we go to that state
        if( photon.getEnergy() == HighEnergyState.instance().getEnergyLevel() - this.getEnergyLevel() ) {

            // Absorb the photon and change state
            photon.removeFromSystem();

            // Change state
            atom.setState( HighEnergyState.instance() );
        }
    }

    public AtomicState getNextLowerEnergyState() {
        return GroundState.instance();
    }

    public AtomicState getNextHigherEnergyState() {
        return HighEnergyState.instance();
    }
}
