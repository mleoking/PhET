/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.color.BSColorScheme;
import edu.colorado.phet.boundstates.enums.BSWellType;
import edu.colorado.phet.boundstates.model.BSDoubleRange;
import edu.colorado.phet.boundstates.model.BSIntegerRange;
import edu.colorado.phet.boundstates.module.BSAbstractModule;
import edu.colorado.phet.boundstates.view.BSBottomPlot;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSControlPanel is the control panel that is used by the 
 * "Single", "Double" and "Many" modules.  Controls can be hidden and/or
 * configured to suit the needs of the module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSControlPanel extends BSAbstractControlPanel {

    //----------------------------------------------------------------------------
    // Class data (public)
    //----------------------------------------------------------------------------
    
    private static final int INDENTATION = 10; // pixels
    
    //----------------------------------------------------------------------------
    // Class data (private)
    //----------------------------------------------------------------------------
    
    private static final boolean NOTIFY_WHILE_DRAGGING = false; // behavior of sliders
    
    private static final int SUBPANEL_SPACING = 5; // pixels
    private static final Insets SLIDER_INSETS = new Insets( 0, 0, 0, 0 );

    // Color key
    private static final int COLOR_KEY_WIDTH = 25; // pixels
    private static final int COLOR_KEY_HEIGHT = 3; // pixels
    private static final int PHASE_KEY_WIDTH = COLOR_KEY_WIDTH;
    private static final int PHASE_KEY_HEIGHT = 10;
    private static final int COLOR_KEY_SPACING = 7; // pixels, space between checkbox and color key

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private BSAbstractModule _module;

    // Energy controls
    private BSWellComboBox _wellTypeComboBox;
    private SliderControl _numberOfWellsSlider;
    private JButton _configureEnergyButton;
    private JButton _superpositionButton;

    // Wave Function controls
    private JRadioButton _waveFunctionRadioButton, _probabilityDensityRadioButton;
    private JCheckBox _realCheckBox, _imaginaryCheckBox, _magnitudeCheckBox, _phaseCheckBox;
    private JLabel _realLegend, _imaginaryLegend, _magnitudeLegend, _phaseLegend;

    // Particle controls
    private SliderControl _massMultiplierSlider;

    private EventListener _listener;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param module
     */
    public BSControlPanel( BSAbstractModule module,
            BSWellType[] wellTypes,
            BSIntegerRange numberOfWellsRange,
            BSDoubleRange massMultiplierRange,
            final boolean supportsSuperpositionControls,
            final boolean supportsParticleControls
            ) {
        super( module );

        _module = module;

        // Set the control panel's minimum width.
        String widthString = SimStrings.get( "width.controlPanel" );
        if ( widthString != null ) {
            int width = Integer.parseInt( widthString );
            setMinumumWidth( width );
        }

        // Potential Well
        JPanel energyControlsPanel = new JPanel();
        {
            // Titled border
            String title = SimStrings.get( "title.energyChartControls" );
            energyControlsPanel.setBorder( new TitledBorder( title ) );

            // Well type 
            JLabel wellTypeLabel = new JLabel( SimStrings.get( "label.wellType" ) );
            _wellTypeComboBox = new BSWellComboBox();
            for ( int i = 0; i < wellTypes.length; i++ ) {
                _wellTypeComboBox.addChoice( wellTypes[i] );   
            }

            // Number of wells
            _numberOfWellsSlider = new SliderControl( 
                    numberOfWellsRange.getDefault(),
                    numberOfWellsRange.getMin(), 
                    numberOfWellsRange.getMax(),
                    1, 0, 0, SimStrings.get( "label.numberOfWells" ), "", 2, SLIDER_INSETS );
            _numberOfWellsSlider.setTextEditable( true );
            _numberOfWellsSlider.setNotifyWhileDragging( NOTIFY_WHILE_DRAGGING );
            _numberOfWellsSlider.getSlider().setSnapToTicks( true );
            
            // Configure button
            _configureEnergyButton = new JButton( SimStrings.get( "button.configureEnergy" ) );

            // Superposition button
            _superpositionButton = new JButton( SimStrings.get( "button.superposition" ) );

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
            if ( numberOfWellsRange.getLength() > 0 ) {
                layout.addComponent( _numberOfWellsSlider, row, col );
                row++;
            }
            if ( supportsSuperpositionControls ) {
                layout.addComponent( _superpositionButton, row, col );
                row++;
            }
            energyControlsPanel.setLayout( new BorderLayout() );
            energyControlsPanel.add( innerPanel, BorderLayout.WEST );
        }

        // Bottom chart controls
        JPanel bottomChartControlsPanel = new JPanel();
        {          
            // Title
            String title = SimStrings.get( "title.bottomChartControls" );
            bottomChartControlsPanel.setBorder( new TitledBorder( title ) );
            
            // Display 
            JPanel displayPanel = new JPanel();
            {
                // Display label
                JLabel label = new JLabel( SimStrings.get( "label.display" ) );

                // Radio buttons
                _probabilityDensityRadioButton = new JRadioButton( SimStrings.get( "choice.display.probabilityDensity" ) );
                _waveFunctionRadioButton = new JRadioButton( SimStrings.get( "choice.display.waveFunction" ) );

                // Button group
                ButtonGroup buttonGroup = new ButtonGroup();
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
                JLabel label = new JLabel( SimStrings.get( "label.view" ) );
                _realCheckBox = new JCheckBox( SimStrings.get( "choice.view.real" ) );
                _imaginaryCheckBox = new JCheckBox( SimStrings.get( "choice.view.imaginary" ) );
                _magnitudeCheckBox = new JCheckBox( SimStrings.get( "choice.view.magnitude" ) );
                _phaseCheckBox = new JCheckBox( SimStrings.get( "choice.view.phase" ) );

                // Real
                JPanel realPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
                realPanel.add( _realCheckBox);
                realPanel.add( Box.createHorizontalStrut( COLOR_KEY_SPACING ) );
                Icon realIcon = createColorKey( BSConstants.COLOR_SCHEME.getRealColor() );
                _realLegend = new JLabel( realIcon );
                realPanel.add( _realLegend );

                // Imaginary
                JPanel imaginaryPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
                imaginaryPanel.add( _imaginaryCheckBox );
                imaginaryPanel.add( Box.createHorizontalStrut( COLOR_KEY_SPACING ) );
                Icon imaginaryIcon = createColorKey( BSConstants.COLOR_SCHEME.getImaginaryColor() );
                _imaginaryLegend = new JLabel( imaginaryIcon );
                imaginaryPanel.add( _imaginaryLegend );
                
                // Magnitude
                JPanel magnitudePanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
                magnitudePanel.add( _magnitudeCheckBox );
                magnitudePanel.add( Box.createHorizontalStrut( COLOR_KEY_SPACING ) );
                Icon magnitudeIcon = createColorKey( BSConstants.COLOR_SCHEME.getMagnitudeColor() );
                _magnitudeLegend = new JLabel( magnitudeIcon );
                magnitudePanel.add( _magnitudeLegend );   
                
                // Phase 
                JPanel phasePanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
                phasePanel.add( _phaseCheckBox );
                phasePanel.add( Box.createHorizontalStrut( COLOR_KEY_SPACING ) );
                Icon phaseIcon = createPhaseKey();
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
            final double value = massMultiplierRange.getDefault();
            final double min = massMultiplierRange.getMin();
            final double max = massMultiplierRange.getMax();
            final double tickSpacing = ( max - min );
            final int decimalPlaces = massMultiplierRange.getSignificantDecimalPlaces();
            String massLabel = SimStrings.get( "label.particleMass" );
            String massUnits = "<html>m<sub>e</sub></html>";
            final int columns = 3;
            _massMultiplierSlider = new BSMassMultiplierSlider( value, min, max,
                    tickSpacing, decimalPlaces, decimalPlaces,
                    massLabel, massUnits, columns, SLIDER_INSETS );
            _massMultiplierSlider.setNotifyWhileDragging( NOTIFY_WHILE_DRAGGING );
            
            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setInsets( new Insets( 0, 0, 0, 0 ) );
            layout.addComponent( _massMultiplierSlider, 0, 0 );
            particleControlsPanel.setLayout( new BorderLayout() );
            particleControlsPanel.add( innerPanel, BorderLayout.WEST );
        }

        // Layout
        {
            addControlFullWidth( energyControlsPanel );
            addVerticalSpace( SUBPANEL_SPACING );
            addControlFullWidth( bottomChartControlsPanel );
            addVerticalSpace( SUBPANEL_SPACING );
            if ( supportsParticleControls ) {
                addControlFullWidth( particleControlsPanel );
                addVerticalSpace( SUBPANEL_SPACING );
            }
            addResetButton();
        }

        // Interactivity
        {
            _listener = new EventListener();
            _wellTypeComboBox.addItemListener( _listener );
            _numberOfWellsSlider.addChangeListener( _listener );
            _configureEnergyButton.addActionListener( _listener );
            _superpositionButton.addActionListener( _listener );
            _waveFunctionRadioButton.addActionListener( _listener );
            _probabilityDensityRadioButton.addActionListener( _listener );
            _realCheckBox.addActionListener( _listener );
            _imaginaryCheckBox.addActionListener( _listener );
            _magnitudeCheckBox.addActionListener( _listener );
            _phaseCheckBox.addActionListener( _listener );
            if ( supportsParticleControls ) {
                _massMultiplierSlider.addChangeListener( _listener );
            }
        }
    }

    //----------------------------------------------------------------------------
    // Color key creation
    //----------------------------------------------------------------------------

    /*
     * Creates a color key icon by drawing a solid horizontal line.
     * 
     * @param color
     * @return Icon
     */
    private Icon createColorKey( Color color ) {
        BufferedImage image = new BufferedImage( COLOR_KEY_WIDTH, COLOR_KEY_HEIGHT, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = image.createGraphics();
        Rectangle2D r = new Rectangle2D.Double( 0, 0, COLOR_KEY_WIDTH, COLOR_KEY_HEIGHT );
        g2.setPaint( color );
        g2.fill( r );
        Icon icon = new ImageIcon( image );
        return icon;
    }
    
    /*
     * Creates a color key icon for phase by drawing the phase color series.
     * 
     * @return Icon
     */
    private Icon createPhaseKey() {
        BufferedImage image = new BufferedImage( PHASE_KEY_WIDTH, PHASE_KEY_HEIGHT, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = image.createGraphics();
        Rectangle2D r = new Rectangle2D.Double();
        for ( int i = 0; i < 360; i++ ) {
            r.setRect( i * PHASE_KEY_WIDTH / 360.0, 0, PHASE_KEY_WIDTH / 360.0, PHASE_KEY_HEIGHT );
            Color color = Color.getHSBColor( i / 360f, 1f, 1f );
            g2.setColor( color );
            g2.fill( r );
        }
        Icon icon = new ImageIcon( image );
        return icon;
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the color scheme.
     * 
     * @param scheme
     */
    public void setColorScheme( BSColorScheme scheme ) {
        
        // Rebuild the "Well Type" combo box...
        _wellTypeComboBox.removeItemListener( _listener );
        _wellTypeComboBox.setWellColor( scheme.getPotentialEnergyColor() );
        _wellTypeComboBox.addItemListener( _listener );
        
        // Change the legends for the wave function views...
        _realLegend.setIcon( createColorKey( scheme.getRealColor() ) );
        _imaginaryLegend.setIcon( createColorKey( scheme.getImaginaryColor() ) );
        _magnitudeLegend.setIcon( createColorKey( scheme.getMagnitudeColor() ) );
    }
    
    public void setWellType( BSWellType wellType ) {
        _wellTypeComboBox.setSelectedWellType( wellType );
        handleWellTypeSelection();
    }
    
    public BSWellType getWellType() {
        return _wellTypeComboBox.getSelectedWellType();
    }
    
    public void setNumberOfWells( int numberOfWells ) {
        _numberOfWellsSlider.setValue( numberOfWells );
        handleNumberOfWells();
    }
    
    public int getNumberOfWells() {
        return (int) _numberOfWellsSlider.getValue();
    }

    public void setBottomPlotMode( int mode ) {
        if ( mode == BSBottomPlot.MODE_WAVE_FUNCTION ) {
            _waveFunctionRadioButton.setSelected( true );
        }
        else if ( mode == BSBottomPlot.MODE_PROBABILITY_DENSITY ) {
            _probabilityDensityRadioButton.setSelected( true );
        }
        else {
            throw new IllegalArgumentException( "invalid mode: " + mode );
        }
        handleDisplaySelection();
    }

    public int getBottomPlotMode() {
        int mode = BSBottomPlot.MODE_WAVE_FUNCTION;
        if ( _probabilityDensityRadioButton.isSelected() ) {
            mode = BSBottomPlot.MODE_PROBABILITY_DENSITY;
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
        _massMultiplierSlider.setValue( mass / BSConstants.ELECTRON_MASS );;
        handleMassSlider();
    }
    
    public double getParticleMass() {
        return _massMultiplierSlider.getValue() * BSConstants.ELECTRON_MASS;
    }

    //----------------------------------------------------------------------------
    // Generic getters, for attaching help items
    //----------------------------------------------------------------------------
    
    public JComponent getPotentialControl() {
        return _wellTypeComboBox;
    }
    
    public JComponent getNumberOfWellsControl() {
        return _numberOfWellsSlider;
    }
    
    public JComponent getParticleMassControl() {
        return _massMultiplierSlider;
    }

    //----------------------------------------------------------------------------
    // Feature enablers
    //----------------------------------------------------------------------------
    
    public void setNumberOfWellsControlVisible( boolean visible ) {
        _numberOfWellsSlider.setVisible( visible );
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
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }

        public void stateChanged( ChangeEvent event ) {
            if ( event.getSource() == _numberOfWellsSlider ) {
                handleNumberOfWells();
            }
            else if ( event.getSource() == _massMultiplierSlider ) {
                handleMassSlider();
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
    }

    private void handleWellTypeSelection() {
        BSWellType wellType = _wellTypeComboBox.getSelectedWellType();
        _module.setWellType( wellType );
    }

    private void handleNumberOfWells() {
        int numberOfWells = (int) _numberOfWellsSlider.getValue();
        _module.setNumberOfWells( numberOfWells );
    }
    
    private void handleConfigureEnergyButton() {
        _module.showConfigureEnergyDialog();
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
            _module.setBottomPlotMode( BSBottomPlot.MODE_WAVE_FUNCTION );
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
            _module.setBottomPlotMode( BSBottomPlot.MODE_PROBABILITY_DENSITY );
            _realCheckBox.setEnabled( false );
            _imaginaryCheckBox.setEnabled( false );
            _magnitudeCheckBox.setEnabled( false );
            _phaseCheckBox.setEnabled( false );
        }
    }

    private void handleMassSlider() {
        double mass = _massMultiplierSlider.getValue() * BSConstants.ELECTRON_MASS;
        _module.setParticleMass( mass );
    }
    
}
