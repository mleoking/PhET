/* Copyright 2007, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.opticaltweezers.model.Fluid;


public class FluidControlPanel extends VerticalLayoutPanel implements Observer {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Fluid _fluid;
    
    private SliderControl _speedControl;
    private SliderControl _viscosityControl;
    private SliderControl _temperatureControl;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public FluidControlPanel( Fluid fluid, Font font ) {
        super();
        
        _fluid = fluid;
        _fluid.addObserver( this );
        
        setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
        
        double value = fluid.getSpeed();
        double min = fluid.getSpeedRange().getMin();
        double max = fluid.getSpeedRange().getMax();
        double tickSpacing = max-min;
        int tickDecimalPlaces = 0;
        int valueDecimalPlaces = fluid.getSpeedRange().getSignificantDecimalPlaces();
        String label = SimStrings.get( "label.fluidSpeed" );
        String units = SimStrings.get( "units.fluidSpeed" );
        int columns = 4;
        _speedControl = new SliderControl( value, min, max, tickSpacing, tickDecimalPlaces, valueDecimalPlaces, label, units, columns );
        _speedControl.setTextFieldEditable( true );
        _speedControl.setFont( font );
        
        value = fluid.getViscosity();
        min = fluid.getViscosityRange().getMin();
        max = fluid.getViscosityRange().getMax();
        tickSpacing = max-min;
        tickDecimalPlaces = 0;
        valueDecimalPlaces = fluid.getViscosityRange().getSignificantDecimalPlaces();
        label = SimStrings.get( "label.fluidViscosity" );
        units = SimStrings.get( "units.fluidViscosity" );
        columns = 4;
        _viscosityControl = new SliderControl( value, min, max, tickSpacing, tickDecimalPlaces, valueDecimalPlaces, label, units, columns );
        _viscosityControl.setTextFieldEditable( true );
        _viscosityControl.setFont( font );
        
        value = fluid.getTemperature();
        min = fluid.getTemperatureRange().getMin();
        max = fluid.getTemperatureRange().getMax();
        tickSpacing = max-min;
        tickDecimalPlaces = 0;
        valueDecimalPlaces = fluid.getTemperatureRange().getSignificantDecimalPlaces();
        label = SimStrings.get( "label.fluidTemperature" );
        units = SimStrings.get( "units.fluidTemperature" );
        columns = 4;
        _temperatureControl = new SliderControl( value, min, max, tickSpacing, tickDecimalPlaces, valueDecimalPlaces, label, units, columns );
        _temperatureControl.setTextFieldEditable( true );
        _temperatureControl.setFont( font );
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( _speedControl, row++, column );
        layout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
        layout.addComponent( _viscosityControl, row++, column );
        layout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
        layout.addComponent( _temperatureControl, row++, column );
        
        // Adjust all sliders to be the same width
        int sliderWidth = Math.max( (int) _speedControl.getPreferredSize().getWidth(), 
                Math.max( (int) _viscosityControl.getPreferredSize().getWidth(), (int) _temperatureControl.getPreferredSize().getWidth() ) );
        _speedControl.setSliderWidth( sliderWidth );
        _viscosityControl.setSliderWidth( sliderWidth );
        _temperatureControl.setSliderWidth( sliderWidth );
        
        _speedControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                handleSpeedChange();
            }
        } );
        _viscosityControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                handleViscosityChange();
            }
        } );
        _temperatureControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                handleTemperatureChange();
            }
        } );
    }
    
    public void cleanup() {
        _fluid.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleSpeedChange() {
        double speed = _speedControl.getValue();
        System.out.println( "FluidControlPanel.handleSpeedChange " + speed );//XXX
        _fluid.deleteObserver( this );
        _fluid.setSpeed( speed );
        _fluid.addObserver( this );
    }
    
    private void handleViscosityChange() {
        double viscosity = _viscosityControl.getValue();
        System.out.println( "FluidControlPanel.handleViscosityChange " + viscosity );//XXX
        _fluid.deleteObserver( this );
        _fluid.setViscosity( viscosity );
        _fluid.addObserver( this );
    }
    
    private void handleTemperatureChange() {
        double temperature = _temperatureControl.getValue();
        System.out.println( "FluidControlPanel.handleTemperatureChange " + temperature );//XXX
        _fluid.deleteObserver( this );
        _fluid.setTemperature( temperature );
        _fluid.addObserver( this );
    }

    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( o == _fluid ) {
            _speedControl.setValue( _fluid.getSpeed() );
            _viscosityControl.setValue( _fluid.getViscosity() );
            _temperatureControl.setValue( _fluid.getTemperature() );
        }
    }
}
