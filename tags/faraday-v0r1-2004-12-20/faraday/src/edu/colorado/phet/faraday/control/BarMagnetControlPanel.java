/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.control;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.module.BarMagnetModule;

/**
 * BarMagnetControlPanel
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BarMagnetControlPanel extends ControlPanel {
    
    public static final int STRENGTH_MAX_PERCENTAGE = 60;
    public static final int STRENGTH_MIN_PERCENTAGE = 30;
    public static final int AREA_MAX_PERCENTAGE = 100;
    public static final int AREA_MIN_PERCENTAGE = 50;
    
    private BarMagnetModule _module;
    private JButton _flipPolarityButton;
    private JSlider _strengthSlider;
    private JCheckBox _fieldCheckBox;
    private JSpinner _loopsSpinner;
    private JSlider _areaSlider;
    private JRadioButton _meterRadioButton;
    private JRadioButton _bulbRadioButton;
    
    /**
     * Sole constructor.
     * 
     * @param module the module that this control panel is associated with.
     */
    public BarMagnetControlPanel( BarMagnetModule module ) {
        
        super( module );
        
        _module = module;
        
        JPanel panel = new JPanel();
        {
            Font defaultFont = panel.getFont();
            Font titleFont = new Font( defaultFont.getName(), defaultFont.getStyle(), defaultFont.getSize() + 4 );
            
            // WORKAROUND: Filler to set consistent panel width
            JPanel fillerPanel = new JPanel();
            fillerPanel.setLayout( new BoxLayout( fillerPanel, BoxLayout.X_AXIS ) );
            fillerPanel.add( Box.createHorizontalStrut( FaradayConfig.CONTROL_PANEL_MIN_WIDTH ) );
            
            // Bar Magnet panel
            JPanel barMagnetPanel = new JPanel();
            {
                // Titled border with a larger font.
                TitledBorder border = new TitledBorder( SimStrings.get( "barMagnetPanel.title" ) );
                border.setTitleFont( titleFont );
                barMagnetPanel.setBorder( border );

                // Flip Polarity button
                _flipPolarityButton = new JButton( SimStrings.get( "flipPolarityButton.label" ) );
                
                // Strength slider
                JPanel sliderPanel = new JPanel();
                {
                    // Label
                    JLabel label = new JLabel( SimStrings.get( "strengthSlider.label" ) );
                    
                    // Slider
                    _strengthSlider = new JSlider();
                    _strengthSlider.setMinimum( STRENGTH_MIN_PERCENTAGE );
                    _strengthSlider.setMaximum( STRENGTH_MAX_PERCENTAGE );
                    _strengthSlider.setValue( STRENGTH_MIN_PERCENTAGE );
                    
                    // Slider labels
                    Hashtable table = new Hashtable();
                    JLabel weakerLabel = new JLabel(SimStrings.get("strengthSlider.weaker"));
                    table.put( new Integer(STRENGTH_MIN_PERCENTAGE), weakerLabel );
                    JLabel strongerLabel = new JLabel(SimStrings.get("strengthSlider.stronger"));
                    table.put( new Integer(STRENGTH_MAX_PERCENTAGE), strongerLabel );
                    _strengthSlider.setLabelTable( table );
                    _strengthSlider.setPaintLabels( true );
                    _strengthSlider.setMajorTickSpacing( STRENGTH_MAX_PERCENTAGE - STRENGTH_MIN_PERCENTAGE );
                    _strengthSlider.setPaintTicks( true );
                    
                    // Layout
                    sliderPanel.setLayout( new BoxLayout( sliderPanel, BoxLayout.X_AXIS ) );
                    sliderPanel.add( label );
                    sliderPanel.add( _strengthSlider );
                }
                
                // B-Field on/off
                _fieldCheckBox = new JCheckBox( SimStrings.get( "fieldCheckBox.label" ) );
                
                // Layout
                barMagnetPanel.setLayout( new BoxLayout( barMagnetPanel, BoxLayout.Y_AXIS ) );
                barMagnetPanel.add( sliderPanel );
                barMagnetPanel.add( _flipPolarityButton );
                barMagnetPanel.add( _fieldCheckBox );
            }
            
            // Pickup Coil panel
            JPanel pickupCoilPanel = new JPanel();
            {
                // Titled border with a larger font.
                TitledBorder border = new TitledBorder( SimStrings.get( "pickupCoilPanel.title" ) );
                border.setTitleFont( titleFont );
                pickupCoilPanel.setBorder( border );
                
                // Number of loops
                JPanel loopsPanel = new JPanel();
                {
                   // Label 
                    JLabel label = new JLabel( SimStrings.get( "numberOfLoops.label" ) );
                    
                    // Spinner, keyboard editing disabled
                    SpinnerNumberModel spinnerModel = new SpinnerNumberModel();
                    spinnerModel.setMinimum( new Integer( FaradayConfig.MIN_PICKUP_LOOPS ) );
                    spinnerModel.setMaximum( new Integer( FaradayConfig.MAX_PICKUP_LOOPS ) );
                    spinnerModel.setValue( new Integer( FaradayConfig.MIN_PICKUP_LOOPS ) );
                    _loopsSpinner = new JSpinner( spinnerModel );
                    JFormattedTextField tf = ((JSpinner.DefaultEditor)_loopsSpinner.getEditor()).getTextField();
                    tf.setEditable( false );
                    
                    // Layout
                    loopsPanel.setLayout( new BoxLayout( loopsPanel, BoxLayout.X_AXIS ) );
                    loopsPanel.add( label );
                    loopsPanel.add( _loopsSpinner );
                }
                
                // Loop Area
                JPanel areaPanel = new JPanel();
                {
                    // Label
                    JLabel label = new JLabel( SimStrings.get( "areaSlider.label" ) );
                    
                    // Slider
                    _areaSlider = new JSlider();
                    _areaSlider.setMinimum( AREA_MIN_PERCENTAGE );
                    _areaSlider.setMaximum( AREA_MAX_PERCENTAGE );
                    _areaSlider.setValue( AREA_MIN_PERCENTAGE );
                    
                    // Slider labels
                    Hashtable table = new Hashtable();
                    JLabel smallerLabel = new JLabel(SimStrings.get("areaSlider.smaller"));
                    table.put( new Integer(AREA_MIN_PERCENTAGE), smallerLabel );
                    JLabel largerLabel = new JLabel(SimStrings.get("areaSlider.larger"));
                    table.put( new Integer(AREA_MAX_PERCENTAGE), largerLabel );
                    _areaSlider.setLabelTable( table );
                    _areaSlider.setPaintLabels( true );
                    _areaSlider.setMajorTickSpacing( AREA_MAX_PERCENTAGE - AREA_MIN_PERCENTAGE );
                    _areaSlider.setPaintTicks( true );
                    
                    // Layout
                    areaPanel.setLayout( new BoxLayout( areaPanel, BoxLayout.X_AXIS ) );
                    areaPanel.add( label );
                    areaPanel.add( _areaSlider );
                }
                
                // Type of "load"
                JPanel loadPanel = new JPanel();
                {
                    // Titled border with a larger font.
                    TitledBorder border2 = new TitledBorder( SimStrings.get( "loadPanel.title" ) );
                    border.setTitleFont( titleFont );
                    loadPanel.setBorder( border2 );
                    
                    // Radio buttons
                    _bulbRadioButton = new JRadioButton( SimStrings.get( "bulbRadioButton.label" ) );
                    _meterRadioButton = new JRadioButton( SimStrings.get( "meterRadioButton.label" ) );
                    ButtonGroup group = new ButtonGroup();
                    group.add( _bulbRadioButton );
                    group.add( _meterRadioButton );

                    // Layout
                    loadPanel.setLayout( new BoxLayout( loadPanel, BoxLayout.Y_AXIS ) );
                    loadPanel.add( _bulbRadioButton );
                    loadPanel.add( _meterRadioButton );
                }
                
                // Layout
                pickupCoilPanel.setLayout( new BoxLayout( pickupCoilPanel, BoxLayout.Y_AXIS ) );
                pickupCoilPanel.add( loopsPanel );
                pickupCoilPanel.add( areaPanel );
                pickupCoilPanel.add( loadPanel );
            }
            
            //  Layout so that control groups fill horizontal space.
            BorderLayout layout = new BorderLayout();
            layout.setVgap( 20 ); // vertical space between control groups
            panel.setLayout( layout );
            panel.add( fillerPanel, BorderLayout.NORTH );
            panel.add( barMagnetPanel, BorderLayout.CENTER );
            panel.add( pickupCoilPanel, BorderLayout.SOUTH );
            
            // Wire up event handling
            EventListener listener = new EventListener();
            _flipPolarityButton.addActionListener( listener );
            _strengthSlider.addChangeListener( listener );
            _fieldCheckBox.addActionListener( listener );
            _loopsSpinner.addChangeListener( listener );
            _areaSlider.addChangeListener( listener );
            _bulbRadioButton.addActionListener( listener );
            _meterRadioButton.addActionListener( listener );
        }
        super.setControlPane( panel );
    }
    
    public void setFieldLinesEnabled( boolean enabled ) {
        _fieldCheckBox.setSelected( enabled );
    }
    
    public void setBarMagnetStrengthScale( int value ) {
        _strengthSlider.setValue( value );
    }
    
    public void setNumberOfLoops( int value ) {
        _loopsSpinner.setValue( new Integer(value) );
    }
    
    public void setLoopAreaScale( int value ) {
        _areaSlider.setValue( value );
    }
    
    public void setBulbEnabled( boolean value ) {
        _bulbRadioButton.setSelected( value );
    }
    
    public void setMeterEnabled( boolean value ) {
        _meterRadioButton.setSelected( value );
    }
    
    private class EventListener implements ActionListener, ChangeListener {
        
        public EventListener() {}
        
        public void actionPerformed( ActionEvent e ) {
            if ( e.getSource() == _flipPolarityButton ) {
                _module.flipBarMagnetPolarity();
            }
            else if ( e.getSource() == _fieldCheckBox ) {
                _module.setFieldLinesEnabled( _fieldCheckBox.isSelected() );
            }
            else if ( e.getSource() == _bulbRadioButton ) {
                _module.enableBulb(); 
            }
            else if ( e.getSource() == _meterRadioButton ) {
                _module.enableMeter();
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + e );
            }
        }
        
        public void stateChanged( ChangeEvent e ) {
            if ( e.getSource() == _strengthSlider ) {
                _module.scaleBarMagnetStrength( _strengthSlider.getValue()/100.0 );
            }
            else if ( e.getSource() == _areaSlider ) {
                _module.scalePickupLoopArea( _areaSlider.getValue()/100.0 );
            }
            else if ( e.getSource() == _loopsSpinner ) {
                int value = ((Integer)_loopsSpinner.getValue()).intValue();
                _module.setNumberOfPickupLoops( value );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + e );
            }
        }
    }
    
}