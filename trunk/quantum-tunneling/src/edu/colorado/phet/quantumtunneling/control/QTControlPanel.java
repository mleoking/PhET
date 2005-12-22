/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.control;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.enum.Direction;
import edu.colorado.phet.quantumtunneling.enum.PotentialType;
import edu.colorado.phet.quantumtunneling.enum.WaveType;
import edu.colorado.phet.quantumtunneling.model.AbstractPotential;
import edu.colorado.phet.quantumtunneling.model.BarrierPotential;
import edu.colorado.phet.quantumtunneling.model.ConstantPotential;
import edu.colorado.phet.quantumtunneling.model.StepPotential;
import edu.colorado.phet.quantumtunneling.module.QTModule;


/**
 * QTControlPanel is the sole control panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTControlPanel extends AbstractControlPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int LEFT_MARGIN = 0;
    private static final int SUBPANEL_SPACING = 5;
    private static final String EXPAND_SYMBOL = ">>";
    private static final String COLLAPSE_SYMBOL = "<<";
    private static final double WIDTH_TICK_SPACING = 1.0; // nm
    private static final double CENTER_TICK_SPACING = 5.0; // nm
    private static final int WIDTH_SIGNIFICANT_DECIMAL_PLACES = 1;
    private static final int CENTER_SIGNIFICANT_DECIMAL_PLACES = 1;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private QTModule _module;
    private JComboBox _potentialComboBox;
    private JCheckBox _realCheckBox, _imaginaryCheckBox, _magnitudeCheckBox, _phaseCheckBox;
    private Object _constantItem, _stepItem, _singleBarrierItem, _doubleBarrierItem;
    private JRadioButton _separateRadioButton, _sumRadioButton;
    private JRadioButton _leftToRightRadioButton, _rightToLeftRadioButton;
    private JRadioButton _planeWaveRadioButton, _packetWaveRadioButton;
    private JButton _propertiesButton;
    private String _sPropertiesExpand, _sPropertiesCollapse;
    private JPanel _propertiesPanel;
    private SliderControl _widthSlider, _centerSlider;
    private JButton _measureButton;
    private EventListener _listener;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param module
     */
    public QTControlPanel( QTModule module ) {
        super( module );
        
        _module = module;
        
        // Set the control panel's minimum width.
        String widthString = SimStrings.get( "width.controlPanel" );
        if ( widthString != null ) {
            int width = Integer.parseInt( widthString );
            setMinumumWidth( width );
        }
        
        // Potential
        JPanel potentialPanel = new JPanel();
        {
            JLabel label = new JLabel( SimStrings.get( "label.potential" ) );
            
            _constantItem = SimStrings.get( "choice.potential.constant" );
            _stepItem = SimStrings.get( "choice.potential.step" );
            _singleBarrierItem = SimStrings.get( "choice.potential.barrier" );
            _doubleBarrierItem = SimStrings.get( "choice.potential.double" );
            
            Object[] items = { _constantItem, _stepItem, _singleBarrierItem, _doubleBarrierItem };
            _potentialComboBox = new JComboBox( items );
            
            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setMinimumWidth( 0, LEFT_MARGIN );
            layout.addComponent( label, 0, 1 );
            layout.addComponent( _potentialComboBox, 1, 1 );
            potentialPanel.setLayout( new BorderLayout() );
            potentialPanel.add( innerPanel, BorderLayout.WEST );
        }
        
        // Wave Function View 
        JPanel viewPanel = new JPanel();
        {
            JLabel label = new JLabel( SimStrings.get( "label.view" ) );
            _realCheckBox = new JCheckBox( SimStrings.get( "choice.view.real" ) );
            _imaginaryCheckBox = new JCheckBox( SimStrings.get( "choice.view.imaginary" ) );
            _magnitudeCheckBox = new JCheckBox( SimStrings.get( "choice.view.magnitude" ) );
            _phaseCheckBox = new JCheckBox( SimStrings.get( "choice.view.phase" ) );
            
            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setMinimumWidth( 0, LEFT_MARGIN );
            layout.addComponent( label, 0, 1 );
            layout.addComponent( _realCheckBox, 1, 1 );
            layout.addComponent( _imaginaryCheckBox, 2, 1 );
            layout.addComponent( _magnitudeCheckBox, 3, 1 );
            layout.addComponent( _phaseCheckBox, 4, 1 );
            viewPanel.setLayout( new BorderLayout() );
            viewPanel.add( innerPanel, BorderLayout.WEST );
        }
        
        // Incident/Reflected waves
        JPanel irPanel = new JPanel();
        {
            JLabel label = new JLabel( SimStrings.get( "label.ir" ) );
            _sumRadioButton = new JRadioButton( SimStrings.get( "choice.ir.sum" ) );
            _separateRadioButton = new JRadioButton( SimStrings.get( "choice.ir.separate" ) );
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( _sumRadioButton );
            buttonGroup.add( _separateRadioButton );

            int horizontalSpaceBetweenButtons = 10;
            JPanel buttonPanel = new JPanel();
            EasyGridBagLayout buttonLayout = new EasyGridBagLayout( buttonPanel );
            buttonPanel.setLayout( buttonLayout );
            buttonLayout.setAnchor( GridBagConstraints.WEST );
            buttonLayout.setMinimumWidth( 1, horizontalSpaceBetweenButtons );
            buttonLayout.addComponent( _sumRadioButton, 0, 0 );
            buttonLayout.addComponent( _separateRadioButton, 0, 2 );

            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setMinimumWidth( 0, LEFT_MARGIN );
            layout.addComponent( label, 0, 1 );
            layout.addComponent( buttonPanel, 1, 1 );
            irPanel.setLayout( new BorderLayout() );
            irPanel.add( innerPanel, BorderLayout.WEST );
        }
        
        // Direction
        JPanel directionPanel = new JPanel();
        {
            JLabel label = new JLabel( SimStrings.get( "label.direction" ) );
            
            try {
                // Pretty icons to indicate direction
                ImageIcon l2rIcon = new ImageIcon( ImageLoader.loadBufferedImage( QTConstants.IMAGE_ARROW_L2R ) );
                ImageIcon r2lIcon = new ImageIcon( ImageLoader.loadBufferedImage( QTConstants.IMAGE_ARROW_R2L ) );
                ImageIcon l2rIconSelected = new ImageIcon( ImageLoader.loadBufferedImage( QTConstants.IMAGE_ARROW_L2R_SELECTED ) );
                ImageIcon r2lIconSelected = new ImageIcon( ImageLoader.loadBufferedImage( QTConstants.IMAGE_ARROW_R2L_SELECTED ) );
                _leftToRightRadioButton = new JRadioButton( l2rIcon );
                _leftToRightRadioButton.setSelectedIcon( l2rIconSelected );
                _rightToLeftRadioButton = new JRadioButton( r2lIcon );
                _rightToLeftRadioButton.setSelectedIcon( r2lIconSelected );
            }
            catch ( IOException e ) {
                // Fallback to crude arrows if we can't load icons
                _leftToRightRadioButton = new JRadioButton( "-->" );
                _rightToLeftRadioButton = new JRadioButton( "<--" );
                e.printStackTrace();
            }

            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( _leftToRightRadioButton );
            buttonGroup.add( _rightToLeftRadioButton );
            
            JPanel buttonPanel = new JPanel();
            EasyGridBagLayout buttonLayout = new EasyGridBagLayout( buttonPanel );
            buttonPanel.setLayout( buttonLayout );
            buttonLayout.setAnchor( GridBagConstraints.WEST );
            buttonLayout.addComponent( _leftToRightRadioButton, 0, 0 );
            buttonLayout.addComponent( _rightToLeftRadioButton, 0, 1 );
            
            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setMinimumWidth( 0, LEFT_MARGIN );
            layout.addComponent( label, 0, 1 );
            layout.addComponent( buttonPanel, 1, 1 );
            directionPanel.setLayout( new BorderLayout() );
            directionPanel.add( innerPanel, BorderLayout.WEST );
        }
        
        // Wave function form
        JPanel formPanel = new JPanel();
        {
            JLabel label = new JLabel( SimStrings.get( "label.wave" ) );
            _planeWaveRadioButton = new JRadioButton( SimStrings.get( "choice.wave.plane" ) );
            _packetWaveRadioButton = new JRadioButton( SimStrings.get( "choice.wave.packet" ) );
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( _planeWaveRadioButton );
            buttonGroup.add( _packetWaveRadioButton );
            
            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setMinimumWidth( 0, LEFT_MARGIN );
            layout.addComponent( label, 0, 1 );
            layout.addComponent( _planeWaveRadioButton, 1, 1 );
            layout.addComponent( _packetWaveRadioButton, 2, 1 );
            formPanel.setLayout( new BorderLayout() );
            formPanel.add( innerPanel, BorderLayout.WEST );
        }
        
        // Wave packet properties
        JPanel propertiesPanel = new JPanel();
        {
            _sPropertiesExpand = SimStrings.get( "button.packetProperties" ) + " " + EXPAND_SYMBOL;
            _sPropertiesCollapse = SimStrings.get( "button.packetProperties" ) + " " + COLLAPSE_SYMBOL;
            _propertiesButton = new JButton( _sPropertiesExpand  );
            
            // Subpanel
            _propertiesPanel = new JPanel();
            {
                _widthSlider = new SliderControl( 
                        QTConstants.MIN_PACKET_WIDTH, QTConstants.MAX_PACKET_WIDTH,
                        WIDTH_TICK_SPACING,
                        WIDTH_SIGNIFICANT_DECIMAL_PLACES,
                        SimStrings.get( "label.packetWidth" ) + " {0} (" + SimStrings.get( "units.position" ) + ")", 
                        DEFAULT_INSETS );
                _widthSlider.setInverted( true );
                
                _centerSlider = new SliderControl( 
                        QTConstants.MIN_PACKET_CENTER, QTConstants.MAX_PACKET_CENTER,
                        CENTER_TICK_SPACING,
                        CENTER_SIGNIFICANT_DECIMAL_PLACES,
                        SimStrings.get( "label.packetCenter" ) + " {0} (" + SimStrings.get( "units.position" ) + ")",
                        DEFAULT_INSETS );
                
                EasyGridBagLayout layout = new EasyGridBagLayout( _propertiesPanel );
                _propertiesPanel.setLayout( layout );
                layout.setAnchor( GridBagConstraints.WEST );
                layout.addComponent( _widthSlider, 0, 0 );
                layout.addComponent( _centerSlider, 1, 0 );
            }
            
            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setMinimumWidth( 0, LEFT_MARGIN );
            layout.addComponent( _propertiesButton, 0, 1 );
            layout.addComponent( _propertiesPanel, 1, 1 );
            propertiesPanel.setLayout( new BorderLayout() );
            propertiesPanel.add( innerPanel, BorderLayout.WEST );
        }
        
        // Measure
        _measureButton = new JButton( SimStrings.get( "button.measure"  ) );
        
        // Layout
        {  
            setLogoVisible( false );
            addFullWidth( potentialPanel );
            addVerticalSpace( SUBPANEL_SPACING );
            addSeparator();
            addFullWidth( viewPanel );
            addVerticalSpace( SUBPANEL_SPACING );
            addSeparator();
            addFullWidth( irPanel );
            addVerticalSpace( SUBPANEL_SPACING );
            addSeparator();
            addFullWidth( directionPanel );
            addVerticalSpace( SUBPANEL_SPACING );
            addSeparator();
            addFullWidth( formPanel );
            addVerticalSpace( SUBPANEL_SPACING );
            addSeparator();
            addFullWidth( propertiesPanel );
            addVerticalSpace( SUBPANEL_SPACING );
            addSeparator();
            addControl( _measureButton );
            addResetButton();
        }
        
        // Interactivity
        {
            _listener = new EventListener();
            _potentialComboBox.addItemListener( _listener );
            _realCheckBox.addActionListener( _listener );
            _imaginaryCheckBox.addActionListener( _listener );
            _magnitudeCheckBox.addActionListener( _listener );
            _phaseCheckBox.addActionListener( _listener );
            _separateRadioButton.addActionListener( _listener );
            _sumRadioButton.addActionListener( _listener );
            _leftToRightRadioButton.addActionListener( _listener );
            _rightToLeftRadioButton.addActionListener( _listener );
            _planeWaveRadioButton.addActionListener( _listener );
            _packetWaveRadioButton.addActionListener( _listener );
            _propertiesButton.addActionListener( _listener );
            _widthSlider.addChangeListener( _listener );
            _centerSlider.addChangeListener( _listener );
            _measureButton.addActionListener( _listener );
        }
        
        reset();
    }

    //----------------------------------------------------------------------------
    // AbstractControlPanel implementation
    //----------------------------------------------------------------------------
    
    /**
     * Resets the control panel to default (initial) values.
     */
    public void reset() {
        _potentialComboBox.setSelectedItem( _constantItem );
        handlePotentialSelection();
        _realCheckBox.setSelected( true );
        handleRealSelection();
        _imaginaryCheckBox.setSelected( false );
        handleImaginarySelection();
        _magnitudeCheckBox.setSelected( false );
        handleMagnitudeSelection();
        _phaseCheckBox.setSelected( false );
        handlePhaseSelection();
        _sumRadioButton.setSelected( true );
        handleSeparateSelection();
        _leftToRightRadioButton.setSelected( true );
        handleDirectionSelection();
        _planeWaveRadioButton.setSelected( true );
        handleWaveTypeSelection();
        _widthSlider.setValue( QTConstants.DEFAULT_PACKET_WIDTH );
        _centerSlider.setValue( QTConstants.DEFAULT_PACKET_CENTER );
        
        // Disable stuff that's not implemented yet
        {
            _phaseCheckBox.setEnabled( false );
            _planeWaveRadioButton.setEnabled( false );
            _packetWaveRadioButton.setEnabled( false );
            _measureButton.setEnabled( false );
        }
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setPotentialEnergy( AbstractPotential pe ) {
        _potentialComboBox.removeItemListener( _listener );
        if ( pe instanceof ConstantPotential ) {
            _potentialComboBox.setSelectedItem( _constantItem );
        }
        else if ( pe instanceof StepPotential ) {
            _potentialComboBox.setSelectedItem( _stepItem );
        }
        else if ( pe instanceof BarrierPotential ) {
            int numberOfBarriers = ( (BarrierPotential) pe ).getNumberOfBarriers();
            if ( numberOfBarriers == 1 ) {
                _potentialComboBox.setSelectedItem( _singleBarrierItem );
            }
            else if ( numberOfBarriers == 2 ) {
                _potentialComboBox.setSelectedItem( _doubleBarrierItem );
            }
            else {
                throw new IllegalStateException( "unsupported number of barriers: " + numberOfBarriers );
            }
        }
        else {
            throw new IllegalStateException( "unsupported potential type: " + pe.getClass().getName() );
        }
        _potentialComboBox.addItemListener( _listener );
    }
    
    /**
     * Sets the potential type.
     * 
     * @param potentialType
     */
    public void setPotentialType( PotentialType potentialType ) {
        if ( potentialType == PotentialType.CONSTANT ) {
            _potentialComboBox.setSelectedItem( _constantItem );
        }
        else if ( potentialType == PotentialType.STEP ) {
            _potentialComboBox.setSelectedItem( _stepItem );
        }
        else if ( potentialType == PotentialType.SINGLE_BARRIER ) {
            _potentialComboBox.setSelectedItem( _singleBarrierItem );
        }
        else if ( potentialType == PotentialType.DOUBLE_BARRIER ) {
            _potentialComboBox.setSelectedItem( _doubleBarrierItem );
        }
        handlePotentialSelection();
    }
    
    /**
     * Gets the potential type selection.
     * 
     * @return
     */
    private PotentialType getPotentialType() {
        PotentialType potentialType = null;
        Object selection = _potentialComboBox.getSelectedItem();
        if ( selection == _constantItem ) {
            potentialType = PotentialType.CONSTANT;
        }
        else if ( selection == _stepItem ) {
            potentialType = PotentialType.STEP;
        }
        else if ( selection == _singleBarrierItem ) {
            potentialType = PotentialType.SINGLE_BARRIER;
        }
        else if ( selection == _doubleBarrierItem ) {
            potentialType = PotentialType.DOUBLE_BARRIER;
        }
        return potentialType;
    }
    
    public void setRealSelected( boolean selected ) {
        _realCheckBox.setSelected( selected );
        handleRealSelection();
    }
    
    public boolean isRealSelected() {
        return _realCheckBox.isSelected();
    }
    
    public void setImaginarySelected( boolean selected ) {
        _imaginaryCheckBox.setSelected( selected );
        handleImaginarySelection();
    }
    
    public boolean isImaginarySelected() {
        return _imaginaryCheckBox.isSelected();
    }
    
    public void setMagnitudeSelected( boolean selected ) {
        _magnitudeCheckBox.setSelected( selected );
        handleMagnitudeSelection();
    }
    
    public boolean isMagnitudeSelected( boolean selected ) {
        return _magnitudeCheckBox.isSelected();
    }
    
    public void setPhaseSelected( boolean selected ) {
        _phaseCheckBox.setSelected( selected );
        handlePhaseSelection();
    }
    
    public boolean isPhaseSelected() {
        return _phaseCheckBox.isSelected();
    }
    
    /**
     * Enables or disabled the "separate" view.
     * 
     * @param enabled
     */
    public void setSeparateSelected( boolean selected ) {
        _separateRadioButton.setSelected( selected );
        handleSeparateSelection();
    }
    
    /**
     * Are we in "separate" view?
     * 
     * @return
     */
    private boolean isSeparateSelected() {
        return _separateRadioButton.isSelected();
    }
    
    public void setLeftToRightSelected( boolean selected ) {
        _leftToRightRadioButton.setSelected( selected );
        handleDirectionSelection();
    }
    
    public boolean isLeftToRightSelected() {
        return _leftToRightRadioButton.isSelected();
    }
    
    /**
     * Sets the wave type selection.
     * 
     * @param waveType
     */
    public void setWaveType( WaveType waveType ) {
        if ( waveType == WaveType.PLANE ) {
            _planeWaveRadioButton.setSelected( true );
        }
        else {
            _packetWaveRadioButton.setSelected( true );
        }
        handleWaveTypeSelection();
    }
    
    /**
     * Gets the current wave type selection.
     * 
     * @return
     */
    public WaveType getWaveType() {
        WaveType waveType = null;
        if ( _planeWaveRadioButton.isSelected() ) {
            waveType = WaveType.PLANE;
        }
        else { /* wave packet */
            waveType = WaveType.PACKET;
        }
        return waveType;
    }
    
    public void setPropertiesVisible( boolean visible ) {
        _propertiesPanel.setVisible( !visible );
        handlePropertiesButton();
    }
    
    public boolean isPropertiesVisible() {
        return _propertiesPanel.isVisible();
    }
    
    public void setPacketWidth( double width ) {
        _widthSlider.setValue( width );
        handleWidthSlider();
    }
    
    public double getPacketWidth() {
        return _widthSlider.getValue();
    }
    
    public void setPacketCenter( double center ) {
        _centerSlider.setValue( center );
        handleCenterSlider();
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    /*
     * EventListener dispatches events for all controls in this control panel.
     */
    private class EventListener implements ActionListener, ChangeListener, ItemListener {
        
        public EventListener() {}

        public void actionPerformed( ActionEvent event ) {

            if ( event.getSource() == _realCheckBox ) {
                handleRealSelection();
            }
            else if ( event.getSource() == _imaginaryCheckBox ) {
                handleImaginarySelection();
            }
            else if ( event.getSource() == _magnitudeCheckBox ) {
                handleMagnitudeSelection();
            }
            else if ( event.getSource() == _phaseCheckBox ) {
                handlePhaseSelection();
            }
            else if ( event.getSource() == _separateRadioButton || event.getSource() == _sumRadioButton ) {
                handleSeparateSelection();
            }
            else if ( event.getSource() == _leftToRightRadioButton || event.getSource() == _rightToLeftRadioButton ) {
                handleDirectionSelection();
            }
            else if ( event.getSource() == _planeWaveRadioButton || event.getSource() == _packetWaveRadioButton ) {
                handleWaveTypeSelection();
            }
            else if ( event.getSource() == _propertiesButton ) {
                handlePropertiesButton();
            }
            else if ( event.getSource() == _measureButton ) {
                handleMeasure();
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }

        public void stateChanged( ChangeEvent event ) {
            if ( event.getSource() == _widthSlider ) {
                handleWidthSlider();
            }
            else if ( event.getSource() == _centerSlider ) {
                handleCenterSlider();
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }

        public void itemStateChanged( ItemEvent event ) {
            if ( event.getStateChange() == ItemEvent.SELECTED ) {
                if ( event.getSource() == _potentialComboBox ) {
                    handlePotentialSelection();
                }
                else {
                    throw new IllegalArgumentException( "unexpected event: " + event );
                }
            }
        }
    }
    
    private void handlePotentialSelection() {
        AbstractPotential pe = null;
        Object o = _potentialComboBox.getSelectedItem();
        if ( o == _constantItem ) {
            pe = new ConstantPotential();
        }
        else if ( o == _stepItem ) {
            pe = new StepPotential();
        }
        else if ( o == _singleBarrierItem ) {
            pe = new BarrierPotential();
        }
        else if ( o == _doubleBarrierItem ) {
            pe = new BarrierPotential( 2 );
        }
        else {
            throw new IllegalStateException( "unsupported potential selection: " + o );
        }
        _module.setPotentialEnergy( pe );
    }

    private void handleRealSelection() {
        _module.setRealVisible( _realCheckBox.isSelected() );
    }
    
    private void handleImaginarySelection() {
        _module.setImaginaryVisible( _imaginaryCheckBox.isSelected() );
    }
    
    private void handleMagnitudeSelection() {
        _module.setMagnitudeVisible( _magnitudeCheckBox.isSelected() );
    }
    
    private void handlePhaseSelection() {
        _module.setPhaseVisible( _magnitudeCheckBox.isSelected() );
    }
    
    private void handleSeparateSelection() {
        _module.setViewSeparateSelected( _separateRadioButton.isSelected() );
    }
    
    private void handleDirectionSelection() {
        if ( _leftToRightRadioButton.isSelected() ) {
            _module.setDirection( Direction.LEFT_TO_RIGHT );
        }
        else {
            _module.setDirection( Direction.RIGHT_TO_LEFT );
        }
    }
    
    private void handleWaveTypeSelection() {
        if ( _planeWaveRadioButton.isSelected() ) {
            _sumRadioButton.setEnabled( true );
            _separateRadioButton.setEnabled( true );
            _propertiesPanel.setVisible( false );
            _propertiesButton.setEnabled( false );
            _propertiesButton.setText( _sPropertiesExpand );
            _module.setWaveType( WaveType.PLANE );
        }
        else { /* wave packet */
            _sumRadioButton.setEnabled( false );
            _separateRadioButton.setEnabled( false );
            _sumRadioButton.setSelected( true );
            handleSeparateSelection();
            _propertiesButton.setEnabled( true );
            _propertiesPanel.setVisible( false );
            _module.setWaveType( WaveType.PACKET );
        }
    }
    
    private void handlePropertiesButton() {
        if ( _propertiesPanel.isVisible() ) {
            _propertiesPanel.setVisible( false );
            _propertiesButton.setText( _sPropertiesExpand );
        }
        else {
            _propertiesPanel.setVisible( true );
            _propertiesButton.setText( _sPropertiesCollapse );
        }
    }
    
    private void handleWidthSlider() {
        double width = _widthSlider.getValue();
       _module.setWavePacketWidth( width );
    }
    
    private void handleCenterSlider() {
        double center = _centerSlider.getValue();
        _module.setWavePacketCenter( center );
    }
    
    private void handleMeasure() {
        _module.measure();
    }
}
