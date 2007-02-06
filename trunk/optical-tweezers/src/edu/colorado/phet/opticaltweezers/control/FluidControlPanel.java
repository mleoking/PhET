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

import javax.swing.BorderFactory;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.opticaltweezers.OTConstants;


public class FluidControlPanel extends VerticalLayoutPanel {
    
    private SliderControl _velocityControl;
    private SliderControl _viscosityControl;
    private SliderControl _temperatureControl;
    
    public FluidControlPanel( Font font ) {
        super();
        
        //XXX Use font!
        
        setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
        
        double value = OTConstants.FLOW_VELOCITY_RANGE.getDefault();
        double min = OTConstants.FLOW_VELOCITY_RANGE.getMin();
        double max = OTConstants.FLOW_VELOCITY_RANGE.getMax();
        double tickSpacing = max-min; //XXX
        int tickDecimalPlaces = 0; //XXX
        int valueDecimalPlaces = OTConstants.FLOW_VELOCITY_RANGE.getSignificantDecimalPlaces();
        String label = SimStrings.get( "label.flowVelocity" );
        String units = SimStrings.get( "units.flowVelocity" );
        int columns = 4; //XXX
        _velocityControl = new SliderControl( value, min, max, tickSpacing, tickDecimalPlaces, valueDecimalPlaces, label, units, columns );

        value = OTConstants.FLUID_VISCOSITY_RANGE.getDefault();
        min = OTConstants.FLUID_VISCOSITY_RANGE.getMin();
        max = OTConstants.FLUID_VISCOSITY_RANGE.getMax();
        tickSpacing = max-min; //XXX
        tickDecimalPlaces = 0; //XXX
        valueDecimalPlaces = OTConstants.FLUID_VISCOSITY_RANGE.getSignificantDecimalPlaces();
        label = SimStrings.get( "label.fluidViscosity" );
        units = SimStrings.get( "units.fluidViscosity" );
        columns = 4; //XXX
        _viscosityControl = new SliderControl( value, min, max, tickSpacing, tickDecimalPlaces, valueDecimalPlaces, label, units, columns );
        
        value = OTConstants.FLUID_TEMPERATURE_RANGE.getDefault();
        min = OTConstants.FLUID_TEMPERATURE_RANGE.getMin();
        max = OTConstants.FLUID_TEMPERATURE_RANGE.getMax();
        tickSpacing = max-min; //XXX
        tickDecimalPlaces = 0; //XXX
        valueDecimalPlaces = OTConstants.FLUID_TEMPERATURE_RANGE.getSignificantDecimalPlaces();
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
        System.out.println( "FluidControlPanel.handleVelocityChange " + _velocityControl.getValue() );//XXX
        //XXX update model
    }
    
    private void handleViscosityChange() {
        System.out.println( "FluidControlPanel.handleViscosityChange " + _viscosityControl.getValue() );//XXX
        //XXX update model
    }
    
    private void handleTemperatureChange() {
        System.out.println( "FluidControlPanel.handleTemperatureChange " + _temperatureControl.getValue() );//XXX
        //XXX update model
    }
}
