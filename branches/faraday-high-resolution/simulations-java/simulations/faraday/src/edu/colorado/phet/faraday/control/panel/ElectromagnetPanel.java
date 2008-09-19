/* Copyright 2005-2008, University of Colorado */

package edu.colorado.phet.faraday.control.panel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.faraday.FaradayConstants;
import edu.colorado.phet.faraday.FaradayResources;
import edu.colorado.phet.faraday.FaradayStrings;
import edu.colorado.phet.faraday.model.*;
import edu.colorado.phet.faraday.view.BFieldOutsideGraphic;
import edu.colorado.phet.faraday.view.CoilGraphic;
import edu.colorado.phet.faraday.view.ElectromagnetGraphic;


/**
 * ElectromagnetPanel contains the controls related to the electromagnet.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ElectromagnetPanel extends FaradayPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model & view components to be controlled.
    private Electromagnet _electromagnetModel;
    private SourceCoil _sourceCoilModel;
    private Battery _batteryModel;
    private ACPowerSupply _acPowerSupplyModel;
    private Compass _compassModel;
    private FieldMeter _fieldMeterModel;
    private CoilGraphic _coilGraphic;
    private BFieldOutsideGraphic _bFieldOutsideGraphic;

    // UI components
    private JRadioButton _batteryRadioButton;
    private JRadioButton _acRadioButton;
    private JCheckBox _bFieldCheckBox;
    private JCheckBox _fieldMeterCheckBox;
    private JCheckBox _compassCheckBox; 
    private JSpinner _loopsSpinner;
    private JCheckBox _electronsCheckBox;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param electromagnetModel
     * @param sourceCoilModel
     * @param batteryModel
     * @param acPowerSupplyModel
     * @param compassModel
     * @param fieldMeterModel
     * @param electromagnetGraphic
     * @param bFieldOutsideGraphic
     */
    public ElectromagnetPanel(
            Electromagnet electromagnetModel,
            SourceCoil sourceCoilModel,
            Battery batteryModel,
            ACPowerSupply acPowerSupplyModel,
            Compass compassModel,
            FieldMeter fieldMeterModel,
            ElectromagnetGraphic electromagnetGraphic,
            BFieldOutsideGraphic bFieldOutsideGraphic ) {
        
        assert ( electromagnetModel != null );
        assert ( sourceCoilModel != null );
        assert ( batteryModel != null );
        assert ( acPowerSupplyModel != null );
        assert ( compassModel != null );
        assert ( fieldMeterModel != null );
        assert ( electromagnetGraphic != null );
        assert ( bFieldOutsideGraphic != null );

        // Things we'll be controlling.
        _electromagnetModel = electromagnetModel;
        _sourceCoilModel = sourceCoilModel;
        _batteryModel = batteryModel;
        _acPowerSupplyModel = acPowerSupplyModel;
        _compassModel = compassModel;
        _fieldMeterModel = fieldMeterModel;
        _coilGraphic = electromagnetGraphic.getCoilGraphic();
        _bFieldOutsideGraphic = bFieldOutsideGraphic;
        
        // Title
        Border border = BorderFactory.createLineBorder( Color.BLACK, 2 );
        TitledBorder titledBorder = BorderFactory.createTitledBorder( border, FaradayStrings.TITLE_ELECTROMAGNET_PANEL );
//        titledBorder.setTitleFont( getTitleFont() );
        setBorder( titledBorder );
        
        // B-field on/off
        _bFieldCheckBox = new JCheckBox( FaradayStrings.CHECK_BOX_SHOW_B_FIELD );
        
        // Field Meter on/off
        _fieldMeterCheckBox = new JCheckBox( FaradayStrings.CHECK_BOX_SHOW_FIELD_METER );

        // Compass on/off
        _compassCheckBox = new JCheckBox( FaradayStrings.CHECK_BOX_SHOW_COMPASS );

        // Electrons on/off
        _electronsCheckBox = new JCheckBox( FaradayStrings.CHECK_BOX_SHOW_ELECTRONS );
        
        // Number of loops
        JPanel loopsPanel = new JPanel();
        {
            JLabel loopsLabel = new JLabel( FaradayStrings.LABEL_NUMBER_OF_LOOPS );

            // Spinner, keyboard editing disabled.
            SpinnerNumberModel spinnerModel = new SpinnerNumberModel();
            spinnerModel.setMaximum( new Integer( FaradayConstants.ELECTROMAGNET_LOOPS_MAX ) );
            spinnerModel.setMinimum( new Integer( FaradayConstants.ELECTROMAGNET_LOOPS_MIN ) );
            spinnerModel.setValue( new Integer( FaradayConstants.ELECTROMAGNET_LOOPS_MIN ) );
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
        
        JPanel sourcePanel = new JPanel();
        {
            // Title
            TitledBorder indicatorBorder = new TitledBorder( FaradayStrings.TITLE_CURRENT_SOURCE );
            sourcePanel.setBorder( indicatorBorder );

            // Layout
            EasyGridBagLayout layout = new EasyGridBagLayout( sourcePanel );
            sourcePanel.setLayout( layout );

            // Radio buttons with text & icons.
            ImageIcon batteryIcon = new ImageIcon( FaradayResources.getImage( FaradayConstants.BATTERY_ICON ) );
            ImageIcon batteryIconSelected = new ImageIcon( FaradayResources.getImage( FaradayConstants.BATTERY_ICON_SELECTED ) );
            _batteryRadioButton = new JRadioButton( FaradayStrings.RADIO_BUTTON_DC, batteryIcon );
            _batteryRadioButton.setVerticalTextPosition( SwingConstants.BOTTOM );
            _batteryRadioButton.setHorizontalTextPosition( SwingConstants.CENTER );
            _batteryRadioButton.setSelectedIcon( batteryIconSelected );

            ImageIcon acIcon = new ImageIcon( FaradayResources.getImage( FaradayConstants.AC_POWER_SUPPLY_ICON ) );
            ImageIcon acIconSelected = new ImageIcon( FaradayResources.getImage( FaradayConstants.AC_POWER_SUPPLY_ICON_SELECTED ) );
            _acRadioButton = new JRadioButton( FaradayStrings.RADIO_BUTTON_AC, acIcon );
            _acRadioButton.setVerticalTextPosition( SwingConstants.BOTTOM );
            _acRadioButton.setHorizontalTextPosition( SwingConstants.CENTER );
            _acRadioButton.setSelectedIcon( acIconSelected );

            // Horizontal layout
            layout.addAnchoredComponent( _batteryRadioButton, 0, 0, GridBagConstraints.WEST );
            layout.addAnchoredComponent( _acRadioButton, 0, 1, GridBagConstraints.WEST );

            // Button group
            ButtonGroup group = new ButtonGroup();
            group.add( _batteryRadioButton );
            group.add( _acRadioButton );
        }
            
        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        layout.addFilledComponent( sourcePanel, row++, 0, GridBagConstraints.HORIZONTAL );
        layout.addComponent( loopsPanel, row++, 0 );
        layout.addComponent( _bFieldCheckBox, row++, 0 );
        layout.addComponent( _compassCheckBox, row++, 0 );
        layout.addComponent( _fieldMeterCheckBox, row++, 0 );
        layout.addComponent( _electronsCheckBox, row++, 0 );

        // Wire up event handling.
        EventListener listener = new EventListener();
        _batteryRadioButton.addActionListener( listener );
        _acRadioButton.addActionListener( listener );
        _bFieldCheckBox.addActionListener( listener );
        _fieldMeterCheckBox.addActionListener( listener );
        _compassCheckBox.addActionListener( listener );
        _electronsCheckBox.addActionListener( listener );
        _loopsSpinner.addChangeListener( listener );

        // Set the state of the controls.
        update();
    }
    
    /**
     * Updates the control panel to match the state of the things that it's controlling.
     */
    public void update() {
        _batteryRadioButton.setSelected( _batteryModel.isEnabled() );
        _acRadioButton.setSelected( _acPowerSupplyModel.isEnabled() );
        _bFieldCheckBox.setSelected( _bFieldOutsideGraphic.isVisible() );
        _fieldMeterCheckBox.setSelected( _fieldMeterModel.isEnabled() );
        _compassCheckBox.setSelected( _compassModel.isEnabled() );
        _electronsCheckBox.setSelected( _coilGraphic.isElectronAnimationEnabled() );
        _loopsSpinner.setValue( new Integer( _sourceCoilModel.getNumberOfLoops() ) ); 
    }
    
    //----------------------------------------------------------------------------
    // Feature controls
    //----------------------------------------------------------------------------
    
    /**
     * Access to the "Show Field Meter" control.
     * 
     * @param visible true or false
     */
    public void setFieldMeterControlVisible( boolean visible ) {
        _fieldMeterCheckBox.setVisible( visible );
    } 
    
    /**
     * Access to the "Show B-Field" control.
     * 
     * @param visible true or false
     * @return
     */
    public void setBFieldControlVisible( boolean visible ) {
        _bFieldCheckBox.setVisible( visible );
    }
    
    /**
     * Access to the "Show Electrons" control.
     * 
     * @param visible true or false
     * @return
     */
    public void setElectronsControlVisible( boolean visible ) {
        _electronsCheckBox.setVisible( visible );
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
            if ( e.getSource() == _batteryRadioButton ) {
                // Battery (DC) source
                _batteryModel.setEnabled( true );
                _acPowerSupplyModel.setEnabled( false );
                _electromagnetModel.setCurrentSource( _batteryModel );
            }
            else if ( e.getSource() == _acRadioButton ) {
                // AC source
                _batteryModel.setEnabled( false );
                _acPowerSupplyModel.setEnabled( true );
                _electromagnetModel.setCurrentSource( _acPowerSupplyModel );
            }
            else if ( e.getSource() == _bFieldCheckBox ) {
                // B-field enable
                _bFieldOutsideGraphic.setVisible( _bFieldCheckBox.isSelected() );
            }
            else if ( e.getSource() == _fieldMeterCheckBox ) {
                // Meter enable
                _fieldMeterModel.setEnabled( _fieldMeterCheckBox.isSelected() );
            }
            else if ( e.getSource() == _compassCheckBox ) {
                // Compass enable
                _compassModel.setEnabled( _compassCheckBox.isSelected() );
            }
            else if ( e.getSource() == _electronsCheckBox ) {
                // Electrons enable
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
            if ( e.getSource() == _loopsSpinner ) {
                // Read the value.
                int numberOfLoops = ( (Integer) _loopsSpinner.getValue() ).intValue();
                // Update the model.
                _sourceCoilModel.setNumberOfLoops( numberOfLoops );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + e );
            }
        }
    }
}
