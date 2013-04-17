// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.control;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.BSResources;
import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.enums.BSBottomPlotMode;
import edu.colorado.phet.boundstates.enums.BSWellType;
import edu.colorado.phet.boundstates.module.BSAbstractModule;
import edu.colorado.phet.boundstates.module.BSAbstractModuleSpec;
import edu.colorado.phet.boundstates.view.ViewLegend;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.AbstractValueControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;


/**
 * BSControlPanel is the control panel that is used by the 
 * "One Well", "Two Wells" and "Many Wells" modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSControlPanel extends BSAbstractControlPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int INDENTATION = 10; // pixels
    
    private static final int SUBPANEL_SPACING = 0; // pixels

    // Color key
    private static final int COLOR_KEY_SPACING = 7; // pixels, space between checkbox and color key

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private BSAbstractModule _module;

    // Energy controls
    private BSWellComboBox _wellTypeComboBox;
    private LinearValueControl _numberOfWellsControl;
    private JButton _configureEnergyButton;
    private JButton _superpositionButton;
    private JCheckBox _magnifyingGlassCheckBox;
    private LinearValueControl _fieldConstantControl;

    // Wave Function controls
    private JRadioButton _waveFunctionRadioButton;
    private JRadioButton _probabilityDensityRadioButton;
    private JRadioButton _averageProbabilityDensityRadioButton;
    private JCheckBox _realCheckBox, _imaginaryCheckBox, _magnitudeCheckBox, _phaseCheckBox;
    private JLabel _realLegend, _imaginaryLegend, _magnitudeLegend, _phaseLegend;

    // Particle controls
    private LinearValueControl _massMultiplierControl;

    private EventListener _listener;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param module
     */
    public BSControlPanel( BSAbstractModule module, BSAbstractModuleSpec moduleSpec ) {
        super( module );

        _module = module;

        // Set the control panel's minimum width.
        int width = BSResources.getInt( "width.controlPanel", 235 );
        setMinimumWidth( width );

        // Energy chart controls
        JPanel energyControlsPanel = new JPanel();
        {
            // Titled border
            String title = BSResources.getString( "title.energyChartControls" );
            energyControlsPanel.setBorder( new TitledBorder( title ) );

            // Well type 
            JLabel wellTypeLabel = new JLabel( BSResources.getString( "label.wellType" ) );
            _wellTypeComboBox = new BSWellComboBox();
            BSWellType[] wellTypes = moduleSpec.getWellTypes();
            for ( int i = 0; i < wellTypes.length; i++ ) {
                _wellTypeComboBox.addChoice( wellTypes[i] );   
            }

            // Number of wells
            if ( moduleSpec.isNumberOfWellsSupported() ) {
                IntegerRange numberOfWellsRange = moduleSpec.getNumberOfWellsRange();
                int minWells = numberOfWellsRange.getMin();
                int maxWells = numberOfWellsRange.getMax();
                int defaultWells = numberOfWellsRange.getDefault();
                _numberOfWellsControl = new LinearValueControl( minWells, maxWells, BSResources.getString( "label.numberOfWells" ), "0", "" );
                _numberOfWellsControl.setValue( defaultWells );
                _numberOfWellsControl.setUpDownArrowDelta( 1 );
                _numberOfWellsControl.setTextFieldEditable( true );
                _numberOfWellsControl.setTextFieldColumns( 2 );
                _numberOfWellsControl.setMajorTickSpacing( 1 );
                _numberOfWellsControl.setNotifyWhileAdjusting( false );
                _numberOfWellsControl.setSnapToTicks( true );
            }
            
            // Configure button
            _configureEnergyButton = new JButton( BSResources.getString( "button.configureEnergy" ) );

            // Superposition button
            _superpositionButton = new JButton( BSResources.getString( "button.superposition" ) );
            
            // Field Constant (electric field)
            if ( moduleSpec.isFieldConstantSupported() ) {
                DoubleRange fieldConstantRange = moduleSpec.getFieldConstantRange();
                final double value = fieldConstantRange.getDefault();
                final double min = fieldConstantRange.getMin();
                final double max = fieldConstantRange.getMax();
                final String valuePattern = "0.0";
                String label = BSResources.getString( "label.fieldConstant" );
                String units = BSResources.getString( "units.fieldConstant" );
                final int columns = 3;
                _fieldConstantControl = new LinearValueControl( min, max, label, valuePattern, units );
                _fieldConstantControl.setValue( value );
                _fieldConstantControl.setUpDownArrowDelta( 0.1 );
                _fieldConstantControl.setTextFieldColumns( columns );
                _fieldConstantControl.setTextFieldEditable( true );
                _fieldConstantControl.setMajorTickSpacing( 0.5 );
                _fieldConstantControl.setNotifyWhileAdjusting( false );
            }
            
            // Magnifying glass on/off
            String magnifyingGlassLabel = BSResources.getString( "choice.magnifyingGlass" ) + " (?x)";
            _magnifyingGlassCheckBox = new JCheckBox( magnifyingGlassLabel );
            
            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            int row = 0;
            int col = 0;
            layout.addComponent( wellTypeLabel, row, col, 2, 1 );
            row++;
            layout.addComponent( _wellTypeComboBox, row, col );
            row++;
            layout.addComponent( _configureEnergyButton, row, col );
            row++;
            if ( _numberOfWellsControl != null ) {
                layout.addComponent( _numberOfWellsControl, row, col );
                row++;
            }
            if ( moduleSpec.isSuperpositionControlsSupported() ) {
                layout.addComponent( _superpositionButton, row, col );
                row++;
            }
            if ( _fieldConstantControl != null ) {
                layout.addComponent( _fieldConstantControl, row, col );
                row++;
            }
            if ( moduleSpec.isMagnifyingGlassSupported() ) {
                layout.addComponent( _magnifyingGlassCheckBox, row, col );
                row++;
            }
            energyControlsPanel.setLayout( new BorderLayout() );
            energyControlsPanel.add( innerPanel, BorderLayout.WEST );
        }

        // Bottom chart controls
        JPanel bottomChartControlsPanel = new JPanel();
        {          
            // Title
            String title = BSResources.getString( "title.bottomChartControls" );
            bottomChartControlsPanel.setBorder( new TitledBorder( title ) );
            
            // Display 
            JPanel displayPanel = new JPanel();
            {
                // Display label
                JLabel label = new JLabel( BSResources.getString( "label.display" ) );

                // Radio buttons
                _averageProbabilityDensityRadioButton = new JRadioButton( BSResources.getString( "choice.display.averageProbabilityDensity" ) );
                _probabilityDensityRadioButton = new JRadioButton( BSResources.getString( "choice.display.probabilityDensity" ) );
                _waveFunctionRadioButton = new JRadioButton( BSResources.getString( "choice.display.waveFunction" ) );

                // Button group
                ButtonGroup buttonGroup = new ButtonGroup();
                buttonGroup.add( _averageProbabilityDensityRadioButton );
                buttonGroup.add( _probabilityDensityRadioButton );
                buttonGroup.add( _waveFunctionRadioButton );
                
                // Layout
                JPanel innerPanel = new JPanel();
                EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
                innerPanel.setLayout( layout );
                layout.setAnchor( GridBagConstraints.WEST );
                layout.setInsets( new Insets( 0, 0, 0, 0 ) );
                layout.setMinimumWidth( 0, INDENTATION );
                int row = 0;
                int col = 0;
                layout.addComponent( label, row, col, 2, 1 );
                row++;
                col++;
                if ( moduleSpec.isAverageProbabilityDensitySupported() ) {
                    layout.addComponent( _averageProbabilityDensityRadioButton, row, col );
                    row++;
                }
                layout.addComponent( _probabilityDensityRadioButton, row, col );
                row++;
                layout.addComponent( _waveFunctionRadioButton, row, col );
                row++;
                displayPanel.setLayout( new BorderLayout() );
                displayPanel.add( innerPanel, BorderLayout.WEST );
            }

            // View 
            JPanel viewPanel = new JPanel();
            {
                JLabel label = new JLabel( BSResources.getString( "label.view" ) );
                _realCheckBox = new JCheckBox( BSResources.getString( "choice.view.real" ) );
                _imaginaryCheckBox = new JCheckBox( BSResources.getString( "choice.view.imaginary" ) );
                _magnitudeCheckBox = new JCheckBox( BSResources.getString( "choice.view.magnitude" ) );
                _phaseCheckBox = new JCheckBox( BSResources.getString( "choice.view.phase" ) );

                // Real
                JPanel realPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
                realPanel.add( _realCheckBox);
                realPanel.add( Box.createHorizontalStrut( COLOR_KEY_SPACING ) );
                Icon realIcon = ViewLegend.createColorKey( BSConstants.COLOR_SCHEME.getRealColor() );
                _realLegend = new JLabel( realIcon );
                realPanel.add( _realLegend );

                // Imaginary
                JPanel imaginaryPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
                imaginaryPanel.add( _imaginaryCheckBox );
                imaginaryPanel.add( Box.createHorizontalStrut( COLOR_KEY_SPACING ) );
                Icon imaginaryIcon = ViewLegend.createColorKey( BSConstants.COLOR_SCHEME.getImaginaryColor() );
                _imaginaryLegend = new JLabel( imaginaryIcon );
                imaginaryPanel.add( _imaginaryLegend );
                
                // Magnitude
                JPanel magnitudePanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
                magnitudePanel.add( _magnitudeCheckBox );
                magnitudePanel.add( Box.createHorizontalStrut( COLOR_KEY_SPACING ) );
                Icon magnitudeIcon = ViewLegend.createColorKey( BSConstants.COLOR_SCHEME.getMagnitudeColor() );
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
                layout.setInsets( new Insets( 0, 0, 0, 0 ) );
                layout.setMinimumWidth( 0, INDENTATION );
                layout.addComponent( label, 0, 0, 2, 1 );
                layout.addComponent( realPanel, 1, 1 );
                layout.addComponent( imaginaryPanel, 2, 1 );
                layout.addComponent( magnitudePanel, 3, 1 );
                layout.addComponent( phasePanel, 4, 1 );
                viewPanel.setLayout( new BorderLayout() );
                viewPanel.add( innerPanel, BorderLayout.WEST );
            }
            
            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.addComponent( displayPanel, 0, 0 );
            layout.addComponent( viewPanel, 1, 0 );
            bottomChartControlsPanel.setLayout( new BorderLayout() );
            bottomChartControlsPanel.add( innerPanel, BorderLayout.WEST );
        }
        
        // Particle controls
        JPanel particleControlsPanel = new JPanel();
        {
            // Border
            particleControlsPanel.setBorder( new TitledBorder( "" ) );
            
            // Mass Multiplier slider
            DoubleRange massMultiplierRange = moduleSpec.getMassMultiplierRange();
            final double value = massMultiplierRange.getDefault();
            final double min = massMultiplierRange.getMin();
            final double max = massMultiplierRange.getMax();
            String massLabel = BSResources.getString( "label.particleMass" );
            String valuePattern = "0.00";
            String massUnits = "<html>m<sub>e</sub></html>";
            final int columns = 3;
            _massMultiplierControl = new BSMassMultiplierSlider( min, max, massLabel, valuePattern, massUnits );
            _massMultiplierControl.setValue( value );
            _massMultiplierControl.setUpDownArrowDelta( 0.01 );
            _massMultiplierControl.setTextFieldEditable( true );
            _massMultiplierControl.setTextFieldColumns( columns );
            _massMultiplierControl.setNotifyWhileAdjusting( true );
            
            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setInsets( new Insets( 0, 0, 0, 0 ) );
            layout.addComponent( _massMultiplierControl, 0, 0 );
            particleControlsPanel.setLayout( new BorderLayout() );
            particleControlsPanel.add( innerPanel, BorderLayout.WEST );
        }

        // Layout
        {
            addControlFullWidth( energyControlsPanel );
            addVerticalSpace( SUBPANEL_SPACING );
            addControlFullWidth( bottomChartControlsPanel );
            addVerticalSpace( SUBPANEL_SPACING );
            if ( moduleSpec.isParticleControlsSupported() ) {
                addControlFullWidth( particleControlsPanel );
                addVerticalSpace( SUBPANEL_SPACING );
            }
            addResetAllButton( module );
        }

        // Interactivity
        {
            _listener = new EventListener();
            _wellTypeComboBox.addItemListener( _listener );
            if ( _numberOfWellsControl != null ) {
                _numberOfWellsControl.addChangeListener( _listener );
            }
            _configureEnergyButton.addActionListener( _listener );
            _superpositionButton.addActionListener( _listener );
            _magnifyingGlassCheckBox.addActionListener( _listener );
            if ( _fieldConstantControl != null ) {
                _fieldConstantControl.addChangeListener( _listener );
            }
            _waveFunctionRadioButton.addActionListener( _listener );
            _probabilityDensityRadioButton.addActionListener( _listener );
            _averageProbabilityDensityRadioButton.addActionListener( _listener );
            _realCheckBox.addActionListener( _listener );
            _imaginaryCheckBox.addActionListener( _listener );
            _magnitudeCheckBox.addActionListener( _listener );
            _phaseCheckBox.addActionListener( _listener );
            _massMultiplierControl.addChangeListener( _listener );
        }
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    public void setColorScheme( BSColorScheme scheme ) {
        
        // Rebuild the "Well Type" combo box...
        _wellTypeComboBox.removeItemListener( _listener );
        _wellTypeComboBox.setWellColor( scheme.getPotentialEnergyColor() );
        _wellTypeComboBox.addItemListener( _listener );
        
        // Change the legends for the wave function views...
        _realLegend.setIcon( ViewLegend.createColorKey( scheme.getRealColor() ) );
        _imaginaryLegend.setIcon( ViewLegend.createColorKey( scheme.getImaginaryColor() ) );
        _magnitudeLegend.setIcon( ViewLegend.createColorKey( scheme.getMagnitudeColor() ) );
    }
    
    public void setWellType( BSWellType wellType ) {
        _wellTypeComboBox.setSelectedWellType( wellType );
        handleWellTypeSelection();
    }
    
    public BSWellType getWellType() {
        return _wellTypeComboBox.getSelectedWellType();
    }
    
    public void setNumberOfWells( int numberOfWells ) {
        if ( _numberOfWellsControl != null ) {
            _numberOfWellsControl.setValue( numberOfWells );
            handleNumberOfWells();
        }
    }
    
    public int getNumberOfWells() {
        int numberOfWells = 0;
        if ( _numberOfWellsControl != null ) {
            numberOfWells = (int) _numberOfWellsControl.getValue();
        }
        return numberOfWells;
    }
    
    public void setMagnifyingGlassSelected( boolean selected ) {
        _magnifyingGlassCheckBox.setSelected( selected );
        handleMagnifyingGlassSelection();
    }

    public void setMagnification( double magnification ) {
        String magString = BSConstants.MAGNIFICATION_FORMAT.format( magnification );
        String text = BSResources.getString( "choice.magnifyingGlass" ) + " (" + magString + "x)";
        _magnifyingGlassCheckBox.setText( text );
    }
    
    public boolean isMagnifyingGlassSelected() {
        return _magnifyingGlassCheckBox.isSelected();
    }
    
    public void setBottomPlotMode( BSBottomPlotMode mode ) {
        if ( mode == BSBottomPlotMode.WAVE_FUNCTION ) {
            _waveFunctionRadioButton.setSelected( true );
        }
        else if ( mode == BSBottomPlotMode.PROBABILITY_DENSITY ) {
            _probabilityDensityRadioButton.setSelected( true );
        }
        else if ( mode == BSBottomPlotMode.AVERAGE_PROBABILITY_DENSITY ) {
            _averageProbabilityDensityRadioButton.setSelected( true );
        }
        else {
            throw new UnsupportedOperationException( "unsupported mode: " + mode );
        }
        handleDisplaySelection();
    }

    public BSBottomPlotMode getBottomPlotMode() {
        BSBottomPlotMode mode = BSBottomPlotMode.WAVE_FUNCTION;
        if ( _probabilityDensityRadioButton.isSelected() ) {
            mode = BSBottomPlotMode.PROBABILITY_DENSITY;
        }
        else if ( _waveFunctionRadioButton.isSelected() ) {
            mode = BSBottomPlotMode.WAVE_FUNCTION;
        }
        else if ( _averageProbabilityDensityRadioButton.isSelected() ) {
            mode = BSBottomPlotMode.AVERAGE_PROBABILITY_DENSITY;
        }
        else {
            throw new UnsupportedOperationException( "unsupported BSBottomPlotMode" );
        }
        return mode;
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

    public boolean isMagnitudeSelected() {
        return _magnitudeCheckBox.isSelected();
    }

    public void setPhaseSelected( boolean selected ) {
        _phaseCheckBox.setSelected( selected );
        handlePhaseSelection();
    }

    public boolean isPhaseSelected() {
        return _phaseCheckBox.isSelected();
    }

    public void setParticleMass( double mass ) {
        _massMultiplierControl.setValue( mass / BSConstants.ELECTRON_MASS );;
        handleMassSlider();
    }
    
    public double getParticleMass() {
        return _massMultiplierControl.getValue() * BSConstants.ELECTRON_MASS;
    }

    public void setFieldConstant( double value ) {
        if ( _fieldConstantControl != null ) {
            _fieldConstantControl.setValue( value );
        }
    }
    
    public double getFieldConstant() {
        double fieldConstant = 0;
        if ( _fieldConstantControl != null ) {
            fieldConstant = _fieldConstantControl.getValue();
        }
        return fieldConstant;
    }
    
    //----------------------------------------------------------------------------
    // Generic getters, for attaching help items
    //----------------------------------------------------------------------------
    
    public JComponent getPotentialControl() {
        return _wellTypeComboBox;
    }
    
    public JComponent getNumberOfWellsControl() {
        return _numberOfWellsControl;
    }
    
    public JComponent getParticleMassControl() {
        return _massMultiplierControl;
    }

    //----------------------------------------------------------------------------
    // Feature enablers
    //----------------------------------------------------------------------------
    
    public void setNumberOfWellsControlVisible( boolean visible ) {
        if ( _numberOfWellsControl != null ) {
            _numberOfWellsControl.setVisible( visible );
        }
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------

    /*
     * EventListener dispatches events for all controls in this control panel.
     */
    private class EventListener implements ActionListener, ChangeListener, ItemListener {

        private boolean _isSliderDragging; // is the slider dragging?
        private boolean _clockWasRunning; // was the clock running when we started dragging the slider?
        
        public EventListener() {
            _isSliderDragging = false;
            _clockWasRunning = false;
        }

        public void actionPerformed( ActionEvent event ) {

            if ( event.getSource() == _configureEnergyButton ) {
                handleConfigureEnergyButton();
            }
            else if ( event.getSource() == _superpositionButton ) {
                handleSuperpositionButton();
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
            else if ( event.getSource() == _waveFunctionRadioButton ) {
                handleDisplaySelection();
            }
            else if ( event.getSource() == _probabilityDensityRadioButton ) {
                handleDisplaySelection();
            }
            else if ( event.getSource() == _averageProbabilityDensityRadioButton ) {
                handleDisplaySelection();
            }
            else if ( event.getSource() == _magnifyingGlassCheckBox ) {
                handleMagnifyingGlassSelection();
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }
        
        public void stateChanged( ChangeEvent event ) {
            if ( event.getSource() == _numberOfWellsControl ) {
                handleNumberOfWells();
                adjustClockState( _numberOfWellsControl );
            }
            else if ( event.getSource() == _fieldConstantControl ) {
                handleFieldConstantSlider();
                adjustClockState( _fieldConstantControl );
            }
            else if ( event.getSource() == _massMultiplierControl ) {
                handleMassSlider();
                adjustClockState( _massMultiplierControl );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }

        public void itemStateChanged( ItemEvent event ) {
            if ( event.getStateChange() == ItemEvent.SELECTED ) {
                if ( event.getSource() == _wellTypeComboBox ) {
                    handleWellTypeSelection();
                }
                else {
                    throw new IllegalArgumentException( "unexpected event: " + event );
                }
            }
        }
        
        /*
         * Controls the clock while dragging a slider.
         * The clock is paused while the slider is dragged,
         * and the clock state is restore when the slider is released.
         */
        private void adjustClockState( AbstractValueControl valueControl ) {
            if ( valueControl.getNotifyWhileAdjusting() == true ) {
                if ( !valueControl.isAdjusting() ) {
                    // Pause the clock while dragging the slider.
                    _isSliderDragging = false;
                    if ( _clockWasRunning ) {
                        _module.getClock().start();
                        _clockWasRunning = false;
                    }
                }
                else if ( !_isSliderDragging ) {
                    // Restore the clock when the slider is released.
                    _isSliderDragging = true;
                    _clockWasRunning = _module.getClock().isRunning();
                    if ( _clockWasRunning ) {
                        _module.getClock().pause();
                    }
                }
            }
        }
    }

    private void handleWellTypeSelection() {
        BSWellType wellType = _wellTypeComboBox.getSelectedWellType();
        _module.setWellType( wellType );
    }

    private void handleNumberOfWells() {
        if ( _numberOfWellsControl != null ) {
            int numberOfWells = (int) Math.round( _numberOfWellsControl.getValue() );
            _module.setNumberOfWells( numberOfWells );
        }
    }
    
    private void handleConfigureEnergyButton() {
        _module.showConfigureDialog();
    }
    
    private void handleSuperpositionButton() {
        _module.showSuperpositionStateDialog();
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

    private void handleDisplaySelection() {
        if ( _waveFunctionRadioButton.isSelected() ) {
            _module.setBottomPlotMode( BSBottomPlotMode.WAVE_FUNCTION );
            _module.setRealVisible( _realCheckBox.isSelected() );
            _module.setImaginaryVisible( _imaginaryCheckBox.isSelected() );
            _module.setMagnitudeVisible( _magnitudeCheckBox.isSelected() );
            _module.setPhaseVisible( _phaseCheckBox.isSelected() );
            _realCheckBox.setEnabled( true );
            _imaginaryCheckBox.setEnabled( true );
            _magnitudeCheckBox.setEnabled( true );
            _phaseCheckBox.setEnabled( true );
        }
        else if ( _probabilityDensityRadioButton.isSelected() ) {
            _module.setBottomPlotMode( BSBottomPlotMode.PROBABILITY_DENSITY );
            _realCheckBox.setEnabled( false );
            _imaginaryCheckBox.setEnabled( false );
            _magnitudeCheckBox.setEnabled( false );
            _phaseCheckBox.setEnabled( false );
        }
        else if ( _averageProbabilityDensityRadioButton.isSelected() ) {
            _module.setBottomPlotMode( BSBottomPlotMode.AVERAGE_PROBABILITY_DENSITY );
            _realCheckBox.setEnabled( false );
            _imaginaryCheckBox.setEnabled( false );
            _magnitudeCheckBox.setEnabled( false );
            _phaseCheckBox.setEnabled( false );
        }
    }

    private void handleMassSlider() {
        double mass = _massMultiplierControl.getValue() * BSConstants.ELECTRON_MASS;
        _module.setParticleMass( mass );
    }
    
    private void handleMagnifyingGlassSelection() {
        _module.setMagnifyingGlassVisible( _magnifyingGlassCheckBox.isSelected() );
    }
    
    private void handleFieldConstantSlider() {
        if ( _fieldConstantControl != null ) {
            double value = _fieldConstantControl.getValue();
            _module.setFieldConstant( value );
        }
    }
}
