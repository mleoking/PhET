
package edu.colorado.phet.acidbasesolutions.model.solutions;


public interface IAcidSolution {

    // c
    public void setInitialAcidConcentration( double c );

    // c
    public double getInitialAcidConcentration();

    // [HA]
    public double getAcidConcentration();

    // [A-]
    public double getConjugateBaseConcentration();

    // [H3O+]
    public double getHydroniumConcentration();

    // [OH-]
    public double getHydroxideConcentration();

    // [H2O]
    public double getWaterConcentration();
}
