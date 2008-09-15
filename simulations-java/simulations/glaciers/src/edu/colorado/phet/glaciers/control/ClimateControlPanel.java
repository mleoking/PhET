/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.*;
import java.util.Dictionary;
import java.util.Enumeration;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
    
    private static final String ENGLISH_CARD_NAME = "english";
    private static final String METRIC_CARD_NAME = "metric";

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Climate _climate;
    
    private final LinearValueControl _temperatureControlMetric;
    private final LinearValueControl _snowfallControlMetric;
    private final LinearValueControl _temperatureControlEnglish;
    private final LinearValueControl _snowfallControlEnglish;
    private final CardLayout _cardLayout;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ClimateControlPanel( Climate climate, boolean englishUnits ) {
        super( TITLE_STRING, TITLE_COLOR, TITLE_FONT );
        
        _climate = climate;
        _climate.addClimateListener( new ClimateListener() {

            public void snowfallChanged() {
                setSnowfall( _climate.getSnowfall() );
            }

            public void temperatureChanged() {
                setTemperature( _climate.getTemperature() );
            }
        } );
        
        // temperature (metric)
        JLabel temperatureLabelMetric = new JLabel( GlaciersStrings.SLIDER_TEMPERATURE );
        {
            double min = GlaciersConstants.TEMPERATURE_RANGE.getMin();
            double max = GlaciersConstants.TEMPERATURE_RANGE.getMax();
            String label = "";
            String textfieldPattern = "#0.00";
            String units = GlaciersStrings.UNITS_CELSIUS;
            ILayoutStrategy layout = new HorizontalLayoutStrategy();
            _temperatureControlMetric = new LinearValueControl( min, max, label, textfieldPattern, units, layout );
            _temperatureControlMetric.setUpDownArrowDelta( 0.01 );
            _temperatureControlMetric.addChangeListener( new ChangeListener() { 
                public void stateChanged( ChangeEvent event ) {
                    if ( GlaciersConstants.UPDATE_WHILE_DRAGGING_SLIDERS || !_temperatureControlMetric.isAdjusting() ) {
                        _climate.setTemperature( _temperatureControlMetric.getValue() );
                    }
                }
            } );
            
            // fonts & colors
            temperatureLabelMetric.setForeground( CONTROL_COLOR );
            temperatureLabelMetric.setFont( CONTROL_FONT );
            _temperatureControlMetric.setFont( CONTROL_FONT );
            _temperatureControlMetric.getUnitsLabel().setForeground( CONTROL_COLOR );
            Dictionary d = _temperatureControlMetric.getSlider().getLabelTable();
            Enumeration e = d.elements();
            while ( e.hasMoreElements() ) {
                Object o = e.nextElement();
                if ( o instanceof JComponent )
                    ( (JComponent) o ).setForeground( CONTROL_COLOR );
                    ( (JComponent) o ).setFont( CONTROL_FONT );
            }
        }
        
        // temperature (English)
        JLabel temperatureLabelEnglish = new JLabel( GlaciersStrings.SLIDER_TEMPERATURE );
        {
            double min = UnitsConverter.celsiusToFahrenheit( GlaciersConstants.TEMPERATURE_RANGE.getMin() );
            double max = UnitsConverter.celsiusToFahrenheit( GlaciersConstants.TEMPERATURE_RANGE.getMax() );
            String label = "";
            String textfieldPattern = "#0.00";
            String units = GlaciersStrings.UNITS_FAHRENHEIT;
            ILayoutStrategy layout = new HorizontalLayoutStrategy();
            _temperatureControlEnglish = new LinearValueControl( min, max, label, textfieldPattern, units, layout );
            _temperatureControlEnglish.setUpDownArrowDelta( 0.01 );
            _temperatureControlEnglish.addChangeListener( new ChangeListener() { 
                public void stateChanged( ChangeEvent event ) {
                    if ( GlaciersConstants.UPDATE_WHILE_DRAGGING_SLIDERS || !_temperatureControlMetric.isAdjusting() ) {
                        _climate.setTemperature( UnitsConverter.fahrenheitToCelsius( _temperatureControlEnglish.getValue() ) );
                    }
                }
            } );
            
            // fonts & colors
            temperatureLabelEnglish.setForeground( CONTROL_COLOR );
            temperatureLabelEnglish.setFont( CONTROL_FONT );
            _temperatureControlEnglish.setFont( CONTROL_FONT );
            _temperatureControlEnglish.getUnitsLabel().setForeground( CONTROL_COLOR );
            Dictionary d = _temperatureControlEnglish.getSlider().getLabelTable();
            Enumeration e = d.elements();
            while ( e.hasMoreElements() ) {
                Object o = e.nextElement();
                if ( o instanceof JComponent )
                    ( (JComponent) o ).setForeground( CONTROL_COLOR );
                    ( (JComponent) o ).setFont( CONTROL_FONT );
            }
        }
        
        // snowfall (metric)
        JLabel snowfallLabelMetric = new JLabel( GlaciersStrings.SLIDER_SNOWFALL );        
        {
            double min = GlaciersConstants.SNOWFALL_RANGE.getMin();
            double max = GlaciersConstants.SNOWFALL_RANGE.getMax();
            String label = "";
            String textfieldPattern = "#0.00";
            String units = GlaciersStrings.UNITS_METERS;
            ILayoutStrategy layout = new HorizontalLayoutStrategy();
            _snowfallControlMetric = new LinearValueControl( min, max, label, textfieldPattern, units, layout );
            _snowfallControlMetric.setUpDownArrowDelta( 0.01 );
            _snowfallControlMetric.addChangeListener( new ChangeListener() { 
                public void stateChanged( ChangeEvent event ) {
                    if ( GlaciersConstants.UPDATE_WHILE_DRAGGING_SLIDERS || !_snowfallControlMetric.isAdjusting() ) {
                        _climate.setSnowfall( _snowfallControlMetric.getValue() );
                    }
                }
            } );
            
            // fonts & colors
            snowfallLabelMetric.setForeground( CONTROL_COLOR );
            snowfallLabelMetric.setFont( CONTROL_FONT );
            _snowfallControlMetric.setFont( CONTROL_FONT );
            _snowfallControlMetric.getUnitsLabel().setForeground( CONTROL_COLOR );
            Dictionary d = _snowfallControlMetric.getSlider().getLabelTable();
            Enumeration e = d.elements();
            while ( e.hasMoreElements() ) {
                Object o = e.nextElement();
                if ( o instanceof JComponent )
                    ( (JComponent) o ).setForeground( CONTROL_COLOR );
                    ( (JComponent) o ).setFont( CONTROL_FONT );
            }
        }
        
        // snowfall (English)
        JLabel snowfallLabelEnglish = new JLabel( GlaciersStrings.SLIDER_SNOWFALL );
        {
            double min = UnitsConverter.metersToFeet( GlaciersConstants.SNOWFALL_RANGE.getMin() );
            double max = UnitsConverter.metersToFeet( GlaciersConstants.SNOWFALL_RANGE.getMax() );
            String label = "";
            String textfieldPattern = "#0.00";
            String units = GlaciersStrings.UNITS_FEET;
            ILayoutStrategy layout = new HorizontalLayoutStrategy();
            _snowfallControlEnglish = new LinearValueControl( min, max, label, textfieldPattern, units, layout );
            _snowfallControlEnglish.setUpDownArrowDelta( 0.01 );
            _snowfallControlEnglish.addChangeListener( new ChangeListener() { 
                public void stateChanged( ChangeEvent event ) {
                    if ( GlaciersConstants.UPDATE_WHILE_DRAGGING_SLIDERS || !_snowfallControlMetric.isAdjusting() ) {
                        _climate.setSnowfall( UnitsConverter.feetToMeters( _snowfallControlEnglish.getValue() ) );
                    }
                }
            } );
            
            // fonts & colors
            snowfallLabelEnglish.setForeground( CONTROL_COLOR );
            snowfallLabelEnglish.setFont( CONTROL_FONT );
            _snowfallControlEnglish.setFont( CONTROL_FONT );
            _snowfallControlEnglish.getUnitsLabel().setForeground( CONTROL_COLOR );
            Dictionary d = _snowfallControlEnglish.getSlider().getLabelTable();
            Enumeration e = d.elements();
            while ( e.hasMoreElements() ) {
                Object o = e.nextElement();
                if ( o instanceof JComponent )
                    ( (JComponent) o ).setForeground( CONTROL_COLOR );
                    ( (JComponent) o ).setFont( CONTROL_FONT );
            }
        }
        
        JPanel englishPanel = new JPanel();
        EasyGridBagLayout englishLayout = new EasyGridBagLayout( englishPanel );
        englishLayout.setInsets( new Insets( 0, 2, 0, 2 ) ); // top, left, bottom, right
        englishPanel.setLayout( englishLayout );
        int row = 0;
        int column = 0;
        englishLayout.addAnchoredComponent( temperatureLabelEnglish, row, column++, GridBagConstraints.EAST );
        englishLayout.addAnchoredComponent( _temperatureControlEnglish, row++, column, GridBagConstraints.WEST );
        column = 0;
        englishLayout.addAnchoredComponent( snowfallLabelEnglish, row, column++, GridBagConstraints.EAST );
        englishLayout.addAnchoredComponent( _snowfallControlEnglish, row++, column, GridBagConstraints.WEST );
        
        JPanel metricPanel = new JPanel();
        EasyGridBagLayout metricLayout = new EasyGridBagLayout( metricPanel );
        metricLayout.setInsets( new Insets( 0, 2, 0, 2 ) ); // top, left, bottom, right
        metricPanel.setLayout( metricLayout );
        row = 0;
        column = 0;
        metricLayout.addAnchoredComponent( temperatureLabelMetric, row, column++, GridBagConstraints.EAST );
        metricLayout.addAnchoredComponent( _temperatureControlMetric, row++, column, GridBagConstraints.WEST );
        column = 0;
        metricLayout.addAnchoredComponent( snowfallLabelMetric, row, column++, GridBagConstraints.EAST );
        metricLayout.addAnchoredComponent( _snowfallControlMetric, row++, column, GridBagConstraints.WEST );
        
        _cardLayout = new CardLayout();
        setLayout( _cardLayout );
        add( englishPanel, ENGLISH_CARD_NAME );
        add( metricPanel, METRIC_CARD_NAME );
        
        Class[] excludedClasses = { JTextField.class };
        SwingUtils.setBackgroundDeep( this, BACKGROUND_COLOR, excludedClasses, false /* processContentsOfExcludedContainers */ );
        
        unitsChanged( englishUnits );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setSnowfall( double snowfall ) {
        if ( snowfall != _snowfallControlMetric.getValue() ) {
            _snowfallControlMetric.setValue( snowfall );
        }
        if ( snowfall != _snowfallControlEnglish.getValue() ) {
            _snowfallControlEnglish.setValue( UnitsConverter.metersToFeet( snowfall ) );
        }
    }

    /**
     * Gets the snowfall in meters.
     * @return
     */
    public double getSnowfall() {
        return _snowfallControlMetric.getValue();
    }

    public void setTemperature( double temperature ) {
        if ( temperature != _temperatureControlMetric.getValue() ) {
            _temperatureControlMetric.setValue( temperature );
        }
        if ( temperature != _temperatureControlEnglish.getValue() ) {
            _temperatureControlEnglish.setValue( UnitsConverter.celsiusToFahrenheit( temperature ) );
        }
    }

    /**
     * Gets the temperature in degrees Fahrenheit.
     * @return
     */
    public double getTemperature() {
        return _temperatureControlMetric.getValue();
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
}
