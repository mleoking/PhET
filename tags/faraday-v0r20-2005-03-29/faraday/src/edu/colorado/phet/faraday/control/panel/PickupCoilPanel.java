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
import java.io.IOException;
import java.text.MessageFormat;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.Lightbulb;
import edu.colorado.phet.faraday.model.PickupCoil;
import edu.colorado.phet.faraday.model.Voltmeter;
import edu.colorado.phet.faraday.view.CoilGraphic;
import edu.colorado.phet.faraday.view.PickupCoilGraphic;


/**
 * PickupCoilPanel contains the controls for the pickup coil.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PickupCoilPanel extends FaradayPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model & view components to be controlled.
    private PickupCoil _pickupCoilModel;
    private CoilGraphic _coilGraphic;
    private Lightbulb _lightbulbModel;
    private Voltmeter _voltmeterModel;

    // UI components
    private JSpinner _loopsSpinner;
    private JSlider _areaSlider;
    private JRadioButton _voltmeterRadioButton;
    private JRadioButton _lightbulbRadioButton;
    private JCheckBox _electronsCheckBox;
    private JLabel _areaValue;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @paaram pickupCoilModel
     * @param coilGraphic
     * @param lightBulbModel
     * @param voltmeterModel
     */
    public PickupCoilPanel( 
            PickupCoil pickupCoilModel,
            PickupCoilGraphic pickupCoilGraphic,
            Lightbulb lightbulbModel,
            Voltmeter voltmeterModel ) {

        super();
        
        assert( pickupCoilModel != null );
        assert( pickupCoilGraphic != null );
        assert( lightbulbModel != null );
        assert( voltmeterModel != null );
        
        // Things we'll be controlling.
        _pickupCoilModel = pickupCoilModel;
        _coilGraphic = pickupCoilGraphic.getCoilGraphic();
        _lightbulbModel = lightbulbModel;
        _voltmeterModel = voltmeterModel;
        
        //  Title
        Border lineBorder = BorderFactory.createLineBorder( Color.BLACK, 2 );
        String title = SimStrings.get( "PickupCoilPanel.title" );
        TitledBorder titleBorder = BorderFactory.createTitledBorder( lineBorder, title );
        titleBorder.setTitleFont( getTitleFont() );
        setBorder( titleBorder );

        // Number of loops
        JPanel loopsPanel = new JPanel();
        {
            JLabel loopsLabel = new JLabel( SimStrings.get( "PickupCoilPanel.numberOfLoops" ) );

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

        // Loop area
        JPanel areaPanel = new JPanel();
        {
            areaPanel.setBorder( BorderFactory.createEtchedBorder() );

            // Values are a percentage of the maximum.
            int max = 100;
            int min = (int) ( 100.0 * FaradayConfig.MIN_PICKUP_LOOP_AREA / FaradayConfig.MAX_PICKUP_LOOP_AREA );
            int range = max - min;

            // Slider
            _areaSlider = new JSlider();
            _areaSlider.setMaximum( max );
            _areaSlider.setMinimum( min );
            _areaSlider.setValue( min );

            // Slider tick marks
            _areaSlider.setMajorTickSpacing( range );
            _areaSlider.setMinorTickSpacing( 10 );
            _areaSlider.setSnapToTicks( false );
            _areaSlider.setPaintTicks( true );
            _areaSlider.setPaintLabels( true );

            // Value
            _areaValue = new JLabel( UNKNOWN_VALUE );

            // Layout
            EasyGridBagLayout layout = new EasyGridBagLayout( areaPanel );
            areaPanel.setLayout( layout );
            layout.addAnchoredComponent( _areaValue, 0, 0, GridBagConstraints.WEST );
            layout.addAnchoredComponent( _areaSlider, 1, 0, GridBagConstraints.WEST );
        }

        JPanel indicatorPanel = new JPanel();
        {
            // Title
            TitledBorder indicatorBorder = new TitledBorder( SimStrings.get( "PickupCoilPanel.indicator" ) );
            indicatorPanel.setBorder( indicatorBorder );

            // Layout
            EasyGridBagLayout layout = new EasyGridBagLayout( indicatorPanel );
            indicatorPanel.setLayout( layout );
            
            // Radio buttons
            try {
                // Radio buttons with icons.
                ImageIcon lightbulbIcon = new ImageIcon( ImageLoader.loadBufferedImage( FaradayConfig.LIGHTBULB_ICON ) );
                ImageIcon lightbulbIconSelected = new ImageIcon( ImageLoader.loadBufferedImage( FaradayConfig.LIGHTBULB_ICON_SELECTED ) );
                _lightbulbRadioButton = new JRadioButton( lightbulbIcon );
                _lightbulbRadioButton.setSelectedIcon( lightbulbIconSelected );
                
                ImageIcon voltmeterIcon = new ImageIcon( ImageLoader.loadBufferedImage( FaradayConfig.VOLTMETER_ICON ) );
                ImageIcon voltmeterIconSelected = new ImageIcon( ImageLoader.loadBufferedImage( FaradayConfig.VOLTMETER_ICON_SELECTED ) );
                _voltmeterRadioButton = new JRadioButton( voltmeterIcon );
                _voltmeterRadioButton.setSelectedIcon( voltmeterIconSelected );
                
                // Horizontal layout.
                layout.addAnchoredComponent( _lightbulbRadioButton, 0, 0, GridBagConstraints.WEST );
                layout.addAnchoredComponent( _voltmeterRadioButton, 0, 1, GridBagConstraints.WEST );
            }
            catch ( IOException ioe ) {
                // Radio buttons with text.
                _lightbulbRadioButton = new JRadioButton( SimStrings.get( "PickupCoilPanel.lightbulb" ) );
                _voltmeterRadioButton = new JRadioButton( SimStrings.get( "PickupCoilPanel.voltmeter" ) );
                
                // Vertical layout
                layout.addAnchoredComponent( _lightbulbRadioButton, 0, 0, GridBagConstraints.WEST );
                layout.addAnchoredComponent( _voltmeterRadioButton, 1, 0, GridBagConstraints.WEST );
            }
            
            // Button group
            ButtonGroup group = new ButtonGroup();
            group.add( _lightbulbRadioButton );
            group.add( _voltmeterRadioButton );   
        }

        // Electrons on/off
        _electronsCheckBox = new JCheckBox( SimStrings.get( "PickupCoilPanel.showElectrons" ) );

        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        layout.addFilledComponent( indicatorPanel, row++, 0, GridBagConstraints.HORIZONTAL );
        layout.addComponent( loopsPanel, row++, 0 );
        layout.addFilledComponent( areaPanel, row++, 0, GridBagConstraints.HORIZONTAL );
        layout.addComponent( _electronsCheckBox, row++, 0 );

        // Wire up event handling
        EventListener listener = new EventListener();
        _loopsSpinner.addChangeListener( listener );
        _areaSlider.addChangeListener( listener );
        _lightbulbRadioButton.addActionListener( listener );
        _voltmeterRadioButton.addActionListener( listener );
        _electronsCheckBox.addActionListener( listener );

        // Update control panel to match the components that it's controlling.
        _loopsSpinner.setValue( new Integer( _pickupCoilModel.getNumberOfLoops() ) );
        _areaSlider.setValue( (int) ( 100.0 * _pickupCoilModel.getLoopArea() / FaradayConfig.MAX_PICKUP_LOOP_AREA ) );
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
            if ( e.getSource() == _lightbulbRadioButton ) {
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
            if ( e.getSource() == _areaSlider ) {
                // Read the value.
                int percent = _areaSlider.getValue();
                // Update the model.
                double area = ( percent / 100.0 ) * FaradayConfig.MAX_PICKUP_LOOP_AREA;
                _pickupCoilModel.setLoopArea( area );
                // Update the label.
                Object[] args = { new Integer( percent ) };
                String text = MessageFormat.format( SimStrings.get( "PickupCoilPanel.area" ), args );
                _areaValue.setText( text );
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