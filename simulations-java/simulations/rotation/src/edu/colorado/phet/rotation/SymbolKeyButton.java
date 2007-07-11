package edu.colorado.phet.rotation;

import edu.colorado.phet.rotation.controls.SymbolKey;

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
        JButton button = new JButton( "Show Symbol Key" );
        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dialog.show();
            }
        } );
        dialog.setContentPane( new SymbolKey() );
        dialog.pack();
        add( button );
    }
}
