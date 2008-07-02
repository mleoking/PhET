package edu.colorado.phet.phscale.developer;

import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.phscale.model.LiquidDescriptor;


public class LiquidColorsDialog extends JDialog {

    public LiquidColorsDialog( Frame owner ) {
        super( owner );
        super.setTitle( "Liquid Colors" );
        super.setModal( false );
        super.setResizable( false );

        JPanel inputPanel = new LiquidColorsPanel( owner, LiquidDescriptor.getAllInstances() );

        getContentPane().add( inputPanel );
        pack();
        SwingUtils.centerDialogInParent( this );
    }
}