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
import edu.colorado.phet.boundstates.module.BSModule;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSControlPanel is the sole control panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSControlPanel extends BSAbstractControlPanel {

    //----------------------------------------------------------------------------
    // Class data (public)
    //----------------------------------------------------------------------------
    
    // Display types
    public static final int DISPLAY_WAVE_FUNCTION = 0;
    public static final int DISPLAY_PROBABIITY_DENSITY = 1;
    public static final int DISPLAY_CLASSICAL_PARTICLE = 2;
    
    //----------------------------------------------------------------------------
    // Class data (private)
    //----------------------------------------------------------------------------
    
    private static final String EXPAND_SYMBOL = ">>";
    private static final String COLLAPSE_SYMBOL = "<<";
    
    private static final int INDENTATION = 0; // pixels
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
    
    private BSModule _module;
    private JComboBox _potentialComboBox;
    private JRadioButton _waveFunctionRadioButton, _probabilityDensityRadioButton, _classicalParticleRadioButton;
    private JCheckBox _realCheckBox, _imaginaryCheckBox, _magnitudeCheckBox, _phaseCheckBox;
    private JButton _advancedButton;
    private String _sAdvancedExpand, _sAdvancedCollapse;
    private JPanel _advancedPanel;
    private SliderControl _numberSlider, _widthSlider, _depthSlider, _spacingSlider;
    private NumberSpinnerControl _c1Spinner, _c2Spinner, _c3Spinner, _c4Spinner;
    private JButton _normalizeButton;
    
    // Developer only controls...
    private JButton _developerButton;
    private String _sDeveloperExpand, _sDeveloperCollapse;
    private JPanel _developerPanel;
    private JCheckBox _showEigenstateCheckboxes;
    private SliderControl _eigenstateCheckBoxesScaleSlider;
    private SliderControl _eigenstateWidthSlider;
    
    private EventListener _listener;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param module
     */
    public BSControlPanel( BSModule module ) {
        super( module );
        
        _module = module;
        
        // Set the control panel's minimum width.
        String widthString = SimStrings.get( "width.controlPanel" );
        if ( widthString != null ) {
            int width = Integer.parseInt( widthString );
            setMinumumWidth( width );
        }
        
        // Potential Well
        JPanel wellPanel = new JPanel();
        {
            // Potential label
            JLabel label = new JLabel( SimStrings.get( "label.well" ) );

            // Potential combo box 
            _potentialComboBox = new JComboBox();
            _potentialComboBox.addItem( SimStrings.get( "choice.well.hydrogenAtom") );
            _potentialComboBox.addItem( SimStrings.get( "choice.well.harmonicOscillator") );
            _potentialComboBox.addItem( SimStrings.get( "choice.well.infiniteSquare") );
            _potentialComboBox.addItem( SimStrings.get( "choice.well.finiteSquare") );
            _potentialComboBox.addItem( SimStrings.get( "choice.well.asymmetric") );
            
            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.addComponent( label, 0, 0 );
            layout.addComponent( _potentialComboBox, 1, 0 );
            wellPanel.setLayout( new BorderLayout() );
            wellPanel.add( innerPanel, BorderLayout.WEST );
        }
        
        // Display 
        JPanel displayPanel = new JPanel();
        {
            // Display label
            JLabel label = new JLabel( SimStrings.get( "label.display" ) );
            
            // Radio buttons
            _waveFunctionRadioButton = new JRadioButton( SimStrings.get( "choice.display.waveFunction" ) );
            _probabilityDensityRadioButton = new JRadioButton( SimStrings.get( "choice.display.probabilityDensity" ) );
            _classicalParticleRadioButton = new JRadioButton( SimStrings.get( "choice.display.classicalParticle" ) );
            
            // Button group
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( _waveFunctionRadioButton );
            buttonGroup.add( _probabilityDensityRadioButton );
            buttonGroup.add( _classicalParticleRadioButton );
            
            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setMinimumWidth( 0, INDENTATION );
            layout.addComponent( label, 0, 1 );
            layout.addComponent( _waveFunctionRadioButton, 1, 1 );
            layout.addComponent( _probabilityDensityRadioButton, 2, 1 );
            layout.addComponent( _classicalParticleRadioButton, 3, 1 );
            displayPanel.setLayout( new BorderLayout() );
            displayPanel.add( innerPanel, BorderLayout.WEST );
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
            layout.setMinimumWidth( 0, INDENTATION );
            layout.addComponent( label, 0, 1 );
            layout.addComponent( realPanel, 1, 1 );
            layout.addComponent( imaginaryPanel, 2, 1 );
            layout.addComponent( magnitudePanel, 3, 1 );
            layout.addComponent( phasePanel, 4, 1 );
            viewPanel.setLayout( new BorderLayout() );
            viewPanel.add( innerPanel, BorderLayout.WEST );
        }
        
        // Advanced button
        {
            _sAdvancedExpand = SimStrings.get( "button.advanced" ) + " " + EXPAND_SYMBOL;
            _sAdvancedCollapse = SimStrings.get( "button.advanced" ) + " " + COLLAPSE_SYMBOL;
            _advancedButton = new JButton( _sAdvancedExpand  );
        }
        
        // Advanced panel
        _advancedPanel = new JPanel();
        {
            String sNumber = SimStrings.get( "label.numberOfWells" ) + " {0}";
            _numberSlider = new SliderControl( 1, 1, 10, 1, 0, 0, sNumber, SLIDER_INSETS );
            _numberSlider.getSlider().setSnapToTicks( true );
            
            String sWidth = SimStrings.get( "label.wellWidth" ) + " {0} " + SimStrings.get( "units.position" );
            _widthSlider = new SliderControl( 2, .1, 10, 5, 1, 1, sWidth, SLIDER_INSETS );
            _widthSlider.setInverted( true );

            String sDepth = SimStrings.get( "label.wellDepth" ) + " {0} " + SimStrings.get( "units.energy" );
            _depthSlider = new SliderControl( 2, .1, 10, 5, 1, 1, sDepth, SLIDER_INSETS );
            _depthSlider.setInverted( true );
            
            String sSpacing = SimStrings.get( "label.wellSpacing" ) + " {0} " + SimStrings.get( "units.position" );
            _spacingSlider = new SliderControl( 2, .1, 10, 5, 1, 1, sSpacing, SLIDER_INSETS );
            _spacingSlider.setInverted( true );
            
            JPanel superpositionPanel = new JPanel();
            {
                superpositionPanel.setBorder( new TitledBorder( SimStrings.get( "title.superpositionState" ) ) );

                Dimension spinnerSize = new Dimension( 55, 25 );
                _c1Spinner = new NumberSpinnerControl( SimStrings.get( "label.c1" ), 0.25, 0, 1, 0.01, "0.00", spinnerSize );
                _c2Spinner = new NumberSpinnerControl( SimStrings.get( "label.c2" ), 0.25, 0, 1, 0.01, "0.00", spinnerSize );
                _c3Spinner = new NumberSpinnerControl( SimStrings.get( "label.c3" ), 0.25, 0, 1, 0.01, "0.00", spinnerSize );
                _c4Spinner = new NumberSpinnerControl( SimStrings.get( "label.c4" ), 0.25, 0, 1, 0.01, "0.00", spinnerSize );
                
                _normalizeButton = new JButton( SimStrings.get( "button.normalize" ) );
                
                EasyGridBagLayout layout = new EasyGridBagLayout( superpositionPanel );
                superpositionPanel.setLayout( layout );
                layout.setAnchor( GridBagConstraints.WEST );
                layout.addComponent( _c1Spinner, 0, 0 );
                layout.addComponent( _c2Spinner, 1, 0 );
                layout.addComponent( _c3Spinner, 0, 1 );
                layout.addComponent( _c4Spinner, 1, 1 );
                layout.addComponent( _normalizeButton, 2, 0 );
            }
            
            EasyGridBagLayout layout = new EasyGridBagLayout( _advancedPanel );
            _advancedPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            int row = 0;
            layout.addComponent( _numberSlider, row++, 0 );
            layout.addComponent( _widthSlider, row++, 0 );
            layout.addComponent( _depthSlider, row++, 0 );
            layout.addComponent( _spacingSlider, row++, 0 );
            layout.addComponent( superpositionPanel, row++, 0 );
        }
        
        // Developer button
        {
            _sDeveloperExpand = "Developer" + " " + EXPAND_SYMBOL;
            _sDeveloperCollapse = "Developer" + " " + COLLAPSE_SYMBOL;
            _developerButton = new JButton( _sDeveloperExpand  );
        }
        
        // Developer controls
        _developerPanel = new JPanel();
        _developerPanel.setVisible( false );
        {
            _showEigenstateCheckboxes = new JCheckBox( "Show eigenstate checkboxes" );
                  
            _eigenstateCheckBoxesScaleSlider = new SliderControl( 1, 0.5, 1.5, 0.5, 1, 1, "Scale eigenstate check boxes: {0}:1", SLIDER_INSETS );
            
            _eigenstateWidthSlider = new SliderControl( 2, 1, 4, 1, 1, 1, "Eigenstate line width: {0} pixels", SLIDER_INSETS );
            
            EasyGridBagLayout layout = new EasyGridBagLayout( _developerPanel );
            _developerPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            int row = 0;
            layout.addComponent( _showEigenstateCheckboxes, row++, 0 );
            layout.addComponent( _eigenstateCheckBoxesScaleSlider, row++, 0 );
            layout.addComponent( _eigenstateWidthSlider, row++, 0 );
        }
        
        // Layout
        {  
            addControlFullWidth( wellPanel );
            addVerticalSpace( SUBPANEL_SPACING );
            addSeparator();
            addControlFullWidth( displayPanel );
            addVerticalSpace( SUBPANEL_SPACING );
            addSeparator();
            addControlFullWidth( viewPanel );
            addVerticalSpace( SUBPANEL_SPACING );
            addSeparator();
            addControl( _advancedButton );
            addControlFullWidth( _advancedPanel );
            addVerticalSpace( SUBPANEL_SPACING );
            addSeparator();
            addControl( _developerButton );
            addControlFullWidth( _developerPanel );
            addVerticalSpace( SUBPANEL_SPACING );
            addSeparator();
            addResetButton();
        }
        
        // Interactivity
        {
            _listener = new EventListener();
            _potentialComboBox.addItemListener( _listener );
            _waveFunctionRadioButton.addActionListener( _listener );
            _probabilityDensityRadioButton.addActionListener( _listener );
            _classicalParticleRadioButton.addActionListener( _listener );
            _realCheckBox.addActionListener( _listener );
            _imaginaryCheckBox.addActionListener( _listener );
            _magnitudeCheckBox.addActionListener( _listener );
            _phaseCheckBox.addActionListener( _listener );
            _advancedButton.addActionListener( _listener );
            _developerButton.addActionListener( _listener );
            _showEigenstateCheckboxes.addActionListener( _listener );
            _eigenstateCheckBoxesScaleSlider.addChangeListener( _listener );
            _eigenstateWidthSlider.addChangeListener( _listener );
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
    
    public void setDisplayType( int displayType ) {
        if ( displayType == DISPLAY_WAVE_FUNCTION ) {
            _waveFunctionRadioButton.setSelected( true );
        }
        else if ( displayType == DISPLAY_PROBABIITY_DENSITY ) {
            _probabilityDensityRadioButton.setSelected( true );
        }
        else if ( displayType == DISPLAY_CLASSICAL_PARTICLE ) {
            _classicalParticleRadioButton.setSelected( true );
        }
        else {
            throw new IllegalArgumentException( "invalid display type: " + displayType );
        }
        handleDisplaySelection();
    }
    
    public int getDisplayType() {
        int displayType = DISPLAY_CLASSICAL_PARTICLE;
        if ( _waveFunctionRadioButton.isSelected() ) {
            displayType = DISPLAY_WAVE_FUNCTION;
        }
        else if ( _probabilityDensityRadioButton.isSelected() ) {
            displayType = DISPLAY_PROBABIITY_DENSITY;
        }
        return displayType;
    }
    
    public void setAdvancedVisible( boolean visible ) {
        _advancedPanel.setVisible( !visible );
        handleAdvancedButton();
    }
    
    public boolean isAdvancedVisible() {
        return _advancedPanel.isVisible();
    }
    
    /* For help item supper */
    public JComponent getPotentialControl() {
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
            else if ( event.getSource() == _waveFunctionRadioButton ) {
                handleDisplaySelection();
            }
            else if ( event.getSource() == _probabilityDensityRadioButton ) {
                handleDisplaySelection();
            }
            else if ( event.getSource() == _classicalParticleRadioButton ) {
                handleDisplaySelection();
            }
            else if ( event.getSource() == _advancedButton ) {
                handleAdvancedButton();
            }
            else if ( event.getSource() == _developerButton ) {
                handleDeveloperButton();
            }
            else if ( event.getSource() == _showEigenstateCheckboxes ) {
                handleShowEigenstateCheckBoxes();
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }
        
        public void stateChanged( ChangeEvent event ) {
            if ( event.getSource() == _eigenstateCheckBoxesScaleSlider ) {
                handleEigenstateCheckBoxesScale();
            }
            else if ( event.getSource() == _eigenstateWidthSlider ) {
                handleEigenstateWidth();
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
        //XXX
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
            setWaveFunctionViewOptionsEnabled( true );
            _module.setDisplayType( DISPLAY_WAVE_FUNCTION );
        }
        else if ( _probabilityDensityRadioButton.isSelected() ) {
            setWaveFunctionViewOptionsEnabled( true );
            _module.setDisplayType( DISPLAY_PROBABIITY_DENSITY );
        }
        else if ( _classicalParticleRadioButton.isSelected() ) {
            setWaveFunctionViewOptionsEnabled( false );
            _module.setDisplayType( DISPLAY_CLASSICAL_PARTICLE );
        }
    }
    
    private void setWaveFunctionViewOptionsEnabled( boolean enabled ) {
        _realCheckBox.setEnabled( enabled );
        _imaginaryCheckBox.setEnabled( enabled );
        _magnitudeCheckBox.setEnabled( enabled );
        _phaseCheckBox.setEnabled( enabled );
    }
    
    private void handleAdvancedButton() {
        if ( _advancedPanel.isVisible() ) {
            _advancedPanel.setVisible( false );
            _advancedButton.setText( _sAdvancedExpand );
        }
        else {
            _advancedPanel.setVisible( true );
            _advancedButton.setText( _sAdvancedCollapse );
        }
    }
    
    private void handleDeveloperButton() {
        if ( _developerPanel.isVisible() ) {
            _developerPanel.setVisible( false );
            _developerButton.setText( _sDeveloperExpand );
        }
        else {
            _developerPanel.setVisible( true );
            _developerButton.setText( _sDeveloperCollapse );
        }
    }
    
    private void handleShowEigenstateCheckBoxes() {
        _module.showEigenstateCheckBoxes( _showEigenstateCheckboxes.isSelected() );
    }
    
    private void handleEigenstateWidth() {
        double width = _eigenstateWidthSlider.getValue();
        _module.setEigenstateLineWidth( width );
    }
    
    private void handleEigenstateCheckBoxesScale() {
        double scale = _eigenstateCheckBoxesScaleSlider.getValue();
        _module.scaleEigenstateCheckBoxes( scale );
    }
}
