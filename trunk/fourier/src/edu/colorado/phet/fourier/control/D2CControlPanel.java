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

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.control.sliders.WavePacketKWidthSlider;
import edu.colorado.phet.fourier.control.sliders.WavePacketXWidthSlider;
import edu.colorado.phet.fourier.control.sliders.WavePacketCenterSlider;
import edu.colorado.phet.fourier.control.sliders.WavePacketSpacingSlider;
import edu.colorado.phet.fourier.model.GaussianWavePacket;
import edu.colorado.phet.fourier.module.FourierModule;
import edu.colorado.phet.fourier.view.d2c.D2CAmplitudesView;
import edu.colorado.phet.fourier.view.d2c.D2CHarmonicsView;
import edu.colorado.phet.fourier.view.d2c.D2CSumView;
import edu.colorado.phet.fourier.view.tools.WavePacketKWidthTool;
import edu.colorado.phet.fourier.view.tools.WavePacketXWidthTool;
import edu.colorado.phet.fourier.view.tools.WavePacketPeriodTool;
import edu.colorado.phet.fourier.view.tools.WavePacketSpacingTool;



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
    
    private static final int SPACE_BETWEEN_SUBPANELS = 10;
    
    private static final int MIN_SPACING = 0;
    private static final int MAX_SPACING = 100;
    private static final int MIN_X_WIDTH = 0;
    private static final int MAX_X_WIDTH = 100;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Things to be controlled.
    private GaussianWavePacket _wavePacket;
    private D2CAmplitudesView _amplitudesGraph;
    private D2CHarmonicsView _harmonicsGraph;
    private D2CSumView _sumGraph;
    private WavePacketSpacingTool _spacingTool;
    private WavePacketKWidthTool _kWidthTool;
    private WavePacketXWidthTool _xWidthTool;
    private WavePacketPeriodTool _periodTool;

    // UI components
    private FourierComboBox _domainComboBox;
    private WavePacketSpacingSlider _spacingSlider;
    private WavePacketCenterSlider _centerSlider;
    private JCheckBox _continuousCheckBox;
    private WavePacketKWidthSlider _kWidthSlider;
    private WavePacketXWidthSlider _xWidthSlider;
    private FourierComboBox _waveTypeComboBox;
    
    // Choices
    private ArrayList _domainChoices;
    private ArrayList _waveTypeChoices;
    
    private EventListener _listener;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param module
     * @param wavePacket
     * @param amplitudesGraph
     * @param harmonicsGraph
     */
    public D2CControlPanel( FourierModule module, 
            GaussianWavePacket wavePacket, 
            D2CAmplitudesView amplitudesGraph,
            D2CHarmonicsView harmonicsGraph,
            D2CSumView sumGraph,
            WavePacketSpacingTool spacingTool,
            WavePacketKWidthTool kWidthTool,
            WavePacketXWidthTool xWidthTool,
            WavePacketPeriodTool periodTool ) {
        
        super( module );
        
        assert( wavePacket != null );
        assert( amplitudesGraph != null );
        assert( harmonicsGraph != null );
        assert( sumGraph != null );
        assert( spacingTool != null );
        assert( kWidthTool != null );
        assert( xWidthTool != null );
        assert( periodTool != null );
        
        _wavePacket = wavePacket;
        _amplitudesGraph = amplitudesGraph;
        _harmonicsGraph = harmonicsGraph;
        _sumGraph = sumGraph;
        _spacingTool = spacingTool;
        _kWidthTool = kWidthTool;
        _xWidthTool = xWidthTool;
        _periodTool = periodTool;
        
        // Set the control panel's minimum width.
        String widthString = SimStrings.get( "D2CControlPanel.width" );
        int width = Integer.parseInt( widthString );
        setMinumumWidth( width );

        JPanel miscPanel = new JPanel();
        {
            miscPanel.setBorder( new TitledBorder( "" ) );
            
            // Domain
            {
                // Label
                String label = SimStrings.get( "D2CControlPanel.domain" );

                // Choices
                _domainChoices = new ArrayList();
                _domainChoices.add( new FourierComboBox.Choice( FourierConstants.DOMAIN_TIME, SimStrings.get( "domain.time" ) ) );
                _domainChoices.add( new FourierComboBox.Choice( FourierConstants.DOMAIN_SPACE, SimStrings.get( "domain.space" ) ) );
 
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
            
            // Continuous checkbox
            _continuousCheckBox = new JCheckBox( SimStrings.get( "D2CControlPanel.continuous" ) );
            
            // Layout
            EasyGridBagLayout layout = new EasyGridBagLayout( miscPanel );
            miscPanel.setLayout( layout );
            int row = 0;
            layout.addComponent( _domainComboBox, row++, 0 );
            layout.addComponent( _waveTypeComboBox, row++, 0 );
            layout.addComponent( _continuousCheckBox, row++, 0 );
        }
       
        // Packet width panel
        JPanel packetPanel = new JPanel();
        {
            String title = SimStrings.get( "D2CControlPanel.gaussianWavePacket" );
            TitledBorder titledBorder = new TitledBorder( title );
            Font font = titledBorder.getTitleFont();
            titledBorder.setTitleFont( new Font( font.getName(), Font.BOLD, font.getSize() ) );
            packetPanel.setBorder( titledBorder );
            
            // spacing (k1)
            _spacingSlider = new WavePacketSpacingSlider();
            
            // center point (k0)
            _centerSlider = new WavePacketCenterSlider();
            
            // k-space width
            _kWidthSlider = new WavePacketKWidthSlider();

            // x-space width
            _xWidthSlider = new WavePacketXWidthSlider();
            
            // Layout
            EasyGridBagLayout layout = new EasyGridBagLayout( packetPanel );
            packetPanel.setLayout( layout );
            int row = 0;
            layout.addComponent( _spacingSlider, row++, 0 );
            layout.addComponent( _centerSlider, row++, 0 );
            layout.addComponent( _kWidthSlider, row++, 0 );
            layout.addComponent( _xWidthSlider, row++, 0 );
        }

        // Layout
        addFullWidth( miscPanel );
        addVerticalSpace( SPACE_BETWEEN_SUBPANELS );
        addFullWidth( packetPanel );
        
        // Set the state of the controls.
        reset();
        
        // Wire up event handling (after setting state with reset).
        {
            _listener = new EventListener();
            _domainComboBox.addItemListener( _listener );
            _spacingSlider.addChangeListener( _listener );
            _continuousCheckBox.addActionListener( _listener );
            _centerSlider.addChangeListener( _listener );
            _kWidthSlider.addChangeListener( _listener );
            _xWidthSlider.addChangeListener( _listener );
            _waveTypeComboBox.addItemListener( _listener );
        }
    }
    
    public void reset() {
        
        _continuousCheckBox.setSelected( _amplitudesGraph.isContinuousEnabled() );
        
        _domainComboBox.setSelectedKey( FourierConstants.DOMAIN_TIME );
        handleDomain();
        
        _spacingSlider.setValue( _wavePacket.getK1() );
        _centerSlider.setValue( _wavePacket.getK0() );
        _kWidthSlider.setValue( _wavePacket.getDeltaK() );
        _xWidthSlider.setValue( _wavePacket.getDeltaX() );
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
            else if ( event.getSource() == _centerSlider ) {
                handleCenter();
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
    
    /*
     * Handles changes to the domain combo box.
     */
    private void handleDomain() {
        int domain = _domainComboBox.getSelectedKey();
        
        _amplitudesGraph.setDomain( domain );
        _harmonicsGraph.setDomain( domain );
        _sumGraph.setDomain( domain );
        _spacingTool.setDomain( domain );
        _kWidthTool.setDomain( domain );
        _xWidthTool.setDomain( domain );
        _periodTool.setDomain( domain );
        
        if ( domain == FourierConstants.DOMAIN_SPACE ) {
            _spacingSlider.setFormat( SimStrings.get( "WavePacketSpacingSlider.format.space" ) );
            _centerSlider.setFormat( SimStrings.get( "WavePacketCenterSlider.format.space" ) );
            _kWidthSlider.setFormat( SimStrings.get( "WavePacketKWidthSlider.format.space" ) );
            _xWidthSlider.setFormat( SimStrings.get( "WavePacketXWidthSlider.format.space" ) );
        }
        else if ( domain == FourierConstants.DOMAIN_TIME ) {
            _spacingSlider.setFormat( SimStrings.get( "WavePacketSpacingSlider.format.time" ) );
            _centerSlider.setFormat( SimStrings.get( "WavePacketCenterSlider.format.time" ) );
            _kWidthSlider.setFormat( SimStrings.get( "WavePacketKWidthSlider.format.time" ) );
            _xWidthSlider.setFormat( SimStrings.get( "WavePacketXWidthSlider.format.time" ) );   
        }
    }
    
    /*
     * Handles changes to the wave type combo box.
     */
    private void handleWaveType() {
        int waveType = _waveTypeComboBox.getSelectedKey();
        _harmonicsGraph.setWaveType( waveType );
        _sumGraph.setWaveType( waveType );
    }
    
    /*
     * Handles changes to the "continuous" check box.
     */
    private void handleContinuous() {
        _amplitudesGraph.setContinuousEnabled( _continuousCheckBox.isSelected() );
    }
    
    /*
     * Handles changes to the spacing (k1) slider.
     */
    private void handleSpacing() {
        
        // Update the wave packet if the user is done dragging the slider.
        if ( !_spacingSlider.isAdjusting() ) {
            setWaitCursorEnabled( true );
            double k1 = _spacingSlider.getValue();
            _wavePacket.setK1( k1 );
            setWaitCursorEnabled( false );
        }
    }
    
    /*
     * Handles changes to the center-point (k0) slider.
     */
    private void handleCenter() {
        // Update the wave packet if the user is done dragging the slider.
        if ( !_centerSlider.isAdjusting() ) {
            setWaitCursorEnabled( true );
            double k0 = _centerSlider.getValue();
            _wavePacket.setK0( k0 );
            setWaitCursorEnabled( false );
        }
    }
    
    /*
     * Handles changes to the k-space width slider.
     */
    private void handleKWidth() {
        
        setWaitCursorEnabled( true );
        
        double deltaK = _kWidthSlider.getValue();
        
        // Update the slider.
        _xWidthSlider.removeChangeListener( _listener );
        _xWidthSlider.setValue( 1 / deltaK );
        _xWidthSlider.addChangeListener( _listener );
        
        // Update the wave packet if the user is done dragging the slider.
        if ( !_kWidthSlider.isAdjusting() ) {
            _wavePacket.setDeltaK( deltaK );
        }
        
        setWaitCursorEnabled( false );
    }
   
    /*
     * Handles changes to the x-space width slider.
     */
    private void handleXWidth() {
        
        setWaitCursorEnabled( true );
        
        double deltaX = _xWidthSlider.getValue();
        
        // Update the slider.
        _kWidthSlider.removeChangeListener( _listener );
        _kWidthSlider.setValue( 1 / deltaX );
        _kWidthSlider.addChangeListener( _listener );
        
        // Update the wave packet if the user is done dragging the slider.
        if ( !_xWidthSlider.isAdjusting() ) {
            _wavePacket.setDeltaX( deltaX );
        }
        
        setWaitCursorEnabled( false );
    }
}