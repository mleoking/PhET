package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import edu.colorado.phet.balloons.BalloonsApplication;

public class AutomaticUpdateDialog extends UpdateResultDialog {
    public AutomaticUpdateDialog( final Frame parent, String html, final ITrackingInfo trackingInfo, final IManuallyCheckForUpdates iManuallyCheckForUpdates ) {
        super( parent, "New Update Available", html );
        JPanel buttonStrip = new JPanel();
        buttonStrip.add( new JButton( "Update Now" ) );//todo: implement

        JButton askMeLater = new JButton( "Ask me later" );
        askMeLater.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dispose();
            }
        } );
        buttonStrip.add( askMeLater );

        JButton skipThisVersion = new JButton( "Skip this version" );
        skipThisVersion.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dispose();
            }
        } );
        buttonStrip.add( skipThisVersion );

        JButton preferencesButton = new JButton( "Preferences" );
        preferencesButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                new PreferencesDialog( parent, trackingInfo, iManuallyCheckForUpdates ).setVisible( true );
            }
        } );
        buttonStrip.add( preferencesButton );
        addComponent( buttonStrip );
        pack();
        center();
    }

    public static void main( String[] args ) {
        BalloonsApplication.BalloonsApplicationConfig config = new BalloonsApplication.BalloonsApplicationConfig( args );
        AutomaticUpdateDialog dialog = new AutomaticUpdateDialog( null, "<html>Your current version of Glaciers is 1.01.<br>A newer version (1.02) is available.</html>", config, new ApplicationConfigManualCheckForUpdates( null, config ) );
        dialog.setVisible( true );
        dialog.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                System.exit( 0 );
            }
        } );
    }
}
