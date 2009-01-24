package edu.colorado.phet.acidbasesolutions.model.solutions;

import edu.colorado.phet.acidbasesolutions.model.bases.IWeakBase;


public class WeakBaseSolution extends AbstractBaseSolution {

    private final IWeakBase _base;
    private double _conjugateBaseConcentration;
    
    public WeakBaseSolution( IWeakBase base, double initialBaseConcentration ) {
        super( base, initialBaseConcentration );
        _base = base;
        updateConjugateAcidConcentration();
    }
    
    public IWeakBase getWeakBase() {
        return _base;
    }
    
    // [B] = c - [BH+]
    public double getBaseConcentration() {
        return getInitialBaseConcentration() - getConjugateAcidConcentration();
    }
    
    // [BH+]
    public double getConjugateAcidConcentration() {
        return _conjugateBaseConcentration;
    }
    
    // [H3O+] = Kw / [OH-]
    public double getHydroniumConcentration() {
        return getWater().getEquilibriumConstant() / getHydroxideConcentration();
    }
    
    // [OH-] = [BH+]
    public double getHydroxideConcentration() {
        return getConjugateAcidConcentration();
    }
    
    // [H2O] = W - [BH+]
    public double getWaterConcentration() {
        return getWater().getConcentration() - getConjugateAcidConcentration();
    }
    
    // superclass override
    protected void notifyStateChanged() {
        updateConjugateAcidConcentration();
        super.notifyStateChanged();
    }
    
    // [BH+] optimization
    protected void updateConjugateAcidConcentration() {
        final double Kb = _base.getStrength();
        final double c = getInitialBaseConcentration();
        _conjugateBaseConcentration = -Kb + Math.sqrt( ( Kb * Kb ) + ( 4 * Kb * c ) );
    }
}
