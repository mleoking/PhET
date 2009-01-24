package edu.colorado.phet.acidbasesolutions.model.solutions;

import edu.colorado.phet.acidbasesolutions.model.PureWater;
import edu.colorado.phet.acidbasesolutions.model.bases.IStrongBase;


public class StrongBaseSolution extends AbstractBaseSolution {
    
    private final IStrongBase _base;
    
    public StrongBaseSolution( IStrongBase base, double c ) {
        super( base, c );
        _base = base;
    }
    
    public IStrongBase getStrongBase() {
        return _base;
    }
    
    // [MOH] = 0
    public double getBaseConcentration() {
        return 0;
    }
    
    // [M+] = c
    public double getConjugateAcidConcentration() {
        return getInitialBaseConcentration();
    }
    
    // [H3O+] = Kw / [OH-]
    public double getHydroniumConcentration() {
        return PureWater.getEquilibriumConstant() / getHydroniumConcentration();
    }
    
    // [OH-] = c
    public double getHydroxideConcentration() {
        return getInitialBaseConcentration();
    }
    
    // [H2O] = W
    public double getWaterConcentration() {
        return PureWater.getConcentration();
    }
}
