package edu.colorado.phet.rotation.controls;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.MultiStateButton;
import edu.colorado.phet.rotation.RotationStrings;
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
        dialog = new JDialog( parentFrame, RotationStrings.getString( "controls.symbol.key" ), false );
        button = new MultiStateButton();
        button.addMode( SHOW_MODE, RotationStrings.getString( "controls.symbol.key" ), null );
        button.addMode( HIDE_MODE, RotationStrings.getString( "controls.hide.symbol.key" ), null );

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
