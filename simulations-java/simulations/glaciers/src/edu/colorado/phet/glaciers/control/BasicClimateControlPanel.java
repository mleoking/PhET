/* Copyright 2007, University of Colorado */

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
import edu.colorado.phet.glaciers.GlaciersResources;

/**
 * BasicClimateControlPanel is the climate control panel for the "Basic" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BasicClimateControlPanel extends JPanel {

    private static final Color BACKGROUND_COLOR = new Color( 82, 126, 90 ); // green
    private static final Color TITLE_COLOR = Color.WHITE;
    private static final Color CONTROL_COLOR = Color.WHITE;
    
    private LinearValueControl _snowfallControl;
    private LinearValueControl _temperatureControl;
    
    private ArrayList _listenerList;
    
    public BasicClimateControlPanel( Font titleFont, Font controlFont, DoubleRange snowfallRange, DoubleRange temperatureRange ) {
        super();
        
        _listenerList = new ArrayList();
        
        // snowfall
        JLabel snowfallLabel = new JLabel( GlaciersResources.getString( "slider.snowfall.label" ) );        
        snowfallLabel.setForeground( CONTROL_COLOR );
        snowfallLabel.setFont( controlFont );
        {
            double min = snowfallRange.getMin();
            double max = snowfallRange.getMax();
            _snowfallControl = new LinearValueControl( min, max, "", "", "", new SliderOnlyLayoutStrategy() );
            _snowfallControl.setFont( controlFont );
            _snowfallControl.addChangeListener( new ChangeListener() { 
                public void stateChanged( ChangeEvent event ) {
                    notifySnowfallChanged();
                }
            } );
            
            // Tick labels
            String minString = GlaciersResources.getString( "slider.snowfall.none" );
            String maxString = GlaciersResources.getString( "slider.snowfall.lots" );
            Hashtable labelTable = new Hashtable();
            labelTable.put( new Double( min ), new JLabel( minString ) );
            labelTable.put( new Double( max ), new JLabel( maxString ) );
            _snowfallControl.setTickLabels( labelTable );
            
            // Change the font and color of the tick labels
            Dictionary d = _snowfallControl.getSlider().getLabelTable();
            Enumeration e = d.elements();
            while ( e.hasMoreElements() ) {
                Object o = e.nextElement();
                if ( o instanceof JComponent )
                    ( (JComponent) o ).setForeground( CONTROL_COLOR );
                    ( (JComponent) o ).setFont( controlFont );
            }
        }
        
        // temperature
        JLabel temperatureLabel = new JLabel( GlaciersResources.getString( "slider.temperature.label" ) );
        temperatureLabel.setForeground( CONTROL_COLOR );
        temperatureLabel.setFont( controlFont );
        {
            double min = temperatureRange.getMin();
            double max = temperatureRange.getMax();
            _temperatureControl = new LinearValueControl( min, max, "", "", "", new SliderOnlyLayoutStrategy() );
            _temperatureControl.setFont( controlFont );
            _temperatureControl.addChangeListener( new ChangeListener() { 
                public void stateChanged( ChangeEvent event ) {
                    notifyTemperatureChanged();
                }
            } );
            
            // Tick labels
            String minString = GlaciersResources.getString( "slider.temperature.cold" );
            String maxString = GlaciersResources.getString( "slider.temperature.hot" );
            Hashtable labelTable = new Hashtable();
            labelTable.put( new Double( min ), new JLabel( minString ) );
            labelTable.put( new Double( max ), new JLabel( maxString ) );
            _temperatureControl.setTickLabels( labelTable );
            
            // Change the font and color of the tick labels
            Dictionary d = _temperatureControl.getSlider().getLabelTable();
            Enumeration e = d.elements();
            while ( e.hasMoreElements() ) {
                Object o = e.nextElement();
                if ( o instanceof JComponent )
                    ( (JComponent) o ).setForeground( CONTROL_COLOR );
                    ( (JComponent) o ).setFont( controlFont );
            }
        }
        
        Border emptyBorder = BorderFactory.createEmptyBorder( 3, 3, 3, 3 );
        TitledBorder titledBorder = new TitledBorder( GlaciersResources.getString( "title.climateControls" ) );
        titledBorder.setTitleFont( titleFont );
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
    
    /**
     * Interface implemented by all listeners who are interested in changes to this control panel.
     */
    public static interface BasicClimateControlPanelListener {
        public void snowfallChanged( double snowfall );
        public void temperatureChanged( double temperature );
    }
    
    /**
     * Default implementation of BasicClimateControlPanelListener.
     */
    public static class BasicClimateControlPanelAdapter {
        public void snowfallChanged( double snowfall ) {};
        public void temperatureChanged( double temperature ) {};
    }
    
    /**
     * Adds a BasicClimateControlPanelListener.
     * @param listener
     */
    public void addListener( BasicClimateControlPanelListener listener ) {
        _listenerList.add( listener );
    }
    
    /**
     * Removes a BasicClimateControlPanelListener.
     * @param listener
     */
    public void removeListener( BasicClimateControlPanelListener listener ) {
        _listenerList.remove( listener );
    }
    
    private void notifySnowfallChanged() {
        double value = getSnowfall();
        Iterator i = _listenerList.iterator();
        while ( i.hasNext() ) {
            ( (BasicClimateControlPanelListener) i.next() ).snowfallChanged( value );
        }
    }
    
    private void notifyTemperatureChanged() {
        double value = getTemperature();
        Iterator i = _listenerList.iterator();
        while ( i.hasNext() ) {
            ( (BasicClimateControlPanelListener) i.next() ).temperatureChanged( value );
        }
    }
}
