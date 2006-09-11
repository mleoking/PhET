/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.control;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * LightControlPanel
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class LightControlPanel extends PhetPNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double MARGIN = 30;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private LightTypeControl _lightTypeControl;
    private IntensityControl _intensityControl;
    private WavelengthControl _wavelengthControl;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public LightControlPanel( PSwingCanvas canvas ) {
        super();
        
        // Components
        _lightTypeControl = new LightTypeControl();
        _intensityControl = new IntensityControl();
        _wavelengthControl = new WavelengthControl( canvas,
                HAConstants.MIN_WAVELENGTH, HAConstants.MAX_WAVELENGTH,
                HAConstants.UV_COLOR, HAConstants.IR_COLOR );
        
        // Layout
        JPanel panel = new JPanel();
        panel.setBorder( HAConstants.CONTROL_PANEL_BORDER );

        // Wrappers for Swing components
        PSwing panelWrapper = new PSwing( canvas, panel );
        PSwing typeControlWrapper = new PSwing( canvas, _lightTypeControl );
        PSwing intensityControlWrapper = new PSwing( canvas, _intensityControl );
        
        // Layering
        addChild( panelWrapper );
        addChild( typeControlWrapper );
        addChild( intensityControlWrapper );
        addChild( _wavelengthControl );
        
        // Layout
        {
            double panelWidth = Math.max( Math.max( typeControlWrapper.getFullBounds().getWidth(), intensityControlWrapper.getFullBounds().getWidth() ), _wavelengthControl.getFullBounds().getWidth() ) + ( 2 * MARGIN );
            double panelHeight = typeControlWrapper.getFullBounds().getHeight() + intensityControlWrapper.getFullBounds().getHeight() + _wavelengthControl.getFullBounds().getHeight() + 15;
            panel.setPreferredSize( new Dimension( (int) panelWidth, (int) panelHeight ) );
            panelWrapper.setOffset( 0, 0 );
            
            typeControlWrapper.setOffset( MARGIN, 5 );
            intensityControlWrapper.setOffset( typeControlWrapper.getFullBounds().getX(), typeControlWrapper.getFullBounds().getY() + typeControlWrapper.getFullBounds().getHeight() + 5 );
            _wavelengthControl.setOffset( intensityControlWrapper.getFullBounds().getX() + 8, intensityControlWrapper.getFullBounds().getY() + intensityControlWrapper.getFullBounds().getHeight() + 22 );
        }
        
        // Colors
        panel.setBackground( HAConstants.GUN_CONTROLS_BACKGROUND );
        _lightTypeControl.setLabelsForeground( HAConstants.GUN_CONTROLS_FOREGROUND );
        _intensityControl.setUnitsForeground( HAConstants.GUN_CONTROLS_FOREGROUND );
        _wavelengthControl.setUnitsForeground( HAConstants.GUN_CONTROLS_FOREGROUND );
        
        // Light type control handler
        _lightTypeControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleLightTypeChange();
            }
        } );
        
        // Wavelength control handler
        _wavelengthControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleWavelengthChange();
            }
        } );
        
        // Default state
        _lightTypeControl.setMonochromaticSelected();
        _intensityControl.setValue( 100 );
        _wavelengthControl.setWavelength( VisibleColor.MIN_WAVELENGTH );
    }
    
    //----------------------------------------------------------------------------
    // Mutators
    //----------------------------------------------------------------------------
    
    public LightTypeControl getLightTypeControl() {
        return _lightTypeControl;
    }
    
    public IntensityControl getIntensityControl() {
        return _intensityControl;
    }
    
    public WavelengthControl getWavelengthControl() {
        return _wavelengthControl;
    }
    
    //----------------------------------------------------------------------------
    // private
    //----------------------------------------------------------------------------
    
    /*
     * Changes visibility and color of controls to match the type of light.
     */
    private void handleLightTypeChange() {
        if ( _lightTypeControl.isMonochromaticSelected() ) {
            _intensityControl.setColor( _wavelengthControl.getColor() );
            _wavelengthControl.setVisible( true );
        }
        else {
            _intensityControl.setColor( HAConstants.UV_COLOR );
            _wavelengthControl.setVisible( false );
        }
    }
    
    /*
     * Changes the intensity control's color to match the wavelength.
     */
    private void handleWavelengthChange() {
        _intensityControl.setColor( _wavelengthControl.getColor() );
    }
}
