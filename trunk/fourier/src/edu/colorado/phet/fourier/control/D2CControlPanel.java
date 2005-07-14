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
import java.util.Hashtable;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.MathStrings;
import edu.colorado.phet.fourier.module.FourierModule;
import edu.colorado.phet.fourier.util.EasyGridBagLayout;



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
    
    private static final int MIN_SPACING = 0;
    private static final int MAX_SPACING = 100;
    private static final int MIN_K_WIDTH = 0;
    private static final int MAX_K_WIDTH = 100;
    private static final int MIN_X_WIDTH = 0;
    private static final int MAX_X_WIDTH = 100;
    
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

        JPanel miscPanel = new JPanel();
        {
            // Domain
            {
                // Label
                String label = SimStrings.get( "D2CControlPanel.domain" );

                // Choices
                _domainChoices = new ArrayList();
                _domainChoices.add( new FourierComboBox.Choice( FourierConstants.DOMAIN_SPACE, SimStrings.get( "domain.space" ) ) );
                _domainChoices.add( new FourierComboBox.Choice( FourierConstants.DOMAIN_TIME, SimStrings.get( "domain.time" ) ) );
 
                // Function combo box
                _domainComboBox = new FourierComboBox( label, _domainChoices );
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
            EasyGridBagLayout layout = new EasyGridBagLayout( miscPanel );
            miscPanel.setLayout( layout );
            int row = 0;
            layout.addComponent( _domainComboBox, row++, 0 );
            layout.addComponent( _waveTypeComboBox, row++, 0 );
        }
        
        JPanel harmonicsPanel = new JPanel();
        {
            String title = SimStrings.get( "D2CControlPanel.harmonics" );
            harmonicsPanel.setBorder( new TitledBorder( title ) );
            
            // Spacing
            {
                String format = SimStrings.get( "D2CControlPanel.spacing" );
                _spacingSlider = new FourierSlider( format );
                _spacingSlider.setMinimum( MIN_SPACING );
                _spacingSlider.setMaximum( MAX_SPACING );
                
                Hashtable labelTable = new Hashtable();
                labelTable.put( new Integer( 0 ), new JLabel( "0" ) );
                labelTable.put( new Integer( 20 ), new JLabel( MathStrings.C_PI + "/8" ) );
                labelTable.put( new Integer( 40 ), new JLabel( MathStrings.C_PI + "/4" ) );
                labelTable.put( new Integer( 60 ), new JLabel( MathStrings.C_PI + "/2" ) );
                labelTable.put( new Integer( 80 ), new JLabel( "" + MathStrings.C_PI ) );
                labelTable.put( new Integer( 100 ), new JLabel( "2" + MathStrings.C_PI ) );
                _spacingSlider.setLabelTable( labelTable );
                _spacingSlider.setMajorTickSpacing( 20 );
            }

            // Continuous checkbox
            _continuousCheckBox = new JCheckBox( SimStrings.get( "D2CControlPanel.continuous" ) );
            
            // Layout
            EasyGridBagLayout layout = new EasyGridBagLayout( harmonicsPanel );
            harmonicsPanel.setLayout( layout );
            int row = 0;
            layout.addComponent( _spacingSlider, row++, 0 );
            layout.addComponent( _continuousCheckBox, row++, 0 );
        }
        
        // Packet width panel
        JPanel packetWidthPanel = new JPanel();
        {
            String title = SimStrings.get( "D2CControlPanel.packetWidth" );
            packetWidthPanel.setBorder( new TitledBorder( title ) );
            
            // k-space width
            {
                String format = SimStrings.get( "D2CControlPanel.kWidth" );
                _kWidthSlider = new FourierSlider( format );
                _kWidthSlider.setMinimum( MIN_K_WIDTH );
                _kWidthSlider.setMaximum( MAX_K_WIDTH );
                
                Hashtable labelTable = new Hashtable();
                labelTable.put( new Integer( 0 ), new JLabel( "1" ) );
                labelTable.put( new Integer( 20 ), new JLabel( "" + MathStrings.C_PI ) );
                labelTable.put( new Integer( 40 ), new JLabel( "2" + MathStrings.C_PI ) );
                labelTable.put( new Integer( 60 ), new JLabel( "3" + MathStrings.C_PI ) );
                labelTable.put( new Integer( 80 ), new JLabel( "4" + MathStrings.C_PI ) );
                labelTable.put( new Integer( 100 ), new JLabel( "5" + MathStrings.C_PI ) );
                _kWidthSlider.setLabelTable( labelTable );
                _kWidthSlider.setMajorTickSpacing( 20 );
            }

            // x-space width
            {
                String format = SimStrings.get( "D2CControlPanel.xWidth" );
                _xWidthSlider = new FourierSlider( format );
                _xWidthSlider.setMinimum( MIN_X_WIDTH );
                _xWidthSlider.setMaximum( MAX_X_WIDTH );
                
                Hashtable labelTable = new Hashtable();
                labelTable.put( new Integer( 0 ), new JLabel( "1/5" + MathStrings.C_PI ) );
                labelTable.put( new Integer( 20 ), new JLabel( ".2" ) );
                labelTable.put( new Integer( 40 ), new JLabel( ".4" ) );
                labelTable.put( new Integer( 60 ), new JLabel( ".6" ) );
                labelTable.put( new Integer( 80 ), new JLabel( ".8" ) );
                labelTable.put( new Integer( 100 ), new JLabel( "1" ) );
                _xWidthSlider.setLabelTable( labelTable );
                _xWidthSlider.setMajorTickSpacing( 20 );
            }
            
            // Layout
            EasyGridBagLayout layout = new EasyGridBagLayout( packetWidthPanel );
            packetWidthPanel.setLayout( layout );
            int row = 0;
            layout.addComponent( _kWidthSlider, row++, 0 );
            layout.addComponent( _xWidthSlider, row++, 0 );
        }


        // Layout
        addFullWidth( miscPanel );
        addVerticalSpace( 15 );
        addFullWidth( harmonicsPanel );
        addVerticalSpace( 15 );
        addFullWidth( packetWidthPanel );
  
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
