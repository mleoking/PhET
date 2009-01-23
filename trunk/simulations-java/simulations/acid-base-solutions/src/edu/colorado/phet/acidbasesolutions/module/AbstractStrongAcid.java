package edu.colorado.phet.acidbasesolutions.module;

import edu.colorado.phet.acidbasesolutions.ABSConstants;


public class AbstractStrongAcid extends AbstractChemicalCompound implements IAcid {

    public double getPercentIonization() {
        return 100;
    }

    public double getConcentrationA() {
        return getConcentration();
    }

    public double getConcentrationH2O() {
       return ABSConstants.WATER_CONCENTRATION - getConcentration();
    }

    public double getConcentrationH3O() {
        return getConcentration();
    }

    public double getConcentrationHA() {
        return 0;
    }

    public double getConcentrationOH() {
        return 1E-14 - getConcentration();
    }

}
