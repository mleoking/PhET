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

import java.awt.BorderLayout;
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

    private static final boolean ENABLE_DEBUG_CONTROLS = false;
    private static final String UNKNOWN_VALUE = "??????";

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
    private JCheckBox _probeCheckBox;
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
                _strengthSlider.setMinimum( (int) BarMagnetModule.MAGNET_STRENGTH_MIN );
                _strengthSlider.setMaximum( (int) BarMagnetModule.MAGNET_STRENGTH_MAX );
                _strengthSlider.setValue( (int) BarMagnetModule.MAGNET_STRENGTH_MIN );

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

            // Magnet width
            JPanel widthPanel = new JPanel();
            {
                // Label
                JLabel label = new JLabel( SimStrings.get( "magnetWidth.label" ) );

                // Slider
                _magnetWidthSlider = new JSlider();
                _magnetWidthSlider.setMinimum( BarMagnetModule.MAGNET_SIZE_MIN.width );
                _magnetWidthSlider.setMaximum( BarMagnetModule.MAGNET_SIZE_MAX.width );
                _magnetWidthSlider.setValue( BarMagnetModule.MAGNET_SIZE_MIN.width );

                // Value
                _magnetWidthValue = new JLabel( UNKNOWN_VALUE );

                // Layout
                widthPanel.setLayout( new BoxLayout( widthPanel, BoxLayout.X_AXIS ) );
                widthPanel.add( label );
                widthPanel.add( _magnetWidthSlider );
                widthPanel.add( _magnetWidthValue );
            }

            // Magnet height
            JPanel heightPanel = new JPanel();
            {
                // Label
                JLabel label = new JLabel( SimStrings.get( "magnetHeight.label" ) );

                // Slider
                _magnetHeightSlider = new JSlider();
                _magnetHeightSlider.setMinimum( BarMagnetModule.MAGNET_SIZE_MIN.height );
                _magnetHeightSlider.setMaximum( BarMagnetModule.MAGNET_SIZE_MAX.height );
                _magnetHeightSlider.setValue( BarMagnetModule.MAGNET_SIZE_MIN.height );

                // Value
                _magnetHeightValue = new JLabel( UNKNOWN_VALUE );

                // Layout
                heightPanel.setLayout( new BoxLayout( heightPanel, BoxLayout.X_AXIS ) );
                heightPanel.add( label );
                heightPanel.add( _magnetHeightSlider );
                heightPanel.add( _magnetHeightValue );
            }

            // Layout
            barMagnetPanel.setLayout( new BoxLayout( barMagnetPanel, BoxLayout.Y_AXIS ) );
            barMagnetPanel.add( _flipPolarityButton );
            barMagnetPanel.add( strengthPanel );
            barMagnetPanel.add( _magnetTransparencyCheckBox );
            if ( ENABLE_DEBUG_CONTROLS ) {
                barMagnetPanel.add( widthPanel );
                barMagnetPanel.add( heightPanel );
            }
        }

        // Grid panel
        JPanel gridPanel = new JPanel();
        {
            // Titled border with a larger font.
            TitledBorder border = new TitledBorder( SimStrings.get( "gridPanel.title" ) );
            border.setTitleFont( titleFont );
            gridPanel.setBorder( border );

            // X axis density
            JPanel xPanel = new JPanel();
            {
                // Label
                JLabel label = new JLabel( SimStrings.get( "xSpacing.label" ) );

                // Slider
                _xSpacingSlider = new JSlider();
                _xSpacingSlider.setMinimum( BarMagnetModule.GRID_X_SPACING_MIN );
                _xSpacingSlider.setMaximum( BarMagnetModule.GRID_X_SPACING_MAX );
                _xSpacingSlider.setValue( BarMagnetModule.GRID_X_SPACING_MIN );

                // Value
                _xSpacingValue = new JLabel( UNKNOWN_VALUE );

                // Layout
                xPanel.setLayout( new BoxLayout( xPanel, BoxLayout.X_AXIS ) );
                xPanel.add( label );
                xPanel.add( _xSpacingSlider );
                xPanel.add( _xSpacingValue );
            }

            // Y axis density
            JPanel yPanel = new JPanel();
            {
                // Label
                JLabel label = new JLabel( SimStrings.get( "ySpacing.label" ) );

                // Slider
                _ySpacingSlider = new JSlider();
                _ySpacingSlider.setMinimum( BarMagnetModule.GRID_Y_SPACING_MIN );
                _ySpacingSlider.setMaximum( BarMagnetModule.GRID_Y_SPACING_MAX );
                _ySpacingSlider.setValue( BarMagnetModule.GRID_Y_SPACING_MIN );

                // Value
                _ySpacingValue = new JLabel( UNKNOWN_VALUE );

                // Layout
                yPanel.setLayout( new BoxLayout( yPanel, BoxLayout.X_AXIS ) );
                yPanel.add( label );
                yPanel.add( _ySpacingSlider );
                yPanel.add( _ySpacingValue );
            }

            // Needle width
            JPanel widthPanel = new JPanel();
            {
                // Label
                JLabel label = new JLabel( SimStrings.get( "needleWidth.label" ) );

                // Slider
                _needleWidthSlider = new JSlider();
                _needleWidthSlider.setMinimum( BarMagnetModule.GRID_NEEDLE_SIZE_MIN.width );
                _needleWidthSlider.setMaximum( BarMagnetModule.GRID_NEEDLE_SIZE_MAX.width );
                _needleWidthSlider.setValue( BarMagnetModule.GRID_NEEDLE_SIZE_MIN.width );

                // Value
                _needleWidthValue = new JLabel( UNKNOWN_VALUE );

                // Layout
                widthPanel.setLayout( new BoxLayout( widthPanel, BoxLayout.X_AXIS ) );
                widthPanel.add( label );
                widthPanel.add( _needleWidthSlider );
                widthPanel.add( _needleWidthValue );
            }

            // Needle height
            JPanel heightPanel = new JPanel();
            {
                // Label
                JLabel label = new JLabel( SimStrings.get( "needleHeight.label" ) );

                // Slider
                _needleHeightSlider = new JSlider();
                _needleHeightSlider.setMinimum( BarMagnetModule.GRID_NEEDLE_SIZE_MIN.height );
                _needleHeightSlider.setMaximum( BarMagnetModule.GRID_NEEDLE_SIZE_MAX.height );
                _needleHeightSlider.setValue( BarMagnetModule.GRID_NEEDLE_SIZE_MIN.height );

                // Value
                _needleHeightValue = new JLabel( UNKNOWN_VALUE );

                // Layout
                heightPanel.setLayout( new BoxLayout( heightPanel, BoxLayout.X_AXIS ) );
                heightPanel.add( label );
                heightPanel.add( _needleHeightSlider );
                heightPanel.add( _needleHeightValue );
            }

            // Layout
            gridPanel.setLayout( new BoxLayout( gridPanel, BoxLayout.Y_AXIS ) );
            gridPanel.add( xPanel );
            gridPanel.add( yPanel );
            gridPanel.add( widthPanel );
            gridPanel.add( heightPanel );
        }

        JPanel probePanel = new JPanel();
        {
            _probeCheckBox = new JCheckBox( SimStrings.get( "probeCheckBox.label" ) );
            
            probePanel.setLayout( new BoxLayout( probePanel, BoxLayout.X_AXIS ) );
            probePanel.add( _probeCheckBox );
        }
        
        // Reset panel
        JPanel resetPanel = new JPanel();
        {
            // Reset button
            _resetButton = new JButton( SimStrings.get( "resetButton.label" ) );

            resetPanel.setLayout( new BoxLayout( resetPanel, BoxLayout.X_AXIS ) );
            resetPanel.add( _resetButton );
        }

        // Add panels to control panel.
        addFullWidth( barMagnetPanel );
        addFullWidth( probePanel );
        if ( ENABLE_DEBUG_CONTROLS ) {
            addFullWidth( gridPanel );
            addFullWidth( resetPanel );
        }

        // Wire up event handling.
        EventListener listener = new EventListener();
        _resetButton.addActionListener( listener );
        _flipPolarityButton.addActionListener( listener );
        _strengthSlider.addChangeListener( listener );
        _magnetTransparencyCheckBox.addActionListener( listener );
        _magnetWidthSlider.addChangeListener( listener );
        _magnetHeightSlider.addChangeListener( listener );
        _xSpacingSlider.addChangeListener( listener );
        _ySpacingSlider.addChangeListener( listener );
        _needleWidthSlider.addChangeListener( listener );
        _needleHeightSlider.addChangeListener( listener );
        _probeCheckBox.addActionListener( listener );
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
        _magnetWidthSlider.setValue( size.width );
        _magnetHeightSlider.setValue( size.height );
    }

    /**
     * Sets the compass grid spacing.
     * 
     * @param x space between compasses in X direction
     * @param y space between compasses in Y direction
     */
    public void setGridSpacing( int x, int y ) {
        _xSpacingSlider.setValue( x );
        _ySpacingSlider.setValue( y );
    }

    /**
     * Sets the size of the compass needles in the grid.
     * 
     * @param size the size
     */
    public void setGridNeedleSize( Dimension size ) {
        _needleWidthSlider.setValue( size.width );
        _needleHeightSlider.setValue( size.height );
    }

    /**
     * Enables or disabled the B-Field Probe.
     * 
     * @param enabled true to enable, false to disable
     */
    public void setProbeEnabled( boolean enabled ) {
        _probeCheckBox.setSelected( enabled );
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
            else if ( e.getSource() == _probeCheckBox ) {
                // Probe enable
                _module.setProbeEnabled( _probeCheckBox.isSelected() );
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