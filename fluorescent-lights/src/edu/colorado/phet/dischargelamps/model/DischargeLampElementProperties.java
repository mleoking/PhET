/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.model;

import edu.colorado.phet.quantum.model.ElementProperties;
import edu.colorado.phet.quantum.model.EnergyEmissionStrategy;

/**
 * DischargeLampElementProperties
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DischargeLampElementProperties extends ElementProperties {
    private EnergyAbsorptionStrategy energyAbsorptionStrategy;

    protected DischargeLampElementProperties( String name, double[] energyLevels,
                                              EnergyEmissionStrategy energyEmissionStrategy,
                                              EnergyAbsorptionStrategy energyAbsorptionStrategy ) {
        this( name, energyLevels, energyEmissionStrategy, energyAbsorptionStrategy, 0 );
    }

    protected DischargeLampElementProperties( String name, double[] energyLevels,
                                              EnergyEmissionStrategy energyEmissionStrategy,
                                              EnergyAbsorptionStrategy energyAbsorptionStrategy,
                                              double meanStateLifetime ) {
        super( name, energyLevels, energyEmissionStrategy, meanStateLifetime );
        setEnergyAbsorptionStrategy( energyAbsorptionStrategy );
    }

    public EnergyAbsorptionStrategy getEnergyAbsorptionStrategy() {
        return energyAbsorptionStrategy;
    }

    public void setEnergyAbsorptionStrategy( EnergyAbsorptionStrategy energyAbsorptionStrategy ) {
        this.energyAbsorptionStrategy = energyAbsorptionStrategy;
    }



    public static class TransitionEntry {
        int sourceStateIdx;
        int targetStateIdx;
        double txStrength;

        public TransitionEntry( int sourceStateIdx, int targetStateIdx, double txStrength ) {
            this.sourceStateIdx = sourceStateIdx;
            this.targetStateIdx = targetStateIdx;
            this.txStrength = txStrength;
        }

        public int getSourceStateIdx() {
            return sourceStateIdx;
        }

        public int getTargetStateIdx() {
            return targetStateIdx;
        }

        public double getTxStrength() {
            return txStrength;
        }
    }
}
