/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.*;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.ILayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.Climate;
import edu.colorado.phet.glaciers.model.Climate.ClimateListener;
import edu.colorado.phet.glaciers.util.UnitsConverter;
import edu.colorado.phet.glaciers.view.UnitsChangeListener;

/**
 * ClimateControlPanel contains climate controls, switchable between English and metric units.
 * Units switching is handled by having 2 panels of controls (one English, one metric) and
 * and making the appropriate panel visible using CardLayout.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ClimateControlPanel extends AbstractSubPanel implements UnitsChangeListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color BACKGROUND_COLOR = GlaciersConstants.SUBPANEL_BACKGROUND_COLOR;
    private static final String TITLE_STRING = GlaciersStrings.TITLE_CLIMATE_CONTROLS;
    private static final Color TITLE_COLOR = GlaciersConstants.SUBPANEL_TITLE_COLOR;
    private static final Font TITLE_FONT = GlaciersConstants.SUBPANEL_TITLE_FONT;
    private static final Color CONTROL_COLOR = GlaciersConstants.SUBPANEL_CONTROL_COLOR;
    private static final Font CONTROL_FONT = GlaciersConstants.SUBPANEL_CONTROL_FONT;
    
    private static final DoubleRange METRIC_TEMPERATURE_RANGE = GlaciersConstants.TEMPERATURE_RANGE;
    private static final DoubleRange ENGLISH_TEMPERATURE_RANGE = new DoubleRange( 
            UnitsConverter.celsiusToFahrenheit( METRIC_TEMPERATURE_RANGE.getMin() ),
            UnitsConverter.celsiusToFahrenheit( METRIC_TEMPERATURE_RANGE.getMax() ) );
    
    private static final DoubleRange METRIC_SNOWFALL_RANGE = GlaciersConstants.SNOWFALL_RANGE;
    private static final DoubleRange ENGLISH_SNOWFALL_RANGE = new DoubleRange( 
            UnitsConverter.metersToFeet( METRIC_SNOWFALL_RANGE.getMin() ),
            UnitsConverter.metersToFeet( METRIC_SNOWFALL_RANGE.getMax() ) );
    
    private static final String ENGLISH_CARD_NAME = "english";
    private static final String METRIC_CARD_NAME = "metric";

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final AbstractClimateControls _englishClimateControls, _metricClimateControls;
    private final CardLayout _cardLayout;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ClimateControlPanel( Climate climate, boolean englishUnits ) {
        super( TITLE_STRING, TITLE_COLOR, TITLE_FONT );
        
        _englishClimateControls = new EnglishClimateControls( climate );
        _metricClimateControls = new MetricClimateControls( climate );
        
        _cardLayout = new CardLayout();
        setLayout( _cardLayout );
        add( _englishClimateControls, ENGLISH_CARD_NAME );
        add( _metricClimateControls, METRIC_CARD_NAME );
        
        Class[] excludedClasses = { JTextField.class };
        SwingUtils.setBackgroundDeep( this, BACKGROUND_COLOR, excludedClasses, false /* processContentsOfExcludedContainers */ );
        
        unitsChanged( englishUnits );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Set the snowfall.
     * @param snowfall in meters
     */
    public void setSnowfall( double snowfall ) {
        _englishClimateControls.setSnowfall( UnitsConverter.metersToFeet( snowfall ) );
        _metricClimateControls.setSnowfall( snowfall );
    }
    
    /**
     * Gets the snowfall
     * @return snowfall in meters
     */
    public double getSnowfall() {
        return _metricClimateControls.getSnowfall();
    }
    
    /**
     * Sets the temperature.
     * @param temperature in degrees Celsius
     */
    public void setTemperature( double temperature ) {
        _englishClimateControls.setTemperature( UnitsConverter.celsiusToFahrenheit( temperature ) );
        _metricClimateControls.setTemperature( temperature );
    }
    
    /**
     * Gets the temperature.
     * @return temperature in degrees Celsius
     */
    public double getTemperature() {
        return _metricClimateControls.getTemperature();
    }
    
    //----------------------------------------------------------------------------
    // UnitsChangeListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Switches between English and metric "cards" when the units are changed.
     */
    public void unitsChanged( boolean englishUnits ) {
        if ( englishUnits ) {
            _cardLayout.show( this, ENGLISH_CARD_NAME );
        }
        else {
            _cardLayout.show( this, METRIC_CARD_NAME );
        }
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * Climate controls in English units.
     * The model is in metric units, so this class must perform conversions.
     */
    private static class EnglishClimateControls extends AbstractClimateControls {
        
        public EnglishClimateControls( final Climate climate ) {
           super( ENGLISH_TEMPERATURE_RANGE, GlaciersStrings.UNITS_FAHRENHEIT, ENGLISH_SNOWFALL_RANGE, GlaciersStrings.UNITS_FEET ); 
           
           // update controls to match model, model is in metric units
           climate.addClimateListener( new ClimateListener() {

               public void snowfallChanged() {
                   setSnowfall( UnitsConverter.metersToFeet( climate.getSnowfall() ) );
               }

               public void temperatureChanged() {
                   setTemperature( UnitsConverter.celsiusToFahrenheit( climate.getTemperature()) );
               }
           } );
           
           // update model to match controls, model is in metric units
           addClimateControlsChangeListener( new ClimateControlsChangeListener() {

                public void temperatureChanged( double newTemperature ) {
                    climate.setTemperature( UnitsConverter.fahrenheitToCelsius( getTemperature() ) );
                }

                public void snowfallChanged( double newSnowfall ) {
                    climate.setSnowfall( UnitsConverter.feetToMeters( getSnowfall() ) );
                }

            } );
        }
    }
    
    /*
     * Climate controls in metric units.
     * The model is also in metric units, so no conversions are required by this class.
     */
    private static class MetricClimateControls extends AbstractClimateControls {

        public MetricClimateControls( final Climate climate ) {
            super( METRIC_TEMPERATURE_RANGE, GlaciersStrings.UNITS_CELSIUS, METRIC_SNOWFALL_RANGE, GlaciersStrings.UNITS_METERS );

            // update controls to match model, model is in metric units
            climate.addClimateListener( new ClimateListener() {

                public void snowfallChanged() {
                    setSnowfall( climate.getSnowfall() );
                }

                public void temperatureChanged() {
                    setTemperature( climate.getTemperature() );
                }
            } );
            
            // update model to match controls, model is in metric units
            addClimateControlsChangeListener( new ClimateControlsChangeListener() {

                public void temperatureChanged( double newTemperature ) {
                    climate.setTemperature( getTemperature() );
                }

                public void snowfallChanged( double newSnowfall ) {
                    climate.setSnowfall( getSnowfall() );
                }

            } );
        }
    }
    
    /*
     * Base class, climates controls that are independent of units and model.
     */
    private static abstract class AbstractClimateControls extends JPanel {
        
        private final LinearValueControl _temperatureControl;
        private final LinearValueControl _snowfallControl;
        private final ArrayList _listeners;
        
        public AbstractClimateControls( DoubleRange temperatureRange, String temperatureUnits, DoubleRange snowfallRange, String snowfallUnits ) {
            super();
            
            _listeners = new ArrayList();
            
            // temperature
            JLabel temperatureLabel = new JLabel( GlaciersStrings.SLIDER_TEMPERATURE );
            {
                double min = temperatureRange.getMin();
                double max = temperatureRange.getMax();
                String label = "";
                String textfieldPattern = "#0.00";
                ILayoutStrategy layout = new HorizontalLayoutStrategy();
                _temperatureControl = new LinearValueControl( min, max, label, textfieldPattern, temperatureUnits, layout );
                _temperatureControl.setUpDownArrowDelta( 0.01 );
                _temperatureControl.addChangeListener( new ChangeListener() { 
                    public void stateChanged( ChangeEvent event ) {
                        if ( GlaciersConstants.UPDATE_WHILE_DRAGGING_SLIDERS || !_temperatureControl.isAdjusting() ) {
                            notifyTemperatureChanged();
                        }
                    }
                } );
                
                // fonts & colors
                temperatureLabel.setForeground( CONTROL_COLOR );
                temperatureLabel.setFont( CONTROL_FONT );
                _temperatureControl.setFont( CONTROL_FONT );
                _temperatureControl.getUnitsLabel().setForeground( CONTROL_COLOR );
                Dictionary d = _temperatureControl.getSlider().getLabelTable();
                Enumeration e = d.elements();
                while ( e.hasMoreElements() ) {
                    Object o = e.nextElement();
                    if ( o instanceof JComponent )
                        ( (JComponent) o ).setForeground( CONTROL_COLOR );
                        ( (JComponent) o ).setFont( CONTROL_FONT );
                }
            }
            
            // snowfall
            JLabel snowfallLabel = new JLabel( GlaciersStrings.SLIDER_SNOWFALL );        
            {
                double min = snowfallRange.getMin();
                double max = snowfallRange.getMax();
                String label = "";
                String textfieldPattern = "#0.00";
                ILayoutStrategy layout = new HorizontalLayoutStrategy();
                _snowfallControl = new LinearValueControl( min, max, label, textfieldPattern, snowfallUnits, layout );
                _snowfallControl.setUpDownArrowDelta( 0.01 );
                _snowfallControl.addChangeListener( new ChangeListener() { 
                    public void stateChanged( ChangeEvent event ) {
                        if ( GlaciersConstants.UPDATE_WHILE_DRAGGING_SLIDERS || !_snowfallControl.isAdjusting() ) {
                           notifySnowfallChanged();
                        }
                    }
                } );
                
                // fonts & colors
                snowfallLabel.setForeground( CONTROL_COLOR );
                snowfallLabel.setFont( CONTROL_FONT );
                _snowfallControl.setFont( CONTROL_FONT );
                _snowfallControl.getUnitsLabel().setForeground( CONTROL_COLOR );
                Dictionary d = _snowfallControl.getSlider().getLabelTable();
                Enumeration e = d.elements();
                while ( e.hasMoreElements() ) {
                    Object o = e.nextElement();
                    if ( o instanceof JComponent )
                        ( (JComponent) o ).setForeground( CONTROL_COLOR );
                        ( (JComponent) o ).setFont( CONTROL_FONT );
                }
            }
            
            EasyGridBagLayout layout = new EasyGridBagLayout( this );
            layout.setInsets( new Insets( 0, 2, 0, 2 ) ); // top, left, bottom, right
            setLayout( layout );
            int row = 0;
            int column = 0;
            layout.addAnchoredComponent( temperatureLabel, row, column++, GridBagConstraints.EAST );
            layout.addAnchoredComponent( _temperatureControl, row++, column, GridBagConstraints.WEST );
            column = 0;
            layout.addAnchoredComponent( snowfallLabel, row, column++, GridBagConstraints.EAST );
            layout.addAnchoredComponent( _snowfallControl, row++, column, GridBagConstraints.WEST );
        }
        
        //----------------------------------------------------------------------------
        // Setters and getters
        //----------------------------------------------------------------------------
       
        /*
         * Sets the snowfall. 
         * Units should be interpreted based on the snowfallUnits constructor arg.
         */
        public void setSnowfall( double snowfall ) {
            if ( snowfall != getSnowfall() ) {
                _snowfallControl.setValue( snowfall );
            }
        }

        /*
         * Gets the snowfall. 
         * Units should be interpreted based on the snowfallUnits constructor arg.
         */
        public double getSnowfall() {
            return _snowfallControl.getValue();
        }

        /*
         * Sets the temperature. 
         * Units should be interpreted based on the temperatureUnits constructor arg.
         */
        public void setTemperature( double temperature ) {
            if ( temperature != _temperatureControl.getValue() ) {
                _temperatureControl.setValue( temperature );
            }
        }

        /*
         * Gets the temperature. 
         * Units should be interpreted based on the temperatureUnits constructor arg.
         */
        public double getTemperature() {
            return _temperatureControl.getValue();
        }
        
        //----------------------------------------------------------------------------
        // Listener interface
        //----------------------------------------------------------------------------
        
        public interface ClimateControlsChangeListener {
            public void temperatureChanged( double newTemperature );
            public void snowfallChanged( double newSnowfall );
        }
        
        public void addClimateControlsChangeListener( ClimateControlsChangeListener listener ) {
            _listeners.add( listener );
        }
        
        public void removeClimateControlsChangeListener( ClimateControlsChangeListener listener ) {
            _listeners.remove( listener );
        }
        
        private void notifyTemperatureChanged() {
            Iterator i = _listeners.iterator();
            while ( i.hasNext() ) {
                ( ( ClimateControlsChangeListener) i.next() ).temperatureChanged( getTemperature() );
            }
        }
        
        private void notifySnowfallChanged() {
            Iterator i = _listeners.iterator();
            while ( i.hasNext() ) {
                ( ( ClimateControlsChangeListener) i.next() ).snowfallChanged( getSnowfall() );
            }
        }
    }
    

}
