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
        return 0; // TODO implement model
    }

    public double getHydroxideConcentration() {
        return 0; // TODO implement model
    }

    public double getWaterConcentration() {
        return 0; // TODO implement model
    }
}
