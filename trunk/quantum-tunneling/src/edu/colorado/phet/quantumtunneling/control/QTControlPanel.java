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
import edu.colorado.phet.quantumtunneling.module.AbstractModule;


/**
 * QTControlPanel
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTControlPanel extends AbstractControlPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int LEFT_MARGIN = 0;
    private static final int SUBPANEL_SPACING = 10;
    private static final String EXPAND_SYMBOL = ">>";
    private static final String COLLAPSE_SYMBOL = "<<";
    private static final double WIDTH_TICK_SPACING = 1.0; // nm
    private static final double CENTER_TICK_SPACING = 5.0; // nm
    private static final int WIDTH_SIGNIFICANT_DECIMAL_PLACES = 1;
    private static final int CENTER_SIGNIFICANT_DECIMAL_PLACES = 1;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JComboBox _potentialComboBox;
    private JCheckBox _realCheckBox, _imaginaryCheckBox, _magnitudeCheckBox, _phaseCheckBox;
    private Object _constantItem, _stepItem, _barrierItem, _doubleBarrierItem;
    private JRadioButton _separateRadioButton, _sumRadioButton;
    private JRadioButton _leftToRightRadioButton, _rightToLeftRadioButton;
    private JRadioButton _planeWaveRadioButton, _packetWaveRadioButton;
    private JButton _propertiesButton;
    private String _sPropertiesExpand, _sPropertiesCollapse;
    private JPanel _propertiesPanel;
    private SliderControl _widthSlider, _centerSlider;
    private JButton _measureButton;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param module
     */
    public QTControlPanel( AbstractModule module ) {
        super( module );
        
        // Set the control panel's minimum width.
        String widthString = SimStrings.get( "QTControlPanel.width" );
        int width = Integer.parseInt( widthString );
        setMinumumWidth( width );
        
        // Potential
        JPanel potentialPanel = new JPanel();
        {
            JLabel label = new JLabel( SimStrings.get( "QTControlPanel.potential" ) );
            
            _constantItem = SimStrings.get( "QTControlPanel.potential.constant" );
            _stepItem = SimStrings.get( "QTControlPanel.potential.step" );
            _barrierItem = SimStrings.get( "QTControlPanel.potential.barrier" );
            _doubleBarrierItem = SimStrings.get( "QTControlPanel.potential.double" );
            
            Object[] items = { _constantItem, _stepItem, _barrierItem, _doubleBarrierItem };
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
            JLabel label = new JLabel( SimStrings.get( "QTControlPanel.waveFunctionView" ) );
            _realCheckBox = new JCheckBox( SimStrings.get( "QTControlPanel.realPart" ) );
            _imaginaryCheckBox = new JCheckBox( SimStrings.get( "QTControlPanel.imaginaryPart" ) );
            _magnitudeCheckBox = new JCheckBox( SimStrings.get( "QTControlPanel.magnitude" ) );
            _phaseCheckBox = new JCheckBox( SimStrings.get( "QTControlPanel.phase" ) );
            
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
            JLabel label = new JLabel( SimStrings.get( "QTControlPanel.irWaves" ) );
            _sumRadioButton = new JRadioButton( SimStrings.get( "QTControlPanel.sum" ) );
            _separateRadioButton = new JRadioButton( SimStrings.get( "QTControlPanel.separate" ) );
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
            JLabel label = new JLabel( SimStrings.get( "QTControlPanel.direction" ) );
            
            JLabel l2rLabel = null;
            JLabel r2lLabel = null;
            try {
                // Pretty icons to indicate direction
                ImageIcon l2rIcon = new ImageIcon( ImageLoader.loadBufferedImage( QTConstants.IMAGE_ARROW_L2R ) );
                l2rLabel = new JLabel( l2rIcon );
                ImageIcon r2lIcon = new ImageIcon( ImageLoader.loadBufferedImage( QTConstants.IMAGE_ARROW_R2L ) );
                r2lLabel = new JLabel( r2lIcon );
            }
            catch ( IOException e ) {
                // Fallback to crude arrows if we can't load icons
                l2rLabel = new JLabel( "-->" );
                r2lLabel = new JLabel( "<--" );
                e.printStackTrace();
            }

            _leftToRightRadioButton = new JRadioButton();
            _rightToLeftRadioButton = new JRadioButton();
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( _leftToRightRadioButton );
            buttonGroup.add( _rightToLeftRadioButton );
            
            int horizontalSpaceBetweenButtons = 20;
            JPanel buttonPanel = new JPanel();
            EasyGridBagLayout buttonLayout = new EasyGridBagLayout( buttonPanel );
            buttonPanel.setLayout( buttonLayout );
            buttonLayout.setAnchor( GridBagConstraints.WEST );
            buttonLayout.setMinimumWidth( 2, horizontalSpaceBetweenButtons );
            buttonLayout.addComponent( _leftToRightRadioButton, 0, 0 );
            buttonLayout.addComponent( l2rLabel, 0, 1 );
            buttonLayout.addComponent( _rightToLeftRadioButton, 0, 3 );
            buttonLayout.addComponent( r2lLabel, 0, 4 );
            
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
            JLabel label = new JLabel( SimStrings.get( "QTControlPanel.waveFunctionForm" ) );
            _planeWaveRadioButton = new JRadioButton( SimStrings.get( "QTControlPanel.planeWave" ) );
            _packetWaveRadioButton = new JRadioButton( SimStrings.get( "QTControlPanel.packetWave" ) );
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
            _sPropertiesExpand = SimStrings.get( "QTControlPanel.packetProperties" ) + " " + EXPAND_SYMBOL;
            _sPropertiesCollapse = SimStrings.get( "QTControlPanel.packetProperties" ) + " " + COLLAPSE_SYMBOL;
            _propertiesButton = new JButton( _sPropertiesExpand  );
            
            // Subpanel
            _propertiesPanel = new JPanel();
            {
                _widthSlider = new SliderControl( 
                        QTConstants.MIN_PACKET_WIDTH, QTConstants.MAX_PACKET_WIDTH,
                        WIDTH_TICK_SPACING,
                        WIDTH_SIGNIFICANT_DECIMAL_PLACES,
                        SimStrings.get( "QTControlPanel.packetWidth" ), 
                        DEFAULT_INSETS );
                _widthSlider.setInverted( true );
                
                _centerSlider = new SliderControl( 
                        QTConstants.MIN_PACKET_CENTER, QTConstants.MAX_PACKET_CENTER,
                        CENTER_TICK_SPACING,
                        CENTER_SIGNIFICANT_DECIMAL_PLACES,
                        SimStrings.get( "QTControlPanel.packetCenter" ),
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
        _measureButton = new JButton( SimStrings.get( "QTControlPanel.measure"  ) );
        
        // Layout
        {
            addSeparator();
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
            EventListener listener = new EventListener();
            _potentialComboBox.addItemListener( listener );
            _realCheckBox.addActionListener( listener );
            _imaginaryCheckBox.addActionListener( listener );
            _magnitudeCheckBox.addActionListener( listener );
            _phaseCheckBox.addActionListener( listener );
            _separateRadioButton.addActionListener( listener );
            _sumRadioButton.addActionListener( listener );
            _leftToRightRadioButton.addActionListener( listener );
            _rightToLeftRadioButton.addActionListener( listener );
            _planeWaveRadioButton.addActionListener( listener );
            _packetWaveRadioButton.addActionListener( listener );
            _propertiesButton.addActionListener( listener );
            _widthSlider.addChangeListener( listener );
            _centerSlider.addChangeListener( listener );
            _measureButton.addActionListener( listener );
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
        _potentialComboBox.setSelectedItem( _barrierItem );
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
        handleSumSelection();
        _leftToRightRadioButton.setSelected( true );
        handleDirectionSelection();
        _planeWaveRadioButton.setSelected( true );
        handleWaveTypeSelection();
        _widthSlider.setValue( QTConstants.DEFAULT_PACKET_WIDTH );
        _centerSlider.setValue( QTConstants.DEFAULT_PACKET_CENTER );
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
                handleSumSelection();
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
        
    }

    private void handleRealSelection() {
        
    }
    
    private void handleImaginarySelection() {
        
    }
    
    private void handleMagnitudeSelection() {
        
    }
    
    private void handlePhaseSelection() {
        
    }
    
    private void handleSumSelection() {
        
    }
    
    private void handleDirectionSelection() {
        
    }
    
    private void handleWaveTypeSelection() {
        if ( _planeWaveRadioButton.isSelected() ) {
            _propertiesPanel.setVisible( false );
            _propertiesButton.setEnabled( false );
            _propertiesButton.setText( _sPropertiesExpand );
            _measureButton.setEnabled( false );
        }
        else {
            _propertiesButton.setEnabled( true );
            _propertiesPanel.setVisible( false );
            _measureButton.setEnabled( true );
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
       
    }
    
    private void handleCenterSlider() {
        
    }
    
    private void handleMeasure() {
        
    }
}
