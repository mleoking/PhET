/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.model.Fluid;

/**
 * FluidControlPanel is the panel used to control fluid properties.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FluidControlPanel extends VerticalLayoutPanel implements Observer {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // prints debugging output when either the control or model changes
    private static final boolean DEBUG_OUTPUT = true;
    
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
    
    /**
     * Constructor.
     * 
     * @param fluid the fluid model that this panel controls and observes
     * @param font
     */
    public FluidControlPanel( Fluid fluid, Font font ) {
        super();
        
        _fluid = fluid;
        _fluid.addObserver( this );
        
        setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
        
        // Speed control
        double value = fluid.getSpeed();
        double min = fluid.getSpeedRange().getMin();
        double max = fluid.getSpeedRange().getMax();
        double tickSpacing = max-min;
        int tickDecimalPlaces = 0;
        int valueDecimalPlaces = fluid.getSpeedRange().getSignificantDecimalPlaces();
        String label = OTResources.getString( "label.fluidSpeed" );
        String units = OTResources.getString( "units.fluidSpeed" );
        int columns = 6;
        _speedControl = new SliderControl( value, min, max, tickSpacing, tickDecimalPlaces, valueDecimalPlaces, label, units, columns );
        _speedControl.setTextFieldEditable( true );
        _speedControl.setFont( font );
        
        // Viscosity control
        value = fluid.getViscosity();
        min = fluid.getViscosityRange().getMin();
        max = fluid.getViscosityRange().getMax();
        tickSpacing = max-min;
        tickDecimalPlaces = 0;
        valueDecimalPlaces = fluid.getViscosityRange().getSignificantDecimalPlaces();
        label = OTResources.getString( "label.fluidViscosity" );
        units = OTResources.getString( "units.fluidViscosity" );
        columns = 6;
        _viscosityControl = new SliderControl( value, min, max, tickSpacing, tickDecimalPlaces, valueDecimalPlaces, label, units, columns );
        _viscosityControl.setTextFieldEditable( true );
        _viscosityControl.setFont( font );
        _viscosityControl.setTickNumberFormat( new DecimalFormat( "0E0" ) );
        _viscosityControl.setValueNumberFormat( new DecimalFormat( "0.0E0" ) );
        
        // Temperature control
        value = fluid.getTemperature();
        min = fluid.getTemperatureRange().getMin();
        max = fluid.getTemperatureRange().getMax();
        tickSpacing = max-min;
        tickDecimalPlaces = 0;
        valueDecimalPlaces = fluid.getTemperatureRange().getSignificantDecimalPlaces();
        label = OTResources.getString( "label.fluidTemperature" );
        units = OTResources.getString( "units.fluidTemperature" );
        columns = 4;
        _temperatureControl = new SliderControl( value, min, max, tickSpacing, tickDecimalPlaces, valueDecimalPlaces, label, units, columns );
        _temperatureControl.setTextFieldEditable( true );
        _temperatureControl.setFont( font );
        
        // Layout
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
        
        // Listeners
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
    
    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        _fluid.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    /*
     * Handles changes to the speed control.
     */
    private void handleSpeedChange() {
        double speed = _speedControl.getValue();
        if ( DEBUG_OUTPUT ) {
            System.out.println( "FluidControlPanel.handleSpeedChange " + speed );
        }
        _fluid.deleteObserver( this );
        _fluid.setSpeed( speed );
        _fluid.addObserver( this );
    }
    
    /*
     * Handles changes to the viscosity control.
     */
    private void handleViscosityChange() {
        double viscosity = _viscosityControl.getValue();
        if ( DEBUG_OUTPUT ) {
            System.out.println( "FluidControlPanel.handleViscosityChange " + viscosity );
        }
        _fluid.deleteObserver( this );
        _fluid.setViscosity( viscosity );
        _fluid.addObserver( this );
    }
    
    /*
     * Handles changes to the temperature control.
     */
    private void handleTemperatureChange() {
        double temperature = _temperatureControl.getValue();
        if ( DEBUG_OUTPUT ) {
            System.out.println( "FluidControlPanel.handleTemperatureChange " + temperature );
        }
        _fluid.deleteObserver( this );
        _fluid.setTemperature( temperature );
        _fluid.addObserver( this );
    }

    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the controls to match the model.
     * 
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if ( DEBUG_OUTPUT ) {
            System.out.println( "FluidControl.update" );
        }
        if ( o == _fluid ) {
            // resync everything if any property changes
            _speedControl.setValue( _fluid.getSpeed() );
            _viscosityControl.setValue( _fluid.getViscosity() );
            _temperatureControl.setValue( _fluid.getTemperature() );
        }
    }
}
