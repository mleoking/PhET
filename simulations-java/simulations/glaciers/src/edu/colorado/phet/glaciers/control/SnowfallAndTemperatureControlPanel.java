/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.util.*;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.GlaciersStrings;

/**
 * SnowfallAndTemperatureControlPanel is the control panel for controlling climate
 * using a simple "snowfall & temperature" model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SnowfallAndTemperatureControlPanel extends JPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean UPDATE_WHILE_DRAGGING_SLIDERS = false;
    
    private static final Color CONTROL_COLOR = GlaciersConstants.INNER_PANEL_CONTROL_COLOR;
    private static final Font CONTROL_FONT = GlaciersConstants.CONTROL_PANEL_CONTROL_FONT;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private LinearValueControl _snowfallControl;
    private LinearValueControl _temperatureControl;
    
    private ArrayList _listeners; // list of SnowfallAndTemperatureControlPanelListener
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public SnowfallAndTemperatureControlPanel( DoubleRange snowfallRange, DoubleRange temperatureRange ) {
        super();
        
        _listeners = new ArrayList();
        
        // snowfall
        JLabel snowfallLabel = new JLabel( GlaciersStrings.SLIDER_SNOWFALL );        
        snowfallLabel.setForeground( CONTROL_COLOR );
        snowfallLabel.setFont( CONTROL_FONT );
        {
            double min = snowfallRange.getMin();
            double max = snowfallRange.getMax();
            _snowfallControl = new LinearValueControl( min, max, "", "", "", new SliderOnlyLayoutStrategy() );
            _snowfallControl.setFont( CONTROL_FONT );
            _snowfallControl.addChangeListener( new ChangeListener() { 
                public void stateChanged( ChangeEvent event ) {
                    if ( UPDATE_WHILE_DRAGGING_SLIDERS || !_snowfallControl.isAdjusting() ) {
                        notifySnowfallChanged();
                    }
                }
            } );
            
            // Tick labels
            Hashtable labelTable = new Hashtable();
            labelTable.put( new Double( min ), new JLabel( GlaciersStrings.SLIDER_SNOWFALL_LITTLE ) );
            labelTable.put( new Double( max ), new JLabel( GlaciersStrings.SLIDER_SNOWFALL_LOTS ) );
            _snowfallControl.setTickLabels( labelTable );
            
            // Change the font and color of the tick labels
            Dictionary d = _snowfallControl.getSlider().getLabelTable();
            Enumeration e = d.elements();
            while ( e.hasMoreElements() ) {
                Object o = e.nextElement();
                if ( o instanceof JComponent )
                    ( (JComponent) o ).setForeground( CONTROL_COLOR );
                    ( (JComponent) o ).setFont( CONTROL_FONT );
            }
        }
        
        // temperature
        JLabel temperatureLabel = new JLabel( GlaciersStrings.SLIDER_TEMPERATURE );
        temperatureLabel.setForeground( CONTROL_COLOR );
        temperatureLabel.setFont( CONTROL_FONT );
        {
            double min = temperatureRange.getMin();
            double max = temperatureRange.getMax();
            _temperatureControl = new LinearValueControl( min, max, "", "", "", new SliderOnlyLayoutStrategy() );
            _temperatureControl.setFont( CONTROL_FONT );
            _temperatureControl.addChangeListener( new ChangeListener() { 
                public void stateChanged( ChangeEvent event ) {
                    if ( UPDATE_WHILE_DRAGGING_SLIDERS || !_temperatureControl.isAdjusting() ) {
                        notifyTemperatureChanged();
                    }
                }
            } );
            
            // Tick labels
            Hashtable labelTable = new Hashtable();
            labelTable.put( new Double( min ), new JLabel( GlaciersStrings.SLIDER_TEMPERATURE_COLD ) );
            labelTable.put( new Double( max ), new JLabel( GlaciersStrings.SLIDER_TEMPERATURE_HOT ) );
            _temperatureControl.setTickLabels( labelTable );
            
            // Change the font and color of the tick labels
            Dictionary d = _temperatureControl.getSlider().getLabelTable();
            Enumeration e = d.elements();
            while ( e.hasMoreElements() ) {
                Object o = e.nextElement();
                if ( o instanceof JComponent )
                    ( (JComponent) o ).setForeground( CONTROL_COLOR );
                    ( (JComponent) o ).setFont( CONTROL_FONT );
            }
        }
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addAnchoredComponent( snowfallLabel, row, column++, GridBagConstraints.EAST );
        layout.addAnchoredComponent( _snowfallControl, row++, column, GridBagConstraints.WEST );
        column = 0;
        layout.addAnchoredComponent( temperatureLabel, row, column++, GridBagConstraints.EAST );
        layout.addAnchoredComponent( _temperatureControl, row++, column, GridBagConstraints.WEST );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public double getSnowfall() {
        return _snowfallControl.getMaximum() - _snowfallControl.getValue(); // value is inverted!
    }
    
    public void setSnowfall( double snowfall ) {
        if ( snowfall != getSnowfall() ) {
            _snowfallControl.setValue( _snowfallControl.getMaximum() - snowfall ); // value is inverted!
        }
    }
    
    public double getTemperature() {
        return _temperatureControl.getValue();
    }
    
    public void setTemperature( double temperature ) {
        if ( temperature != getTemperature() ) {
            _temperatureControl.setValue( temperature );
        }
    }
    
    //----------------------------------------------------------------------------
    // Listeners
    //----------------------------------------------------------------------------
    
    /**
     * Interface implemented by all listeners who are interested in events related to this control panel.
     */
    public interface SnowfallAndTemperatureControlPanelListener {
        public void snowfallChanged( double snowfall );
        public void temperatureChanged( double temperature );
    }
    
    public static class SnowfallAndTemperatureControlPanelAdapter implements SnowfallAndTemperatureControlPanelListener {
        public void snowfallChanged( double snowfall ) {};
        public void temperatureChanged( double temperature ) {};
    }
    
    public void addBasicClimateControlPanelListener( SnowfallAndTemperatureControlPanelListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeBasicClimateControlPanelListener( SnowfallAndTemperatureControlPanelListener listener ) {
        _listeners.remove( listener );
    }
    
    //----------------------------------------------------------------------------
    // Notification
    //----------------------------------------------------------------------------
    
    private void notifySnowfallChanged() {
        double value = getSnowfall();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (SnowfallAndTemperatureControlPanelListener) i.next() ).snowfallChanged( value );
        }
    }
    
    private void notifyTemperatureChanged() {
        double value = getTemperature();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (SnowfallAndTemperatureControlPanelListener) i.next() ).temperatureChanged( value );
        }
    }
}
