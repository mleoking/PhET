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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.*;
import edu.colorado.phet.faraday.module.MagnetAndCoilModule;
import edu.colorado.phet.faraday.view.BarMagnetGraphic;
import edu.colorado.phet.faraday.view.CompassGridGraphic;

/**
 * MagnetAndCoilControlPanel is the control panel for the "Magnet & Coil" module.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class MagnetAndCoilControlPanel extends FaradayControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model & view components to be controlled.
    private AbstractMagnet _magnetModel;
    private AbstractCompass _compassModel;
    private PickupCoil _pickupCoilModel;
    private LightBulb _lightBulbModel;
    private VoltMeter _voltMeterModel;
    private BarMagnetGraphic _magnetGraphic;
    private CompassGridGraphic _gridGraphic;
    
    // UI components
    private JButton _flipPolarityButton;
    private JSlider _strengthSlider;
    private JCheckBox _magnetTransparencyCheckBox, _gridCheckBox, _compassCheckBox;
    private JSpinner _loopsSpinner;
    private JSlider _radiusSlider;
    private JRadioButton _voltMeterRadioButton;
    private JRadioButton _lightBulbRadioButton;
    private JLabel _strengthValue, _radiusValue;

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
     * @param pickupCoilModel
     * @param lightBulbModel
     * @param voltMeterModel
     * @param magnetGraphic
     * @param gridGraphic
     */
    public MagnetAndCoilControlPanel( 
        MagnetAndCoilModule module,
        AbstractMagnet magnetModel,
        AbstractCompass compassModel,
        PickupCoil pickupCoilModel,
        LightBulb lightBulbModel,
        VoltMeter voltMeterModel,
        BarMagnetGraphic magnetGraphic,
        CompassGridGraphic gridGraphic ) {

        super( module );

        assert( magnetModel != null );
        assert( compassModel != null );
        assert( pickupCoilModel != null );
        assert( lightBulbModel != null );
        assert( voltMeterModel != null );
        assert( magnetGraphic != null );
        assert( gridGraphic != null );

        // Things we'll be controlling.
        _magnetModel = magnetModel;
        _compassModel = compassModel;
        _pickupCoilModel = pickupCoilModel;
        _lightBulbModel = lightBulbModel;
        _voltMeterModel = voltMeterModel;
        _magnetGraphic = magnetGraphic;
        _gridGraphic = gridGraphic;

        JPanel fillerPanel = new JPanel();
        {
            fillerPanel.setLayout( new BoxLayout( fillerPanel, BoxLayout.X_AXIS ) );
            // WORKAROUND: Filler to set consistent panel width
            fillerPanel.add( Box.createHorizontalStrut( FaradayConfig.CONTROL_PANEL_MIN_WIDTH ) );
        }

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
            JPanel sliderPanel = new JPanel();
            {
                // Label
                JLabel label = new JLabel( SimStrings.get( "strengthSlider.label" ) );

                // Slider
                _strengthSlider = new JSlider();
                _strengthSlider.setMaximum( (int) FaradayConfig.MAGNET_STRENGTH_MAX );
                _strengthSlider.setMinimum( (int) FaradayConfig.MAGNET_STRENGTH_MIN );
                _strengthSlider.setValue( (int) FaradayConfig.MAGNET_STRENGTH_MIN );
                super.setSliderSize( _strengthSlider, SLIDER_SIZE );;

                // Value
                _strengthValue = new JLabel( UNKNOWN_VALUE );

                // Layout
                sliderPanel.setLayout( new BoxLayout( sliderPanel, BoxLayout.X_AXIS ) );
                sliderPanel.add( label );
                sliderPanel.add( _strengthSlider );
                sliderPanel.add( _strengthValue );
            }

            // Magnet transparency on/off
            _magnetTransparencyCheckBox = new JCheckBox( SimStrings.get( "magnetTransparencyCheckBox.label" ) );

            // B-Field on/off
            _gridCheckBox = new JCheckBox( SimStrings.get( "gridCheckBox.label" ) );

            // Layout
            barMagnetPanel.setLayout( new BoxLayout( barMagnetPanel, BoxLayout.Y_AXIS ) );
            barMagnetPanel.add( sliderPanel );
            barMagnetPanel.add( _flipPolarityButton );
            barMagnetPanel.add( _magnetTransparencyCheckBox );
            barMagnetPanel.add( _gridCheckBox );
        }

        // Pickup Coil panel
        JPanel pickupCoilPanel = new JPanel();
        {
            // Titled border with a larger font.
            TitledBorder border = new TitledBorder( SimStrings.get( "pickupCoilPanel.title" ) );
            border.setTitleFont( super.getTitleFont() );
            pickupCoilPanel.setBorder( border );

            // Number of loops
            JPanel loopsPanel = new JPanel();
            {
                // Label 
                JLabel label = new JLabel( SimStrings.get( "numberOfLoops.label" ) );

                // Spinner, keyboard editing disabled.
                SpinnerNumberModel spinnerModel = new SpinnerNumberModel();
                spinnerModel.setMaximum( new Integer( FaradayConfig.MAX_PICKUP_LOOPS ) );
                spinnerModel.setMinimum( new Integer( FaradayConfig.MIN_PICKUP_LOOPS ) );
                spinnerModel.setValue( new Integer( FaradayConfig.MIN_PICKUP_LOOPS ) );
                _loopsSpinner = new JSpinner( spinnerModel );
                JFormattedTextField tf = ( (JSpinner.DefaultEditor) _loopsSpinner.getEditor() ).getTextField();
                tf.setEditable( false );
                
                // Dimensions
                _loopsSpinner.setPreferredSize( SPINNER_SIZE );
                _loopsSpinner.setMaximumSize( SPINNER_SIZE );
                _loopsSpinner.setMinimumSize( SPINNER_SIZE );

                // Layout
                loopsPanel.setLayout( new BoxLayout( loopsPanel, BoxLayout.X_AXIS ) );
                loopsPanel.add( label );
                loopsPanel.add( _loopsSpinner );
            }

            // Loop Area
            JPanel areaPanel = new JPanel();
            {
                // Label
                JLabel label = new JLabel( SimStrings.get( "radiusSlider.label" ) );

                // Slider
                _radiusSlider = new JSlider();
                _radiusSlider.setMaximum( (int) MagnetAndCoilModule.LOOP_RADIUS_MAX );
                _radiusSlider.setMinimum( (int) MagnetAndCoilModule.LOOP_RADIUS_MIN );
                _radiusSlider.setValue( (int) MagnetAndCoilModule.LOOP_RADIUS_MIN );
                super.setSliderSize( _radiusSlider, SLIDER_SIZE );

                // Value
                _radiusValue = new JLabel( UNKNOWN_VALUE );

                // Layout
                areaPanel.setLayout( new BoxLayout( areaPanel, BoxLayout.X_AXIS ) );
                areaPanel.add( label );
                areaPanel.add( _radiusSlider );
                areaPanel.add( _radiusValue );
            }

            // Type of "load"
            JPanel loadPanel = new JPanel();
            {
                // Titled border with a larger font.
                TitledBorder border2 = new TitledBorder( SimStrings.get( "loadPanel.title" ) );
                border.setTitleFont( super.getTitleFont() );
                loadPanel.setBorder( border2 );

                // Radio buttons
                _lightBulbRadioButton = new JRadioButton( SimStrings.get( "bulbRadioButton.label" ) );
                _voltMeterRadioButton = new JRadioButton( SimStrings.get( "meterRadioButton.label" ) );
                ButtonGroup group = new ButtonGroup();
                group.add( _lightBulbRadioButton );
                group.add( _voltMeterRadioButton );

                // Layout
                loadPanel.setLayout( new BoxLayout( loadPanel, BoxLayout.Y_AXIS ) );
                loadPanel.add( _lightBulbRadioButton );
                loadPanel.add( _voltMeterRadioButton );
            }

            // Layout
            pickupCoilPanel.setLayout( new BoxLayout( pickupCoilPanel, BoxLayout.Y_AXIS ) );
            pickupCoilPanel.add( loopsPanel );
            pickupCoilPanel.add( areaPanel );
            pickupCoilPanel.add( loadPanel );
        }
        
        JPanel compassPanel = new JPanel();
        {
            _compassCheckBox = new JCheckBox( SimStrings.get( "compassCheckBox.label" ) );
            
            compassPanel.setLayout( new BoxLayout( compassPanel, BoxLayout.X_AXIS ) );
            compassPanel.add( _compassCheckBox );
        }

        // Add panels to control panel.
        addFullWidth( fillerPanel );
        addFullWidth( barMagnetPanel );
        addFullWidth( pickupCoilPanel );
        addFullWidth( compassPanel );

        // Wire up event handling
        EventListener listener = new EventListener();
        _flipPolarityButton.addActionListener( listener );
        _strengthSlider.addChangeListener( listener );
        _magnetTransparencyCheckBox.addActionListener( listener );
        _gridCheckBox.addActionListener( listener );
        _loopsSpinner.addChangeListener( listener );
        _radiusSlider.addChangeListener( listener );
        _lightBulbRadioButton.addActionListener( listener );
        _voltMeterRadioButton.addActionListener( listener );
        _compassCheckBox.addActionListener( listener );
        
        // Update control panel to match the components that it's controlling.
        _strengthSlider.setValue( (int) _magnetModel.getStrength() );
        _magnetTransparencyCheckBox.setSelected( _magnetGraphic.isTransparencyEnabled() );
        _compassCheckBox.setSelected( _compassModel.isEnabled() );
        _gridCheckBox.setSelected( _gridGraphic.isVisible() );
        _loopsSpinner.setValue( new Integer( _pickupCoilModel.getNumberOfLoops() ) );
        _radiusSlider.setValue( (int) _pickupCoilModel.getRadius() );
        _lightBulbRadioButton.setSelected( _lightBulbModel.isEnabled() );
        _voltMeterRadioButton.setSelected( _voltMeterModel.isEnabled() );
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
                _pickupCoilModel.setSmoothingEnabled( false );
                double direction = _magnetModel.getDirection();
                direction = ( direction + 180 ) % 360;
                _magnetModel.setDirection( direction );
                _pickupCoilModel.updateEmf();
                _pickupCoilModel.setSmoothingEnabled( true );
            }
            else if ( e.getSource() == _magnetTransparencyCheckBox ) {
                // Magnet transparency
                _magnetGraphic.setTransparencyEnabled( _magnetTransparencyCheckBox.isSelected() );
            }
            else if ( e.getSource() == _gridCheckBox ) {
                // Grid enable
                _gridGraphic.resetSpacing();
                _gridGraphic.setVisible( _gridCheckBox.isSelected() );
            }
            else if ( e.getSource() == _compassCheckBox ) {
                // Compass enable
                _compassModel.setEnabled( _compassCheckBox.isSelected() );
            }
            else if ( e.getSource() == _lightBulbRadioButton ) {
                // Lightbulb enable
                _lightBulbModel.setEnabled( _lightBulbRadioButton.isSelected() );
                _voltMeterModel.setEnabled( !_lightBulbRadioButton.isSelected() );
            }
            else if ( e.getSource() == _voltMeterRadioButton ) {
                // Voltmeter enable
                _voltMeterModel.setEnabled( _voltMeterRadioButton.isSelected() );
                _lightBulbModel.setEnabled( !_voltMeterRadioButton.isSelected() );
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
            else if ( e.getSource() == _radiusSlider ) {
                // Loop radius
                int radius = _radiusSlider.getValue();
                _pickupCoilModel.setSmoothingEnabled( false );
                _pickupCoilModel.setRadius( radius );
                _pickupCoilModel.updateEmf();
                _pickupCoilModel.setSmoothingEnabled( true );
                Integer i = new Integer( radius );
                _radiusValue.setText( i.toString() );
            }
            else if ( e.getSource() == _loopsSpinner ) {
                // Number of loops
                int numberOfLoops = ( (Integer) _loopsSpinner.getValue() ).intValue();
                _pickupCoilModel.setSmoothingEnabled( false );
                _pickupCoilModel.setNumberOfLoops( numberOfLoops );
                _pickupCoilModel.updateEmf();
                _pickupCoilModel.setSmoothingEnabled( true );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + e );
            }
        }
    }
}