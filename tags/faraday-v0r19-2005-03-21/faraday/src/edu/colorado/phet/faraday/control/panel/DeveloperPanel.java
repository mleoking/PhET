/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.control.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.control.dialog.ColorChooserFactory;
import edu.colorado.phet.faraday.model.AbstractMagnet;
import edu.colorado.phet.faraday.view.CoilGraphic;
import edu.colorado.phet.faraday.view.CompassGridGraphic;


/**
 * DeveloperPanel is a panel that contain "developer only" controls for Faraday.
 * This panel is not internationalized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DeveloperPanel extends FaradayPanel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Model & view components to be controlled.
    private AbstractMagnet _magnetModel;
    private CompassGridGraphic _gridGraphic;
    private CoilGraphic _coilGraphic;
    private ApparatusPanel _apparatusPanel;
    
    // Debugging components
    private JSlider _magnetSizeSlider;
    private JSlider _gridSpacingSlider;
    private JSlider _needleSizeSlider;
    private JLabel _magnetSize;
    private JLabel _gridSpacingValue;
    private JLabel _needleSizeValue;
    private JButton _coilForeColorButton, _coilMiddleColorButton, _coilBackColorButton;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor
     * <p>
     * The structure of the code (the way that code blocks are nested)
     * reflects the structure of the panel.
     * 
     * @param magnetModel
     * @param gridGraphic
     * @param coilGraphic
     * @param apparatusPanel
     */
    public DeveloperPanel( AbstractMagnet magnetModel, CompassGridGraphic gridGraphic, CoilGraphic coilGraphic, ApparatusPanel apparatusPanel ) {
        
        super();
        
        assert ( magnetModel != null );
        assert ( gridGraphic != null );
        assert ( apparatusPanel != null );
        // OK if coilGraphic is null
        
        // Things we'll be controlling.
        _magnetModel = magnetModel;
        _gridGraphic = gridGraphic;
        _coilGraphic = coilGraphic;
        _apparatusPanel = apparatusPanel;
        
        //  Titled border
        TitledBorder border = new TitledBorder( "Developer Controls" );
        setBorder( border );

        // Magnet size
        JPanel magnetSizePanel = new JPanel();
        {
            // Label
            JLabel label = new JLabel( "Magnet size:" );

            // Slider
            _magnetSizeSlider = new JSlider();
            _magnetSizeSlider.setMaximum( FaradayConfig.BAR_MAGNET_WIDTH_MAX );
            _magnetSizeSlider.setMinimum( FaradayConfig.BAR_MAGNET_WIDTH_MIN );
            _magnetSizeSlider.setValue( FaradayConfig.BAR_MAGNET_WIDTH_MIN );
            setSliderSize( _magnetSizeSlider, SLIDER_SIZE );

            // Value
            _magnetSize = new JLabel( UNKNOWN_VALUE );

            // Layout
            magnetSizePanel.setLayout( new BoxLayout( magnetSizePanel, BoxLayout.X_AXIS ) );
            magnetSizePanel.add( label );
            magnetSizePanel.add( _magnetSizeSlider );
            magnetSizePanel.add( _magnetSize );
        }

        // Needle size
        JPanel needleSizePanel = new JPanel();
        {
            // Label
            JLabel label = new JLabel( "Needle size:" );

            // Slider
            _needleSizeSlider = new JSlider();
            _needleSizeSlider.setMaximum( FaradayConfig.GRID_NEEDLE_WIDTH_MAX );
            _needleSizeSlider.setMinimum( FaradayConfig.GRID_NEEDLE_WIDTH_MIN );
            _needleSizeSlider.setValue( FaradayConfig.GRID_NEEDLE_WIDTH_MIN );
            setSliderSize( _needleSizeSlider, SLIDER_SIZE );

            // Value
            _needleSizeValue = new JLabel( UNKNOWN_VALUE );

            // Layout
            needleSizePanel.setLayout( new BoxLayout( needleSizePanel, BoxLayout.X_AXIS ) );
            needleSizePanel.add( label );
            needleSizePanel.add( _needleSizeSlider );
            needleSizePanel.add( _needleSizeValue, SLIDER_SIZE );
        }

        // Grid density
        JPanel gridDensityPanel = new JPanel();
        {
            // Label
            JLabel label = new JLabel( "Grid spacing:" );

            // Slider
            _gridSpacingSlider = new JSlider();
            _gridSpacingSlider.setMinimum( FaradayConfig.GRID_SPACING_MIN );
            _gridSpacingSlider.setMaximum( FaradayConfig.GRID_SPACING_MAX );
            setSliderSize( _gridSpacingSlider, SLIDER_SIZE );

            // Value
            _gridSpacingValue = new JLabel( UNKNOWN_VALUE );

            // Layout
            gridDensityPanel.setLayout( new BoxLayout( gridDensityPanel, BoxLayout.X_AXIS ) );
            gridDensityPanel.add( label );
            gridDensityPanel.add( _gridSpacingSlider );
            gridDensityPanel.add( _gridSpacingValue );
        }

        // Colors panel
        JPanel colorsPanel = new JPanel();
        {
            colorsPanel.setLayout( new BoxLayout( colorsPanel, BoxLayout.Y_AXIS ) );
            
            if ( coilGraphic != null ) {
                _coilForeColorButton = new JButton( "Coil Foreground Color..." );
                _coilMiddleColorButton = new JButton( "Coil Middleground Color..." );
                _coilBackColorButton = new JButton( "Coil Background Color..." );
                colorsPanel.add( _coilForeColorButton );
                colorsPanel.add( _coilMiddleColorButton );
                colorsPanel.add( _coilBackColorButton );
            }
        }

        //  Layout
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        add( magnetSizePanel );
        add( needleSizePanel );
        add( gridDensityPanel );
        add( colorsPanel );
        
        // Wire up event handling.
        EventListener listener = new EventListener();
        _magnetSizeSlider.addChangeListener( listener );
        _gridSpacingSlider.addChangeListener( listener );
        _needleSizeSlider.addChangeListener( listener );
        if ( coilGraphic != null ) {
            _coilForeColorButton.addActionListener( listener );
            _coilMiddleColorButton.addActionListener( listener );
            _coilBackColorButton.addActionListener( listener );
        }
        
        // Update control panel to match the components that it's controlling.
        _magnetSizeSlider.setValue( (int) _magnetModel.getSize().width );
        _gridSpacingSlider.setValue( _gridGraphic.getXSpacing() );
        _needleSizeSlider.setValue( _gridGraphic.getNeedleSize().width );
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

        //----------------------------------------------------------------------------
        // ActionListener implementation
        //----------------------------------------------------------------------------

        /**
         * ActionEvent handler.
         * 
         * @param e the event
         * @throws IllegalArgumentException if the event is unexpected
         */
        public void actionPerformed( ActionEvent e ) {
            if ( e.getSource() == _coilForeColorButton ) {
                ColorChooserFactory.Listener listener = new ColorChooserFactory.Listener() {
                    public void colorChanged( Color color ) {
                        _coilGraphic.setColors( color, null, null );
                    }
                    public void ok( Color color ) {
                        _coilGraphic.setColors( color, null, null );
                    }
                    public void cancelled( Color color ) {
                        _coilGraphic.setColors( color, null, null );
                    }  
                };
                ColorChooserFactory.showDialog( "Coil Foreground Color", _apparatusPanel, _coilGraphic.getForegroundColor(), listener );
            }
            else if ( e.getSource() == _coilMiddleColorButton ) {
                ColorChooserFactory.Listener listener = new ColorChooserFactory.Listener() {
                    public void colorChanged( Color color ) {
                        _coilGraphic.setColors( null, color, null );
                    }
                    public void ok( Color color ) {
                        _coilGraphic.setColors( null, color, null );
                    }
                    public void cancelled( Color color ) {
                        _coilGraphic.setColors( null, color, null );
                    }  
                };
                ColorChooserFactory.showDialog( "Coil Middleground Color", _apparatusPanel, _coilGraphic.getMiddlegroundColor(), listener );
            }
            else if ( e.getSource() == _coilBackColorButton ) {
                ColorChooserFactory.Listener listener = new ColorChooserFactory.Listener() {
                    public void colorChanged( Color color ) {
                        _coilGraphic.setColors( null, null, color );
                    }
                    public void ok( Color color ) {
                        _coilGraphic.setColors( null, null, color );
                    }
                    public void cancelled( Color color ) {
                        _coilGraphic.setColors( null, null, color );
                    }  
                };
                ColorChooserFactory.showDialog( "Coil Background Color", _apparatusPanel, _coilGraphic.getBackgroundColor(), listener );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + e );
            }
        }

        //----------------------------------------------------------------------------
        // ChangeListener implementation
        //----------------------------------------------------------------------------

        /**
         * ChangeEvent handler.
         * 
         * @param e the event
         * @throws IllegalArgumentException if the event is unexpected
         */
        public void stateChanged( ChangeEvent e ) {
            if ( e.getSource() == _magnetSizeSlider ) {
                // Magnet width
                int width = _magnetSizeSlider.getValue();
                int height = (int) ( width / FaradayConfig.BAR_MAGNET_ASPECT_RATIO );
                _magnetModel.setSize( new Dimension( width, height ) );
                _magnetSize.setText( String.valueOf( width ) + "x" + String.valueOf( height ) );
            }
            else if ( e.getSource() == _gridSpacingSlider ) {
                // Grid spacing
                int spacing = _gridSpacingSlider.getValue();
                _gridGraphic.setSpacing( spacing, spacing );
                _gridSpacingValue.setText( String.valueOf( spacing ) );
            }
            else if ( e.getSource() == _needleSizeSlider ) {
                // CompassGraphic Needle width
                int width = _needleSizeSlider.getValue();
                int height = (int) ( width / FaradayConfig.GRID_NEEDLE_ASPECT_RATIO );
                _gridGraphic.setNeedleSize( new Dimension( width, height ) );
                _needleSizeValue.setText( String.valueOf( width ) + "x" + String.valueOf( height ) );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + e );
            }
        }
    }
}
