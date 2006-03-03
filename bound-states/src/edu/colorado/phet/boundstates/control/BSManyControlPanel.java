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
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.enum.WellType;
import edu.colorado.phet.boundstates.module.BSManyModule;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSManyControlPanel
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSManyControlPanel extends BSAbstractControlPanel {

    //----------------------------------------------------------------------------
    // Class data (public)
    //----------------------------------------------------------------------------

    // Display types
    public static final int DISPLAY_WAVE_FUNCTION = 0;
    public static final int DISPLAY_PROBABIITY_DENSITY = 1;
    
    //----------------------------------------------------------------------------
    // Class data (private)
    //----------------------------------------------------------------------------

    private static final int INDENTATION = 0; // pixels
    private static final int SUBPANEL_SPACING = 10; // pixels
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

    private BSManyModule _module;

    // Energy controls
    private BSWellComboBox _wellTypeComboBox;
    private SliderControl _numberOfWellsSlider;
    private JButton _configureEnergyButton;
    private JButton _superpositionButton;

    // Wave Function controls
    private JRadioButton _waveFunctionRadioButton,
            _probabilityDensityRadioButton;
    private JCheckBox _realCheckBox, _imaginaryCheckBox, _magnitudeCheckBox,
            _phaseCheckBox;

    // Particle controls
    private SliderControl _massSlider;

    private EventListener _listener;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param module
     */
    public BSManyControlPanel( BSManyModule module ) {
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

            // Potential Well label
            JLabel label = new JLabel( SimStrings.get( "label.well" ) );

            // Potential combo box 
            _wellTypeComboBox = new BSWellComboBox();

            // Number of wells
            String numberFormat = SimStrings.get( "label.numberOfWells" ) + " {0}";
            _numberOfWellsSlider = new SliderControl( 1, 1, 10, 1, 0, 0, numberFormat, SLIDER_INSETS );
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
            layout.addComponent( label, row, col, 2, 1 );
            row++;
            layout.addComponent( _wellTypeComboBox, row, col );
            row++;
            layout.addComponent( _configureEnergyButton, row, col );
            row++;
            layout.addComponent( _superpositionButton, row, col );
            row++;
            layout.addComponent( _numberOfWellsSlider, row, col );
            row++;
            energyControlsPanel.setLayout( new BorderLayout() );
            energyControlsPanel.add( innerPanel, BorderLayout.WEST );
        }

        // Wave Function controls
        JPanel waveFunctionControlsPanel = new JPanel();
        {
            final int indentation = 15;
            
            // Title
            String title = SimStrings.get( "title.waveFunctionChartControls" );
            waveFunctionControlsPanel.setBorder( new TitledBorder( title ) );
            
            // Display 
            JPanel displayPanel = new JPanel();
            {
                // Display label
                JLabel label = new JLabel( SimStrings.get( "label.display" ) );

                // Radio buttons
                _waveFunctionRadioButton = new JRadioButton( SimStrings.get( "choice.display.waveFunction" ) );
                _probabilityDensityRadioButton = new JRadioButton( SimStrings.get( "choice.display.probabilityDensity" ) );

                // Button group
                ButtonGroup buttonGroup = new ButtonGroup();
                buttonGroup.add( _waveFunctionRadioButton );
                buttonGroup.add( _probabilityDensityRadioButton );

                // Layout
                JPanel innerPanel = new JPanel();
                EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
                innerPanel.setLayout( layout );
                layout.setAnchor( GridBagConstraints.WEST );
                layout.setInsets( new Insets( 0, 0, 0, 0 ) );
                layout.setMinimumWidth( 0, indentation );
                int row = 0;
                int col = 0;
                layout.addComponent( label, row, col, 2, 1 );
                row++;
                col++;
                layout.addComponent( _waveFunctionRadioButton, row, col );
                row++;
                layout.addComponent( _probabilityDensityRadioButton, row, col );
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
                realPanel.add( _realCheckBox );
                realPanel.add( Box.createHorizontalStrut( COLOR_KEY_SPACING ) );
                realPanel.add( createColorKey( BSConstants.REAL_WAVE_COLOR ) );

                // Imaginary
                JPanel imaginaryPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
                imaginaryPanel.add( _imaginaryCheckBox );
                imaginaryPanel.add( Box.createHorizontalStrut( COLOR_KEY_SPACING ) );
                imaginaryPanel.add( createColorKey( BSConstants.IMAGINARY_WAVE_COLOR ) );

                // Magnitude
                JPanel magnitudePanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
                magnitudePanel.add( _magnitudeCheckBox );
                magnitudePanel.add( Box.createHorizontalStrut( COLOR_KEY_SPACING ) );
                magnitudePanel.add( createColorKey( BSConstants.MAGNITUDE_WAVE_COLOR ) );

                // Phase 
                JPanel phasePanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
                phasePanel.add( _phaseCheckBox );
                phasePanel.add( Box.createHorizontalStrut( COLOR_KEY_SPACING ) );
                phasePanel.add( createPhaseKey() );

                // Layout
                JPanel innerPanel = new JPanel();
                EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
                innerPanel.setLayout( layout );
                layout.setAnchor( GridBagConstraints.WEST );
                layout.setInsets( new Insets( 0, 0, 0, 0 ) );
                layout.setMinimumWidth( 0, indentation );
                int row = 0;
                int col = 0;
                layout.addComponent( label, row, col, 2, 1 );
                row++;
                col++;
                layout.addComponent( realPanel, row, col );
                row++;
                layout.addComponent( imaginaryPanel, row, col );
                row++;
                layout.addComponent( magnitudePanel, row, col );
                row++;
                layout.addComponent( phasePanel, row, col );
                row++;
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
            waveFunctionControlsPanel.setLayout( new BorderLayout() );
            waveFunctionControlsPanel.add( innerPanel, BorderLayout.WEST );
        }
        
        // Particle controls
        JPanel particleControlsPanel = new JPanel();
        {
            // Title
            String title = SimStrings.get( "title.particleControls" );
            particleControlsPanel.setBorder( new TitledBorder( title ) );
            
            // Mass slider

            String massFormat = "<html>" + SimStrings.get( "label.particleMass" ) + " {0}m<sub>e</sub>" + "</html>";
            _massSlider = new SliderControl( 1.0, 1.0, 10.0, 5.0, 1, 1, massFormat, SLIDER_INSETS );
            _massSlider.setInverted( true );
            // Put a label at each tick mark.
            Hashtable labelTable = new Hashtable();
            labelTable.put( new Integer( 100 ), new JLabel( "<html>m<sub>e</sub></html>" ) );
            labelTable.put( new Integer( 50 ), new JLabel( "<html>5m<sub>e</sub></html>" ) );
            labelTable.put( new Integer( 10 ), new JLabel( "<html>10m<sub>e</sub></html>" ) );
            _massSlider.getSlider().setLabelTable( labelTable );
            
            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setInsets( new Insets( 0, 0, 0, 0 ) );
            layout.addComponent( _massSlider, 0, 0 );
            particleControlsPanel.setLayout( new BorderLayout() );
            particleControlsPanel.add( innerPanel, BorderLayout.WEST );
        }

        // Layout
        {
            addVerticalSpace( SUBPANEL_SPACING );
            addControlFullWidth( energyControlsPanel );
            addVerticalSpace( SUBPANEL_SPACING );
            addControlFullWidth( waveFunctionControlsPanel );
            addVerticalSpace( SUBPANEL_SPACING );
            addControlFullWidth( particleControlsPanel );
            addVerticalSpace( SUBPANEL_SPACING );
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
            _massSlider.addChangeListener( _listener );
        }
    }

    //----------------------------------------------------------------------------
    // Color key creation
    //----------------------------------------------------------------------------

    /*
     * Creates a color key by drawing a solid horizontal line and putting it in a JLabel.
     * 
     * @param color
     * @return JLabel
     */
    private JLabel createColorKey( Color color ) {
        BufferedImage image = new BufferedImage( COLOR_KEY_WIDTH, COLOR_KEY_HEIGHT, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = image.createGraphics();
        Rectangle2D r = new Rectangle2D.Double( 0, 0, COLOR_KEY_WIDTH, COLOR_KEY_HEIGHT );
        g2.setPaint( color );
        g2.fill( r );
        Icon icon = new ImageIcon( image );
        JLabel label = new JLabel( icon );
        return label;
    }

    /*
     * Creates a color key for phase by drawing the phase color series in a JLabel.
     * 
     * @return JLabel
     */
    private JLabel createPhaseKey() {
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
        JLabel label = new JLabel( icon );
        return label;
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    public void setWellType( WellType wellType ) {
        _wellTypeComboBox.setSelectedWellType( wellType );
        handleWellTypeSelection();
    }
    
    public WellType getWellType() {
        return _wellTypeComboBox.getSelectedWellType();
    }
    
    public void setNumberOfWells( int numberOfWells ) {
        _numberOfWellsSlider.setValue( numberOfWells );
        handleNumberOfWells();
    }
    
    public int getNumberOfWells() {
        return (int) _numberOfWellsSlider.getValue();
    }

    public void setDisplayType( int displayType ) {
        if ( displayType == DISPLAY_WAVE_FUNCTION ) {
            _waveFunctionRadioButton.setSelected( true );
        }
        else if ( displayType == DISPLAY_PROBABIITY_DENSITY ) {
            _probabilityDensityRadioButton.setSelected( true );
        }
        else {
            throw new IllegalArgumentException( "invalid display type: " + displayType );
        }
        handleDisplaySelection();
    }

    public int getDisplayType() {
        int displayType = DISPLAY_WAVE_FUNCTION;
        if ( _probabilityDensityRadioButton.isSelected() ) {
            displayType = DISPLAY_PROBABIITY_DENSITY;
        }
        return displayType;
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
        _massSlider.setValue( mass / BSConstants.ELECTRON_MASS );;
        handleMassSlider();
    }
    
    public double getParticleMass() {
        return _massSlider.getValue() * BSConstants.ELECTRON_MASS;
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
        return _massSlider;
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
            else if ( event.getSource() == _massSlider ) {
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
        WellType wellType = _wellTypeComboBox.getSelectedWellType();
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
            _module.setDisplayType( DISPLAY_WAVE_FUNCTION );
        }
        else if ( _probabilityDensityRadioButton.isSelected() ) {
            _module.setDisplayType( DISPLAY_PROBABIITY_DENSITY );
        }
    }

    private void handleMassSlider() {
        double mass = _massSlider.getValue() * BSConstants.ELECTRON_MASS;
        _module.setParticleMass( mass );
    }
    
}
