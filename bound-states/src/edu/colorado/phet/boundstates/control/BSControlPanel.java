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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.module.BSModule;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * QTControlPanel is the sole control panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSControlPanel extends BSAbstractControlPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String EXPAND_SYMBOL = ">>";
    private static final String COLLAPSE_SYMBOL = "<<";
    
    private static final int INDENTATION = 0; // pixels
    private static final int SUBPANEL_SPACING = 5; // pixels
    private static final double WIDTH_TICK_SPACING = 1.0; // nm
    private static final double CENTER_TICK_SPACING = 4.0; // nm
    
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
    private JLabel _c1, _c2, _c3, _c4;
    
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
        
        // Potential
        JPanel energyPanel = new JPanel();
        {
            // Potential label
            JLabel label = new JLabel( SimStrings.get( "label.well" ) );

            // Potential combo box 
            _potentialComboBox = new JComboBox();
            
            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setMinimumWidth( 0, INDENTATION );
            layout.addComponent( label, 0, 1 );
            layout.addComponent( _potentialComboBox, 1, 1 );
            energyPanel.setLayout( new BorderLayout() );
            energyPanel.add( innerPanel, BorderLayout.WEST );
        }
        
        // Display 
        JPanel displayPanel = new JPanel();
        {
            // Display label
            JLabel label = new JLabel( SimStrings.get( "label.display" ) );
            
            //
            _waveFunctionRadioButton = new JRadioButton( SimStrings.get( "choice.display.waveFunction" ) );
            _probabilityDensityRadioButton = new JRadioButton( SimStrings.get( "choice.display.probabilityDensity" ) );
            _classicalParticleRadioButton = new JRadioButton( SimStrings.get( "choice.display.classicalParticle" ) );
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
            realPanel.add( createColorKey( BSConstants.INCIDENT_REAL_WAVE_COLOR ) );
            
            // Imaginary
            JPanel imaginaryPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
            imaginaryPanel.add( _imaginaryCheckBox );
            imaginaryPanel.add( Box.createHorizontalStrut( COLOR_KEY_SPACING ) );
            imaginaryPanel.add( createColorKey( BSConstants.INCIDENT_IMAGINARY_WAVE_COLOR ) );
            
            // Magnitude
            JPanel magnitudePanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
            magnitudePanel.add( _magnitudeCheckBox );
            magnitudePanel.add( Box.createHorizontalStrut( COLOR_KEY_SPACING ) );
            magnitudePanel.add( createColorKey( BSConstants.INCIDENT_MAGNITUDE_WAVE_COLOR ) );   
            
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
        
        // Layout
        {  
            addControlFullWidth( energyPanel );
            addVerticalSpace( SUBPANEL_SPACING );
            addSeparator();
            addControlFullWidth( viewPanel );
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
        }
    }

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
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    /*
     * EventListener dispatches events for all controls in this control panel.
     */
    private class EventListener implements ActionListener, ChangeListener, ItemListener {
        
        private boolean _clockIsRunning = false;
        private boolean _sliderIsAdjusting = false;
        
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
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }
        
        public void stateChanged( ChangeEvent event ) {
            //XXX
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
        //XXX
    }
}
