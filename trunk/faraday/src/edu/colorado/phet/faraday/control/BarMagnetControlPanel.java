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
import edu.colorado.phet.faraday.model.AbstractCompass;
import edu.colorado.phet.faraday.model.AbstractMagnet;
import edu.colorado.phet.faraday.module.BarMagnetModule;
import edu.colorado.phet.faraday.view.BarMagnetGraphic;
import edu.colorado.phet.faraday.view.CompassGridGraphic;
import edu.colorado.phet.faraday.view.FieldMeterGraphic;

/**
 * BarMagnetControlPanel is the control panel for the "Bar Magnet" module.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BarMagnetControlPanel extends FaradayControlPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final boolean ENABLE_DEVELOPER_CONTROLS = true;
    private static final String UNKNOWN_VALUE = "??????";

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model & view components to be controlled.
    private AbstractMagnet _magnetModel;
    private AbstractCompass _compassModel;
    private BarMagnetGraphic _magnetGraphic;
    private CompassGridGraphic _gridGraphic;
    private FieldMeterGraphic _fieldMeterGraphic;
    
    // UI components
    private JButton _flipPolarityButton;
    private JLabel _strengthValue;
    private JSlider _strengthSlider;
    private JCheckBox _magnetTransparencyCheckBox;
    private JCheckBox _fieldMeterCheckBox, _compassCheckBox;
    
    // Debugging components
    private JSlider _magnetWidthSlider, _magnetHeightSlider;
    private JSlider _gridSpacingSlider;
    private JSlider _needleWidthSlider, _needleHeightSlider;
    private JLabel _magnetWidthValue, _magnetHeightValue;
    private JLabel _gridSpacingValue;
    private JLabel _needleWidthValue, _needleHeightValue;

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
     * @param magnetModel
     * @param compassModel
     * @param magnetGraphic
     * @param gridGraphic
     * @param fieldMeterGraphic
     */
    public BarMagnetControlPanel( 
        BarMagnetModule module,
        AbstractMagnet magnetModel,
        AbstractCompass compassModel,
        BarMagnetGraphic magnetGraphic,
        CompassGridGraphic gridGraphic,
        FieldMeterGraphic fieldMeterGraphic ) {

        super( module );

        assert( magnetModel != null );
        assert( compassModel != null );
        assert( magnetGraphic != null );
        assert( gridGraphic != null );
        assert( fieldMeterGraphic != null );
        
        // Things we'll be controlling.
        _magnetModel = magnetModel;
        _compassModel = compassModel;
        _magnetGraphic = magnetGraphic;
        _gridGraphic = gridGraphic;
        _fieldMeterGraphic = fieldMeterGraphic;

        // Bar Magnet panel
        JPanel barMagnetPanel = new JPanel();
        {
            // Titled border with a larger font.
            TitledBorder border = new TitledBorder( SimStrings.get( "barMagnetPanel.title" ) );
            border.setTitleFont( super.getTitleFont() );
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
                _strengthSlider.setMaximum( (int) FaradayConfig.MAGNET_STRENGTH_MAX );
                _strengthSlider.setMinimum( (int) FaradayConfig.MAGNET_STRENGTH_MIN );
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
            _fieldMeterCheckBox = new JCheckBox( SimStrings.get( "meterCheckBox.label" ) );
            
            probePanel.setLayout( new BoxLayout( probePanel, BoxLayout.X_AXIS ) );
            probePanel.add( _fieldMeterCheckBox );
        }
        
        JPanel compassPanel = new JPanel();
        {
            _compassCheckBox = new JCheckBox( SimStrings.get( "compassCheckBox.label" ) );
            
            compassPanel.setLayout( new BoxLayout( compassPanel, BoxLayout.X_AXIS ) );
            compassPanel.add( _compassCheckBox );
        }
        
        // Developer panel
        JPanel developerPanel = new JPanel();
        if ( ENABLE_DEVELOPER_CONTROLS ) {
            
            //  Titled border
            TitledBorder border = new TitledBorder( "Developer Controls" );
            border.setTitleFont( super.getTitleFont() );
            developerPanel.setBorder( border );
            
            // Magnet width
            JPanel magnetWidthPanel = new JPanel();
            {
                // Label
                JLabel label = new JLabel( "Magnet width:" );

                // Slider
                _magnetWidthSlider = new JSlider();
                _magnetWidthSlider.setMaximum( FaradayConfig.BAR_MAGNET_SIZE_MAX.width );
                _magnetWidthSlider.setMinimum( FaradayConfig.BAR_MAGNET_SIZE_MIN.width );
                _magnetWidthSlider.setValue( FaradayConfig.BAR_MAGNET_SIZE_MIN.width );
                super.setSliderSize( _magnetWidthSlider, SLIDER_SIZE );

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
                _magnetHeightSlider.setMaximum( FaradayConfig.BAR_MAGNET_SIZE_MAX.height );
                _magnetHeightSlider.setMinimum( FaradayConfig.BAR_MAGNET_SIZE_MIN.height );
                _magnetHeightSlider.setValue( FaradayConfig.BAR_MAGNET_SIZE_MIN.height );
                super.setSliderSize( _magnetHeightSlider, SLIDER_SIZE );

                // Value
                _magnetHeightValue = new JLabel( UNKNOWN_VALUE );

                // Layout
                magnetHeightPanel.setLayout( new BoxLayout( magnetHeightPanel, BoxLayout.X_AXIS ) );
                magnetHeightPanel.add( label );
                magnetHeightPanel.add( _magnetHeightSlider );
                magnetHeightPanel.add( _magnetHeightValue );
            }
            
            // Grid density
            JPanel gridDensityPanel = new JPanel();
            {
                // Label
                JLabel label = new JLabel( "Grid spacing:" );

                // Slider
                _gridSpacingSlider = new JSlider();
                _gridSpacingSlider.setMinimum( FaradayConfig.GRID_SPACING_MIN );
                _gridSpacingSlider.setMaximum( FaradayConfig.GRID_SPACING_MAX );
                super.setSliderSize( _gridSpacingSlider, SLIDER_SIZE );

                // Value
                _gridSpacingValue = new JLabel( UNKNOWN_VALUE );

                // Layout
                gridDensityPanel.setLayout( new BoxLayout( gridDensityPanel, BoxLayout.X_AXIS ) );
                gridDensityPanel.add( label );
                gridDensityPanel.add( _gridSpacingSlider );
                gridDensityPanel.add( _gridSpacingValue );
            }

            // Needle width
            JPanel needleWidthPanel = new JPanel();
            {
                // Label
                JLabel label = new JLabel( "Needle length:" );

                // Slider
                _needleWidthSlider = new JSlider();
                _needleWidthSlider.setMaximum( FaradayConfig.GRID_NEEDLE_SIZE_MAX.width );
                _needleWidthSlider.setMinimum( FaradayConfig.GRID_NEEDLE_SIZE_MIN.width );
                _needleWidthSlider.setValue( FaradayConfig.GRID_NEEDLE_SIZE_MIN.width );
                super.setSliderSize( _needleWidthSlider, SLIDER_SIZE );

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
                _needleHeightSlider.setMaximum( FaradayConfig.GRID_NEEDLE_SIZE_MAX.height );
                _needleHeightSlider.setMinimum( FaradayConfig.GRID_NEEDLE_SIZE_MIN.height );
                _needleHeightSlider.setValue( FaradayConfig.GRID_NEEDLE_SIZE_MIN.height );
                super.setSliderSize( _needleHeightSlider, SLIDER_SIZE );

                // Value
                _needleHeightValue = new JLabel( UNKNOWN_VALUE );

                // Layout
                needleHeightPanel.setLayout( new BoxLayout( needleHeightPanel, BoxLayout.X_AXIS ) );
                needleHeightPanel.add( label );
                needleHeightPanel.add( _needleHeightSlider );
                needleHeightPanel.add( _needleHeightValue );
            }
            
            //  Layout
            developerPanel.setLayout( new BoxLayout( developerPanel, BoxLayout.Y_AXIS ) );
            developerPanel.add( magnetWidthPanel );
            developerPanel.add( magnetHeightPanel );
            developerPanel.add( gridDensityPanel );
            developerPanel.add( needleWidthPanel );
            developerPanel.add( needleHeightPanel );
        }
        
        // Add panels to control panel.
        addFullWidth( barMagnetPanel );
        addFullWidth( probePanel );
        addFullWidth( compassPanel );
        if ( ENABLE_DEVELOPER_CONTROLS ) {
            addFullWidth( developerPanel );
        }

        // Wire up event handling.
        EventListener listener = new EventListener();
        _flipPolarityButton.addActionListener( listener );
        _strengthSlider.addChangeListener( listener );
        _magnetTransparencyCheckBox.addActionListener( listener );
        _fieldMeterCheckBox.addActionListener( listener );
        _compassCheckBox.addActionListener( listener );
        if ( ENABLE_DEVELOPER_CONTROLS ) {
            _magnetWidthSlider.addChangeListener( listener );
            _magnetHeightSlider.addChangeListener( listener );
            _gridSpacingSlider.addChangeListener( listener );
            _needleWidthSlider.addChangeListener( listener );
            _needleHeightSlider.addChangeListener( listener );
        }
        
        // Call this after wiring up listeners.
        update();
    }
    
    /**
     * Update control panel to match the components that it's controlling.
     */
    public void update() {
        _strengthSlider.setValue( (int) _magnetModel.getStrength() );
        _magnetTransparencyCheckBox.setSelected( _magnetGraphic.isTransparencyEnabled() );
        _fieldMeterCheckBox.setSelected( _fieldMeterGraphic.isVisible() );
        _compassCheckBox.setSelected( _compassModel.isEnabled() );
        if ( ENABLE_DEVELOPER_CONTROLS ) {
            _magnetWidthSlider.setValue( (int) _magnetModel.getSize().width );
            _magnetHeightSlider.setValue( (int) _magnetModel.getSize().height );
            _gridSpacingSlider.setValue( _gridGraphic.getXSpacing() );
            _needleWidthSlider.setValue( _gridGraphic.getNeedleSize().width );
            _needleHeightSlider.setValue( _gridGraphic.getNeedleSize().height );
        }
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
                double direction = _magnetModel.getDirection();
                direction = ( direction + 180 ) % 360;
                _magnetModel.setDirection( direction );
                _compassModel.startMovingNow();
            }
            else if ( e.getSource() == _magnetTransparencyCheckBox ) {
                // Magnet transparency enable
                _magnetGraphic.setTransparencyEnabled( _magnetTransparencyCheckBox.isSelected() );
            }
            else if ( e.getSource() == _fieldMeterCheckBox ) {
                // Meter enable
                _fieldMeterGraphic.setVisible( _fieldMeterCheckBox.isSelected() );
            }
            else if ( e.getSource() == _compassCheckBox ) {
                // Compass enable
                _compassModel.setEnabled( _compassCheckBox.isSelected() );
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
                int strength = _strengthSlider.getValue();
                _magnetModel.setStrength( strength );
                _strengthValue.setText( String.valueOf( strength ) );
            }
            else if ( e.getSource() == _magnetWidthSlider ) {
                // Magnet width
                int width = _magnetWidthSlider.getValue();
                int height = _magnetModel.getSize().height;
                _magnetModel.setSize( new Dimension( width, height ) );
                _magnetWidthValue.setText( String.valueOf( width ) );
            }
            else if ( e.getSource() == _magnetHeightSlider ) {
                // Magnet height
                int width = _magnetModel.getSize().width;
                int height = _magnetHeightSlider.getValue();
                _magnetModel.setSize( new Dimension( width, height ) );
                _magnetHeightValue.setText( String.valueOf( height ) );
            }
            else if ( e.getSource() == _gridSpacingSlider ) {
                // Grid spacing
                int spacing = _gridSpacingSlider.getValue();
                _gridGraphic.setSpacing( spacing, spacing );
                _gridSpacingValue.setText( String.valueOf( spacing ) );
            }
            else if ( e.getSource() == _needleWidthSlider ) {
                // CompassGraphic Needle width
                int width = _needleWidthSlider.getValue();
                int height = _gridGraphic.getNeedleSize().height;
                _gridGraphic.setNeedleSize( new Dimension( width, height ) );
                _needleWidthValue.setText( String.valueOf( width ) );
            }
            else if ( e.getSource() == _needleHeightSlider ) {
                // CompassGraphic Needle height
                int width = _gridGraphic.getNeedleSize().width;
                int height = _needleHeightSlider.getValue();
                _gridGraphic.setNeedleSize( new Dimension( width, height ) );
                _needleHeightValue.setText( String.valueOf( height ) );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + e );
            }
        }
    }
}