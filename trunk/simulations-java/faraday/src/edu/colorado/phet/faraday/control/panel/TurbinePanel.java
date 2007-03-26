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

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConstants;
import edu.colorado.phet.faraday.model.Compass;
import edu.colorado.phet.faraday.model.FieldMeter;
import edu.colorado.phet.faraday.model.Turbine;
import edu.colorado.phet.faraday.view.CompassGridGraphic;


/**
 * TurbinePanel contains the controls for the turbine. 
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TurbinePanel extends FaradayPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Model & view components to be controlled.
    private Turbine _turbineModel;
    private Compass _compassModel;
    private FieldMeter _fieldMeterModel;
    private CompassGridGraphic _gridGraphic;

    // UI components
    private JLabel _strengthValue;
    private JSlider _strengthSlider;
    private JCheckBox _gridCheckBox;
    private JCheckBox _fieldMeterCheckBox;
    private JCheckBox _compassCheckBox;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor
     * 
     * @param turbineModel
     * @param compassModel
     * @param fieldMeterModel
     * @param gridGraphic
     */
    public TurbinePanel( 
            Turbine turbineModel, 
            Compass compassModel, 
            FieldMeter fieldMeterModel,
            CompassGridGraphic gridGraphic )
    {
        super();
        
        assert ( turbineModel != null );
        assert ( compassModel != null );
        assert ( fieldMeterModel != null );
        assert ( gridGraphic != null );

        // Things we'll be controlling.
        _turbineModel = turbineModel;
        _compassModel = compassModel;
        _fieldMeterModel = fieldMeterModel;
        _gridGraphic = gridGraphic;
        
        // Title
        Border lineBorder = BorderFactory.createLineBorder( Color.BLACK, 2 );
        String title = SimStrings.getInstance().getString( "TurbinePanel.title" );
        TitledBorder titleBorder = BorderFactory.createTitledBorder( lineBorder, title );
        titleBorder.setTitleFont( getTitleFont() );
        setBorder( titleBorder );
        
        // Magnet strength
        JPanel strengthPanel = new JPanel();
        {
            strengthPanel.setBorder( BorderFactory.createEtchedBorder() );

            // Values are a percentage of the maximum.
            int max = 100;
            int min = (int) ( 100.0 * _turbineModel.getMinStrength() / _turbineModel.getMaxStrength() );
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

        // Compass Grid on/off
        _gridCheckBox = new JCheckBox( SimStrings.getInstance().getString( "TurbinePanel.showGrid" ) );
        
        // Field Meter on/off
        _fieldMeterCheckBox = new JCheckBox( SimStrings.getInstance().getString( "BarMagnetPanel.showFieldMeter" ) );
        
        // Compass on/off
        _compassCheckBox = new JCheckBox( SimStrings.getInstance().getString( "TurbinePanel.showCompass" ) );

        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        layout.addFilledComponent( strengthPanel, row++, 0, GridBagConstraints.HORIZONTAL );
        layout.addComponent( _gridCheckBox, row++, 0 );
        layout.addComponent( _compassCheckBox, row++, 0 );
        layout.addComponent( _fieldMeterCheckBox, row++, 0 );
        
        // Wire up event handling.
        EventListener listener = new EventListener();
        _strengthSlider.addChangeListener( listener );
        _gridCheckBox.addActionListener( listener );
        _fieldMeterCheckBox.addActionListener( listener );
        _compassCheckBox.addActionListener( listener );

        // Set the state of the controls.
        update(); 
    }
    
    /**
     * Updates the control panel to match the state of the things that it's controlling.
     */
    public void update() {
        _strengthSlider.setValue( (int) ( 100.0 * _turbineModel.getStrength() / _turbineModel.getMaxStrength() ) );
        _gridCheckBox.setSelected( _gridGraphic.isVisible() );
        _fieldMeterCheckBox.setSelected( _fieldMeterModel.isEnabled() );
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
            if ( e.getSource() == _gridCheckBox ) {
                // Grid enable
                _gridGraphic.resetSpacing();
                _gridGraphic.setVisible( _gridCheckBox.isSelected() );
            }
            else if ( e.getSource() == _fieldMeterCheckBox ) {
                // Meter enable
                _fieldMeterModel.setEnabled( _fieldMeterCheckBox.isSelected() );
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
                double strength = (  percent / 100.0 ) * FaradayConstants.BAR_MAGNET_STRENGTH_MAX ;
                _turbineModel.setStrength( strength );
                // Update the label.
                Object[] args = { new Integer( percent ) };
                String text = MessageFormat.format( SimStrings.getInstance().getString( "TurbinePanel.strength" ), args );
                _strengthValue.setText( text );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + e );
            }
        }
    }
}
