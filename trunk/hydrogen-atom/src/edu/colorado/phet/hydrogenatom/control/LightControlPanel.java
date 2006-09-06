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

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;


public class LightControlPanel extends PNode {

    private static final double MARGIN = 30;
    
    private LightTypeControl _typeControl;
    private IntensityControl _intensityControl;
    private WavelengthControl _wavelengthControl;
    
    public LightControlPanel( PSwingCanvas canvas ) {
        super();
        
        // Components
        _typeControl = new LightTypeControl();
        _intensityControl = new IntensityControl();
        _wavelengthControl = new WavelengthControl( canvas,
                HAConstants.MIN_WAVELENGTH, HAConstants.MAX_WAVELENGTH,
                HAConstants.UV_COLOR, HAConstants.IR_COLOR );
        
        // Layout
        JPanel panel = new JPanel();
        panel.setBorder( HAConstants.CONTROL_PANEL_BORDER );

        // Wrappers for Swing components
        PSwing panelWrapper = new PSwing( canvas, panel );
        PSwing typeControlWrapper = new PSwing( canvas, _typeControl );
        PSwing intensityControlWrapper = new PSwing( canvas, _intensityControl );
        
        // Layering
        addChild( panelWrapper );
        addChild( typeControlWrapper );
        addChild( intensityControlWrapper );
        addChild( _wavelengthControl );
        
        // Layout
        {
            double panelWidth = Math.max( Math.max( typeControlWrapper.getFullBounds().getWidth(), intensityControlWrapper.getFullBounds().getWidth() ), _wavelengthControl.getFullBounds().getWidth() + 17 ) + ( 2 * MARGIN );
            double panelHeight = typeControlWrapper.getFullBounds().getHeight() + intensityControlWrapper.getFullBounds().getHeight() + _wavelengthControl.getFullBounds().getHeight() + 30;
            panel.setPreferredSize( new Dimension( (int) panelWidth, (int) panelHeight ) );
            panelWrapper.setOffset( 0, 0 );
            
            typeControlWrapper.setOffset( MARGIN, 5 );
            intensityControlWrapper.setOffset( typeControlWrapper.getFullBounds().getX(), typeControlWrapper.getFullBounds().getY() + typeControlWrapper.getFullBounds().getHeight() + 5 );
            _wavelengthControl.setOffset( intensityControlWrapper.getFullBounds().getX() + 8, intensityControlWrapper.getFullBounds().getY() + intensityControlWrapper.getFullBounds().getHeight() + 22 );
        }
        
        // Colors
        panel.setBackground( HAConstants.GUN_CONTROLS_BACKGROUND );
        _typeControl.setLabelsForeground( HAConstants.GUN_CONTROLS_FOREGROUND );
        _intensityControl.setUnitsForeground( HAConstants.GUN_CONTROLS_FOREGROUND );
        _wavelengthControl.setUnitsForeground( HAConstants.GUN_CONTROLS_FOREGROUND );
    }
    
    public LightTypeControl getTypeControl() {
        return _typeControl;
    }
    
    public IntensityControl getIntensityControl() {
        return _intensityControl;
    }
    
    public WavelengthControl getWavelengthControl() {
        return _wavelengthControl;
    }
}
