// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.fourier.FourierResources;
import edu.colorado.phet.fourier.control.sliders.WavePacketCenterSlider;
import edu.colorado.phet.fourier.control.sliders.WavePacketKWidthSlider;
import edu.colorado.phet.fourier.control.sliders.WavePacketSpacingSlider;
import edu.colorado.phet.fourier.control.sliders.WavePacketXWidthSlider;
import edu.colorado.phet.fourier.enums.Domain;
import edu.colorado.phet.fourier.enums.WaveType;
import edu.colorado.phet.fourier.model.GaussianWavePacket;
import edu.colorado.phet.fourier.module.FourierAbstractModule;
import edu.colorado.phet.fourier.view.d2c.D2CAmplitudesView;
import edu.colorado.phet.fourier.view.d2c.D2CComponentsView;
import edu.colorado.phet.fourier.view.d2c.D2CSumView;
import edu.colorado.phet.fourier.view.tools.WavePacketPeriodTool;
import edu.colorado.phet.fourier.view.tools.WavePacketSpacingTool;


/**
 * D2CControlPanel
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class D2CControlPanel extends FourierAbstractControlPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Layout parameters
    private static final int LEFT_MARGIN = 30; // pixels
    private static final int SUBPANEL_SPACING = 0;  // pixels

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Things to be controlled.
    private GaussianWavePacket _wavePacket;
    private D2CAmplitudesView _amplitudesView;
    private D2CComponentsView _harmonicsView;
    private D2CSumView _sumView;
    private WavePacketSpacingTool _spacingTool;
    private WavePacketPeriodTool _periodTool;

    // UI components
    private FourierComboBox _domainComboBox;
    private WavePacketSpacingSlider _spacingSlider;
    private WavePacketCenterSlider _centerSlider;
    private JCheckBox _amplitudesEnvelopeCheckBox;
    private JCheckBox _sumEnvelopeCheckBox;
    private WavePacketKWidthSlider _kWidthSlider;
    private WavePacketXWidthSlider _xWidthSlider;
    private JRadioButton _sinesRadioButton;
    private JRadioButton _cosinesRadioButton;
    private JCheckBox _showWidthsCheckBox;

    // Choices
    private ArrayList _domainChoices;

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
    public D2CControlPanel( FourierAbstractModule module,
                            GaussianWavePacket wavePacket,
                            D2CAmplitudesView amplitudesGraph,
                            D2CComponentsView harmonicsGraph,
                            D2CSumView sumGraph,
                            WavePacketSpacingTool spacingTool,
                            WavePacketPeriodTool periodTool ) {

        super( module );

        assert ( wavePacket != null );
        assert ( amplitudesGraph != null );
        assert ( harmonicsGraph != null );
        assert ( sumGraph != null );
        assert ( spacingTool != null );
        assert ( periodTool != null );

        _wavePacket = wavePacket;
        _amplitudesView = amplitudesGraph;
        _harmonicsView = harmonicsGraph;
        _sumView = sumGraph;
        _spacingTool = spacingTool;
        _periodTool = periodTool;

        // Set the control panel's minimum width.
        int width = FourierResources.getInt( "D2CControlPanel.width", 275 );
        setMinimumWidth( width );

        // Spacing panel
        FourierTitledPanel spacingPanel = new FourierTitledPanel( FourierResources.getString( "D2CControlPanel.spacing" ) );
        {
            // spacing (k1)
            _spacingSlider = new WavePacketSpacingSlider();

            // amplitudes envelope ("Show continuous...")
            _amplitudesEnvelopeCheckBox = new JCheckBox( FourierResources.getString( "D2CControlPanel.kEnvelope" ) );

            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setInsets( DEFAULT_INSETS );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setMinimumWidth( 0, LEFT_MARGIN );
            int row = 0;
            int column = 1;
            layout.addComponent( _spacingSlider, row++, column );
            layout.addComponent( _amplitudesEnvelopeCheckBox, row++, column );
            spacingPanel.setLayout( new BorderLayout() );
            spacingPanel.add( innerPanel, BorderLayout.WEST );
        }

        // Center Point panel
        FourierTitledPanel centerPanel = new FourierTitledPanel( FourierResources.getString( "D2CControlPanel.center" ) );
        {
            // center point (k0)
            _centerSlider = new WavePacketCenterSlider();

            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setInsets( DEFAULT_INSETS );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setMinimumWidth( 0, LEFT_MARGIN );
            int row = 0;
            int column = 1;
            layout.addComponent( _centerSlider, row++, column );
            centerPanel.setLayout( new BorderLayout() );
            centerPanel.add( innerPanel, BorderLayout.WEST );
        }

        // Width panel
        FourierTitledPanel widthPanel = new FourierTitledPanel( FourierResources.getString( "D2CControlPanel.widthControls" ) );
        {
            // k-space width
            _kWidthSlider = new WavePacketKWidthSlider();

            // x-space width
            _xWidthSlider = new WavePacketXWidthSlider();

            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setInsets( DEFAULT_INSETS );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setMinimumWidth( 0, LEFT_MARGIN );
            int row = 0;
            int column = 1;
            layout.addComponent( _kWidthSlider, row++, column );
            layout.addComponent( _xWidthSlider, row++, column );
            widthPanel.setLayout( new BorderLayout() );
            widthPanel.add( innerPanel, BorderLayout.WEST );
        }

        FourierTitledPanel graphControlsPanel = new FourierTitledPanel( FourierResources.getString( "D2CControlPanel.graphControls" ) );
        {
            // Domain
            {
                // Label
                String label = FourierResources.getString( "D2CControlPanel.domain" );

                // Choices
                _domainChoices = new ArrayList();
                _domainChoices.add( new FourierComboBox.Choice( Domain.SPACE, FourierResources.getString( "domain.space" ) ) );
                _domainChoices.add( new FourierComboBox.Choice( Domain.TIME, FourierResources.getString( "domain.time" ) ) );

                // Function combo box
                _domainComboBox = new FourierComboBox( label, _domainChoices );
            }

            // Wave Type
            JPanel waveTypePanel = new JPanel();
            {
                // Radio buttons
                _sinesRadioButton = new JRadioButton( FourierResources.getString( "waveType.sines" ) );
                _cosinesRadioButton = new JRadioButton( FourierResources.getString( "waveType.cosines" ) );
                ButtonGroup group = new ButtonGroup();
                group.add( _sinesRadioButton );
                group.add( _cosinesRadioButton );

                // Layout
                EasyGridBagLayout layout = new EasyGridBagLayout( waveTypePanel );
                layout.setInsets( DEFAULT_INSETS );
                waveTypePanel.setLayout( layout );
                layout.addComponent( _sinesRadioButton, 0, 0 );
                layout.addComponent( _cosinesRadioButton, 0, 1 );
            }

            // Sum envelope
            _sumEnvelopeCheckBox = new JCheckBox( FourierResources.getString( "D2CControlPanel.xEnvelope" ) );

            // Show widths checkbox
            _showWidthsCheckBox = new JCheckBox( FourierResources.getString( "D2CControlPanel.showWidths" ) );

            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setInsets( DEFAULT_INSETS );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setMinimumWidth( 0, LEFT_MARGIN );
            int row = 0;
            int column = 1;
            layout.addComponent( _domainComboBox, row++, column );
            layout.addComponent( waveTypePanel, row++, column );
            layout.addComponent( _sumEnvelopeCheckBox, row++, column );
            layout.addComponent( _showWidthsCheckBox, row++, column );
            graphControlsPanel.setLayout( new BorderLayout() );
            graphControlsPanel.add( innerPanel, BorderLayout.WEST );
        }

        // Layout
        addFullWidth( spacingPanel );
        addVerticalSpace( SUBPANEL_SPACING );
        addFullWidth( centerPanel );
        addVerticalSpace( SUBPANEL_SPACING );
        addFullWidth( widthPanel );
        addVerticalSpace( SUBPANEL_SPACING );
        addFullWidth( graphControlsPanel );

        // Set the state of the controls.
        reset();

        // Wire up event handling (after setting state with reset).
        {
            _listener = new EventListener();
            // ActionListeners
            _amplitudesEnvelopeCheckBox.addActionListener( _listener );
            _sumEnvelopeCheckBox.addActionListener( _listener );
            _showWidthsCheckBox.addActionListener( _listener );
            _sinesRadioButton.addActionListener( _listener );
            _cosinesRadioButton.addActionListener( _listener );
            // ChangeListeners
            _spacingSlider.addChangeListener( _listener );
            _centerSlider.addChangeListener( _listener );
            _kWidthSlider.addChangeListener( _listener );
            _xWidthSlider.addChangeListener( _listener );
            // ItemListeners
            _domainComboBox.addItemListener( _listener );
        }
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    public void setSpacing( double spacing ) {
        _spacingSlider.setValue( spacing );
        handleSpacing();
    }

    public double getSpacing() {
        return _spacingSlider.getValue();
    }

    public void setAmplitudesEnvelopeEnabled( boolean enabled ) {
        _amplitudesEnvelopeCheckBox.setSelected( enabled );
        handleAmplitudeEnvelope();
    }

    public boolean isAmplitudesEnvelopeEnabled() {
        return _amplitudesEnvelopeCheckBox.isSelected();
    }

    public void setCenter( double center ) {
        _centerSlider.setValue( center );
        handleCenter();
    }

    public double getCenter() {
        return _centerSlider.getValue();
    }

    public void setKWidth( double width ) {
        _kWidthSlider.setValue( width );
        handleKWidth();
    }

    public double getKWidth() {
        return _kWidthSlider.getValue();
    }

    public void setDomain( Domain domain ) {
        _domainComboBox.setSelectedKey( domain );
        handleDomain();
    }

    public Domain getDomain() {
        return (Domain) _domainComboBox.getSelectedKey();
    }

    public void setWaveType( WaveType waveType ) {
        if ( waveType == WaveType.SINES ) {
            _sinesRadioButton.setSelected( true );
        }
        else {
            _cosinesRadioButton.setSelected( true );
        }
        handleWaveType();
    }

    public WaveType getWaveType() {
        WaveType waveType = null;
        if ( _sinesRadioButton.isSelected() ) {
            waveType = WaveType.SINES;
        }
        else {
            waveType = WaveType.COSINES;
        }
        return waveType;
    }

    public void setSumEnvelopeEnabled( boolean enabled ) {
        _sumEnvelopeCheckBox.setSelected( enabled );
        handleSumEnvelope();
    }

    public boolean isSumEnvelopeEnabled() {
        return _sumEnvelopeCheckBox.isSelected();
    }

    public void setShowWidthsEnabled( boolean enabled ) {
        _showWidthsCheckBox.setSelected( enabled );
        handleShowWidths();
    }

    public boolean isShowWidthsEnabled() {
        return _showWidthsCheckBox.isSelected();
    }

    //----------------------------------------------------------------------------
    // FourierControlPanel implementation
    //----------------------------------------------------------------------------

    public void reset() {

        _amplitudesEnvelopeCheckBox.setSelected( _amplitudesView.isEnvelopeEnabled() );
        _sumEnvelopeCheckBox.setSelected( _sumView.isEnvelopeEnabled() );

        _showWidthsCheckBox.setSelected( false );
        _amplitudesView.setKWidthVisible( _showWidthsCheckBox.isSelected() );
        _sumView.setXWidthVisible( _showWidthsCheckBox.isSelected() );

        _domainComboBox.setSelectedKey( Domain.SPACE );
        handleDomain();

        _sinesRadioButton.setSelected( true );

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

        public EventListener() {
        }

        public void actionPerformed( ActionEvent event ) {
            if ( event.getSource() == _amplitudesEnvelopeCheckBox ) {
                handleAmplitudeEnvelope();
            }
            else if ( event.getSource() == _sumEnvelopeCheckBox ) {
                handleSumEnvelope();
            }
            else if ( event.getSource() == _showWidthsCheckBox ) {
                handleShowWidths();
            }
            else if ( event.getSource() == _sinesRadioButton || event.getSource() == _cosinesRadioButton ) {
                handleWaveType();
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
        Domain domain = (Domain) _domainComboBox.getSelectedKey();

        _amplitudesView.setDomain( domain );
        _harmonicsView.setDomain( domain );
        _sumView.setDomain( domain );
        _spacingTool.setDomain( domain );
        _periodTool.setDomain( domain );

        if ( domain == Domain.SPACE ) {
            _spacingSlider.setFormat( FourierResources.getString( "WavePacketSpacingSlider.format.space" ) );
            _centerSlider.setFormat( FourierResources.getString( "WavePacketCenterSlider.format.space" ) );
            _kWidthSlider.setFormat( FourierResources.getString( "WavePacketKWidthSlider.format.space" ) );
            _xWidthSlider.setFormat( FourierResources.getString( "WavePacketXWidthSlider.format.space" ) );
            _amplitudesEnvelopeCheckBox.setText( FourierResources.getString( "D2CControlPanel.kEnvelope" ) );
            _sumEnvelopeCheckBox.setText( FourierResources.getString( "D2CControlPanel.xEnvelope" ) );
        }
        else if ( domain == Domain.TIME ) {
            _spacingSlider.setFormat( FourierResources.getString( "WavePacketSpacingSlider.format.time" ) );
            _centerSlider.setFormat( FourierResources.getString( "WavePacketCenterSlider.format.time" ) );
            _kWidthSlider.setFormat( FourierResources.getString( "WavePacketKWidthSlider.format.time" ) );
            _xWidthSlider.setFormat( FourierResources.getString( "WavePacketXWidthSlider.format.time" ) );
            _amplitudesEnvelopeCheckBox.setText( FourierResources.getString( "D2CControlPanel.wEnvelope" ) );
            _sumEnvelopeCheckBox.setText( FourierResources.getString( "D2CControlPanel.tEnvelope" ) );
        }
        else {
            throw new IllegalArgumentException( "unsupported domain: " + domain );
        }
    }

    /*
    * Handles changes to the wave type combo box.
    */
    private void handleWaveType() {
        setWaitCursorEnabled( true );
        WaveType waveType = ( _sinesRadioButton.isSelected() ? WaveType.SINES : WaveType.COSINES );
        _harmonicsView.setWaveType( waveType );
        _sumView.setWaveType( waveType );
        setWaitCursorEnabled( false );
    }

    /*
    * Handles changes to the k-space envelope check box.
    */
    private void handleAmplitudeEnvelope() {
        setWaitCursorEnabled( true );
        _amplitudesView.setEnvelopeEnabled( _amplitudesEnvelopeCheckBox.isSelected() );
        setWaitCursorEnabled( false );
    }

    /*
    * Handles changes to the x-space envelope check box.
    */
    private void handleSumEnvelope() {
        setWaitCursorEnabled( true );
        _sumView.setEnvelopeEnabled( _sumEnvelopeCheckBox.isSelected() );
        setWaitCursorEnabled( false );
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

    /*
    * Handles changes to the "Show widths" checkbox.
    */
    private void handleShowWidths() {
        _amplitudesView.setKWidthVisible( _showWidthsCheckBox.isSelected() );
        _sumView.setXWidthVisible( _showWidthsCheckBox.isSelected() );
    }
}