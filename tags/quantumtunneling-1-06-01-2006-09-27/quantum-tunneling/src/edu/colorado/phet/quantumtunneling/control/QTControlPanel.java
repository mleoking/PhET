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
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
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
import edu.colorado.phet.quantumtunneling.color.QTColorScheme;
import edu.colorado.phet.quantumtunneling.enums.Direction;
import edu.colorado.phet.quantumtunneling.enums.IRView;
import edu.colorado.phet.quantumtunneling.enums.PotentialType;
import edu.colorado.phet.quantumtunneling.enums.WaveType;
import edu.colorado.phet.quantumtunneling.model.AbstractPotential;
import edu.colorado.phet.quantumtunneling.model.PotentialFactory;
import edu.colorado.phet.quantumtunneling.module.QTModule;
import edu.colorado.phet.quantumtunneling.view.ViewLegend;


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
    
    private static final int INDENTATION = 0; // pixels
    private static final int SUBPANEL_SPACING = 5; // pixels
    private static final double WIDTH_TICK_SPACING = 1.0; // nm
    private static final double CENTER_TICK_SPACING = 4.0; // nm
    
    // Precision of slider controls
    private static final int WIDTH_TICK_PRECISION = 1; // # decimal places
    private static final int WIDTH_LABEL_PRECISION = 1; // # decimal places
    private static final int CENTER_TICK_PRECISION = 0; // # decimal places
    private static final int CENTER_LABEL_PRECISION = 1; // # decimal places
    
    // Color key
    private static final int COLOR_KEY_SPACING = 7; // pixels, space between checkbox and color key
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private QTModule _module;
    private PotentialComboBox _potentialComboBox;
    private JCheckBox _showValuesCheckBox;
    private JCheckBox _realCheckBox, _imaginaryCheckBox, _magnitudeCheckBox, _phaseCheckBox;
    private JLabel _realLegend, _imaginaryLegend, _magnitudeLegend, _phaseLegend;
    private JPanel _irPanel;
    private JRadioButton _separateRadioButton, _sumRadioButton;
    private JRadioButton _leftToRightRadioButton, _rightToLeftRadioButton;
    private JRadioButton _planeWaveRadioButton, _wavePacketRadioButton;
    private JPanel _propertiesPanel;
    private SliderControl _widthSlider, _centerSlider;
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
        JPanel energyPanel = new JPanel();
        {
            // Potential label
            JLabel label = new JLabel( SimStrings.get( "label.potential" ) );

            // Potential combo box 
            _potentialComboBox = new PotentialComboBox();

            // Show values
            _showValuesCheckBox = new JCheckBox( SimStrings.get( "label.showValues" ) );
            
            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setMinimumWidth( 0, INDENTATION );
            layout.addComponent( label, 0, 1 );
            layout.addComponent( _potentialComboBox, 1, 1 );
            layout.addComponent( _showValuesCheckBox, 2, 1 );
            energyPanel.setLayout( new BorderLayout() );
            energyPanel.add( innerPanel, BorderLayout.WEST );
        }
        
        // Wave Function View 
        JPanel viewPanel = new JPanel();
        {
            JLabel label = new JLabel( SimStrings.get( "label.view" ) );
            _realCheckBox = new JCheckBox( SimStrings.get( "choice.view.real" ) );
            _imaginaryCheckBox = new JCheckBox( SimStrings.get( "choice.view.imaginary" ) );
            _magnitudeCheckBox = new JCheckBox( SimStrings.get( "choice.view.magnitude" ) );
            _phaseCheckBox = new JCheckBox( SimStrings.get( "choice.view.phase" ) );
            
            // Real
            JPanel realPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
            realPanel.add( _realCheckBox);
            realPanel.add( Box.createHorizontalStrut( COLOR_KEY_SPACING ) );
            Icon realIcon = ViewLegend.createColorKey( QTConstants.COLOR_SCHEME.getRealColor() );
            _realLegend = new JLabel( realIcon );
            realPanel.add( _realLegend );
            
            // Imaginary
            JPanel imaginaryPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
            imaginaryPanel.add( _imaginaryCheckBox );
            imaginaryPanel.add( Box.createHorizontalStrut( COLOR_KEY_SPACING ) );
            Icon imaginaryIcon = ViewLegend.createColorKey( QTConstants.COLOR_SCHEME.getImaginaryColor() );
            _imaginaryLegend = new JLabel( imaginaryIcon );
            imaginaryPanel.add( _imaginaryLegend );
            
            // Magnitude
            JPanel magnitudePanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
            magnitudePanel.add( _magnitudeCheckBox );
            magnitudePanel.add( Box.createHorizontalStrut( COLOR_KEY_SPACING ) );
            Icon magnitudeIcon = ViewLegend.createColorKey( QTConstants.COLOR_SCHEME.getMagnitudeColor() );
            _magnitudeLegend = new JLabel( magnitudeIcon );
            magnitudePanel.add( _magnitudeLegend );   
            
            // Phase 
            JPanel phasePanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
            phasePanel.add( _phaseCheckBox );
            phasePanel.add( Box.createHorizontalStrut( COLOR_KEY_SPACING ) );
            Icon phaseIcon = ViewLegend.createPhaseKey();
            _phaseLegend = new JLabel( phaseIcon );
            phasePanel.add( _phaseLegend );
            
            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setMinimumWidth( 0, INDENTATION );
            layout.addComponent( label, 0, 1 );
            layout.addComponent( realPanel, 1, 1 );
            layout.addComponent( imaginaryPanel, 2, 1 );
            layout.addComponent( magnitudePanel, 3, 1 );
            layout.addComponent( phasePanel, 4, 1 );
            viewPanel.setLayout( new BorderLayout() );
            viewPanel.add( innerPanel, BorderLayout.WEST );
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
            layout.setMinimumWidth( 0, INDENTATION );
            layout.addComponent( label, 0, 1 );
            layout.addComponent( buttonPanel, 1, 1 );
            directionPanel.setLayout( new BorderLayout() );
            directionPanel.add( innerPanel, BorderLayout.WEST );
        }
        
        // Wave function form
        JPanel formPanel = new JPanel();
        {
            JLabel label = new JLabel( SimStrings.get( "label.wave" ) );
            _wavePacketRadioButton = new JRadioButton( SimStrings.get( "choice.wave.packet" ) );
            _planeWaveRadioButton = new JRadioButton( SimStrings.get( "choice.wave.plane" ) );
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( _wavePacketRadioButton );
            buttonGroup.add( _planeWaveRadioButton );
            
            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setMinimumWidth( 0, INDENTATION );
            layout.addComponent( label, 0, 1 );
            layout.addComponent( _wavePacketRadioButton, 1, 1 );
            layout.addComponent( _planeWaveRadioButton, 2, 1 );
            formPanel.setLayout( new BorderLayout() );
            formPanel.add( innerPanel, BorderLayout.WEST );
        }
        
        // Wave packet properties
        _propertiesPanel = new JPanel();
        {
            JLabel label = new JLabel( SimStrings.get( "label.packetProperties" ) );
        
            _widthSlider = new SliderControl( 
                    QTConstants.MIN_PACKET_WIDTH, QTConstants.MAX_PACKET_WIDTH,
                    WIDTH_TICK_SPACING,
                    WIDTH_TICK_PRECISION,
                    WIDTH_LABEL_PRECISION,
                    SimStrings.get( "label.packetWidth" ) + " {0} " + SimStrings.get( "units.position" ), 
                    new Insets( 5, 0, 0, 0 ) );
            _widthSlider.setInverted( true );
            
            _centerSlider = new SliderControl( 
                    QTConstants.MIN_PACKET_CENTER, QTConstants.MAX_PACKET_CENTER,
                    CENTER_TICK_SPACING,
                    CENTER_TICK_PRECISION,
                    CENTER_LABEL_PRECISION,
                    SimStrings.get( "label.packetCenter" ) + " {0} " + SimStrings.get( "units.position" ),
                    new Insets( 0, 0, 0, 0 ) );
            
            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setMinimumWidth( 0, INDENTATION );
            layout.addComponent( label, 0, 1 );
            layout.addComponent( _widthSlider, 1, 1 );
            layout.addComponent( _centerSlider, 2, 1 );
            _propertiesPanel.setLayout( new BorderLayout() );
            _propertiesPanel.add( innerPanel, BorderLayout.WEST );
        }
             
        // Incident/Reflected view
        _irPanel = new JPanel();
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
            layout.setMinimumWidth( 0, INDENTATION );
            layout.addComponent( label, 0, 1 );
            layout.addComponent( buttonPanel, 1, 1 );
            _irPanel.setLayout( new BorderLayout() );
            _irPanel.add( innerPanel, BorderLayout.WEST );
        }
        
        // Layout
        {  
            addControlFullWidth( energyPanel );
            addVerticalSpace( SUBPANEL_SPACING );
            addSeparator();
            addControlFullWidth( viewPanel );
            addVerticalSpace( SUBPANEL_SPACING );
            addSeparator();
            addControlFullWidth( directionPanel );
            addVerticalSpace( SUBPANEL_SPACING );
            addSeparator();
            addControlFullWidth( formPanel );
            addVerticalSpace( SUBPANEL_SPACING );
            addSeparator();
            addControlFullWidth( _propertiesPanel );
            // no spacing or separator, since only one of _propertiesPanel and _irPanel will be visible at a time.
            addControlFullWidth( _irPanel );
            addVerticalSpace( SUBPANEL_SPACING );
            addSeparator();
            addResetButton();
        }
        
        // Interactivity
        {
            _listener = new EventListener();
            _potentialComboBox.addItemListener( _listener );
            _showValuesCheckBox.addActionListener( _listener );
            _realCheckBox.addActionListener( _listener );
            _imaginaryCheckBox.addActionListener( _listener );
            _magnitudeCheckBox.addActionListener( _listener );
            _phaseCheckBox.addActionListener( _listener );
            _separateRadioButton.addActionListener( _listener );
            _sumRadioButton.addActionListener( _listener );
            _leftToRightRadioButton.addActionListener( _listener );
            _rightToLeftRadioButton.addActionListener( _listener );
            _planeWaveRadioButton.addActionListener( _listener );
            _wavePacketRadioButton.addActionListener( _listener );
            _widthSlider.addChangeListener( _listener );
            _centerSlider.addChangeListener( _listener );
        }
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the color scheme.
     * 
     * @param scheme
     */
    public void setColorScheme( QTColorScheme scheme ) {
        
        // Rebuild the "Potential" combo box...
        _potentialComboBox.removeItemListener( _listener );
        _potentialComboBox.setPotentialColor( scheme.getPotentialEnergyColor() );
        _potentialComboBox.addItemListener( _listener );
        
        // Change the legends for the wave function views...
        _realLegend.setIcon( ViewLegend.createColorKey( scheme.getRealColor() ) );
        _imaginaryLegend.setIcon( ViewLegend.createColorKey( scheme.getImaginaryColor() ) );
        _magnitudeLegend.setIcon( ViewLegend.createColorKey( scheme.getMagnitudeColor() ) );
    }
    
    /**
     * Sets the potential energy type by looking at a potential energy object.
     * 
     * @param pe
     */
    public void setPotentialEnergy( AbstractPotential pe ) {
        _potentialComboBox.removeItemListener( _listener );
        PotentialType potentialType = PotentialFactory.getPotentialType( pe );
        _potentialComboBox.setSelectedPotentialType( potentialType );
        _potentialComboBox.addItemListener( _listener );
    }
    
    /**
     * Sets the potential type.
     * 
     * @param potentialType
     */
    public void setPotentialType( PotentialType potentialType ) {
        _potentialComboBox.setSelectedPotentialType( potentialType );
        handlePotentialSelection();
    }
    
    /**
     * Gets the potential type selection.
     * 
     * @return
     */
    public PotentialType getPotentialType() {
        return _potentialComboBox.getSelectedPotentialType();
    }
    
    /**
     * Determines whether values are shown next to the energy drag handles.
     * @param selected
     */
    public void setShowValuesSelected( boolean selected ) {
        _showValuesCheckBox.setSelected( selected );
        handleShowValuesSelection();
    }
    
    /**
     * Are values shown next to the energy drag handles?
     * @return true or false
     */
    public boolean isShowValuesSelected() {
        return _showValuesCheckBox.isSelected();
    }
    
    /**
     * Selects the "real" view.
     * @param selected
     */
    public void setRealSelected( boolean selected ) {
        _realCheckBox.setSelected( selected );
        handleRealSelection();
    }
    
    /**
     * Is the "real" view selected?
     * @return
     */
    public boolean isRealSelected() {
        return _realCheckBox.isSelected();
    }
    
    /**
     * Selects the "imaginary" view.
     * @param selected
     */
    public void setImaginarySelected( boolean selected ) {
        _imaginaryCheckBox.setSelected( selected );
        handleImaginarySelection();
    }
    
    /**
     * Is the "imaginary" view selected?
     * @return
     */
    public boolean isImaginarySelected() {
        return _imaginaryCheckBox.isSelected();
    }
    
    /**
     * Selects the "magnitude" view.
     * @param selected
     */
    public void setMagnitudeSelected( boolean selected ) {
        _magnitudeCheckBox.setSelected( selected );
        handleMagnitudeSelection();
    }
    
    /**
     * Is the "magnitude" view selected?
     * @return
     */
    public boolean isMagnitudeSelected() {
        return _magnitudeCheckBox.isSelected();
    }
    
    /**
     * Selects the "phase" view.
     * @param selected
     */
    public void setPhaseSelected( boolean selected ) {
        _phaseCheckBox.setSelected( selected );
        handlePhaseSelection();
    }
    
    /**
     * Is the "phase" view selected?
     * @return
     */
    public boolean isPhaseSelected() {
        return _phaseCheckBox.isSelected();
    }
    
    /**
     * Sets the sum/separate control value.
     * @param irView
     */
    public void setIRView( IRView irView ) {
        _separateRadioButton.setSelected( irView == IRView.SEPARATE );
        _sumRadioButton.setSelected( irView == IRView.SUM );
        handleIRViewSelection();
    }
    
    /**
     * Gets the sum/separate control value.
     * @return
     */
    public IRView getIRView() {
        if ( _separateRadioButton.isSelected() ) {
            return IRView.SEPARATE;
        }
        else {
            return IRView.SUM;
        }
    }
    
    /**
     * Sets the direction control value.
     * @param direction
     */
    public void setDirection( Direction direction ) {
        _leftToRightRadioButton.setSelected( direction == Direction.LEFT_TO_RIGHT );
        _rightToLeftRadioButton.setSelected( direction == Direction.RIGHT_TO_LEFT );
        handleDirectionSelection();
    }
    
    /**
     * Gets the direction control value.
     * @return
     */
    public Direction getDirection() {
        if ( _leftToRightRadioButton.isSelected() ) {
            return Direction.LEFT_TO_RIGHT;
        }
        else {
            return Direction.RIGHT_TO_LEFT;
        }
    }
    
    /**
     * Sets the wave type selection.
     * 
     * @param waveType
     */
    public void setWaveType( WaveType waveType ) {
        _planeWaveRadioButton.setSelected( waveType == WaveType.PLANE );
        _wavePacketRadioButton.setSelected( waveType == WaveType.PACKET );
        handleWaveTypeSelection();
    }
    
    /**
     * Gets the current wave type selection.
     * 
     * @return
     */
    public WaveType getWaveType() {
        if ( _planeWaveRadioButton.isSelected() ) {
            return WaveType.PLANE;
        }
        else {
            return WaveType.PACKET;
        }
    }
    
    /**
     * Sets the wave packet width control value.
     * @param width
     */
    public void setPacketWidth( double width ) {
        _widthSlider.setValue( width );
        handleWidthSlider();
    }
    
    /**
     * Gets the wave packet width control value.
     * @return
     */
    public double getPacketWidth() {
        return _widthSlider.getValue();
    }
    
    /**
     * Sets the wave packet center control value.
     * @param center
     */
    public void setPacketCenter( double center ) {
        _centerSlider.setValue( center );
        handleCenterSlider();
    }
    
    /**
     * Gets the wave packet center control value.
     * @return
     */
    public double getPacketCenter() {
        return _centerSlider.getValue();
    }
    
    /**
     * Gets the component used to set the potential type,
     * used for attaching help items.
     * @return
     */
    public JComponent getPotentialComponent() {
        return _potentialComboBox;
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

            if ( event.getSource() == _showValuesCheckBox ) {
                handleShowValuesSelection();
            }
            else if ( event.getSource() == _realCheckBox ) {
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
                handleIRViewSelection();
            }
            else if ( event.getSource() == _leftToRightRadioButton || event.getSource() == _rightToLeftRadioButton ) {
                handleDirectionSelection();
            }
            else if ( event.getSource() == _planeWaveRadioButton || event.getSource() == _wavePacketRadioButton ) {
                handleWaveTypeSelection();
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
        _module.setPotentialType( getPotentialType() );
    }

    private void handleShowValuesSelection() {
        _module.setValuesVisible( _showValuesCheckBox.isSelected() );
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
        _module.setPhaseVisible( _phaseCheckBox.isSelected() );
    }
    
    private void handleIRViewSelection() {
        _module.setIRView( getIRView() );
        if ( getWaveType() == WaveType.PLANE ) {
            if ( getIRView() == IRView.SEPARATE ) {
                // phase view is not supported in separate mode for plane waves
                _phaseCheckBox.setEnabled( false );
                handlePhaseSelection();
            }
            else {
                _phaseCheckBox.setEnabled( true );
                handlePhaseSelection();
            }
        }
    }
    
    private void handleDirectionSelection() {
        _module.setDirection( getDirection() );
    }
    
    private void handleWaveTypeSelection() {
        if ( getWaveType() == WaveType.PLANE ) {
            _phaseCheckBox.setEnabled( getIRView() == IRView.SUM );
            _propertiesPanel.setVisible( false );
            _irPanel.setVisible( true );
            _module.setWaveType( WaveType.PLANE );
        }
        else { /* wave packet */
            _phaseCheckBox.setEnabled( true );
            _propertiesPanel.setVisible( true );
            _irPanel.setVisible( false );
            _module.setWaveType( WaveType.PACKET );
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
}
