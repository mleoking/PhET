/** Sam Reid*/
package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;
import edu.colorado.phet.semiconductor.macro.energy.bands.SemiconductorBandSet;
import edu.colorado.phet.semiconductor.macro.energy.statemodels.NPForward;
import edu.colorado.phet.semiconductor.macro.energy.statemodels.PNBackward;
import edu.colorado.phet.semiconductor.macro.energy.statemodels.PNForward;

/**
 * User: Sam Reid
 * Date: Mar 27, 2004
 * Time: 6:54:35 PM
 * Copyright (c) Mar 27, 2004 by Sam Reid
 */
public class StateModelSet {
    EnergySection energySection;
    NPForward npforward;
    private StateModel nullModel = new NullStateModel();
    private PNForward pnforward;
    private StateModel pnbackward;

    public StateModelSet( EnergySection energySection ) {
        this.energySection = energySection;
        int bot = energySection.bandSetAt( 0 ).bandAt( 0 ).numEnergyLevels();
        int val = energySection.bandSetAt( 0 ).bandAt( 1 ).numEnergyLevels();
        int dop = SemiconductorBandSet.NUM_DOPING_LEVELS;

        int nLvl = bot + val + dop - 1;
        int pLvl = bot + val - dop;

        EnergyCell npEntry = energySection.energyCellAt( nLvl, 0 );
        EnergyCell npExit = energySection.energyCellAt( pLvl, energySection.numColumns() - 1 );

        EnergyCell pnEntry = energySection.energyCellAt( nLvl, energySection.numColumns() - 1 );
        EnergyCell pnExit = energySection.energyCellAt( pLvl, 0 );

        npforward = new NPForward( energySection, npEntry, npExit );
        pnforward = new PNForward( energySection, pnEntry, pnExit );
        pnbackward = new PNBackward( energySection );
    }

    public StateModel getNullModel() {
        return nullModel;
    }

    public StateModel getNPForward() {
        return npforward;
    }

    public PNForward getPnforward() {
        return pnforward;
    }

    public StateModel getPNBackward() {
        return pnbackward;
    }
}
