/** Sam Reid*/
package edu.colorado.phet.semiconductor.macro.energy.bands;

import edu.colorado.phet.semiconductor.common.EnergySpaceRegion;

/**
 * User: Sam Reid
 * Date: Mar 26, 2004
 * Time: 1:13:21 AM
 * Copyright (c) Mar 26, 2004 by Sam Reid
 */
public class BandDescriptor {
    private int numLevels;
    private EnergySpaceRegion region;
    private double energyAbove;

    public BandDescriptor( int numLevels, EnergySpaceRegion region, double energyAbove ) {
        this.numLevels = numLevels;
        this.region = region;
        this.energyAbove = energyAbove;
    }

    public int getNumLevels() {
        return numLevels;
    }

    public EnergySpaceRegion getRegion() {
        return region;
    }

    public double getUpperBandGap() {
        return energyAbove;
    }

    public double getNextEnergyStart() {
        return region.getMinEnergy() + region.getEnergyRange() + energyAbove;
    }
}
