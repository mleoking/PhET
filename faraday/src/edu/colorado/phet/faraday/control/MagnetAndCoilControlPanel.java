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

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.*;
import edu.colorado.phet.faraday.module.MagnetAndCoilModule;
import edu.colorado.phet.faraday.view.BarMagnetGraphic;
import edu.colorado.phet.faraday.view.CoilGraphic;
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
    private Compass _compassModel;
    private PickupCoil _pickupCoilModel;
    private Lightbulb _lightbulbModel;
    private Voltmeter _voltmeterModel;
    private BarMagnetGraphic _magnetGraphic;
    private CompassGridGraphic _gridGraphic;
    private CoilGraphic _coilGraphic;
    
    // UI components
    private JButton _flipPolarityButton;
    private JSlider _strengthSlider;
    private JCheckBox _gridCheckBox;
    private JCheckBox _compassCheckBox;
    private JSpinner _loopsSpinner;
    private JSlider _radiusSlider;
    private JRadioButton _voltmeterRadioButton;
    private JRadioButton _lightbulbRadioButton;
    private JCheckBox _electronsCheckBox;
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
     * @param lightbulbModel
     * @param voltmeterModel
     * @param magnetGraphic
     * @param gridGraphic
     * @param coilGraphic
     */
    public MagnetAndCoilControlPanel( 
        MagnetAndCoilModule module,
        AbstractMagnet magnetModel,
        Compass compassModel,
        PickupCoil pickupCoilModel,
        Lightbulb lightbulbModel,
        Voltmeter voltmeterModel,
        BarMagnetGraphic magnetGraphic,
        CompassGridGraphic gridGraphic,
        CoilGraphic coilGraphic ) {

        super( module );

        assert( magnetModel != null );
        assert( compassModel != null );
        assert( pickupCoilModel != null );
        assert( lightbulbModel != null );
        assert( voltmeterModel != null );
        assert( magnetGraphic != null );
        assert( gridGraphic != null );
        assert( coilGraphic != null );

        // Things we'll be controlling.
        _magnetModel = magnetModel;
        _compassModel = compassModel;
        _pickupCoilModel = pickupCoilModel;
        _lightbulbModel = lightbulbModel;
        _voltmeterModel = voltmeterModel;
        _magnetGraphic = magnetGraphic;
        _gridGraphic = gridGraphic;
        _coilGraphic = coilGraphic;

        JPanel fillerPanel = new JPanel();
        {
            fillerPanel.setLayout( new BoxLayout( fillerPanel, BoxLayout.X_AXIS ) );
            // WORKAROUND: Filler to set consistent panel width
            fillerPanel.add( Box.createHorizontalStrut( FaradayConfig.CONTROL_PANEL_MIN_WIDTH ) );
        }
        
        JPanel magnetPanel = new JPanel();
        {
            // Title
            TitledBorder magnetBorder = BorderFactory.createTitledBorder( SimStrings.get( "MagnetAndCoilModule.magnetControls" ) );
            magnetBorder.setTitleFont( getTitleFont() );
            magnetPanel.setBorder( magnetBorder );
            
            // Magnet strength
            JPanel strengthPanel = new JPanel();
            {
                strengthPanel.setBorder( BorderFactory.createEtchedBorder() );

                // Values are a percentage of the maximum.
                int max = 100;
                int min = (int) ( 100.0 * FaradayConfig.BAR_MAGNET_STRENGTH_MIN / FaradayConfig.BAR_MAGNET_STRENGTH_MAX );
                int range = max - min;
                
                // Slider
                _strengthSlider = new JSlider();
                _strengthSlider.setMaximum( max );
                _strengthSlider.setMinimum( min );
                _strengthSlider.setValue( min );
                
                // Slider tick marks
                _strengthSlider.setMajorTickSpacing( range );
                _strengthSlider.setMinorTickSpacing( 10 );
                _strengthSlider.setSnapToTicks( false );
                _strengthSlider.setPaintTicks( true );
                _strengthSlider.setPaintLabels( true );
                
                // Value
                _strengthValue = new JLabel( UNKNOWN_VALUE );

                // Layout
                EasyGridBagLayout layout = new EasyGridBagLayout( strengthPanel );
                strengthPanel.setLayout( layout );
                layout.addAnchoredComponent( _strengthValue, 0, 0, GridBagConstraints.WEST );
                layout.addAnchoredComponent( _strengthSlider, 1, 0, GridBagConstraints.WEST );
            }

            //  Flip Polarity button
            _flipPolarityButton = new JButton( SimStrings.get( "MagnetAndCoilModule.flipPolarity" ) );

            // Compass Grid on/off
            _gridCheckBox = new JCheckBox( SimStrings.get( "MagnetAndCoilModule.showGrid" ) );

            // Compass on/off
            _compassCheckBox = new JCheckBox( SimStrings.get( "MagnetAndCoilModule.showCompass" ) );
            
            // Layout
            EasyGridBagLayout layout = new EasyGridBagLayout( magnetPanel );
            magnetPanel.setLayout( layout );
            int row = 0;
            layout.addFilledComponent( strengthPanel, row++, 0, GridBagConstraints.HORIZONTAL );
            layout.addComponent( _flipPolarityButton, row++, 0 );
            layout.addComponent( _gridCheckBox, row++, 0 );
            layout.addComponent( _compassCheckBox, row++, 0 );
        }
        
        JPanel coilPanel = new JPanel();
        {
            // Titled border with some space above it.
            Border outsideBorder = BorderFactory.createEmptyBorder( 10, 0, 0, 0 );  // top, left, bottom, right
            TitledBorder insideBorder = BorderFactory.createTitledBorder( SimStrings.get( "MagnetAndCoilModule.coilControls" ) );
            insideBorder.setTitleFont( getTitleFont() );
            Border coilBorder = BorderFactory.createCompoundBorder( outsideBorder, insideBorder );
            coilPanel.setBorder( coilBorder );

            // Number of loops
            JPanel loopsPanel = new JPanel();
            {
                JLabel loopsLabel = new JLabel( SimStrings.get( "MagnetAndCoilModule.numberOfLoops" ) );

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
                EasyGridBagLayout layout = new EasyGridBagLayout( loopsPanel );
                loopsPanel.setLayout( layout );
                layout.addAnchoredComponent( loopsLabel, 0, 0, GridBagConstraints.WEST );
                layout.addAnchoredComponent( _loopsSpinner, 0, 1, GridBagConstraints.WEST );
            }

            // Loop radius
            JPanel radiusPanel = new JPanel();
            {
                radiusPanel.setBorder( BorderFactory.createEtchedBorder() );

                // Values are a percentage of the maximum.
                int max = 100;
                int min = (int) ( 100.0 * FaradayConfig.MIN_PICKUP_RADIUS / FaradayConfig.MAX_PICKUP_RADIUS );
                int range = max - min;
                
                // Slider
                _radiusSlider = new JSlider();
                _radiusSlider.setMaximum( max );
                _radiusSlider.setMinimum( min );
                _radiusSlider.setValue( min );

                // Slider tick marks
                _radiusSlider.setMajorTickSpacing( range );
                _radiusSlider.setMinorTickSpacing( 10 );
                _radiusSlider.setSnapToTicks( false );
                _radiusSlider.setPaintTicks( true );
                _radiusSlider.setPaintLabels( true );
                
                // Value
                _radiusValue = new JLabel( UNKNOWN_VALUE );

                // Layout
                EasyGridBagLayout layout = new EasyGridBagLayout( radiusPanel );
                radiusPanel.setLayout( layout );
                layout.addAnchoredComponent( _radiusValue, 0, 0, GridBagConstraints.WEST );
                layout.addAnchoredComponent( _radiusSlider, 1, 0, GridBagConstraints.WEST );
            }

            JPanel indicatorPanel = new JPanel();
            {
                // Title
                TitledBorder indicatorBorder = new TitledBorder( SimStrings.get( "MagnetAndCoilModule.indicator" ) );
                indicatorPanel.setBorder( indicatorBorder );

                // Radio buttons
                _lightbulbRadioButton = new JRadioButton( SimStrings.get( "MagnetAndCoilModule.lightbulb" ) );
                _voltmeterRadioButton = new JRadioButton( SimStrings.get( "MagnetAndCoilModule.voltmeter" ) );
                ButtonGroup group = new ButtonGroup();
                group.add( _lightbulbRadioButton );
                group.add( _voltmeterRadioButton );

                // Layout
                EasyGridBagLayout layout = new EasyGridBagLayout( indicatorPanel );
                indicatorPanel.setLayout( layout );
                layout.addAnchoredComponent( _lightbulbRadioButton, 0, 0, GridBagConstraints.WEST );
                layout.addAnchoredComponent( _voltmeterRadioButton, 1, 0, GridBagConstraints.WEST );
            }

            // Electrons on/off
            _electronsCheckBox = new JCheckBox( SimStrings.get( "MagnetAndCoilModule.showElectrons" ) );
            
            // Layout
            EasyGridBagLayout layout = new EasyGridBagLayout( coilPanel );
            coilPanel.setLayout( layout );  
            int row = 0;
            layout.addComponent( loopsPanel, row++, 0 );
            layout.addFilledComponent( radiusPanel, row++, 0, GridBagConstraints.HORIZONTAL );
            layout.addFilledComponent( indicatorPanel, row++, 0, GridBagConstraints.HORIZONTAL );
            layout.addComponent( _electronsCheckBox, row++, 0 );
        }
        
        // Add panels.
        addFullWidth( fillerPanel );
        addFullWidth( magnetPanel );
        addFullWidth( coilPanel );

        // Wire up event handling
        EventListener listener = new EventListener();
        _flipPolarityButton.addActionListener( listener );
        _strengthSlider.addChangeListener( listener );
        _gridCheckBox.addActionListener( listener );
        _loopsSpinner.addChangeListener( listener );
        _radiusSlider.addChangeListener( listener );
        _lightbulbRadioButton.addActionListener( listener );
        _voltmeterRadioButton.addActionListener( listener );
        _compassCheckBox.addActionListener( listener );
        _electronsCheckBox.addActionListener( listener );
        
        // Update control panel to match the components that it's controlling.
        _strengthSlider.setValue( (int) ( 100.0 * _magnetModel.getStrength() / FaradayConfig.BAR_MAGNET_STRENGTH_MAX ) );
        _compassCheckBox.setSelected( _compassModel.isEnabled() );
        _gridCheckBox.setSelected( _gridGraphic.isVisible() );
        _loopsSpinner.setValue( new Integer( _pickupCoilModel.getNumberOfLoops() ) );
        _radiusSlider.setValue( (int) ( 100.0 * _pickupCoilModel.getRadius()  / FaradayConfig.MAX_PICKUP_RADIUS ) );
        _lightbulbRadioButton.setSelected( _lightbulbModel.isEnabled() );
        _voltmeterRadioButton.setSelected( _voltmeterModel.isEnabled() );
        _electronsCheckBox.setSelected( _coilGraphic.isElectronAnimationEnabled() );
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
                _magnetModel.setDirection( _magnetModel.getDirection() + Math.PI );
                _compassModel.startMovingNow();
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
            else if ( e.getSource() == _lightbulbRadioButton ) {
                // Lightbulb enable
                _lightbulbModel.setEnabled( _lightbulbRadioButton.isSelected() );
                _voltmeterModel.setEnabled( !_lightbulbRadioButton.isSelected() );
            }
            else if ( e.getSource() == _voltmeterRadioButton ) {
                // Voltmeter enable
                _voltmeterModel.setEnabled( _voltmeterRadioButton.isSelected() );
                _lightbulbModel.setEnabled( !_voltmeterRadioButton.isSelected() );
            }
            else if ( e.getSource() == _electronsCheckBox ) {
                // Electrons enabled
                _coilGraphic.setElectronAnimationEnabled( _electronsCheckBox.isSelected() );
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
                // Read the value
                int percent = _strengthSlider.getValue();
                // Update the model.
                int strength = (int) ( (  percent / 100.0 ) * FaradayConfig.BAR_MAGNET_STRENGTH_MAX );
                _magnetModel.setStrength( strength );
                // Update the label.
                Object[] args = { new Integer( percent ) };
                String text = MessageFormat.format( SimStrings.get( "MagnetAndCoilModule.magnetStrength" ), args );
                _strengthValue.setText( text );
            }
            else if ( e.getSource() == _radiusSlider ) {
                // Read the value.
                int percent = _radiusSlider.getValue();
                // Update the model.
                int radius = (int) ( ( percent / 100.0 ) * FaradayConfig.MAX_PICKUP_RADIUS );
                _pickupCoilModel.setRadius( radius );
                // Update the label.
                Object[] args = { new Integer( percent ) };
                String text = MessageFormat.format( SimStrings.get( "MagnetAndCoilModule.radius" ), args );
                _radiusValue.setText( text );
            }
            else if ( e.getSource() == _loopsSpinner ) {
                // Read the value.
                int numberOfLoops = ( (Integer) _loopsSpinner.getValue() ).intValue();
                // Update the model.
                _pickupCoilModel.setNumberOfLoops( numberOfLoops );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + e );
            }
        }
    }
}