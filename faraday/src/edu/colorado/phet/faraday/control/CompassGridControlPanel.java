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
import edu.colorado.phet.faraday.module.CompassGridModule;

/**
 * CompassGridControlPanel
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class CompassGridControlPanel extends ControlPanel {
  
    public static final int STRENGTH_MAX_PERCENTAGE = 60;
    public static final int STRENGTH_MIN_PERCENTAGE = 30;
    
    private static final int X_SPACING_MIN = 25;
    private static final int X_SPACING_MAX = 200;
    private static final int Y_SPACING_MIN = 25;
    private static final int Y_SPACING_MAX = 200;
    
    private CompassGridModule _module;
    private JButton _flipPolarityButton;
    private JSlider _strengthSlider;
    private JSlider _xSlider, _ySlider;
    private JCheckBox _moveCheckBox;
    
    /**
     * Sole constructor.
     * 
     * @param module the module that this control panel is associated with.
     */
    public CompassGridControlPanel( CompassGridModule module ) {
        
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
                
                // Layout
                barMagnetPanel.setLayout( new BoxLayout( barMagnetPanel, BoxLayout.Y_AXIS ) );
                barMagnetPanel.add( sliderPanel );
                barMagnetPanel.add( _flipPolarityButton );
            }
            
            // Grid panel
            JPanel gridPanel = new JPanel();
            {
                // Titled border with a larger font.
                TitledBorder border = new TitledBorder( "Compass Grid" );
                border.setTitleFont( titleFont );
                gridPanel.setBorder( border );
                
                // X axis density
                JPanel xPanel = new JPanel();
                {
                    JLabel label = new JLabel( "X Spacing:" );
                    
                    _xSlider = new JSlider();
                    _xSlider.setMinimum( X_SPACING_MIN );
                    _xSlider.setMaximum( X_SPACING_MAX );
                    _xSlider.setValue( X_SPACING_MIN );
                    _xSlider.setPaintLabels( true );
                    
                    // Layout
                    xPanel.setLayout( new BoxLayout( xPanel, BoxLayout.X_AXIS ) );
                    xPanel.add( label );
                    xPanel.add( _xSlider );
                }

                // Y axis density
                JPanel yPanel = new JPanel();
                {
                    JLabel label = new JLabel( "Y Spacing:" );
                    
                    _ySlider = new JSlider();
                    _ySlider.setMinimum( Y_SPACING_MIN );
                    _ySlider.setMaximum( Y_SPACING_MAX );
                    _ySlider.setValue( Y_SPACING_MIN );
                    _ySlider.setPaintLabels( true );
                    
                    // Layout
                    yPanel.setLayout( new BoxLayout( yPanel, BoxLayout.X_AXIS ) );
                    yPanel.add( label );
                    yPanel.add( _ySlider );
                }
                
                // Grid movement
                _moveCheckBox = new JCheckBox( "Move With Magnet" );
                
                // Layout
                gridPanel.setLayout( new BoxLayout( gridPanel, BoxLayout.Y_AXIS ) );
                gridPanel.add( xPanel );
                gridPanel.add( yPanel );
                gridPanel.add( _moveCheckBox );
            }
            
            // Layout so that control groups fill horizontal space.
            BorderLayout layout = new BorderLayout();
            layout.setVgap( 20 ); // vertical space between control groups
            panel.setLayout( layout );
            panel.add( fillerPanel, BorderLayout.NORTH );
            panel.add( barMagnetPanel, BorderLayout.CENTER );
            panel.add( gridPanel, BorderLayout.SOUTH );
            
            // Wire up event handling
            EventListener listener = new EventListener();
            _flipPolarityButton.addActionListener( listener );
            _strengthSlider.addChangeListener( listener );
            _xSlider.addChangeListener( listener );
            _ySlider.addChangeListener( listener );
            _moveCheckBox.addActionListener( listener );
        }
        super.setControlPane( panel );
    }
    
    public void setBarMagnetStrengthScale( int value ) {
        _strengthSlider.setValue( value );
    }
    
    public void setGridDensity( int x, int y ) {
        _xSlider.setValue( x );
        _ySlider.setValue( y );
    }
    
    public void setGridFollowsMagnent( boolean value ) {
        _moveCheckBox.setSelected( value );
    }
    
    private class EventListener implements ActionListener, ChangeListener {
        
        public EventListener() {}
        
        public void actionPerformed( ActionEvent e ) {
            if ( e.getSource() == _flipPolarityButton ) {
                _module.flipBarMagnetPolarity();
            }
            else if ( e.getSource() == _moveCheckBox ) {
                _module.setGridFollowsMagnet( _moveCheckBox.isSelected() );
            }
            else {
                 throw new IllegalArgumentException( "unexpected event: " + e );
            }
        }
        
        public void stateChanged( ChangeEvent e ) {
            if ( e.getSource() == _strengthSlider ) {
                _module.scaleBarMagnetStrength( _strengthSlider.getValue()/100.0 );
            }
            else if ( e.getSource() == _xSlider || e.getSource() == _ySlider ) {
                _module.setGridSpacing( _xSlider.getValue(), _ySlider.getValue() );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + e );
            }
        }
    }
    
}