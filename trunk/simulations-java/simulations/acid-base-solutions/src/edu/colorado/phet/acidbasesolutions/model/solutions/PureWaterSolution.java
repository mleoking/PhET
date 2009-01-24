package edu.colorado.phet.acidbasesolutions.model.solutions;

import edu.colorado.phet.acidbasesolutions.model.PureWater;


public class PureWaterSolution extends AbstractAqueousSolution {
    
    private static PureWater _water;
    
    public PureWaterSolution() {
        _water = new PureWater();
    }
    
    public PureWater getWater() {
        return _water;
    }

    public double getHydroniumConcentration() {
        return 1E-7;
    }

    public double getHydroxideConcentration() {
        return 1E-7;
    }

    public double getWaterConcentration() {
        return _water.getConcentration();
    }
}
