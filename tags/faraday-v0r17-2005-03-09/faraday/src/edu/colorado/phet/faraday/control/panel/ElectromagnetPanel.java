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

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.ACSource;
import edu.colorado.phet.faraday.model.Battery;
import edu.colorado.phet.faraday.model.Compass;
import edu.colorado.phet.faraday.model.SourceCoil;
import edu.colorado.phet.faraday.view.CoilGraphic;
import edu.colorado.phet.faraday.view.CompassGridGraphic;
import edu.colorado.phet.faraday.view.FieldMeterGraphic;


/**
 * ElectromagnetPanel contains the controls related to the electromagnet.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ElectromagnetPanel extends FaradayPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model & view components to be controlled.
    private SourceCoil _sourceCoilModel;
    private Battery _batteryModel;
    private ACSource _acSourceModel;
    private Compass _compassModel;
    private CoilGraphic _coilGraphic;
    private CompassGridGraphic _gridGraphic;
    private FieldMeterGraphic _fieldMeterGraphic;

    // UI components
    private JRadioButton _batteryRadioButton;
    private JRadioButton _acRadioButton;
    private JCheckBox _gridCheckBox;
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
     * @param sourceCoilModel
     * @param batteryModel
     * @param acSourceModel
     * @param coilGrpahic
     * @param gridGraphic
     * @param fieldMeterGraphic
     */
    public ElectromagnetPanel(
            SourceCoil sourceCoilModel,
            Battery batteryModel,
            ACSource acSourceModel,
            Compass compassModel,
            CoilGraphic coilGraphic,
            CompassGridGraphic gridGraphic, 
            FieldMeterGraphic fieldMeterGraphic ) {
        
        assert ( sourceCoilModel != null );
        assert ( batteryModel != null );
        assert ( acSourceModel != null );
        assert ( compassModel != null );
        assert ( coilGraphic != null );
        assert ( gridGraphic != null );
        assert ( fieldMeterGraphic != null );

        // Things we'll be controlling.
        _sourceCoilModel = sourceCoilModel;
        _batteryModel = batteryModel;
        _acSourceModel = acSourceModel;
        _compassModel = compassModel;
        _coilGraphic = coilGraphic;
        _gridGraphic = gridGraphic;
        _fieldMeterGraphic = fieldMeterGraphic;
        
        // Title
        Border border = BorderFactory.createLineBorder( Color.BLACK, 2 );
        String title = SimStrings.get( "ElectromagnetPanel.title" );
        TitledBorder titledBorder = BorderFactory.createTitledBorder( border, title );
        titledBorder.setTitleFont( getTitleFont() );
        setBorder( titledBorder );
        
        // Compass Grid on/off
        _gridCheckBox = new JCheckBox( SimStrings.get( "ElectromagnetPanel.showGrid" ) );
        
        // Field Meter on/off
        _fieldMeterCheckBox = new JCheckBox( SimStrings.get( "ElectromagnetPanel.showFieldMeter" ) );

        // Compass on/off
        _compassCheckBox = new JCheckBox( SimStrings.get( "ElectromagnetPanel.showCompass" ) );

        // Electrons on/off
        _electronsCheckBox = new JCheckBox( SimStrings.get( "ElectromagnetPanel.showElectrons" ) );
        
        // Number of loops
        JPanel loopsPanel = new JPanel();
        {
            JLabel loopsLabel = new JLabel( SimStrings.get( "ElectromagnetPanel.numberOfLoops" ) );

            // Spinner, keyboard editing disabled.
            SpinnerNumberModel spinnerModel = new SpinnerNumberModel();
            spinnerModel.setMaximum( new Integer( FaradayConfig.ELECTROMAGNET_LOOPS_MAX ) );
            spinnerModel.setMinimum( new Integer( FaradayConfig.ELECTROMAGNET_LOOPS_MIN ) );
            spinnerModel.setValue( new Integer( FaradayConfig.ELECTROMAGNET_LOOPS_MIN ) );
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
            TitledBorder indicatorBorder = new TitledBorder( SimStrings.get( "ElectromagnetPanel.currentSource" ) );
            sourcePanel.setBorder( indicatorBorder );

            // Radio buttons
            _batteryRadioButton = new JRadioButton( SimStrings.get( "ElectromagnetPanel.dcSource" ) );
            _acRadioButton = new JRadioButton( SimStrings.get( "ElectromagnetPanel.acSource" ) );
            ButtonGroup group = new ButtonGroup();
            group.add( _batteryRadioButton );
            group.add( _acRadioButton );

            // Layout
            EasyGridBagLayout layout = new EasyGridBagLayout( sourcePanel );
            sourcePanel.setLayout( layout );
            layout.addAnchoredComponent( _batteryRadioButton, 0, 0, GridBagConstraints.WEST );
            layout.addAnchoredComponent( _acRadioButton, 1, 0, GridBagConstraints.WEST );
        }
            
        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        layout.addComponent( _gridCheckBox, row++, 0 );
        layout.addComponent( _compassCheckBox, row++, 0 );
        layout.addComponent( _fieldMeterCheckBox, row++, 0 );
        layout.addComponent( _electronsCheckBox, row++, 0 );
        layout.addComponent( loopsPanel, row++, 0 );
        layout.addFilledComponent( sourcePanel, row++, 0, GridBagConstraints.HORIZONTAL );
        
        // Wire up event handling.
        EventListener listener = new EventListener();
        _batteryRadioButton.addActionListener( listener );
        _acRadioButton.addActionListener( listener );
        _gridCheckBox.addActionListener( listener );
        _fieldMeterCheckBox.addActionListener( listener );
        _compassCheckBox.addActionListener( listener );
        _electronsCheckBox.addActionListener( listener );
        _loopsSpinner.addChangeListener( listener );

        // Update control panel to match the components that it's controlling.
        _batteryRadioButton.setSelected( _batteryModel.isEnabled() );
        _acRadioButton.setSelected( _acSourceModel.isEnabled() );
        _gridCheckBox.setSelected( _gridGraphic.isVisible() );
        _fieldMeterCheckBox.setSelected( _fieldMeterGraphic.isVisible() );
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
                _acSourceModel.setEnabled( false );
                _sourceCoilModel.setVoltageSource( _batteryModel );
            }
            else if ( e.getSource() == _acRadioButton ) {
                // AC source
                _batteryModel.setEnabled( false );
                _acSourceModel.setEnabled( true );
                _sourceCoilModel.setVoltageSource( _acSourceModel );
            }
            else if ( e.getSource() == _gridCheckBox ) {
                // Grid enable
                _gridGraphic.resetSpacing();
                _gridGraphic.setVisible( _gridCheckBox.isSelected() );
            }
            else if ( e.getSource() == _fieldMeterCheckBox ) {
                // Meter enable
                _fieldMeterGraphic.setVisible( _fieldMeterCheckBox.isSelected() );
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
