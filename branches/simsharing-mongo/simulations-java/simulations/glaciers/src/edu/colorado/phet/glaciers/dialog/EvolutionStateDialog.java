// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.glaciers.dialog;

import java.awt.Dimension;
import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.glaciers.control.EvolutionStatePanel;
import edu.colorado.phet.glaciers.model.Glacier;


public class EvolutionStateDialog extends PaintImmediateDialog {
    
    private final EvolutionStatePanel _panel;

    public EvolutionStateDialog( Frame owner, Glacier glacier, String moduleName ) {
        super( owner, "Glacier Evolution State (" + moduleName + ")" );
        setModal( false );
        setResizable( false );
        _panel = new EvolutionStatePanel( glacier );
        getContentPane().add( _panel );
        setSize( new Dimension( 450, (int) _panel.getPreferredSize().getHeight() + 50 ) ); // instead of pack() because displayed values will grow
        SwingUtils.centerDialogInParent( this );
    }
    
    public void dispose() {
        _panel.cleanup();
        super.dispose();
    }
}
