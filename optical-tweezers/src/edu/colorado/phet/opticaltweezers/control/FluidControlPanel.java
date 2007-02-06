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

import javax.swing.BorderFactory;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.opticaltweezers.util.DoubleRange;


public class FluidControlPanel extends VerticalLayoutPanel {

    private static final DoubleRange VELOCITY_RANGE = new DoubleRange( 0, 100, 50, 1 );
    private static final DoubleRange VISCOSITY_RANGE = new DoubleRange( 0, 100, 50, 1 );
    private static final DoubleRange TEMPERATURE_RANGE = new DoubleRange( 0, 100, 50, 1 );
    
    private SliderControl _velocityControl;
    private SliderControl _viscosityControl;
    private SliderControl _temperatureControl;
    
    public FluidControlPanel() {
        super();
        
        setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
        
        double value = VELOCITY_RANGE.getDefault();
        double min = VELOCITY_RANGE.getMin();
        double max = VELOCITY_RANGE.getMax();
        double tickSpacing = max-min; //XXX
        int tickDecimalPlaces = 0; //XXX
        int valueDecimalPlaces = VELOCITY_RANGE.getSignificantDecimalPlaces();
        String label = SimStrings.get( "label.flowVelocity" );
        String units = SimStrings.get( "units.flowVelocity" );
        int columns = 4; //XXX
        _velocityControl = new SliderControl( value, min, max, tickSpacing, tickDecimalPlaces, valueDecimalPlaces, label, units, columns );

        value = VISCOSITY_RANGE.getDefault();
        min = VISCOSITY_RANGE.getMin();
        max = VISCOSITY_RANGE.getMax();
        tickSpacing = max-min; //XXX
        tickDecimalPlaces = 0; //XXX
        valueDecimalPlaces = VISCOSITY_RANGE.getSignificantDecimalPlaces();
        label = SimStrings.get( "label.fluidViscosity" );
        units = SimStrings.get( "units.fluidViscosity" );
        columns = 4; //XXX
        _viscosityControl = new SliderControl( value, min, max, tickSpacing, tickDecimalPlaces, valueDecimalPlaces, label, units, columns );
        
        value = TEMPERATURE_RANGE.getDefault();
        min = TEMPERATURE_RANGE.getMin();
        max = TEMPERATURE_RANGE.getMax();
        tickSpacing = max-min; //XXX
        tickDecimalPlaces = 0; //XXX
        valueDecimalPlaces = TEMPERATURE_RANGE.getSignificantDecimalPlaces();
        label = SimStrings.get( "label.fluidTemperature" );
        units = SimStrings.get( "units.fluidTemperature" );
        columns = 4; //XXX
        _temperatureControl = new SliderControl( value, min, max, tickSpacing, tickDecimalPlaces, valueDecimalPlaces, label, units, columns );
        
        add( _velocityControl );
        add( new JSeparator() );
        add( _viscosityControl );
        add( new JSeparator() );
        add( _temperatureControl );
        
        _velocityControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                handleVelocityChange();
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
    
    public SliderControl getVelocityControl() {
        return _velocityControl;
    }

    public SliderControl getViscosityControl() {
        return _viscosityControl;
    }
    
    public SliderControl getTemperatureControl() {
        return _temperatureControl;
    }
    
    private void handleVelocityChange() {
        //XXX update model
    }
    
    private void handleViscosityChange() {
        //XXX update model
    }
    
    private void handleTemperatureChange() {
        //XXX update model
    }
}
