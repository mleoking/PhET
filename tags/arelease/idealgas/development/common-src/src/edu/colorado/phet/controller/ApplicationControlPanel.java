/*
 * Class: ApplicationControlPanel
 * Package: edu.colorado.phet.controller
 *
 * Created by: Ron LeMaster
 * Date: Nov 8, 2002
 */
package edu.colorado.phet.controller;

import edu.colorado.phet.physics.PhysicalSystem;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

/**
 * This panel contains the global control buttons common to all Phet applications, such
 * as "Run", "Stop", and "Clear. This panel is displayed at the bottom of the frame.
 */
public class ApplicationControlPanel extends JPanel implements Observer {

    // The application this panel controls
    private PhetApplication application;

    private boolean running = true;
    private JButton stopGoButton;
    private String helpButtonString = s_hideHelpString;

    /**
     * @param application The application this panel controls
     */
    public ApplicationControlPanel( final PhetApplication application ) {

        this.application = application;

        // Set up control panel
        JPanel buttonPanel = new JPanel();

        stopGoButton = new JButton( "Stop" );
        stopGoButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                toggleStopGo();
            }
        } );
        buttonPanel.add( stopGoButton );

        JButton clearButton = new JButton( "Clear" );
        clearButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                clearApplication();
            }
        } );
        buttonPanel.add( clearButton );

        JButton helpButton = new JButton( helpButtonString );
        helpButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                toggleHelp( e.getSource() );
            }
        } );
        buttonPanel.add( helpButton );

        this.add( buttonPanel );
    }

    /**
     *
     */
    public void runApplication() {
        application.run();
        stopGoButton.setText( "Stop" );
        running = true;
    }

    /**
     *
     */
    public void stopApplication() {
        running = false;
        stopGoButton.setText( "Run" );
        application.stop();
    }

    /**
     *
     */
    private void clearApplication() {
        application.clear();
    }

    /**
     *
     */
    private void toggleStopGo() {
        if( running ) {
            stopApplication();
        }
        else {
            runApplication();
        }
    }

    /**
     * Activates and deactivates the on-screen help
     */
    private void toggleHelp( Object source) {
        if( source instanceof JButton ) {
            JButton button = (JButton)source;
            if( button.getText() == s_showHelpString ) {
                button.setText( s_hideHelpString );
                application.getPhetFrame().showHelp();
            }
            else {
                button.setText( s_showHelpString );
                application.getPhetFrame().hideHelp();
            }
        }
    }

    /**
     * If the observed object is the physical system, the panel updates
     * its buttons based on the physical system's state
     * @param observable
     * @param obj
     */
    public void update( Observable observable, Object obj ) {
        if( observable instanceof PhysicalSystem ) {
            PhysicalSystem physicalSystem = (PhysicalSystem)observable;
            if( physicalSystem.isRunning() ) {
                stopGoButton.setText( "Stop" );
                running = true;
            }
            else {
                stopGoButton.setText( "Run" );
                running = false;
            }
        }
    }

    //
    // Static fields and methods
    //
    private static String s_showHelpString = "Show Help";
    private static String s_hideHelpString = "Hide Help";
}
