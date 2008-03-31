/* Copyright 2005-2008, University of Colorado */

package edu.colorado.phet.faraday.control.panel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.faraday.FaradayConstants;
import edu.colorado.phet.faraday.FaradayStrings;
import edu.colorado.phet.faraday.model.BarMagnet;
import edu.colorado.phet.faraday.model.Compass;
import edu.colorado.phet.faraday.model.FieldMeter;
import edu.colorado.phet.faraday.view.BarMagnetGraphic;
import edu.colorado.phet.faraday.view.CompassGridGraphic;
import edu.colorado.phet.faraday.view.EarthGraphic;


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
    private FieldMeter _fieldMeterModel;
    private BarMagnetGraphic _barMagnetGraphic;
    private CompassGridGraphic _gridGraphic;
    private EarthGraphic _earthGraphic;

    // UI components
    private JButton _flipPolarityButton;
    private LinearValueControl _strengthControl;
    private JCheckBox _seeInsideCheckBox;
    private JCheckBox _gridCheckBox;
    private JCheckBox _fieldMeterCheckBox;
    private JCheckBox _compassCheckBox;
    private JCheckBox _earthCheckBox;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor
     * 
     * @param barMagnetModel
     * @param compassModel
     * @param fieldMeterModel
     * @param barMagnetGraphic
     * @param gridGraphic
     * @param earthGraphic
     */
    public BarMagnetPanel( 
            BarMagnet barMagnetModel, 
            Compass compassModel,
            FieldMeter fieldMeterModel,
            BarMagnetGraphic barMagnetGraphic, 
            CompassGridGraphic gridGraphic,
            EarthGraphic earthGraphic )
    {
        super();
        
        assert ( barMagnetModel != null );
        assert ( compassModel != null );
        assert ( fieldMeterModel != null );
        assert ( barMagnetGraphic != null );
        assert ( gridGraphic != null );

        // Things we'll be controlling.
        _barMagnetModel = barMagnetModel;
        _compassModel = compassModel;
        _fieldMeterModel = fieldMeterModel;
        _barMagnetGraphic = barMagnetGraphic;
        _gridGraphic = gridGraphic;
        _earthGraphic = earthGraphic;
        
        // Title
        Border lineBorder = BorderFactory.createLineBorder( Color.BLACK, 2 );
        TitledBorder titleBorder = BorderFactory.createTitledBorder( lineBorder, FaradayStrings.TITLE_BAR_MAGNET_PANEL );
        titleBorder.setTitleFont( getTitleFont() );
        setBorder( titleBorder );
        
        // Magnet strength
        {
            // Values are a percentage of the maximum.
            int max = 100;
            int min = (int) ( 100.0 * FaradayConstants.BAR_MAGNET_STRENGTH_MIN / FaradayConstants.BAR_MAGNET_STRENGTH_MAX );

            // Slider
            _strengthControl = new LinearValueControl( min, max, FaradayStrings.LABEL_STRENGTH, "0", "%" );
            _strengthControl.setValue( min );
            _strengthControl.setMajorTickSpacing( 50 );
            _strengthControl.setMinorTickSpacing( 10 );
            _strengthControl.setTextFieldEditable( true );
            _strengthControl.setTextFieldColumns( 3 );
            _strengthControl.setUpDownArrowDelta( 1 );
            _strengthControl.setBorder( BorderFactory.createEtchedBorder() );
        }

        //  Flip Polarity button
        _flipPolarityButton = new JButton( FaradayStrings.BUTTON_FLIP_POLARITY );

        // Magnet transparency on/off
        _seeInsideCheckBox = new JCheckBox( FaradayStrings.CHECK_BOX_SEE_INSIDE );

        // Compass Grid on/off
        _gridCheckBox = new JCheckBox( FaradayStrings.CHECK_BOX_SHOW_GRID );
        
        // Field Meter on/off
        _fieldMeterCheckBox = new JCheckBox( FaradayStrings.CHECK_BOX_SHOW_FIELD_METER );

        // Compass on/off
        _compassCheckBox = new JCheckBox( FaradayStrings.CHECK_BOX_SHOW_COMPASS );
        
        // Earth on/off
        _earthCheckBox = new JCheckBox( FaradayStrings.CHECK_BOX_SHOW_EARTH );

        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        layout.addFilledComponent( _strengthControl, row++, 0, GridBagConstraints.HORIZONTAL );
        layout.addComponent( _flipPolarityButton, row++, 0 );
        layout.addComponent( _seeInsideCheckBox, row++, 0 );
        layout.addComponent( _gridCheckBox, row++, 0 );
        layout.addComponent( _compassCheckBox, row++, 0 );
        layout.addComponent( _fieldMeterCheckBox, row++, 0 );
        if ( earthGraphic != null ) {
            layout.addComponent( _earthCheckBox, row++, 0 );
        }
        
        // Wire up event handling.
        EventListener listener = new EventListener();
        _flipPolarityButton.addActionListener( listener );
        _strengthControl.addChangeListener( listener );
        _gridCheckBox.addActionListener( listener );
        _seeInsideCheckBox.addActionListener( listener );
        _fieldMeterCheckBox.addActionListener( listener );
        _compassCheckBox.addActionListener( listener );
        _earthCheckBox.addActionListener( listener );

        // Set the state of the controls.
        update();
    }
   
    /**
     * Updates the control panel to match the state of the things that it's controlling.
     */
    public void update() {
        _strengthControl.setValue( (int) ( 100.0 * _barMagnetModel.getStrength() / FaradayConstants.BAR_MAGNET_STRENGTH_MAX ) );
        _seeInsideCheckBox.setSelected( _barMagnetGraphic.isSeeInsideEnabled() );
        _gridCheckBox.setSelected( _gridGraphic.isVisible() );
        _fieldMeterCheckBox.setSelected( _fieldMeterModel.isEnabled() );
        _compassCheckBox.setSelected( _compassModel.isEnabled() );
        if ( _earthGraphic != null ) {
            _earthCheckBox.setSelected( _earthGraphic.isVisible() );
        }
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
    public void setFieldMeterVisible( boolean visible ) {
        _fieldMeterCheckBox.setVisible( visible );
    }
    
    /**
     * Access to the "Show Earth" control.
     * 
     * @param visible true or false
     */
    public void setShowEarthVisible( boolean visible ) {
        _earthCheckBox.setVisible( visible );
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
                _barMagnetGraphic.setSeeInsideEnabled( _seeInsideCheckBox.isSelected() );
            }
            else if ( e.getSource() == _fieldMeterCheckBox ) {
                // Meter enable
                _fieldMeterModel.setEnabled( _fieldMeterCheckBox.isSelected() );
            }
            else if ( e.getSource() == _compassCheckBox ) {
                // Compass enable
                _compassModel.setEnabled( _compassCheckBox.isSelected() );
            }
            else if ( e.getSource() == _earthCheckBox ) {
                _earthGraphic.setVisible( _earthCheckBox.isSelected() );
                if ( _earthCheckBox.isSelected() ) {
                    _barMagnetModel.setDirection( _barMagnetModel.getDirection() + Math.PI/2 );
                }
                else {
                    _barMagnetModel.setDirection( _barMagnetModel.getDirection() - Math.PI/2 );
                }
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
            if ( e.getSource() == _strengthControl ) {
                // Read the value.
                int percent = (int)_strengthControl.getValue();
                // Update the model.
                double strength = (  percent / 100.0 ) * FaradayConstants.BAR_MAGNET_STRENGTH_MAX ;
                _barMagnetModel.setStrength( strength );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + e );
            }
        }
    }
}
