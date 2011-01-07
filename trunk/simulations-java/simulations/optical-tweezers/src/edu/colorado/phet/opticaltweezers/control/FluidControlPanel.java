// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.control;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.DefaultLayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LogarithmicValueControl;
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
    private static final boolean DEBUG_OUTPUT = false;
    
    private static final double NANOMETERS_PER_MICRON = 1E3;
        
    private static final String SPEED_DISPLAY_PATTERN = "###0"; // displayed in microns/sec, requires fewer digits
    private static final String VISCOSITY_DISPLAY_PATTERN = "0.0E0";
    private static final String TEMPERATURE_DISPLAY_PATTERN = "##0";
    private static final String ATP_DISPLAY_PATTERN = "#0.0";
    
    private static final Insets INSETS = new Insets( 1, 2, 0, 2 ); // top, left, bottom, right
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Fluid _fluid;
    
    private LinearValueControl _speedControl;
    private LogarithmicValueControl _viscosityControl;
    private LinearValueControl _temperatureControl;
    private LinearValueControl _atpControl;
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * Layout strategy for fluid controls, allows us to specify the insets.
     * In this case, we want to reduce the insets significantly to reduce the
     * total vertical height of the panel.
     */
    private static class FluidControlLayoutStrategy extends DefaultLayoutStrategy {
        public FluidControlLayoutStrategy() {
            super( SwingConstants.LEFT, INSETS );
        }
    }
    
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
        
        setInsets( INSETS );
        
        _fluid = fluid;
        _fluid.addObserver( this );
        
        // Speed control, in microns/sec, model is in nm/sec)
        double value = fluid.getSpeed() / NANOMETERS_PER_MICRON;
        double min = fluid.getSpeedRange().getMin() / NANOMETERS_PER_MICRON;
        double max = fluid.getSpeedRange().getMax() / NANOMETERS_PER_MICRON;
        String label = OTResources.getString( "label.fluidSpeed" );
        String valuePattern = SPEED_DISPLAY_PATTERN;
        String units = OTResources.getString( "units.fluidSpeed" );
        _speedControl = new LinearValueControl( min, max, label, valuePattern, units, new FluidControlLayoutStrategy() );
        _speedControl.setValue( value );
        _speedControl.setUpDownArrowDelta( 1 );
        _speedControl.setTextFieldEditable( true );
        _speedControl.setFont( font );
        _speedControl.setTickPattern( "0" );
        _speedControl.setMajorTickSpacing( ( max - min ) / 2 );
        _speedControl.setMinorTickSpacing( 100 );
        _speedControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                handleSpeedChange();
            }
        } );
        
        // Viscosity control
        value = fluid.getViscosity();
        min = fluid.getViscosityRange().getMin();
        max = fluid.getViscosityRange().getMax();
        label = OTResources.getString( "label.fluidViscosity" );
        valuePattern = VISCOSITY_DISPLAY_PATTERN;
        units = OTResources.getString( "units.fluidViscosity" );
        _viscosityControl = new LogarithmicValueControl( min, max, label, valuePattern, units, new FluidControlLayoutStrategy() );
        _viscosityControl.setValue( value );
        _viscosityControl.setTextFieldEditable( true );
        _viscosityControl.setFont( font );
        _viscosityControl.setUpDownArrowDelta( 1E1 );
        _viscosityControl.setTickPattern( "0E0" );
        _viscosityControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                handleViscosityChange();
            }
        } );
        
        // Temperature control
        value = fluid.getTemperature();
        min = fluid.getTemperatureRange().getMin();
        max = fluid.getTemperatureRange().getMax();
        label = OTResources.getString( "label.fluidTemperature" );
        valuePattern = TEMPERATURE_DISPLAY_PATTERN;
        units = OTResources.getString( "units.fluidTemperature" );
        _temperatureControl = new LinearValueControl( min, max, label, valuePattern, units, new FluidControlLayoutStrategy() );
        _temperatureControl.setValue( value );
        _temperatureControl.setTextFieldEditable( true );
        _temperatureControl.setFont( font );
        _temperatureControl.setUpDownArrowDelta( 1 );
        _temperatureControl.setTickPattern( "0" );
        _temperatureControl.setMajorTickSpacing( ( max - min ) / 2 );
        _temperatureControl.setMinorTickSpacing( 50 );
        _temperatureControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                handleTemperatureChange();
            }
        } );
        
        // APT control
        if ( fluid.getATPConcentrationRange().getLength() != 0 ) {
            value = fluid.getATPConcentration();
            min = fluid.getATPConcentrationRange().getMin();
            max = fluid.getATPConcentrationRange().getMax();
            label = OTResources.getString( "label.atpConcentration" );
            valuePattern = ATP_DISPLAY_PATTERN;
            units = "";
            _atpControl = new LinearValueControl( min, max, label, valuePattern, units, new FluidControlLayoutStrategy() );
            _atpControl.setValue( value );
            _atpControl.setTextFieldEditable( true );
            _atpControl.setFont( font );
            _atpControl.setUpDownArrowDelta( 0.1 );
            _atpControl.setTickPattern( "0" );
            _atpControl.setMajorTickSpacing( ( max - min ) / 2 );
            _atpControl.setMinorTickSpacing( 1 );
            _atpControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    handleATPChange();
                }
            } );
        }
        
        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        layout.setInsets( INSETS );
        int row = 0;
        int column = 0;
        if ( _atpControl != null ) {
            layout.addComponent( _atpControl, row++, column );
            layout.addComponent( new JSeparator(), row++, column );
        }
        layout.addComponent( _speedControl, row++, column );
        layout.addComponent( new JSeparator(), row++, column );
        layout.addComponent( _viscosityControl, row++, column );
        layout.addComponent( new JSeparator(), row++, column );
        layout.addComponent( _temperatureControl, row++, column );
        
        // Adjust all sliders to be the same width
        int speedWidth = (int)_speedControl.getPreferredSize().getWidth();
        int viscosityWidth = (int)_viscosityControl.getPreferredSize().getWidth();
        int temperatureWidth = (int)_temperatureControl.getPreferredSize().getWidth();
        int aptWidth = ( _atpControl == null ) ? 0 : (int)_atpControl.getPreferredSize().getWidth();
        int maxWidth = Math.max( Math.max( speedWidth, Math.max( viscosityWidth, temperatureWidth ) ), aptWidth );
        _speedControl.setSliderWidth( maxWidth );
        _viscosityControl.setSliderWidth( maxWidth );
        _temperatureControl.setSliderWidth( maxWidth );
        if ( _atpControl != null ) {
            _atpControl.setSliderWidth( maxWidth );
        }
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
        double speed = _speedControl.getValue() * NANOMETERS_PER_MICRON;
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
    
    /*
     * Handles changes to the ATP control.
     */
    private void handleATPChange() {
        double apt = _atpControl.getValue();
        if ( DEBUG_OUTPUT ) {
            System.out.println( "FluidControlPanel.handleATPChange " + apt );
        }
        _fluid.deleteObserver( this );
        _fluid.setATPConcentration( apt );
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
            _speedControl.setValue( _fluid.getSpeed() / NANOMETERS_PER_MICRON );
            _viscosityControl.setValue( _fluid.getViscosity() );
            _temperatureControl.setValue( _fluid.getTemperature() );
            if ( _atpControl != null ) {
                _atpControl.setValue( _fluid.getATPConcentration() );
            }
        }
    }
}
