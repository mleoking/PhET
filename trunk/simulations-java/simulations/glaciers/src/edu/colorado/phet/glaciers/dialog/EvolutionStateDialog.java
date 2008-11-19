/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.dialog;

import java.awt.Frame;

import javax.swing.JDialog;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.glaciers.control.EvolutionStatePanel;
import edu.colorado.phet.glaciers.model.Glacier;


public class EvolutionStateDialog extends JDialog {
    
    private final EvolutionStatePanel _panel;

    public EvolutionStateDialog( Frame owner, Glacier glacier ) {
        super( owner, "Glacier evolution state" );
        setModal( false );
        setResizable( false );
        _panel = new EvolutionStatePanel( glacier );
        getContentPane().add( _panel );
        pack();
        SwingUtils.centerDialogInParent( this );
    }
    
    public void dispose() {
        _panel.cleanup();
        super.dispose();
    }
}
