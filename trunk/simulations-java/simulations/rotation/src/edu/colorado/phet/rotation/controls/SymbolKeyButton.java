package edu.colorado.phet.rotation.controls;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.MultiStateButton;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Author: Sam Reid
 * Jul 11, 2007, 5:55:37 PM
 */
public class SymbolKeyButton extends JPanel {
    private JDialog dialog;
    private String SHOW_MODE = "SHOW";
    private String HIDE_MODE = "HIDE";
    private MultiStateButton button;

    public SymbolKeyButton( JFrame parentFrame ) {
        dialog = new JDialog( parentFrame, "Symbol Key", false );
        button = new MultiStateButton();
        button.addMode( SHOW_MODE, "Symbol Key", null );
        button.addMode( HIDE_MODE, "Hide Symbol Key", null );

        button.addActionListener( SHOW_MODE, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Point onScreen = PSwing.getLocationOnScreen( button );
//                Point onScreen=button.getLocationOnScreen();
                System.out.println( "onScreen = " + onScreen );
//                SwingUtils.centerDialogInParent( dialog );
                dialog.setLocation( onScreen );
                dialog.show();
                if ( dialog.getContentPane() instanceof JComponent ) {
                    JComponent jComponent = (JComponent) dialog.getContentPane();
                    jComponent.paintImmediately( 0, 0, jComponent.getWidth(), jComponent.getHeight() );
                }
            }
        } );

        dialog.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                button.setMode( SHOW_MODE );
            }
        } );
        button.addActionListener( HIDE_MODE, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dialog.hide();
            }
        } );
        dialog.setContentPane( new SymbolKey() );
        dialog.pack();
        add( button );
    }

    public void reset() {
        dialog.hide();
        button.setMode( SHOW_MODE );
    }
}
