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

import javax.swing.BorderFactory;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.util.DoubleRange;


public class FluidControlPanel extends VerticalLayoutPanel {
    
    private SliderControl _speedControl;
    private SliderControl _viscosityControl;
    private SliderControl _temperatureControl;
    
    public FluidControlPanel( Font font, DoubleRange speedRange, DoubleRange viscosityRange, DoubleRange temperatureRange ) {
        super();
        
        //XXX Use font!
        
        setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
        
        double value = speedRange.getDefault();
        double min = speedRange.getMin();
        double max = speedRange.getMax();
        double tickSpacing = max-min; //XXX
        int tickDecimalPlaces = 0; //XXX
        int valueDecimalPlaces = speedRange.getSignificantDecimalPlaces();
        String label = SimStrings.get( "label.fluidSpeed" );
        String units = SimStrings.get( "units.fluidSpeed" );
        int columns = 4; //XXX
        _speedControl = new SliderControl( value, min, max, tickSpacing, tickDecimalPlaces, valueDecimalPlaces, label, units, columns );
        _speedControl.setTextFieldEditable( true );
        
        value = viscosityRange.getDefault();
        min = viscosityRange.getMin();
        max = viscosityRange.getMax();
        tickSpacing = max-min; //XXX
        tickDecimalPlaces = 0; //XXX
        valueDecimalPlaces = viscosityRange.getSignificantDecimalPlaces();
        label = SimStrings.get( "label.fluidViscosity" );
        units = SimStrings.get( "units.fluidViscosity" );
        columns = 4; //XXX
        _viscosityControl = new SliderControl( value, min, max, tickSpacing, tickDecimalPlaces, valueDecimalPlaces, label, units, columns );
        _viscosityControl.setTextFieldEditable( true );
        
        value = temperatureRange.getDefault();
        min = temperatureRange.getMin();
        max = temperatureRange.getMax();
        tickSpacing = max-min; //XXX
        tickDecimalPlaces = 0; //XXX
        valueDecimalPlaces = temperatureRange.getSignificantDecimalPlaces();
        label = SimStrings.get( "label.fluidTemperature" );
        units = SimStrings.get( "units.fluidTemperature" );
        columns = 4; //XXX
        _temperatureControl = new SliderControl( value, min, max, tickSpacing, tickDecimalPlaces, valueDecimalPlaces, label, units, columns );
        _temperatureControl.setTextFieldEditable( true );
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( _speedControl, row++, column );
        layout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
        layout.addComponent( _viscosityControl, row++, column );
        layout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
        layout.addComponent( _temperatureControl, row++, column );
        
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
    
    public SliderControl getSpeedControl() {
        return _speedControl;
    }

    public SliderControl getViscosityControl() {
        return _viscosityControl;
    }
    
    public SliderControl getTemperatureControl() {
        return _temperatureControl;
    }
    
    private void handleSpeedChange() {
        System.out.println( "FluidControlPanel.handleSpeedChange " + _speedControl.getValue() );//XXX
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
