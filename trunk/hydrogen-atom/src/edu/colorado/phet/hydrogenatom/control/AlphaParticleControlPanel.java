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

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;


public class AlphaParticleControlPanel extends JPanel {

    private IntensityControl _intensityControl;
    
    public AlphaParticleControlPanel( PSwingCanvas canvas ) {
        super();
        
        // Components
        JLabel title = new JLabel( SimStrings.get( "title.alphaParticleControls" ) );
        title.setFont( HAConstants.TITLE_FONT );
        _intensityControl = new IntensityControl();
        _intensityControl.setColor( HAConstants.ALPHA_PARTICLES_COLOR );
        
        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int col = 0;
        layout.addComponent( title, row++, col );
        layout.addComponent( _intensityControl, row, col );
        
        // Border
        setBorder( HAConstants.CONTROL_PANEL_BORDER );
    }
    
    public IntensityControl getIntensityControl() {
        return _intensityControl;
    }
}
