/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.control;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.module.BarMagnetModule;

/**
 * BarMagnetControlPanel is the control panel for the "Bar Magnet" module.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BarMagnetControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final boolean ENABLE_DEBUG_CONTROLS = true;
    private static final String UNKNOWN_VALUE = "??????";
    private static final Dimension SLIDER_SIZE = new Dimension( 100, 20 );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private BarMagnetModule _module;
    private JButton _flipPolarityButton;
    private JSlider _strengthSlider;
    private JCheckBox _magnetTransparencyCheckBox;
    private JSlider _magnetWidthSlider, _magnetHeightSlider;
    private JSlider _xSpacingSlider, _ySpacingSlider;
    private JSlider _needleWidthSlider, _needleHeightSlider;
    private JLabel _strengthValue, _magnetWidthValue, _magnetHeightValue;
    private JLabel _xSpacingValue, _ySpacingValue, _needleWidthValue,
            _needleHeightValue;
    private JCheckBox _meterCheckBox, _compassCheckBox;
    private JButton _resetButton;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * <p>
     * The structure of the code (the way that code blocks are nested)
     * reflects the structure of the panel.
     * 
     * @param module the module that this control panel is associated with.
     */
    public BarMagnetControlPanel( BarMagnetModule module ) {

        super( module );

        _module = module;

        Font defaultFont = super.getFont();
        Font titleFont = new Font( defaultFont.getName(), defaultFont.getStyle(), defaultFont.getSize() + 4 );

        // Bar Magnet panel
        JPanel barMagnetPanel = new JPanel();
        {
            // Titled border with a larger font.
            TitledBorder border = new TitledBorder( SimStrings.get( "barMagnetPanel.title" ) );
            border.setTitleFont( titleFont );
            barMagnetPanel.setBorder( border );

            // Flip Polarity button
            _flipPolarityButton = new JButton( SimStrings.get( "flipPolarityButton.label" ) );

            // Strength slider
            JPanel strengthPanel = new JPanel();
            {
                // Label
                JLabel label = new JLabel( SimStrings.get( "strengthSlider.label" ) );

                // Slider
                _strengthSlider = new JSlider();
                _strengthSlider.setMinimum( (int) FaradayConfig.MAGNET_STRENGTH_MIN );
                _strengthSlider.setMaximum( (int) FaradayConfig.MAGNET_STRENGTH_MAX );
                _strengthSlider.setValue( (int) FaradayConfig.MAGNET_STRENGTH_MIN );
                setSliderSize( _strengthSlider, SLIDER_SIZE );

                // Value
                _strengthValue = new JLabel( UNKNOWN_VALUE );

                // Layout
                strengthPanel.setLayout( new BoxLayout( strengthPanel, BoxLayout.X_AXIS ) );
                strengthPanel.add( label );
                strengthPanel.add( _strengthSlider );
                strengthPanel.add( _strengthValue );
            }
            
            // Magnet transparency on/off
            _magnetTransparencyCheckBox = new JCheckBox( SimStrings.get( "magnetTransparencyCheckBox.label" ) );           

            // Layout
            barMagnetPanel.setLayout( new BoxLayout( barMagnetPanel, BoxLayout.Y_AXIS ) );
            barMagnetPanel.add( strengthPanel );
            barMagnetPanel.add( _flipPolarityButton );
            barMagnetPanel.add( _magnetTransparencyCheckBox );
        }

        JPanel probePanel = new JPanel();
        {
            _meterCheckBox = new JCheckBox( SimStrings.get( "meterCheckBox.label" ) );
            
            probePanel.setLayout( new BoxLayout( probePanel, BoxLayout.X_AXIS ) );
            probePanel.add( _meterCheckBox );
        }
        
        JPanel compassPanel = new JPanel();
        {
            _compassCheckBox = new JCheckBox( SimStrings.get( "compassCheckBox.label" ) );
            
            compassPanel.setLayout( new BoxLayout( compassPanel, BoxLayout.X_AXIS ) );
            compassPanel.add( _compassCheckBox );
        }
        
        // Debug panel
        JPanel debugPanel = new JPanel();
        if ( ENABLE_DEBUG_CONTROLS ) {
            
            //  Titled border
            TitledBorder border = new TitledBorder( "Debug Controls" );
            border.setTitleFont( titleFont );
            debugPanel.setBorder( border );
            
            // Magnet width
            JPanel magnetWidthPanel = new JPanel();
            {
                // Label
                JLabel label = new JLabel( "Magnet width:" );

                // Slider
                _magnetWidthSlider = new JSlider();
                _magnetWidthSlider.setMinimum( FaradayConfig.BAR_MAGNET_SIZE_MIN.width );
                _magnetWidthSlider.setMaximum( FaradayConfig.BAR_MAGNET_SIZE_MAX.width );
                _magnetWidthSlider.setValue( FaradayConfig.BAR_MAGNET_SIZE_MIN.width );
                setSliderSize( _magnetWidthSlider, SLIDER_SIZE );

                // Value
                _magnetWidthValue = new JLabel( UNKNOWN_VALUE );

                // Layout
                magnetWidthPanel.setLayout( new BoxLayout( magnetWidthPanel, BoxLayout.X_AXIS ) );
                magnetWidthPanel.add( label );
                magnetWidthPanel.add( _magnetWidthSlider );
                magnetWidthPanel.add( _magnetWidthValue );
            }

            // Magnet height
            JPanel magnetHeightPanel = new JPanel();
            {
                // Label
                JLabel label = new JLabel( "Magnet height:" );

                // Slider
                _magnetHeightSlider = new JSlider();
                _magnetHeightSlider.setMinimum( FaradayConfig.BAR_MAGNET_SIZE_MIN.height );
                _magnetHeightSlider.setMaximum( FaradayConfig.BAR_MAGNET_SIZE_MAX.height );
                _magnetHeightSlider.setValue( FaradayConfig.BAR_MAGNET_SIZE_MIN.height );
                setSliderSize( _magnetHeightSlider, SLIDER_SIZE );

                // Value
                _magnetHeightValue = new JLabel( UNKNOWN_VALUE );

                // Layout
                magnetHeightPanel.setLayout( new BoxLayout( magnetHeightPanel, BoxLayout.X_AXIS ) );
                magnetHeightPanel.add( label );
                magnetHeightPanel.add( _magnetHeightSlider );
                magnetHeightPanel.add( _magnetHeightValue );
            }
            
            // Grid X spacing
            JPanel gridXSpacingPanel = new JPanel();
            {
                // Label
                JLabel label = new JLabel( "Grid X spacing:" );

                // Slider
                _xSpacingSlider = new JSlider();
                _xSpacingSlider.setMinimum( FaradayConfig.GRID_X_SPACING_MIN );
                _xSpacingSlider.setMaximum( FaradayConfig.GRID_X_SPACING_MAX );
                _xSpacingSlider.setValue( FaradayConfig.GRID_X_SPACING_MIN );
                setSliderSize( _xSpacingSlider, SLIDER_SIZE );

                // Value
                _xSpacingValue = new JLabel( UNKNOWN_VALUE );

                // Layout
                gridXSpacingPanel.setLayout( new BoxLayout( gridXSpacingPanel, BoxLayout.X_AXIS ) );
                gridXSpacingPanel.add( label );
                gridXSpacingPanel.add( _xSpacingSlider );
                gridXSpacingPanel.add( _xSpacingValue );
            }

            // Grid Y spacing
            JPanel gridYSpacingPanel = new JPanel();
            {
                // Label
                JLabel label = new JLabel( "Grid Y spacing:" );

                // Slider
                _ySpacingSlider = new JSlider();
                _ySpacingSlider.setMinimum( FaradayConfig.GRID_Y_SPACING_MIN );
                _ySpacingSlider.setMaximum( FaradayConfig.GRID_Y_SPACING_MAX );
                _ySpacingSlider.setValue( FaradayConfig.GRID_Y_SPACING_MIN );
                setSliderSize( _ySpacingSlider, SLIDER_SIZE );

                // Value
                _ySpacingValue = new JLabel( UNKNOWN_VALUE );

                // Layout
                gridYSpacingPanel.setLayout( new BoxLayout( gridYSpacingPanel, BoxLayout.X_AXIS ) );
                gridYSpacingPanel.add( label );
                gridYSpacingPanel.add( _ySpacingSlider );
                gridYSpacingPanel.add( _ySpacingValue );
            }

            // Needle width
            JPanel needleWidthPanel = new JPanel();
            {
                // Label
                JLabel label = new JLabel( "Needle length:" );

                // Slider
                _needleWidthSlider = new JSlider();
                _needleWidthSlider.setMinimum( FaradayConfig.GRID_NEEDLE_SIZE_MIN.width );
                _needleWidthSlider.setMaximum( FaradayConfig.GRID_NEEDLE_SIZE_MAX.width );
                _needleWidthSlider.setValue( FaradayConfig.GRID_NEEDLE_SIZE_MIN.width );
                setSliderSize( _needleWidthSlider, SLIDER_SIZE );

                // Value
                _needleWidthValue = new JLabel( UNKNOWN_VALUE );

                // Layout
                needleWidthPanel.setLayout( new BoxLayout( needleWidthPanel, BoxLayout.X_AXIS ) );
                needleWidthPanel.add( label );
                needleWidthPanel.add( _needleWidthSlider );
                needleWidthPanel.add( _needleWidthValue );
            }

            // Needle height
            JPanel needleHeightPanel = new JPanel();
            {
                // Label
                JLabel label = new JLabel( "Needle thickness:" );

                // Slider
                _needleHeightSlider = new JSlider();
                _needleHeightSlider.setMinimum( FaradayConfig.GRID_NEEDLE_SIZE_MIN.height );
                _needleHeightSlider.setMaximum( FaradayConfig.GRID_NEEDLE_SIZE_MAX.height );
                _needleHeightSlider.setValue( FaradayConfig.GRID_NEEDLE_SIZE_MIN.height );
                setSliderSize( _needleHeightSlider, SLIDER_SIZE );

                // Value
                _needleHeightValue = new JLabel( UNKNOWN_VALUE );

                // Layout
                needleHeightPanel.setLayout( new BoxLayout( needleHeightPanel, BoxLayout.X_AXIS ) );
                needleHeightPanel.add( label );
                needleHeightPanel.add( _needleHeightSlider );
                needleHeightPanel.add( _needleHeightValue );
            }
            
            // Reset button
            _resetButton = new JButton( "Reset" );
            
            //  Layout
            debugPanel.setLayout( new BoxLayout( debugPanel, BoxLayout.Y_AXIS ) );
            debugPanel.add( magnetWidthPanel );
            debugPanel.add( magnetHeightPanel );
            debugPanel.add( gridXSpacingPanel );
            debugPanel.add( gridYSpacingPanel );
            debugPanel.add( needleWidthPanel );
            debugPanel.add( needleHeightPanel );
            debugPanel.add( _resetButton );
        }
        
        // Add panels to control panel.
        addFullWidth( barMagnetPanel );
        addFullWidth( probePanel );
        addFullWidth( compassPanel );
        if ( ENABLE_DEBUG_CONTROLS ) {
            addFullWidth( debugPanel );
        }

        // Wire up event handling.
        EventListener listener = new EventListener();
        _flipPolarityButton.addActionListener( listener );
        _strengthSlider.addChangeListener( listener );
        _magnetTransparencyCheckBox.addActionListener( listener );
        _meterCheckBox.addActionListener( listener );
        _compassCheckBox.addActionListener( listener );
        if ( ENABLE_DEBUG_CONTROLS ) {
            _magnetWidthSlider.addChangeListener( listener );
            _magnetHeightSlider.addChangeListener( listener );
            _xSpacingSlider.addChangeListener( listener );
            _ySpacingSlider.addChangeListener( listener );
            _needleWidthSlider.addChangeListener( listener );
            _needleHeightSlider.addChangeListener( listener );
            _resetButton.addActionListener( listener );
        }
    }

    //----------------------------------------------------------------------------
    // Setters
    //----------------------------------------------------------------------------

    /**
     * Sets the bar magnet strength.
     * 
     * @param value the value
     */
    public void setMagnetStrength( double value ) {
        _strengthSlider.setValue( (int) value );
    }
    
    /**
     * Sets the magnet transparency checkbox.
     * 
     * @param enabled true to enable, false to disable
     */
    public void setMagnetTransparencyEnabled( boolean enabled ) {
        _magnetTransparencyCheckBox.setSelected( enabled );
    }

    /**
     * Sets the bar magnet size.
     * 
     * @param size the size
     */
    public void setMagnetSize( Dimension size ) {
        if ( ENABLE_DEBUG_CONTROLS ) {
            _magnetWidthSlider.setValue( size.width );
            _magnetHeightSlider.setValue( size.height );
        }
    }

    /**
     * Sets the compass grid spacing.
     * 
     * @param x space between compasses in X direction
     * @param y space between compasses in Y direction
     */
    public void setGridSpacing( int x, int y ) {
        if ( ENABLE_DEBUG_CONTROLS ) {
            _xSpacingSlider.setValue( x );
            _ySpacingSlider.setValue( y );
        }
    }

    /**
     * Sets the size of the compass needles in the grid.
     * 
     * @param size the size
     */
    public void setGridNeedleSize( Dimension size ) {
        if ( ENABLE_DEBUG_CONTROLS ) {
            _needleWidthSlider.setValue( size.width );
            _needleHeightSlider.setValue( size.height );
        }
    }

    /**
     * Enables or disabled the Field Meter.
     * 
     * @param enabled true to enable, false to disable
     */
    public void setMeterEnabled( boolean enabled ) {
        _meterCheckBox.setSelected( enabled );
    }
    
    /**
     * Enables or disabled the compass.
     * 
     * @param enabled true to enable, false to disable
     */
    public void setCompassEnabled( boolean enabled ) {
        _compassCheckBox.setSelected( enabled );
    }
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    /*
     * Sets a slider to a fixed size.
     * 
     * @param slider the slider
     * @param size the size
     */
    private static void setSliderSize( JSlider slider, Dimension size ) {
        assert( slider != null );
        slider.setPreferredSize( size );
        slider.setMaximumSize( size );
        slider.setMinimumSize( size );
    }
    
    //----------------------------------------------------------------------------
    // Event Handling
    //----------------------------------------------------------------------------

    /**
     * EventListener is a nested class that is private to this control panel.
     * It handles dispatching of all events generated by the controls.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    private class EventListener implements ActionListener, ChangeListener {

        /** Sole constructor */
        public EventListener() {}

        /**
         * ActionEvent handler.
         * 
         * @param e the event
         * @throws IllegalArgumentException if the event is unexpected
         */
        public void actionPerformed( ActionEvent e ) {
            if ( e.getSource() == _flipPolarityButton ) {
                // Magnet polarity
                _module.flipMagnetPolarity();
            }
            else if ( e.getSource() == _magnetTransparencyCheckBox ) {
                // Magnet transparency enable
                _module.setMagnetTransparencyEnabled( _magnetTransparencyCheckBox.isSelected() );
            }
            else if ( e.getSource() == _meterCheckBox ) {
                // Probe enable
                _module.setMeterEnabled( _meterCheckBox.isSelected() );
            }
            else if ( e.getSource() == _compassCheckBox ) {
                // Compass enable
                _module.setCompassEnabled( _compassCheckBox.isSelected() );
            }
            else if ( e.getSource() == _resetButton ) {
                // Reset
                _module.reset();
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + e );
            }
        }

        /**
         * ChangeEvent handler.
         * 
         * @param e the event
         * @throws IllegalArgumentException if the event is unexpected
         */
        public void stateChanged( ChangeEvent e ) {
            if ( e.getSource() == _strengthSlider ) {
                // Magnet strength
                _module.setMagnetStrength( _strengthSlider.getValue() );
                Integer i = new Integer( _strengthSlider.getValue() );
                _strengthValue.setText( i.toString() );
            }
            else if ( e.getSource() == _magnetWidthSlider || e.getSource() == _magnetHeightSlider ) {
                // Magnet dimensions
                int width = _magnetWidthSlider.getValue();
                int height = _magnetHeightSlider.getValue();
                _module.setMagnetSize( new Dimension( width, height ) );
                _magnetWidthValue.setText( String.valueOf( width ) );
                _magnetHeightValue.setText( String.valueOf( height ) );
            }
            else if ( e.getSource() == _xSpacingSlider || e.getSource() == _ySpacingSlider ) {
                // Grid spacing
                int x = _xSpacingSlider.getValue();
                int y = _ySpacingSlider.getValue();
                _module.setGridSpacing( x, y );
                _xSpacingValue.setText( String.valueOf( x ) );
                _ySpacingValue.setText( String.valueOf( y ) );
            }
            else if ( e.getSource() == _needleWidthSlider || e.getSource() == _needleHeightSlider ) {
                // CompassGraphic Needle dimensions
                int width = _needleWidthSlider.getValue();
                int height = _needleHeightSlider.getValue();
                _module.setGridNeedleSize( new Dimension( width, height ) );
                _needleWidthValue.setText( String.valueOf( width ) );
                _needleHeightValue.setText( String.valueOf( height ) );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + e );
            }
        }
    }
    


}