package edu.colorado.phet.semiconductor_semi.macro.energy.states;

import edu.colorado.phet.semiconductor_semi.macro.energy.EnergySection;
import edu.colorado.phet.semiconductor_semi.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor_semi.macro.energy.bands.BandParticleState;

/**
 * User: Sam Reid
 * Date: Feb 9, 2004
 * Time: 12:49:20 PM
 */
public class Remove implements BandParticleState {
    private EnergySection diodeSection;

    public Remove( EnergySection diodeSection ) {
        this.diodeSection = diodeSection;
    }

    public boolean stepInTime( BandParticle particle, double dt ) {
        diodeSection.removeParticle( particle );
//        particle.setVelocity(new PhetVector());
        return true;
    }

}
