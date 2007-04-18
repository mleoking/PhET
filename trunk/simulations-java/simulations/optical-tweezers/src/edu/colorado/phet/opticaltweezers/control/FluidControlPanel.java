/* Copyright 2007, University of Colorado */

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

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.defaults.PhysicsDefaults;
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
        DoubleRange range = fluid.getSpeedRange();
        String label = OTResources.getString( "label.fluidSpeed" );
        String valuePattern = PhysicsDefaults.FLUID_SPEED_DISPLAY_PATTERN; //XXX module specific!
        String units = OTResources.getString( "units.fluidSpeed" );
        _speedControl = new SliderControl( range, label, valuePattern, units );
        _speedControl.setValue( value );
        _speedControl.setTextFieldEditable( true );
        _speedControl.setFont( font );
        _speedControl.setTickNumberPattern( "0" );
        
        // Viscosity control
        value = fluid.getViscosity();
        range = fluid.getViscosityRange();
        label = OTResources.getString( "label.fluidViscosity" );
        valuePattern = PhysicsDefaults.FLUID_VISCOSITY_DISPLAY_PATTERN; //XXX module specific!
        units = OTResources.getString( "units.fluidViscosity" );
        _viscosityControl = new SliderControl( range, label, valuePattern, units );
        _viscosityControl.setValue( value );
        _viscosityControl.setTextFieldEditable( true );
        _viscosityControl.setFont( font );
        _viscosityControl.setTickNumberPattern( "0E0" );
        
        // Temperature control
        value = fluid.getTemperature();
        range = fluid.getTemperatureRange();
        label = OTResources.getString( "label.fluidTemperature" );
        valuePattern = PhysicsDefaults.FLUID_TEMPERATURE_DISPLAY_PATTERN; //XXX module specific!
        units = OTResources.getString( "units.fluidTemperature" );
        _temperatureControl = new SliderControl( range, label, valuePattern, units, true /* logarithmic */ );
        _temperatureControl.setTextFieldEditable( true );
        _temperatureControl.setFont( font );
        _temperatureControl.setTickNumberPattern( "0" );
        
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
