/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.module.FourierModule;



/**
 * D2CControlPanel
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class D2CControlPanel extends FourierControlPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int TITLED_BORDER_WIDTH = 1;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Things to be controlled.
    //XXX

    // UI components
    private FourierComboBox _domainComboBox;
    private FourierSlider _spacingSlider;
    private JCheckBox _continuousCheckBox;
    private FourierSlider _kWidthSlider;
    private FourierSlider _xWidthSlider;
    private FourierComboBox _waveTypeComboBox;
    
    // Choices
    private ArrayList _domainChoices;
    private ArrayList _waveTypeChoices;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param module
     */
    public D2CControlPanel( FourierModule module ) {
        super( module );
        
        // Set the control panel's minimum width.
        String widthString = SimStrings.get( "D2CControlPanel.width" );
        int width = Integer.parseInt( widthString );
        setMinumumWidth( width );

        // Domain
        {
            // Label
            String label = SimStrings.get( "D2CControlPanel.domain" );

            // Choices
            _domainChoices = new ArrayList();
            _domainChoices.add( new FourierComboBox.Choice( FourierConstants.DOMAIN_SPACE, SimStrings.get( "domain.space" ) ) );
            _domainChoices.add( new FourierComboBox.Choice( FourierConstants.DOMAIN_TIME, SimStrings.get( "domain.time" ) ) );
            _domainChoices.add( new FourierComboBox.Choice( FourierConstants.DOMAIN_SPACE_AND_TIME, SimStrings.get( "domain.spaceAndTime" ) ) );

            // Function combo box
            _domainComboBox = new FourierComboBox( label, _domainChoices );
        }
        
        // Spacing
        {
            String format = SimStrings.get( "D2CControlPanel.spacing" );
            _spacingSlider = new FourierSlider( format );
        }
        
        // Continuous checkbox
        _continuousCheckBox = new JCheckBox( SimStrings.get( "D2CControlPanel.continuous" ) );
        
        // k-space width
        {
            String format = SimStrings.get( "D2CControlPanel.kWidth" );
            _kWidthSlider = new FourierSlider( format );
        }
        
        // x-space width
        {
            String format = SimStrings.get( "D2CControlPanel.xWidth" );
            _xWidthSlider = new FourierSlider( format ); 
        }
        
        // Wave Type
        {
            // Label
            String label = SimStrings.get( "D2CControlPanel.waveType" );
            
            // Choices
            _waveTypeChoices = new ArrayList();
            _waveTypeChoices.add( new FourierComboBox.Choice( FourierConstants.WAVE_TYPE_SINE, SimStrings.get( "waveType.sines" ) ) );
            _waveTypeChoices.add( new FourierComboBox.Choice( FourierConstants.WAVE_TYPE_COSINE, SimStrings.get( "waveType.cosines" ) ) );
            
            // Wave Type combo box
            _waveTypeComboBox = new FourierComboBox( label, _waveTypeChoices ); 
        }

        // Layout
        addFullWidth( _domainComboBox );
        addFullWidth( _continuousCheckBox );
        addFullWidth( _spacingSlider );
        addFullWidth( _kWidthSlider );
        addFullWidth( _xWidthSlider );
        addFullWidth( _waveTypeComboBox );
        
        // Set the state of the controls.
        reset();
        
        // Wire up event handling (after setting state with reset).
        {
            EventListener listener = new EventListener();
            _domainComboBox.addItemListener( listener );
            _spacingSlider.addChangeListener( listener );
            _continuousCheckBox.addActionListener( listener );
            _kWidthSlider.addChangeListener( listener );
            _xWidthSlider.addChangeListener( listener );
            _waveTypeComboBox.addItemListener( listener );
        }
    }
    
    public void reset() {
        _spacingSlider.setValue( _spacingSlider.getSlider().getMinimum() );//XXX
        _kWidthSlider.setValue( _kWidthSlider.getSlider().getMinimum() );//XXX
        _xWidthSlider.setValue( _xWidthSlider.getSlider().getMinimum() );//XXX
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * EventListener is a nested class that is private to this control panel.
     * It handles dispatching of all events generated by the controls.
     */
    private class EventListener implements ActionListener, ChangeListener, ItemListener {

        public EventListener() {}

        public void actionPerformed( ActionEvent event ) {
            if ( event.getSource() == _continuousCheckBox ) {
                handleContinuous();
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }
        
        public void stateChanged( ChangeEvent event ) {

            if ( event.getSource() == _spacingSlider ) {
                handleSpacing();
            }
            else if ( event.getSource() == _kWidthSlider ) {
                handleKWidth();
            }
            else if ( event.getSource() == _xWidthSlider ) {
                handleXWidth();
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }

        public void itemStateChanged( ItemEvent event ) {
            if ( event.getStateChange() == ItemEvent.SELECTED ) {
                if ( event.getSource() == _domainComboBox.getComboBox() ) {
                    handleDomain();
                }
                else if ( event.getSource() == _waveTypeComboBox.getComboBox() ) {
                    handleWaveType();
                }
                else {
                    throw new IllegalArgumentException( "unexpected event: " + event );
                }
            }
        } 
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    private void handleContinuous() {
        System.out.println( "continuous=" + _continuousCheckBox.isSelected() );//XXX
    }
    
    private void handleSpacing() {
        System.out.println( "spacing=" + _spacingSlider.getValue() );//XXX
    }
    
    private void handleKWidth() {
        System.out.println( "k width=" + _kWidthSlider.getValue() );//XXX
    }
    
    private void handleXWidth() {
        System.out.println( "x width=" + _xWidthSlider.getValue() );//XXX
    }
    
    private void handleDomain() {
        System.out.println( "domain=" + _domainComboBox.getSelectedItem() );//XXX
    }
    
    private void handleWaveType() {
        System.out.println( "wave type=" + _waveTypeComboBox.getSelectedItem() );//XXX
    }

}
