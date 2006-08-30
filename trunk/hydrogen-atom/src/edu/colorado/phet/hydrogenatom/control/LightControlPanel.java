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

import java.awt.GridBagConstraints;

import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;


public class LightControlPanel extends JPanel {

    private LightTypeControl _typeControl;
    private IntensityControl _intensityControl;
    
    public LightControlPanel( PSwingCanvas canvas ) {
        super();
        
        // Components
        JLabel title = new JLabel( SimStrings.get( "title.lightControls" ) );
        title.setFont( HAConstants.TITLE_FONT );
        _typeControl = new LightTypeControl();
        _intensityControl = new IntensityControl();
        
        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int col = 0;
        layout.addComponent( title, row++, col );
        layout.addComponent( _typeControl, row++, col );
        layout.addComponent( _intensityControl, row++, col );
        
        // Border
        setBorder( HAConstants.CONTROL_PANEL_BORDER );
    }
    
    public LightTypeControl getTypeControl() {
        return _typeControl;
    }
    
    public IntensityControl getIntensityControl() {
        return _intensityControl;
    }
}
