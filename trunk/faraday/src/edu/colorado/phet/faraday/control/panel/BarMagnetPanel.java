/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.control.panel;

import java.awt.Color;
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
import edu.colorado.phet.faraday.control.ControlPanelSlider;
import edu.colorado.phet.faraday.model.BarMagnet;
import edu.colorado.phet.faraday.model.Compass;
import edu.colorado.phet.faraday.util.EasyGridBagLayout;
import edu.colorado.phet.faraday.view.BarMagnetGraphic;
import edu.colorado.phet.faraday.view.CompassGridGraphic;
import edu.colorado.phet.faraday.view.FieldMeterGraphic;


/**
 * BarMagnetPanel contains the controls for the bar magnet. 
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BarMagnetPanel extends FaradayPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Model & view components to be controlled.
    private BarMagnet _barMagnetModel;
    private Compass _compassModel;
    private BarMagnetGraphic _magnetGraphic;
    private CompassGridGraphic _gridGraphic;
    private FieldMeterGraphic _fieldMeterGraphic;

    // UI components
    private JButton _flipPolarityButton;
    private ControlPanelSlider _strengthSlider;
    private JCheckBox _seeInsideCheckBox;
    private JCheckBox _gridCheckBox;
    private JCheckBox _fieldMeterCheckBox;
    private JCheckBox _compassCheckBox;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor
     * 
     * @param barMagnetModel
     * @param compassModel
     * @param magnetGraphic
     * @param gridGraphic
     * @param fieldMeterGraphic
     */
    public BarMagnetPanel( 
            BarMagnet barMagnetModel, 
            Compass compassModel, 
            BarMagnetGraphic magnetGraphic, 
            CompassGridGraphic gridGraphic, 
            FieldMeterGraphic fieldMeterGraphic )
    {
        super();
        
        assert ( barMagnetModel != null );
        assert ( compassModel != null );
        assert ( magnetGraphic != null );
        assert ( gridGraphic != null );
        assert ( fieldMeterGraphic != null );

        // Things we'll be controlling.
        _barMagnetModel = barMagnetModel;
        _compassModel = compassModel;
        _magnetGraphic = magnetGraphic;
        _gridGraphic = gridGraphic;
        _fieldMeterGraphic = fieldMeterGraphic;
        
        // Title
        Border lineBorder = BorderFactory.createLineBorder( Color.BLACK, 2 );
        String title = SimStrings.get( "BarMagnetPanel.title" );
        TitledBorder titleBorder = BorderFactory.createTitledBorder( lineBorder, title );
        titleBorder.setTitleFont( getTitleFont() );
        setBorder( titleBorder );
        
        // Magnet strength
        {
            // Values are a percentage of the maximum.
            int max = 100;
            int min = (int) ( 100.0 * FaradayConfig.BAR_MAGNET_STRENGTH_MIN / FaradayConfig.BAR_MAGNET_STRENGTH_MAX );
            int range = max - min;

            // Slider
            String format = SimStrings.get( "BarMagnetPanel.strength" );
            _strengthSlider = new ControlPanelSlider( format );
            _strengthSlider.setMaximum( max );
            _strengthSlider.setMinimum( min );
            _strengthSlider.setValue( min );
            _strengthSlider.setMajorTickSpacing( range );
            _strengthSlider.setMinorTickSpacing( 10 );
            _strengthSlider.setSnapToTicks( false );
        }

        //  Flip Polarity button
        _flipPolarityButton = new JButton( SimStrings.get( "BarMagnetPanel.flipPolarity" ) );

        // Magnet transparency on/off
        _seeInsideCheckBox = new JCheckBox( SimStrings.get( "BarMagnetPanel.seeInside" ) );

        // Compass Grid on/off
        _gridCheckBox = new JCheckBox( SimStrings.get( "BarMagnetPanel.showGrid" ) );
        
        // Field Meter on/off
        _fieldMeterCheckBox = new JCheckBox( SimStrings.get( "BarMagnetPanel.showFieldMeter" ) );

        // Compass on/off
        _compassCheckBox = new JCheckBox( SimStrings.get( "BarMagnetPanel.showCompass" ) );

        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        layout.addFilledComponent( _strengthSlider, row++, 0, GridBagConstraints.HORIZONTAL );
        layout.addComponent( _flipPolarityButton, row++, 0 );
        layout.addComponent( _seeInsideCheckBox, row++, 0 );
        layout.addComponent( _gridCheckBox, row++, 0 );
        layout.addComponent( _compassCheckBox, row++, 0 );
        layout.addComponent( _fieldMeterCheckBox, row++, 0 );
        
        // Wire up event handling.
        EventListener listener = new EventListener();
        _flipPolarityButton.addActionListener( listener );
        _strengthSlider.addChangeListener( listener );
        _gridCheckBox.addActionListener( listener );
        _seeInsideCheckBox.addActionListener( listener );
        _fieldMeterCheckBox.addActionListener( listener );
        _compassCheckBox.addActionListener( listener );

        // Update control panel to match the components that it's controlling.
        _strengthSlider.setValue( (int) ( 100.0 * _barMagnetModel.getStrength() / FaradayConfig.BAR_MAGNET_STRENGTH_MAX ) );
        _seeInsideCheckBox.setSelected( _magnetGraphic.isTransparencyEnabled() );
        _gridCheckBox.setSelected( _gridGraphic.isVisible() );
        _fieldMeterCheckBox.setSelected( _fieldMeterGraphic.isVisible() );
        _compassCheckBox.setSelected( _compassModel.isEnabled() );
    }
   
    //----------------------------------------------------------------------------
    // Feature controls
    //----------------------------------------------------------------------------
    
    /**
     * Access to the "Flip Polarity" control.
     * 
     * @param visible true of false
     */
    public void setFlipPolarityVisible( boolean visible ) {
        _flipPolarityButton.setVisible( visible );
    }
    
    /**
     * Access to the "See Inside" control.
     * 
     * @param visible true of false
     */
    public void setSeeInsideVisible( boolean visible ) {
        _seeInsideCheckBox.setVisible( visible );
    }
    
    /**
     * Access to the "Show Field Meter" control.
     * 
     * @param visible true or false
     */
    public void setFieldMeterEnabled( boolean visible ) {
        _fieldMeterCheckBox.setVisible( visible );
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
                _barMagnetModel.flipPolarity();
                _compassModel.startMovingNow();
            }
            else if ( e.getSource() == _gridCheckBox ) {
                // Grid enable
                _gridGraphic.resetSpacing();
                _gridGraphic.setVisible( _gridCheckBox.isSelected() );
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
                _barMagnetModel.setStrength( strength );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + e );
            }
        }
    }
}
