package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.semiconductor.common.Particle;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandSet;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyLevel;

/**
 * User: Sam Reid
 * Date: Feb 9, 2004
 * Time: 12:25:12 PM
 * Copyright (c) Feb 9, 2004 by Sam Reid
 */
public class PlusCharge extends Particle {
    EnergyCell cell;

    public PlusCharge( EnergyCell cell ) {
        super( cell.getX(), cell.getEnergy() );
        this.cell = cell;
    }

    public void relayout() {
        setPosition( cell.getX(), cell.getEnergy() );
    }

    public EnergyLevel getEnergyLevel() {
        return cell.getEnergyLevel();
    }

    public BandSet getBandSet() {
        return cell.getBandSet();
    }

    public EnergyCell getCell() {
        return cell;
    }
}
