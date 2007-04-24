
package edu.colorado.phet.semiconductor_semi.macro.energy;

import edu.colorado.phet.common_semiconductor.model.ModelElement;
import edu.colorado.phet.semiconductor_semi.macro.energy.bands.Band;
import edu.colorado.phet.semiconductor_semi.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor_semi.macro.energy.bands.EnergyLevel;
import edu.colorado.phet.semiconductor_semi.macro.energy.transitions.ExitRightFrom;
import edu.colorado.phet.semiconductor_semi.macro.energy.transitions.Move;

/**
 * User: Sam Reid
 * Date: Apr 26, 2004
 * Time: 2:14:24 PM
 *
 */
public class DepleteRight implements ModelElement {
    int bandSet;
    int band;
    EnergySection energySection;

    public DepleteRight( int bandSet, int band, EnergySection energySection ) {
        this.bandSet = bandSet;
        this.band = band;
        this.energySection = energySection;
    }

    public int highestClaimedLevel() {
        Band b = energySection.bandSetAt( bandSet ).bandAt( band );
        for( int i = 0; i < b.numEnergyLevels(); i++ ) {
            int index = b.numEnergyLevels() - 1 - i;
            EnergyLevel level = b.energyLevelAt( index );
            if( energySection.isClaimed( level.cellAt( 0 ) ) || energySection.isClaimed( level.cellAt( 1 ) ) ) {
                return index;
            }
        }
        return -1;
    }

    public void stepInTime( double dt ) {
        int hi = highestClaimedLevel();
        if( hi == -1 ) {
            return;
        }
        EnergyLevel level = energySection.bandSetAt( bandSet ).bandAt( band ).energyLevelAt( hi );
        BandParticle left = energySection.getBandParticle( level.cellAt( 0 ) );
        BandParticle right = energySection.getBandParticle( level.cellAt( 1 ) );
        if( left != null ) {
            if( left.isExcited() ) {
                Move m = new Move( left.getEnergyCell(), energySection.getRightNeighbor( left.getEnergyCell() ), energySection.getSpeed() );
                boolean out = m.apply( left, energySection );
            }
            else {
                Move m = new Move( left.getEnergyCell(), energySection.getUpperNeighbor( left.getEnergyCell() ), energySection.getSpeed() );
                boolean out = m.apply( left, energySection );
            }
        }
        if( right != null ) {

            if( right.isExcited() ) {
                if( bandSet == energySection.numBandSets() - 1 ) {
                    ExitRightFrom erf = new ExitRightFrom( right.getEnergyCell() );
                    boolean out = erf.apply( right, energySection );
                }
                else{
                    Move m=new Move( right.getEnergyCell(), energySection.getRightNeighbor( right.getEnergyCell() ),energySection.getSpeed() );
                    boolean out = m.apply( right, energySection );
                }
            }
            else {
                Move m = new Move( right.getEnergyCell(), energySection.getUpperNeighbor( right.getEnergyCell() ), energySection.getSpeed() );
                boolean out = m.apply( right, energySection );
            }

        }
    }
}
