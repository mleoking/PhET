/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.control;

import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.rutherfordscattering.RSConstants;
import edu.colorado.phet.rutherfordscattering.module.RutherfordAtomModule;


public class RutherfordAtomControlPanel extends AbstractControlPanel {

    private static final double CLOCK_STEP_TO_ENERGY_MULTIPLER = 100;
    
    private EnergyControl _energyControl;
    private IntegerSliderControl _protonsControl;
    private IntegerSliderControl _neutronsControl;
    
    public RutherfordAtomControlPanel( RutherfordAtomModule module ) {
        super( module );
        
        JLabel alphaParticlePropertiesLabel = new JLabel( SimStrings.get( "label.alphaParticleProperties" ) );
        alphaParticlePropertiesLabel.setFont( RSConstants.TITLE_FONT );
        
        // Energy
        int minEnergy = clockStepToEnergy( RSConstants.MIN_CLOCK_STEP );
        int maxEnergy = clockStepToEnergy( RSConstants.MAX_CLOCK_STEP );
        int defaultEnergy = clockStepToEnergy( RSConstants.DEFAULT_CLOCK_STEP );
        _energyControl = new EnergyControl( minEnergy, maxEnergy, defaultEnergy );
        _energyControl.setTitleFont( RSConstants.CONTROL_FONT );
        _energyControl.setSliderFont( RSConstants.CONTROL_FONT );
        _energyControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleEnergyChange();
            }
        });
        
        JLabel atomPropertiesLabel = new JLabel( SimStrings.get( "label.atomProperties" ) );
        atomPropertiesLabel.setFont( RSConstants.TITLE_FONT );
        
        // Number of protons
        String labelProtons = SimStrings.get( "label.numberOfProtons" );
        int minProtons = RSConstants.MIN_PROTONS;
        int maxProtons = RSConstants.MAX_PROTONS;
        int defaultProtons = RSConstants.DEFAULT_PROTONS;
        _protonsControl = new IntegerSliderControl( labelProtons, minProtons, maxProtons, defaultProtons );
        _protonsControl.setTitleFont( RSConstants.CONTROL_FONT );
        _protonsControl.setSliderFont( RSConstants.CONTROL_FONT );
        _protonsControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleProtonsChange();
            }
        });
        
        // Number of neutrons
        String labelNeutrons = SimStrings.get( "label.numberOfNeutrons" );
        int minNeutrons = RSConstants.MIN_NEUTRONS;
        int maxNeutrons = RSConstants.MAX_NEUTRONS;
        int defaultNeutrons = RSConstants.DEFAULT_NEUTRONS;
        _neutronsControl = new IntegerSliderControl( labelNeutrons, minNeutrons, maxNeutrons, defaultNeutrons );
        _neutronsControl.setTitleFont( RSConstants.CONTROL_FONT );
        _neutronsControl.setSliderFont( RSConstants.CONTROL_FONT );
        _neutronsControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleNeutronsChange();
            }
        });
        
        addVerticalSpace( 20 );
        addControlFullWidth( alphaParticlePropertiesLabel );
        addVerticalSpace( 20 );
        addControlFullWidth( _energyControl );
        addVerticalSpace( 20 );
        addSeparator();
        addVerticalSpace( 20 );
        addControlFullWidth( atomPropertiesLabel );
        addVerticalSpace( 20 );
        addControlFullWidth( _protonsControl );
        addControlFullWidth( _neutronsControl );
    }

    private static double energyToClockStep( int energy ) {
        return energy / (double) CLOCK_STEP_TO_ENERGY_MULTIPLER;
    }

    private static int clockStepToEnergy( double clockStep ) {
        return (int) ( clockStep * CLOCK_STEP_TO_ENERGY_MULTIPLER );
    }
    
    private void handleEnergyChange() {
        int energy = _energyControl.getValue();
        double clockStep = energyToClockStep( energy );
        getModule().setDt( clockStep );
    }
    
    private void handleProtonsChange() {
        int numberOfProtons = _protonsControl.getValue();
        //XXX
    }
    
    private void handleNeutronsChange() {
        int numberOfNeutrons = _neutronsControl.getValue();
        //XXX
    }
}
