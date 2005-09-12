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

import edu.colorado.phet.lasers.model.atom.AtomicState;

/**
 * HydrogenProperties
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ConfigurableElementProperties extends ElementProperties {
    private static double[] energyLevels = {
        -13.6
    };

    private double meanStateLifetime = 15;

    public ConfigurableElementProperties( int numEnergyLevels, DischargeLampModel model ) {
        super( "Configurable", energyLevels,
               new ConfigurableAtomEnergyEmissionStrategy(),
//               new HydrogenEnergyEmissionStrategy(),
               new FiftyPercentAbsorptionStrategy() );
        setMeanStateLifetime( meanStateLifetime );
        model.addChangeListener( new LevelChangeHandler() );
        setNumEnergyLevels( numEnergyLevels );
    }

    public void setNumEnergyLevels( int numEnergyLevels ) {
        AtomicState[] states = null;
        states = new AtomicStateFactory().createAtomicStates( numEnergyLevels, getStates() );
        double[] newEnergyLevels = new double[numEnergyLevels];
        for( int i = 0; i < newEnergyLevels.length; i++ ) {
            newEnergyLevels[i] = states[i].getEnergyLevel();
        }
        setEnergyLevels( newEnergyLevels );
        setLevelsMovable( true );
    }


    private class LevelChangeHandler extends DischargeLampModel.ChangeListenerAdapter {
        public void energyLevelsChanged( DischargeLampModel.ChangeEvent event ) {
            if( event.getDischargeLampModel().getElementProperties() == ConfigurableElementProperties.this ) {
                AtomicState[] states = event.getDischargeLampModel().getAtomicStates();
                double[] newEnergyLevels = new double[states.length];
                for( int i = 0; i < states.length; i++ ) {
                    newEnergyLevels[i] = states[i].getEnergyLevel();
                }
                setEnergyLevels( newEnergyLevels );
            }
        }
    }
}

