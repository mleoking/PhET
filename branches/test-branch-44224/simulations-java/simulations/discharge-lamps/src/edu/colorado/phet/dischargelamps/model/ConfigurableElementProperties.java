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

import edu.colorado.phet.common.quantum.model.AtomicState;
import edu.colorado.phet.dischargelamps.DischargeLampsResources;
import edu.colorado.phet.dischargelamps.quantum.AtomicStateFactory;

/**
 * ConfigurableElementProperties
 * <p/>
 * These are the properties for the configurable element.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ConfigurableElementProperties extends DischargeLampElementProperties {
    private static double[] energyLevels = {
            -13.6
    };

    private double meanStateLifetime = DischargeLampAtom.DEFAULT_STATE_LIFETIME;

    public ConfigurableElementProperties( int numEnergyLevels, DischargeLampModel model ) {
        super( DischargeLampsResources.getString( "Element.configurable" ), energyLevels,
               new ConfigurableAtomEnergyEmissionStrategy(),
               new EqualLikelihoodAbsorptionStrategy(),
               DischargeLampAtom.DEFAULT_STATE_LIFETIME );
        setMeanStateLifetime( meanStateLifetime );
        model.addChangeListener( new LevelChangeHandler() );
        setNumEnergyLevels( numEnergyLevels );
    }

    public void setNumEnergyLevels( int numEnergyLevels ) {
        AtomicState[] states = null;
        states = new AtomicStateFactory().createAtomicStates( numEnergyLevels, getStates() );
        double[] newEnergyLevels = new double[numEnergyLevels];
        for ( int i = 0; i < newEnergyLevels.length; i++ ) {
            newEnergyLevels[i] = states[i].getEnergyLevel();
        }
        setEnergyLevels( newEnergyLevels );
        setLevelsMovable( true );
    }


    private class LevelChangeHandler extends DischargeLampModel.ChangeListenerAdapter {
        public void energyLevelsChanged( DischargeLampModel.ChangeEvent event ) {
            if ( event.getDischargeLampModel().getElementProperties() == ConfigurableElementProperties.this ) {
                AtomicState[] states = event.getDischargeLampModel().getAtomicStates();
                double[] newEnergyLevels = new double[states.length];
                for ( int i = 0; i < states.length; i++ ) {
                    newEnergyLevels[i] = states[i].getEnergyLevel();
                }
                setEnergyLevels( newEnergyLevels );
            }
        }
    }
}

