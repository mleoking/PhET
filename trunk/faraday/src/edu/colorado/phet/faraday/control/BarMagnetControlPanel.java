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
 * BarMagnetControlPanel is the control panel for the "Bar Magnet" module.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BarMagnetControlPanel extends ControlPanel {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    public static final int STRENGTH_MAX = 1000;
    public static final int STRENGTH_MIN = 100;
    public static final int AREA_MAX_PERCENTAGE = 100;
    public static final int AREA_MIN_PERCENTAGE = 50;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private BarMagnetModule _module;
    private JButton _flipPolarityButton;
    private JSlider _strengthSlider;
    private JCheckBox _gridCheckBox;
    private JSpinner _loopsSpinner;
    private JSlider _areaSlider;
    private JRadioButton _meterRadioButton;
    private JRadioButton _bulbRadioButton;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * <p>
     * The structure of the code (the way that code blocks are nested)
     * reflects the structure of the panel.
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
            
            
            JPanel fillerPanel = new JPanel();
            {
                fillerPanel.setLayout( new BoxLayout( fillerPanel, BoxLayout.X_AXIS ) );
                // WORKAROUND: Filler to set consistent panel width
                fillerPanel.add( Box.createHorizontalStrut( FaradayConfig.CONTROL_PANEL_MIN_WIDTH ) );
            }
            
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
                    _strengthSlider.setMinimum( STRENGTH_MIN );
                    _strengthSlider.setMaximum( STRENGTH_MAX );
                    _strengthSlider.setValue( STRENGTH_MIN );
                    
                    // Slider labels
                    Hashtable table = new Hashtable();
                    JLabel weakerLabel = new JLabel(SimStrings.get("strengthSlider.weaker"));
                    table.put( new Integer(STRENGTH_MIN), weakerLabel );
                    JLabel strongerLabel = new JLabel(SimStrings.get("strengthSlider.stronger"));
                    table.put( new Integer(STRENGTH_MAX), strongerLabel );
                    _strengthSlider.setLabelTable( table );
                    _strengthSlider.setPaintLabels( true );
                    _strengthSlider.setMajorTickSpacing( STRENGTH_MAX - STRENGTH_MIN );
                    _strengthSlider.setPaintTicks( true );
                    
                    // Layout
                    sliderPanel.setLayout( new BoxLayout( sliderPanel, BoxLayout.X_AXIS ) );
                    sliderPanel.add( label );
                    sliderPanel.add( _strengthSlider );
                }
                
                // B-Field on/off
                _gridCheckBox = new JCheckBox( SimStrings.get( "gridCheckBox.label" ) );
                
                // Layout
                barMagnetPanel.setLayout( new BoxLayout( barMagnetPanel, BoxLayout.Y_AXIS ) );
                barMagnetPanel.add( sliderPanel );
                barMagnetPanel.add( _flipPolarityButton );
                barMagnetPanel.add( _gridCheckBox );
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
            _gridCheckBox.addActionListener( listener );
            _loopsSpinner.addChangeListener( listener );
            _areaSlider.addChangeListener( listener );
            _bulbRadioButton.addActionListener( listener );
            _meterRadioButton.addActionListener( listener );
        }
        super.setControlPane( panel );
    }
    
    //----------------------------------------------------------------------------
    // Controller methods
    //----------------------------------------------------------------------------

    /**
     * Enables/disables the compass grid.
     * 
     * @param enable true to enable, false to disable
     */
    public void setCompassGridEnabled( boolean enabled ) {
        _gridCheckBox.setSelected( enabled );
    }
    
    /**
     * Sets the magnet strength.
     * 
     * @param strength the strength
     */
    public void setMagnetStrength( double strength ) {
        _strengthSlider.setValue( (int)strength );
    }
    
    /**
     * Sets the number of loops in the pickup coil.
     * 
     * @param numberOfLoops the number of loops
     */
    public void setNumberOfLoops( int numberOfLoops ) {
        _loopsSpinner.setValue( new Integer(numberOfLoops) );
    }
    
    /**
     * XXX
     * @param scale
     */
    public void setLoopAreaScale( int scale ) {
        _areaSlider.setValue( scale );
    }
    
    /**
     * Enables/disabled the lightbulb.
     * 
     * @param enabled true to enable, false to disable
     */
    public void setBulbEnabled( boolean enabled ) {
        _bulbRadioButton.setSelected( enabled );
    }
    
    /**
     * Enables/disables the voltmeter.
     * 
     * @param enabled true to enable, false to disable
     */
    public void setMeterEnabled( boolean enabled ) {
        _meterRadioButton.setSelected( enabled );
    }
    
    //----------------------------------------------------------------------------
    // Event Handling
    //----------------------------------------------------------------------------

    /**
     * EventListener is a nested class that is private to this control panel.
     * It handles dispatching of all events generated by the controls.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    private class EventListener implements ActionListener, ChangeListener {
        
        /** Sole constructor */
        public EventListener() {}
        
        /**
         * ActionEvent handler.
         * 
         * @param e the event
         * @throws IllegalArgumentException if the event is unexpected
         */
        public void actionPerformed( ActionEvent e ) {
            if ( e.getSource() == _flipPolarityButton ) {
                // Magnet polarity
                _module.flipMagnetPolarity();
            }
            else if ( e.getSource() == _gridCheckBox ) {
                // Grid enable
                _module.setCompassGridEnabled( _gridCheckBox.isSelected() );
            }
            else if ( e.getSource() == _bulbRadioButton ) {
                // Lightbulb enable
                _module.setBulbEnabled( _bulbRadioButton.isSelected() ); 
            }
            else if ( e.getSource() == _meterRadioButton ) {
                // Voltmeter enable
                _module.setMeterEnabled( _meterRadioButton.isSelected() );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + e );
            }
        }
        
        /**
         * ChangeEvent handler.
         * 
         * @param e the event
         * @throws IllegalArgumentException if the event is unexpected
         */
        public void stateChanged( ChangeEvent e ) {
            if ( e.getSource() == _strengthSlider ) {
                // Magnet strength
                _module.setMagnetStrength( _strengthSlider.getValue() );
            }
            else if ( e.getSource() == _areaSlider ) {
                // Loop area
                _module.scalePickupLoopArea( _areaSlider.getValue()/100.0 );
            }
            else if ( e.getSource() == _loopsSpinner ) {
                // Number of loops
                int value = ((Integer)_loopsSpinner.getValue()).intValue();
                _module.setNumberOfPickupLoops( value );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + e );
            }
        }
    }
    
}