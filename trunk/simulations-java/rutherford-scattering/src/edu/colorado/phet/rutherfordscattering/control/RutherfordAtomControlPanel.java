/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.control;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.rutherfordscattering.RSConstants;
import edu.colorado.phet.rutherfordscattering.module.RutherfordAtomModule;


public class RutherfordAtomControlPanel extends AbstractControlPanel {

    private SliderControl _clockStepControl;
    private SliderControl _protonsControl;
    private SliderControl _neutronsControl;
    
    public RutherfordAtomControlPanel( RutherfordAtomModule module ) {
        super( module );
        
        JLabel alphaParticlePropertiesLabel = new JLabel( SimStrings.get( "label.alphaParticleProperties" ) );
        alphaParticlePropertiesLabel.setFont( RSConstants.TITLE_FONT );
        
        // Clock (Energy) control
        {
            double value = RSConstants.DEFAULT_CLOCK_STEP;
            double min = RSConstants.MIN_CLOCK_STEP;
            double max = RSConstants.MAX_CLOCK_STEP;
            double tickSpacing = max - min;
            int tickDecimalPlaces = 0;
            int valueDecimalPlaces = 1;
            String label = SimStrings.get( "label.energy" ); // labeled "Energy" !
            String units = "";
            int columns = 1;
            _clockStepControl = new SliderControl( value, min, max, tickSpacing, tickDecimalPlaces, valueDecimalPlaces, label, units, columns );
            _clockStepControl.setBorder( BorderFactory.createEtchedBorder() );
            _clockStepControl.setTextFieldVisible( false );
            _clockStepControl.setMinMaxLabels( SimStrings.get( "label.minEnergy" ), SimStrings.get( "label.maxEnergy" ) );
            _clockStepControl.addChangeListener( new ChangeListener() {

                public void stateChanged( ChangeEvent event ) {
                    handleClockChange();
                }
            } );
        }
        
        JLabel atomPropertiesLabel = new JLabel( SimStrings.get( "label.atomProperties" ) );
        atomPropertiesLabel.setFont( RSConstants.TITLE_FONT );
        
        // Number of protons
        {
            int value = RSConstants.DEFAULT_PROTONS;
            int min = RSConstants.MIN_PROTONS;
            int max = RSConstants.MAX_PROTONS;
            int tickSpacing = max - min;
            int tickDecimalPlaces = 0;
            int valueDecimalPlaces = 0;
            String label = SimStrings.get( "label.numberOfProtons" );
            String units = "";
            int columns = 3;
            _protonsControl = new SliderControl( value, min, max, tickSpacing, tickDecimalPlaces, valueDecimalPlaces, label, units, columns );
            _protonsControl.setBorder( BorderFactory.createEtchedBorder() );
            _protonsControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    handleProtonsChange();
                }
            } );
        }
        
        // Number of neutrons
        {
            int value = RSConstants.DEFAULT_NEUTRONS;
            int min = RSConstants.MIN_NEUTRONS;
            int max = RSConstants.MAX_NEUTRONS;
            int tickSpacing = max - min;
            int tickDecimalPlaces = 0;
            int valueDecimalPlaces = 0;
            String label = SimStrings.get( "label.numberOfNeutrons" );
            String units = "";
            int columns = 3;
            _neutronsControl = new SliderControl( value, min, max, tickSpacing, tickDecimalPlaces, valueDecimalPlaces, label, units, columns );
            _neutronsControl.setBorder( BorderFactory.createEtchedBorder() );
            _neutronsControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    handleNeutronsChange();
                }
            } );
        }
        
        addVerticalSpace( 20 );
        addControlFullWidth( alphaParticlePropertiesLabel );
        addVerticalSpace( 20 );
        addControlFullWidth( _clockStepControl );
        addVerticalSpace( 20 );
        addSeparator();
        addVerticalSpace( 20 );
        addControlFullWidth( atomPropertiesLabel );
        addVerticalSpace( 20 );
        addControlFullWidth( _protonsControl );
        addControlFullWidth( _neutronsControl );
        addVerticalSpace( 20 );
        addSeparator();
    }
    
    public void setClockStep( double clockStep ) {
        _clockStepControl.setValue( clockStep );
    }
    
    public void setNumberOfProtons( int numberOfProtons ) {
        _protonsControl.setValue( numberOfProtons );
    }
    
    public void setNumberOfNeutrons( int numberOfNeutrons ) {
        _neutronsControl.setValue( numberOfNeutrons );
    }

    private void handleClockChange() {
        double dt = _clockStepControl.getValue();
        System.out.println( "RutherfordAtomControlPanel.handleClockChange dt=" + dt );//XXX
        getModule().setClockStep( dt );
    }
    
    private void handleProtonsChange() {
        int numberOfProtons = (int) _protonsControl.getValue();
        System.out.println( "RutherfordAtomControlPanel.handleProtonsChange numberOfProtons=" + numberOfProtons );//XXX
        //XXX
    }

    private void handleNeutronsChange() {
        int numberOfNeutrons = (int) _neutronsControl.getValue();
        System.out.println( "RutherfordAtomControlPanel.handleNeutronsChange numberOfNeutrons=" + numberOfNeutrons );//XXX
        //XXX
    }
}
