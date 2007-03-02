/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.control;

import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.rutherfordscattering.RSConstants;
import edu.colorado.phet.rutherfordscattering.module.PlumPuddingModule;


public class PlumPuddingControlPanel extends AbstractControlPanel {

    private static final double CLOCK_STEP_TO_ENERGY_MULTIPLER = 100;
    
    private EnergyControl _energyControl;
    
    public PlumPuddingControlPanel( PlumPuddingModule module ) {
        super( module );
        
        JLabel titleLabel = new JLabel( SimStrings.get( "label.alphaParticleProperties" ) );
        titleLabel.setFont( RSConstants.TITLE_FONT );
        
        int min = clockStepToEnergy( RSConstants.MIN_CLOCK_STEP );
        int max = clockStepToEnergy( RSConstants.MAX_CLOCK_STEP );
        int value = clockStepToEnergy( RSConstants.DEFAULT_CLOCK_STEP );
        _energyControl = new EnergyControl( min, max, value );
        _energyControl.setTitleFont( RSConstants.CONTROL_FONT );
        _energyControl.setSliderFont( RSConstants.CONTROL_FONT );
        _energyControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleEnergyChange();
            }
        });
        
        addVerticalSpace( 20 );
        addControlFullWidth( titleLabel );
        addVerticalSpace( 20 );
        addControlFullWidth( _energyControl );
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
}
