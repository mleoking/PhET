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
    
    private JSlider _energySlider;
    
    public PlumPuddingControlPanel( PlumPuddingModule module ) {
        super( module );
        
        JLabel titleLabel = new JLabel( SimStrings.get( "label.alphaParticleProperties" ) );
        titleLabel.setFont( RSConstants.TITLE_FONT );
        
        JPanel energyPanel = new JPanel();
        TitledBorder titledBorder = new TitledBorder( SimStrings.get( "label.energy" ) );
        titledBorder.setTitleFont( RSConstants.CONTROL_FONT  );
        energyPanel.setBorder( titledBorder );
        
        _energySlider = new JSlider();
        _energySlider.setFont( RSConstants.CONTROL_FONT );
        _energySlider.setMinimum( clockStepToEnergy( RSConstants.MIN_CLOCK_STEP ) );
        _energySlider.setMaximum( clockStepToEnergy( RSConstants.MAX_CLOCK_STEP ) );
        _energySlider.setValue( clockStepToEnergy( RSConstants.DEFAULT_CLOCK_STEP ) );
        _energySlider.setMajorTickSpacing( _energySlider.getMaximum() - _energySlider.getMinimum() );
        _energySlider.setPaintTicks( true );
        _energySlider.setPaintLabels( true );
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( _energySlider.getMinimum() ), new JLabel( SimStrings.get(  "label.minEnergy"  ) ) );
        labelTable.put( new Integer( _energySlider.getMaximum() ), new JLabel( SimStrings.get(  "label.maxEnergy"  ) ) );
        _energySlider.setLabelTable( labelTable );
        _energySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleEnergySliderChange();
            }
        });
        
        energyPanel.add( _energySlider );
        
        addVerticalSpace( 20 );
        addControlFullWidth( titleLabel );
        addVerticalSpace( 20 );
        addControlFullWidth( energyPanel );
    }
    
    private static double energyToClockStep( int energy ) {
        return energy / (double)CLOCK_STEP_TO_ENERGY_MULTIPLER;
    }
    
    private static int clockStepToEnergy( double clockStep ) {
        return (int)( clockStep * CLOCK_STEP_TO_ENERGY_MULTIPLER );
    }

    private void handleEnergySliderChange() {
        int energy = _energySlider.getValue();
        double clockStep = energyToClockStep( energy );
        getModule().setDt( clockStep );
    }
}
