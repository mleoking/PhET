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
import javax.swing.border.EmptyBorder;
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
    private JCheckBox _seeInsideCheckBox;
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

        JPanel fillerPanel = new JPanel();
        {
            fillerPanel.setLayout( new BoxLayout( fillerPanel, BoxLayout.X_AXIS ) );
            // WORKAROUND: Filler to set consistent panel width
            fillerPanel.add( Box.createHorizontalStrut( FaradayConfig.CONTROL_PANEL_MIN_WIDTH ) );
        }

        JPanel magnetPanel = new JPanel();
        {
            // Title
            TitledBorder magnetBorder = BorderFactory.createTitledBorder( SimStrings.get( "BarMagnetModule.magnetControls" ) );
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
            _flipPolarityButton = new JButton( SimStrings.get( "BarMagnetModule.flipPolarity" ) );

            // Magnet transparency on/off
            _seeInsideCheckBox = new JCheckBox( SimStrings.get( "BarMagnetModule.seeInside" ) );

            // Field Meter on/off
            _fieldMeterCheckBox = new JCheckBox( SimStrings.get( "BarMagnetModule.showFieldMeter" ) );

            // Compass on/off
            _compassCheckBox = new JCheckBox( SimStrings.get( "BarMagnetModule.showCompass" ) );

            EasyGridBagLayout layout = new EasyGridBagLayout( magnetPanel );
            magnetPanel.setLayout( layout );
            int row = 0;
            layout.addFilledComponent( strengthPanel, row++, 0, GridBagConstraints.HORIZONTAL );
            layout.addComponent( _flipPolarityButton, row++, 0 );
            layout.addComponent( _seeInsideCheckBox, row++, 0 );
            layout.addComponent( _fieldMeterCheckBox, row++, 0 );
            layout.addComponent( _compassCheckBox, row++, 0 );
        }

        // Add panels.
        addFullWidth( fillerPanel );
        addFullWidth( magnetPanel );
        
        // Wire up event handling.
        EventListener listener = new EventListener();
        _flipPolarityButton.addActionListener( listener );
        _strengthSlider.addChangeListener( listener );
        _seeInsideCheckBox.addActionListener( listener );
        _fieldMeterCheckBox.addActionListener( listener );
        _compassCheckBox.addActionListener( listener );

        // Update control panel to match the components that it's controlling.
        _strengthSlider.setValue( (int) ( 100.0 * _magnetModel.getStrength() / FaradayConfig.BAR_MAGNET_STRENGTH_MAX ) );
        _seeInsideCheckBox.setSelected( _magnetGraphic.isTransparencyEnabled() );
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
                _magnetModel.setDirection( _magnetModel.getDirection() + Math.PI );
                _compassModel.startMovingNow();
            }
            else if ( e.getSource() == _seeInsideCheckBox ) {
                // Magnet transparency enable
                _magnetGraphic.setTransparencyEnabled( _seeInsideCheckBox.isSelected() );
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
                // Read the value.
                int percent = _strengthSlider.getValue();
                // Update the model.
                double strength = (  percent / 100.0 ) * FaradayConfig.BAR_MAGNET_STRENGTH_MAX ;
                _magnetModel.setStrength( strength );
                // Update the label.
                Object[] args = { new Integer( percent ) };
                String text = MessageFormat.format( SimStrings.get( "BarMagnetModule.magnetStrength" ), args );
                _strengthValue.setText( text );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + e );
            }
        }
    }
}