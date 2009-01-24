package edu.colorado.phet.acidbasesolutions.model.solutions;

import edu.colorado.phet.acidbasesolutions.model.acids.IWeakAcid;


public class WeakAcidSolution extends AbstractAcidSolution {
    
    private final IWeakAcid _acid;
    private double _conjugateBaseConcentration;
    
    public WeakAcidSolution( IWeakAcid acid, double initialAcidConcentration ) {
        super( acid, initialAcidConcentration );
        _acid = acid;
        updateConjugateBaseConcentration();
    }
    
    public IWeakAcid getWeakAcid() {
        return _acid;
    }
    
    // [HA] = c - [A-]
    public double getAcidConcentration() {
        return getInitialAcidConcentration() - getConjugateBaseConcentration();
    }
    
    // [A-]
    public double getConjugateBaseConcentration() {
        return _conjugateBaseConcentration;
    }
    
    // [H3O+] = [A-]
    public double getHydroniumConcentration() {
        return getConjugateBaseConcentration();
    }
    
    // [OH-] = Kw / [H3O+]
    public double getHydroxideConcentration() {
        return getWater().getEquilibriumConstant() / getHydroniumConcentration();
    }
    
    // [H2O] = W - [A-]
    public double getWaterConcentration() {
        return getWater().getConcentration() - getConjugateBaseConcentration();
    }
    
    // superclass override
    protected void notifyStateChanged() {
        updateConjugateBaseConcentration();
        super.notifyStateChanged();
    }
    
    // [A-] optimization
    protected void updateConjugateBaseConcentration() {
        final double Ka = _acid.getStrength();
        final double c = getInitialAcidConcentration();
        _conjugateBaseConcentration = -Ka + Math.sqrt( ( Ka * Ka ) + ( 4 * Ka * c ) );
    }
}
