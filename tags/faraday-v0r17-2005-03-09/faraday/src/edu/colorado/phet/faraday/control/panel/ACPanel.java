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
import java.text.MessageFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.ACSource;


/**
 * ACPanel contains the controls for the AC source.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ACPanel extends FaradayPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model & view components to be controlled.
    private ACSource _acSourceModel;

    // UI components
    private JSlider _acMaxAmplitudeSlider;
    private JLabel _acMaxAmplitudeValue;
    private JSlider _acFrequencySlider;
    private JLabel _acFrequencyValue;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param acSourceModel
     */
    public ACPanel( ACSource acSourceModel ) {

        super();

        assert ( acSourceModel != null );
        _acSourceModel = acSourceModel;

        // Title
        Border border = BorderFactory.createLineBorder( Color.BLACK, 2 );
        String title = SimStrings.get( "ACPanel.title" );
        TitledBorder titledBorder = BorderFactory.createTitledBorder( border, title );
        titledBorder.setTitleFont( getTitleFont() );
        setBorder( titledBorder );

        JPanel acAmplitudePanel = new JPanel();
        {
            acAmplitudePanel.setBorder( BorderFactory.createEtchedBorder() );

            // Range of values
            int max = (int) ( 100.0 * FaradayConfig.AC_MAXAMPLITUDE_MAX );
            int min = (int) ( 100.0 * FaradayConfig.AC_MAXAMPLITUDE_MIN );
            int range = max - min;

            // Slider
            _acMaxAmplitudeSlider = new JSlider();
            _acMaxAmplitudeSlider.setMaximum( max );
            _acMaxAmplitudeSlider.setMinimum( min );
            _acMaxAmplitudeSlider.setValue( min );

            // Slider tick marks
            _acMaxAmplitudeSlider.setMajorTickSpacing( range );
            _acMaxAmplitudeSlider.setMinorTickSpacing( range / 10 );
            _acMaxAmplitudeSlider.setSnapToTicks( false );
            _acMaxAmplitudeSlider.setPaintTicks( true );
            _acMaxAmplitudeSlider.setPaintLabels( true );

            // Value
            _acMaxAmplitudeValue = new JLabel( UNKNOWN_VALUE );

            // Layout
            EasyGridBagLayout layout = new EasyGridBagLayout( acAmplitudePanel );
            acAmplitudePanel.setLayout( layout );
            layout.addAnchoredComponent( _acMaxAmplitudeValue, 0, 0, GridBagConstraints.WEST );
            layout.addAnchoredComponent( _acMaxAmplitudeSlider, 1, 0, GridBagConstraints.WEST );
        }

        JPanel acFrequencyPanel = new JPanel();
        {
            acFrequencyPanel.setBorder( BorderFactory.createEtchedBorder() );

            // Range of values
            int max = (int) ( 100.0 * FaradayConfig.AC_FREQUENCY_MAX );
            int min = (int) ( 100.0 * FaradayConfig.AC_FREQUENCY_MIN );
            ;
            int range = max - min;

            // Slider
            _acFrequencySlider = new JSlider();
            _acFrequencySlider.setMaximum( max );
            _acFrequencySlider.setMinimum( min );
            _acFrequencySlider.setValue( max );

            // Slider tick marks
            _acFrequencySlider.setMajorTickSpacing( range );
            _acFrequencySlider.setMinorTickSpacing( range / 10 );
            _acFrequencySlider.setSnapToTicks( false );
            _acFrequencySlider.setPaintTicks( true );
            _acFrequencySlider.setPaintLabels( true );

            // Value
            _acFrequencyValue = new JLabel( UNKNOWN_VALUE );

            // Layout
            EasyGridBagLayout layout = new EasyGridBagLayout( acFrequencyPanel );
            acFrequencyPanel.setLayout( layout );
            layout.addAnchoredComponent( _acFrequencyValue, 0, 0, GridBagConstraints.WEST );
            layout.addAnchoredComponent( _acFrequencySlider, 1, 0, GridBagConstraints.WEST );
        }

        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        layout.addFilledComponent( acAmplitudePanel, row++, 0, GridBagConstraints.HORIZONTAL );
        layout.addFilledComponent( acFrequencyPanel, row++, 0, GridBagConstraints.HORIZONTAL );

        // Wire up event handling.
        EventListener listener = new EventListener();
        _acMaxAmplitudeSlider.addChangeListener( listener );
        _acFrequencySlider.addChangeListener( listener );

        // Update control panel to match the components that it's controlling.
        _acMaxAmplitudeSlider.setValue( (int) ( 100.0 * _acSourceModel.getMaxAmplitude() ) );
        _acFrequencySlider.setValue( (int) ( 100.0 * _acSourceModel.getFrequency() ) );
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
    private class EventListener implements ChangeListener {

        /** Sole constructor */
        public EventListener() {}

        /**
         * ChangeEvent handler.
         * 
         * @param e the event
         * @throws IllegalArgumentException if the event is unexpected
         */
        public void stateChanged( ChangeEvent e ) {
            if ( e.getSource() == _acMaxAmplitudeSlider ) {
                // Read the value.
                int percent = _acMaxAmplitudeSlider.getValue();
                // Update the model.
                _acSourceModel.setMaxAmplitude( percent / 100.0 );
                // Update the label.
                Object[] args = { new Integer( percent ) };
                String text = MessageFormat.format( SimStrings.get( "ACPanel.amplitude" ), args );
                _acMaxAmplitudeValue.setText( text );
            }
            else if ( e.getSource() == _acFrequencySlider ) {
                // Read the value.
                int percent = _acFrequencySlider.getValue();
                // Update the model.
                _acSourceModel.setFrequency( percent / 100.0 );
                // Update the label.
                Object[] args = { new Integer( percent ) };
                String text = MessageFormat.format( SimStrings.get( "ACPanel.frequency" ), args );
                _acFrequencyValue.setText( text );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + e );
            }
        }
    }
}