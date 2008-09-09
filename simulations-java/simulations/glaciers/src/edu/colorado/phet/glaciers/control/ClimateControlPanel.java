/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Dictionary;
import java.util.Enumeration;

import javax.swing.JComponent;
import javax.swing.JLabel;
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

/**
 * ClimateControlPanel contains climate controls.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ClimateControlPanel extends AbstractSubPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color BACKGROUND_COLOR = GlaciersConstants.SUBPANEL_BACKGROUND_COLOR;
    private static final String TITLE_STRING = GlaciersStrings.TITLE_CLIMATE_CONTROLS;
    private static final Color TITLE_COLOR = GlaciersConstants.SUBPANEL_TITLE_COLOR;
    private static final Font TITLE_FONT = GlaciersConstants.SUBPANEL_TITLE_FONT;
    private static final Color CONTROL_COLOR = GlaciersConstants.SUBPANEL_CONTROL_COLOR;
    private static final Font CONTROL_FONT = GlaciersConstants.SUBPANEL_CONTROL_FONT;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Climate _climate;
    private final LinearValueControl _temperatureControl;
    private final LinearValueControl _snowfallControl;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ClimateControlPanel( Climate climate ) {
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
        
        // temperature
        JLabel temperatureLabel = new JLabel( GlaciersStrings.SLIDER_TEMPERATURE );
        {
            double min = GlaciersConstants.TEMPERATURE_RANGE.getMin();
            double max = GlaciersConstants.TEMPERATURE_RANGE.getMax();
            String label = "";
            String textfieldPattern = "#0.00";
            String units = GlaciersStrings.UNITS_CELSIUS;
            ILayoutStrategy layout = new HorizontalLayoutStrategy();
            _temperatureControl = new LinearValueControl( min, max, label, textfieldPattern, units, layout );
            _temperatureControl.setUpDownArrowDelta( 0.01 );
            _temperatureControl.addChangeListener( new ChangeListener() { 
                public void stateChanged( ChangeEvent event ) {
                    if ( GlaciersConstants.UPDATE_WHILE_DRAGGING_SLIDERS || !_temperatureControl.isAdjusting() ) {
                        _climate.setTemperature( getTemperature() );
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
            double min = GlaciersConstants.SNOWFALL_RANGE.getMin();
            double max = GlaciersConstants.SNOWFALL_RANGE.getMax();
            String label = "";
            String textfieldPattern = "#0.00";
            String units = GlaciersStrings.UNITS_METERS;
            ILayoutStrategy layout = new HorizontalLayoutStrategy();
            _snowfallControl = new LinearValueControl( min, max, label, textfieldPattern, units, layout );
            _snowfallControl.setUpDownArrowDelta( 0.01 );
            _snowfallControl.addChangeListener( new ChangeListener() { 
                public void stateChanged( ChangeEvent event ) {
                    if ( GlaciersConstants.UPDATE_WHILE_DRAGGING_SLIDERS || !_snowfallControl.isAdjusting() ) {
                        _climate.setSnowfall( getSnowfall() );
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
        
        Class[] excludedClasses = { JTextField.class };
        SwingUtils.setBackgroundDeep( this, BACKGROUND_COLOR, excludedClasses, false /* processContentsOfExcludedContainers */ );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setSnowfall( double snowfall ) {
        if ( snowfall != getSnowfall() ) {
            _snowfallControl.setValue( snowfall );
        }
    }

    public double getSnowfall() {
        return _snowfallControl.getValue();
    }

    public void setTemperature( double temperature ) {
        if ( temperature != getTemperature() ) {
            _temperatureControl.setValue( temperature );
        }
    }

    public double getTemperature() {
        return _temperatureControl.getValue();
    }
}
