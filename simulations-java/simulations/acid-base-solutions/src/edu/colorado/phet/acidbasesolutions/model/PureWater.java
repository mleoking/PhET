package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.acidbasesolutions.ABSStrings;

/**
 * Pure water (no solute).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PureWater {
    
    private static final double H3O_CONCENTRATION = 1E-7;
    private static final double OH_CONCENTRATION = 1E-7;

    private final Water water;
    
    public PureWater() {
        this.water = new Water();
    }
    
    public Water getWater() {
        return water;
    }
    
    public String getName() {
        return ABSStrings.PURE_WATER;
    }
    
    public double getH3OConcentration() {
        return H3O_CONCENTRATION;
    }

    public double getOHConcentration() {
        return OH_CONCENTRATION;
    }
    
    public String toString() {
        return getName();
    }
}
