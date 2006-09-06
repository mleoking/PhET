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
import java.awt.GridBagConstraints;

import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;


public class AlphaParticleControlPanel extends PNode {

    private IntensityControl _intensityControl;
    
    public AlphaParticleControlPanel( PSwingCanvas canvas ) {
        super();
        
        // Components
        _intensityControl = new IntensityControl();
        _intensityControl.setColor( HAConstants.ALPHA_PARTICLES_COLOR );
        
        // Panel
        JPanel panel = new JPanel();
        panel.setBorder( HAConstants.CONTROL_PANEL_BORDER );
        
        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        panel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int col = 0;
        layout.addComponent( _intensityControl, row, col );
        
        // PSwing wrapper
        PSwing pswing = new PSwing( canvas, panel );
        addChild( pswing );
        
        // Colors
        panel.setBackground( HAConstants.GUN_CONTROLS_BACKGROUND );
        _intensityControl.setUnitsForeground( HAConstants.GUN_CONTROLS_FOREGROUND );
    }
    
    public IntensityControl getIntensityControl() {
        return _intensityControl;
    }
}
