package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

public class AutomaticUpdateDialog extends UpdateResultDialog {
    public AutomaticUpdateDialog( Frame parent, String html ) {
        super( parent, "New Update Available", html );
        JPanel buttonStrip = new JPanel();
        buttonStrip.add( new JButton( "Update Now" ) );
        buttonStrip.add( new JButton( "Ask me later" ) );
        buttonStrip.add( new JButton( "Skip this version" ) );
        buttonStrip.add( new JButton( "Preferences" ) );
        addComponent( buttonStrip );
        pack();
        center();
    }

    public static void main( String[] args ) {
        AutomaticUpdateDialog dialog = new AutomaticUpdateDialog( null, "<html>Your current version of Glaciers is 1.01.<br>A newer version (1.02) is available.</html>" );
        dialog.setVisible( true );
        dialog.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                System.exit( 0 );
            }
        } );
    }
}
