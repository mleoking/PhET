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
import edu.colorado.phet.faraday.model.AbstractCompass;
import edu.colorado.phet.faraday.model.AbstractMagnet;
import edu.colorado.phet.faraday.module.BarMagnetModule;
import edu.colorado.phet.faraday.view.BarMagnetGraphic;
import edu.colorado.phet.faraday.view.CompassGridGraphic;
import edu.colorado.phet.faraday.view.FieldMeterGraphic;


/**
 * DeveloperPanel is a panel that contain "developer only" controls for Faraday.
 * This panel is not internationalized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DeveloperPanel extends JPanel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Model & view components to be controlled.
    private AbstractMagnet _magnetModel;
    private CompassGridGraphic _gridGraphic;
    private ApparatusPanel _apparatusPanel;
    
    // Debugging components
    private JSlider _magnetWidthSlider, _magnetHeightSlider;
    private JSlider _gridSpacingSlider;
    private JSlider _needleWidthSlider, _needleHeightSlider;
    private JLabel _magnetWidthValue, _magnetHeightValue;
    private JLabel _gridSpacingValue;
    private JLabel _needleWidthValue, _needleHeightValue;
    private JButton _backgroundColorButton;
    
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
     * @param compassModel
     * @param magnetGraphic
     * @param gridGraphic
     * @param fieldMeterGraphic
     * @param apparatusPanel
     */
    public DeveloperPanel( AbstractMagnet magnetModel, CompassGridGraphic gridGraphic, ApparatusPanel apparatusPanel ) {
        
        super();
        
        assert ( magnetModel != null );
        assert ( gridGraphic != null );
        assert ( apparatusPanel != null );
        
        // Things we'll be controlling.
        _magnetModel = magnetModel;
        _gridGraphic = gridGraphic;
        _apparatusPanel = apparatusPanel;
        
        //  Titled border
        TitledBorder border = new TitledBorder( "Developer Controls" );
        setBorder( border );

        // Magnet width
        JPanel magnetWidthPanel = new JPanel();
        {
            // Label
            JLabel label = new JLabel( "Magnet width:" );

            // Slider
            _magnetWidthSlider = new JSlider();
            _magnetWidthSlider.setMaximum( FaradayConfig.BAR_MAGNET_SIZE_MAX.width );
            _magnetWidthSlider.setMinimum( FaradayConfig.BAR_MAGNET_SIZE_MIN.width );
            _magnetWidthSlider.setValue( FaradayConfig.BAR_MAGNET_SIZE_MIN.width );
            FaradayControlPanel.setSliderSize( _magnetWidthSlider, FaradayControlPanel.SLIDER_SIZE );

            // Value
            _magnetWidthValue = new JLabel( FaradayControlPanel.UNKNOWN_VALUE );

            // Layout
            magnetWidthPanel.setLayout( new BoxLayout( magnetWidthPanel, BoxLayout.X_AXIS ) );
            magnetWidthPanel.add( label );
            magnetWidthPanel.add( _magnetWidthSlider );
            magnetWidthPanel.add( _magnetWidthValue );
        }

        // Magnet height
        JPanel magnetHeightPanel = new JPanel();
        {
            // Label
            JLabel label = new JLabel( "Magnet height:" );

            // Slider
            _magnetHeightSlider = new JSlider();
            _magnetHeightSlider.setMaximum( FaradayConfig.BAR_MAGNET_SIZE_MAX.height );
            _magnetHeightSlider.setMinimum( FaradayConfig.BAR_MAGNET_SIZE_MIN.height );
            _magnetHeightSlider.setValue( FaradayConfig.BAR_MAGNET_SIZE_MIN.height );
            FaradayControlPanel.setSliderSize( _magnetHeightSlider, FaradayControlPanel.SLIDER_SIZE );

            // Value
            _magnetHeightValue = new JLabel( FaradayControlPanel.UNKNOWN_VALUE );

            // Layout
            magnetHeightPanel.setLayout( new BoxLayout( magnetHeightPanel, BoxLayout.X_AXIS ) );
            magnetHeightPanel.add( label );
            magnetHeightPanel.add( _magnetHeightSlider );
            magnetHeightPanel.add( _magnetHeightValue );
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
            FaradayControlPanel.setSliderSize( _gridSpacingSlider, FaradayControlPanel.SLIDER_SIZE );

            // Value
            _gridSpacingValue = new JLabel( FaradayControlPanel.UNKNOWN_VALUE );

            // Layout
            gridDensityPanel.setLayout( new BoxLayout( gridDensityPanel, BoxLayout.X_AXIS ) );
            gridDensityPanel.add( label );
            gridDensityPanel.add( _gridSpacingSlider );
            gridDensityPanel.add( _gridSpacingValue );
        }

        // Needle width
        JPanel needleWidthPanel = new JPanel();
        {
            // Label
            JLabel label = new JLabel( "Needle length:" );

            // Slider
            _needleWidthSlider = new JSlider();
            _needleWidthSlider.setMaximum( FaradayConfig.GRID_NEEDLE_SIZE_MAX.width );
            _needleWidthSlider.setMinimum( FaradayConfig.GRID_NEEDLE_SIZE_MIN.width );
            _needleWidthSlider.setValue( FaradayConfig.GRID_NEEDLE_SIZE_MIN.width );
            FaradayControlPanel.setSliderSize( _needleWidthSlider, FaradayControlPanel.SLIDER_SIZE );

            // Value
            _needleWidthValue = new JLabel( FaradayControlPanel.UNKNOWN_VALUE );

            // Layout
            needleWidthPanel.setLayout( new BoxLayout( needleWidthPanel, BoxLayout.X_AXIS ) );
            needleWidthPanel.add( label );
            needleWidthPanel.add( _needleWidthSlider );
            needleWidthPanel.add( _needleWidthValue, FaradayControlPanel.SLIDER_SIZE );
        }

        // Needle height
        JPanel needleHeightPanel = new JPanel();
        {
            // Label
            JLabel label = new JLabel( "Needle thickness:" );

            // Slider
            _needleHeightSlider = new JSlider();
            _needleHeightSlider.setMaximum( FaradayConfig.GRID_NEEDLE_SIZE_MAX.height );
            _needleHeightSlider.setMinimum( FaradayConfig.GRID_NEEDLE_SIZE_MIN.height );
            _needleHeightSlider.setValue( FaradayConfig.GRID_NEEDLE_SIZE_MIN.height );
            FaradayControlPanel.setSliderSize( _needleHeightSlider, FaradayControlPanel.SLIDER_SIZE );

            // Value
            _needleHeightValue = new JLabel( FaradayControlPanel.UNKNOWN_VALUE );

            // Layout
            needleHeightPanel.setLayout( new BoxLayout( needleHeightPanel, BoxLayout.X_AXIS ) );
            needleHeightPanel.add( label );
            needleHeightPanel.add( _needleHeightSlider );
            needleHeightPanel.add( _needleHeightValue );
        }

        // Background Color button
        _backgroundColorButton = new JButton( "Background Color..." );

        //  Layout
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        add( magnetWidthPanel );
        add( magnetHeightPanel );
        add( gridDensityPanel );
        add( needleWidthPanel );
        add( needleHeightPanel );
        add( _backgroundColorButton );
        
        // Wire up event handling.
        EventListener listener = new EventListener();
        _magnetWidthSlider.addChangeListener( listener );
        _magnetHeightSlider.addChangeListener( listener );
        _gridSpacingSlider.addChangeListener( listener );
        _needleWidthSlider.addChangeListener( listener );
        _needleHeightSlider.addChangeListener( listener );
        _backgroundColorButton.addActionListener( listener );
        
        // Update control panel to match the components that it's controlling.
        _magnetWidthSlider.setValue( (int) _magnetModel.getSize().width );
        _magnetHeightSlider.setValue( (int) _magnetModel.getSize().height );
        _gridSpacingSlider.setValue( _gridGraphic.getXSpacing() );
        _needleWidthSlider.setValue( _gridGraphic.getNeedleSize().width );
        _needleHeightSlider.setValue( _gridGraphic.getNeedleSize().height );
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
    private class EventListener implements ActionListener, ChangeListener, ColorDialog.Listener {

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
            if ( e.getSource() == _backgroundColorButton ) {
                ColorDialog.showDialog( "Background Color", _apparatusPanel, _apparatusPanel.getBackground(), this );
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
            if ( e.getSource() == _magnetWidthSlider ) {
                // Magnet width
                int width = _magnetWidthSlider.getValue();
                int height = _magnetModel.getSize().height;
                _magnetModel.setSize( new Dimension( width, height ) );
                _magnetWidthValue.setText( String.valueOf( width ) );
            }
            else if ( e.getSource() == _magnetHeightSlider ) {
                // Magnet height
                int width = _magnetModel.getSize().width;
                int height = _magnetHeightSlider.getValue();
                _magnetModel.setSize( new Dimension( width, height ) );
                _magnetHeightValue.setText( String.valueOf( height ) );
            }
            else if ( e.getSource() == _gridSpacingSlider ) {
                // Grid spacing
                int spacing = _gridSpacingSlider.getValue();
                _gridGraphic.setSpacing( spacing, spacing );
                _gridSpacingValue.setText( String.valueOf( spacing ) );
            }
            else if ( e.getSource() == _needleWidthSlider ) {
                // CompassGraphic Needle width
                int width = _needleWidthSlider.getValue();
                int height = _gridGraphic.getNeedleSize().height;
                _gridGraphic.setNeedleSize( new Dimension( width, height ) );
                _needleWidthValue.setText( String.valueOf( width ) );
            }
            else if ( e.getSource() == _needleHeightSlider ) {
                // CompassGraphic Needle height
                int width = _gridGraphic.getNeedleSize().width;
                int height = _needleHeightSlider.getValue();
                _gridGraphic.setNeedleSize( new Dimension( width, height ) );
                _needleHeightValue.setText( String.valueOf( height ) );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + e );
            }
        }
        
        //----------------------------------------------------------------------------
        // ColorDialog.Listener implementation
        //----------------------------------------------------------------------------
        
        /**
         * Handles selection in the color chooser.
         * 
         * @param color the selected color
         */
        public void colorChanged( Color color ) {
            _apparatusPanel.setBackground( color );
        }
        
        /**
         * Handles "OK" button in the color chooser.
         * 
         * @param color the selected color
         */
        public void ok( Color color ) {
            _apparatusPanel.setBackground( color );
        }
        
        /**
         * Handles "Canel" button in the color chooser.
         * Restore the original color.
         * 
         * @param color the color to restore
         */
        public void cancelled( Color color ) {
            _apparatusPanel.setBackground( color );
        }
    }
}
