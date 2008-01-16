/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.util.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.GlaciersStrings;

/**
 * BasicClimateControlPanel is the climate control panel for the "Basic" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BasicClimateControlPanel extends JPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color BACKGROUND_COLOR = GlaciersConstants.INNER_PANEL_BACKGROUND_COLOR;
    private static final Color TITLE_COLOR = GlaciersConstants.INNER_PANEL_TITLE_COLOR;
    private static final Color CONTROL_COLOR = GlaciersConstants.INNER_PANEL_CONTROL_COLOR;
    private static final Font TITLE_FONT = GlaciersConstants.CONTROL_PANEL_TITLE_FONT;
    private static final Font CONTROL_FONT = GlaciersConstants.CONTROL_PANEL_CONTROL_FONT;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private LinearValueControl _snowfallControl;
    private LinearValueControl _temperatureControl;
    
    private ArrayList _listeners; // list of BasicClimateControlPanelListener
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BasicClimateControlPanel( DoubleRange snowfallRange, DoubleRange temperatureRange ) {
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
                    notifySnowfallChanged();
                }
            } );
            
            // Tick labels
            Hashtable labelTable = new Hashtable();
            labelTable.put( new Double( min ), new JLabel( GlaciersStrings.SLIDER_SNOWFALL_NONE ) );
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
                    notifyTemperatureChanged();
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
        
        Border emptyBorder = BorderFactory.createEmptyBorder( 3, 3, 3, 3 );
        TitledBorder titledBorder = new TitledBorder( GlaciersStrings.TITLE_CLIMATE_CONTROLS );
        titledBorder.setTitleFont( TITLE_FONT );
        titledBorder.setTitleColor( TITLE_COLOR );
        titledBorder.setBorder( BorderFactory.createLineBorder( TITLE_COLOR, 1 ) );
        Border compoundBorder = BorderFactory.createCompoundBorder( emptyBorder, titledBorder );
        setBorder( compoundBorder );
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addAnchoredComponent( snowfallLabel, row, column++, GridBagConstraints.EAST );
        layout.addAnchoredComponent( _snowfallControl, row++, column, GridBagConstraints.WEST );
        column = 0;
        layout.addAnchoredComponent( temperatureLabel, row, column++, GridBagConstraints.EAST );
        layout.addAnchoredComponent( _temperatureControl, row++, column, GridBagConstraints.WEST );
        
        Class[] excludedClasses = { JSpinner.class };
        SwingUtils.setBackgroundDeep( this, BACKGROUND_COLOR, excludedClasses, false /* processContentsOfExcludedContainers */ );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public double getSnowfall() {
        return _snowfallControl.getValue();
    }
    
    public void setSnowfall( double snowfall ) {
        _snowfallControl.setValue( snowfall );
    }
    
    public double getTemperature() {
        return _temperatureControl.getValue();
    }
    
    public void setTemperture( double temperature ) {
        _temperatureControl.setValue( temperature );
    }
    
    //----------------------------------------------------------------------------
    // Listeners
    //----------------------------------------------------------------------------
    
    /**
     * Interface implemented by all listeners who are interested in events related to this control panel.
     */
    public interface BasicClimateControlPanelListener {
        public void snowfallChanged( double snowfall );
        public void temperatureChanged( double temperature );
    }
    
    public static class BasicClimateControlPanelAdapter implements BasicClimateControlPanelListener {
        public void snowfallChanged( double snowfall ) {};
        public void temperatureChanged( double temperature ) {};
    }
    
    public void addBasicClimateControlPanelListener( BasicClimateControlPanelListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeBasicClimateControlPanelListener( BasicClimateControlPanelListener listener ) {
        _listeners.remove( listener );
    }
    
    //----------------------------------------------------------------------------
    // Notification
    //----------------------------------------------------------------------------
    
    private void notifySnowfallChanged() {
        double value = getSnowfall();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (BasicClimateControlPanelListener) i.next() ).snowfallChanged( value );
        }
    }
    
    private void notifyTemperatureChanged() {
        double value = getTemperature();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (BasicClimateControlPanelListener) i.next() ).temperatureChanged( value );
        }
    }
}
