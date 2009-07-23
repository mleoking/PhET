/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.dialog;

import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JDialog;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.glaciers.control.ModelConstantsPanel;
import edu.colorado.phet.glaciers.model.Glacier;


public class ModelConstantsDialog extends JDialog {
    
    private final ModelConstantsPanel _panel;

    public ModelConstantsDialog( Frame owner, Glacier glacier, String moduleName ) {
        super( owner, "Model Constants (" + moduleName + ")" );
        setModal( false );
        setResizable( false );
        _panel = new ModelConstantsPanel( glacier );
        getContentPane().add( _panel );
        setSize( new Dimension( 300, (int) _panel.getPreferredSize().getHeight() + 50 ) );
        SwingUtils.centerDialogInParent( this );
    }
    
    public void dispose() {
        _panel.cleanup();
        super.dispose();
    }
}
