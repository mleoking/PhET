// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.semiconductor.common.Particle;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandSet;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyLevel;

/**
 * User: Sam Reid
 * Date: Feb 9, 2004
 * Time: 12:25:12 PM
 */
public class PlusCharge extends Particle {
    EnergyCell cell;

    public PlusCharge( EnergyCell cell ) {
        super( cell.getX(), cell.getEnergy() );
        this.cell = cell;
    }

    public EnergyLevel getEnergyLevel() {
        return cell.getEnergyLevel();
    }

    public BandSet getBandSet() {
        return cell.getBandSet();
    }

}
