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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.module.FourierModule;
import edu.colorado.phet.fourier.util.EasyGridBagLayout;



/**
 * DiscreteToContinuousControlPanel
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DiscreteToContinuousControlPanel extends FourierControlPanel {

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
    public DiscreteToContinuousControlPanel( FourierModule module ) {
        super( module );
        
        // Filler, to set the control panel's width.
        JPanel fillerPanel = new JPanel();
        {
            String widthString = SimStrings.get( "DiscreteToContinuousControlPanel.width" );
            int width = Integer.parseInt( widthString );
            fillerPanel.setLayout( new BoxLayout( fillerPanel, BoxLayout.X_AXIS ) );
            fillerPanel.add( Box.createHorizontalStrut( width ) );
        }

        // Domain
        {
            // Label
            String label = SimStrings.get( "DiscreteToContinuousControlPanel.domain" );

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
            String format = SimStrings.get( "DiscreteToContinuousControlPanel.spacing" );
            _spacingSlider = new FourierSlider( format );
            _spacingSlider.setMaximum( 10 );//XXX
            _spacingSlider.setMinimum( 0 );//XXX
            _spacingSlider.setMajorTickSpacing( 1 );//XXX
            _spacingSlider.setSnapToTicks( true );
            //XXX label table: 0, PI/8, PI/4, PI/2, PI, 2PI
        }
        
        // Continuous checkbox
        _continuousCheckBox = new JCheckBox( SimStrings.get( "DiscreteToContinuousControlPanel.continuous" ) );
        
        // k-space width
        {
            String format = SimStrings.get( "DiscreteToContinuousControlPanel.kWidth" );
            _kWidthSlider = new FourierSlider( format );
            _kWidthSlider.setMaximum( 10 );//XXX
            _kWidthSlider.setMinimum( 0 );//XXX
            _kWidthSlider.setMajorTickSpacing( 1 );//XXX
            _kWidthSlider.setSnapToTicks( false );
        }
        
        // x-space width
        {
            String format = SimStrings.get( "DiscreteToContinuousControlPanel.xWidth" );
            _xWidthSlider = new FourierSlider( format ); 
            _xWidthSlider.setMaximum( 10 );//XXX
            _xWidthSlider.setMinimum( 0 );//XXX
            _xWidthSlider.setMajorTickSpacing( 1 );//XXX
            _xWidthSlider.setSnapToTicks( false );
        }
        
        // Wave Type
        {
            // Label
            String label = SimStrings.get( "DiscreteToContinuousControlPanel.waveType" );
            
            // Choices
            _waveTypeChoices = new ArrayList();
            _waveTypeChoices.add( new FourierComboBox.Choice( FourierConstants.WAVE_TYPE_SINE, SimStrings.get( "waveType.sines" ) ) );
            _waveTypeChoices.add( new FourierComboBox.Choice( FourierConstants.WAVE_TYPE_COSINE, SimStrings.get( "waveType.cosines" ) ) );
            
            // Wave Type combo box
            _waveTypeComboBox = new FourierComboBox( label, _waveTypeChoices ); 
        }
        
        // Layout
        addFullWidth( fillerPanel );
        addFullWidth( _domainComboBox );
        addFullWidth( _spacingSlider );
        addFullWidth( _continuousCheckBox );
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
            _continuousCheckBox.addChangeListener( listener );
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
    private class EventListener implements ChangeListener, ItemListener {

        public EventListener() {}

        public void stateChanged( ChangeEvent event ) {
            if ( event.getSource() == _continuousCheckBox ) {
                handleContinuous();
            }
            else if ( event.getSource() == _spacingSlider ) {
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
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    private void handleContinuous() {
        
    }
    
    private void handleSpacing() {
        
    }
    
    private void handleKWidth() {
        
    }
    
    private void handleXWidth() {
        
    }
    
    private void handleDomain() {
        
    }
    
    private void handleWaveType() {
        
    }

}
