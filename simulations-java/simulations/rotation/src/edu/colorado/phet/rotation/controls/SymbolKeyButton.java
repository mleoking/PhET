package edu.colorado.phet.rotation.controls;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Author: Sam Reid
 * Jul 11, 2007, 5:55:37 PM
 */
public class SymbolKeyButton extends JPanel {
    private JDialog dialog;

    public SymbolKeyButton( JFrame parentFrame ) {
        dialog = new JDialog( parentFrame, "Symbol Key", false );
        final JButton button = new JButton( "Show Symbol Key" );
        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                SwingUtils.centerDialogInParent( dialog );
                dialog.show();
                if( dialog.getContentPane() instanceof JComponent ) {
                    JComponent jComponent = (JComponent)dialog.getContentPane();
                    jComponent.paintImmediately( 0, 0, jComponent.getWidth(), jComponent.getHeight() );
                }
            }
        } );
        dialog.setContentPane( new SymbolKey() );
        dialog.pack();
        add( button );
    }
}
