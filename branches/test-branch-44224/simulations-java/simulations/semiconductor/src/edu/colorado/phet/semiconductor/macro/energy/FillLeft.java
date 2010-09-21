package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.semiconductor.macro.energy.bands.Band;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyLevel;
import edu.colorado.phet.semiconductor.macro.energy.statemodels.Entrance;
import edu.colorado.phet.semiconductor.macro.energy.transitions.Move;


/**
 * User: Sam Reid
 * Date: Apr 26, 2004
 * Time: 2:14:24 PM
 */
public class FillLeft implements ModelElement {
    int bandSet;
    int band;
    EnergySection energySection;

    public FillLeft( int bandSet, int band, EnergySection energySection ) {
        this.bandSet = bandSet;
        this.band = band;
        this.energySection = energySection;
    }

    public EnergyLevel getActiveLevel() {
        Band b = energySection.bandSetAt( bandSet ).bandAt( band );
        for ( int i = 0; i < b.numEnergyLevels(); i++ ) {
            EnergyLevel lvl = b.energyLevelAt( i );
            if ( !energySection.isOwned( lvl ) ) {
                return lvl;
            }
        }
        return null;
    }

    public void stepInTime( double dt ) {
        EnergyLevel active = getActiveLevel();
        if ( active == null ) {
            return;
        }
        EnergyCell left = active.cellAt( 0 );
        EnergyCell right = active.cellAt( 1 );
        Move mo = new Move( left, right, energySection.getSpeed() );
        BandParticle bp = energySection.getBandParticle( left );
        if ( bp != null ) {
            boolean ok = mo.apply( bp, energySection );
        }
        Entrance e = new Entrance( energySection, left );
        e.stepInTime( dt );
    }
}
