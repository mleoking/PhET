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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.AbstractMagnet;
import edu.colorado.phet.faraday.model.Compass;
import edu.colorado.phet.faraday.module.BarMagnetModule;
import edu.colorado.phet.faraday.view.BarMagnetGraphic;
import edu.colorado.phet.faraday.view.FieldMeterGraphic;

/**
 * BarMagnetControlPanel is the control panel for the "Bar Magnet" module.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BarMagnetControlPanel extends FaradayControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model & view components to be controlled.
    private AbstractMagnet _magnetModel;
    private Compass _compassModel;
    private BarMagnetGraphic _magnetGraphic;
    private FieldMeterGraphic _fieldMeterGraphic;

    // UI components
    private JButton _flipPolarityButton;
    private JLabel _strengthValue;
    private JSlider _strengthSlider;
    private JCheckBox _magnetTransparencyCheckBox;
    private JCheckBox _fieldMeterCheckBox, _compassCheckBox; 

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
     * @param fieldMeterGraphic
     */
    public BarMagnetControlPanel( BarMagnetModule module, AbstractMagnet magnetModel, Compass compassModel, BarMagnetGraphic magnetGraphic, FieldMeterGraphic fieldMeterGraphic ) {

        super( module );

        assert ( magnetModel != null );
        assert ( compassModel != null );
        assert ( magnetGraphic != null );
        assert ( fieldMeterGraphic != null );

        // Things we'll be controlling.
        _magnetModel = magnetModel;
        _compassModel = compassModel;
        _magnetGraphic = magnetGraphic;
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

        // Add panels to control panel.
        addFullWidth( barMagnetPanel );
        addFullWidth( probePanel );
        addFullWidth( compassPanel );
        
        // Wire up event handling.
        EventListener listener = new EventListener();
        _flipPolarityButton.addActionListener( listener );
        _strengthSlider.addChangeListener( listener );
        _magnetTransparencyCheckBox.addActionListener( listener );
        _fieldMeterCheckBox.addActionListener( listener );
        _compassCheckBox.addActionListener( listener );

        // Update control panel to match the components that it's controlling.
        _strengthSlider.setValue( (int) _magnetModel.getStrength() );
        _magnetTransparencyCheckBox.setSelected( _magnetGraphic.isTransparencyEnabled() );
        _fieldMeterCheckBox.setSelected( _fieldMeterGraphic.isVisible() );
        _compassCheckBox.setSelected( _compassModel.isEnabled() );
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

        //----------------------------------------------------------------------------
        // ActionListener implementation
        //----------------------------------------------------------------------------

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

        //----------------------------------------------------------------------------
        // ChangeListener implementation
        //----------------------------------------------------------------------------

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
            else {
                throw new IllegalArgumentException( "unexpected event: " + e );
            }
        }
    }
}