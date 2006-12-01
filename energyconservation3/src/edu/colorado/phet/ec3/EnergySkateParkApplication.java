/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.SwingClock;

import javax.jnlp.UnavailableServiceException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:07:25 AM
 * Copyright (c) Sep 21, 2005 by Sam Reid
 */

public class EnergySkateParkApplication extends PhetApplication {
    private static final String VERSION = "1.02.23";
    private EnergySkateParkModule module;
    public static double SIMULATION_TIME_DT = 0.03;

    public EnergySkateParkApplication( String[] args ) {
        super( args, EnergySkateParkStrings.getString( "energy.skate.park" ), EnergySkateParkStrings.getString( "energy.conservation" ),
               VERSION,
//               new EnergySkateParkDebugFrameSetup() );
new EnergySkateParkFrameSetup() );
        module = new EnergySkateParkModule( "Module", new SwingClock( 30, SIMULATION_TIME_DT ), getPhetFrame() );
        setModules( new Module[]{module} );
        getPhetFrame().addMenu( new EnergySkateParkTestMenu( this, args ) );

        JMenuItem saveItem = new JMenuItem( "Save" );
        saveItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    module.save();
                }
                catch( UnavailableServiceException e1 ) {
                    e1.printStackTrace();
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }
            }
        } );

        JMenuItem openItem = new JMenuItem( "Open" );
        openItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    module.open();
                }
                catch( UnavailableServiceException e1 ) {
                    e1.printStackTrace();
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }
                catch( ClassNotFoundException e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        getPhetFrame().addFileMenuItem( openItem );
        getPhetFrame().addFileMenuItem( saveItem );

        getPhetFrame().addFileMenuSeparator();
    }

    public static void main( final String[] args ) {
//        EnergySkateParkStrings.init( args, "localization/EnergySkateParkStrings" );
//        new EC3LookAndFeel().initLookAndFeel();
//        new EnergySkateParkApplication( args ).start();
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                EnergySkateParkStrings.init( args, "localization/EnergySkateParkStrings" );
                new EC3LookAndFeel().initLookAndFeel();
                new EnergySkateParkApplication( args ).start();
            }
        } );
    }

    public EnergySkateParkModule getModule() {
        return module;
    }

    private void start() {
        super.startApplication();
        module.getPhetPCanvas().requestFocus();
        final EnergySkateParkSimulationPanel c = module.getEnergyConservationCanvas();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher( new KeyEventDispatcher() {
            public boolean dispatchKeyEvent( KeyEvent e ) {
                if( !c.hasFocus() ) {
                    int id = e.getID();
                    switch( id ) {
                        case KeyEvent.KEY_PRESSED:
                            c.keyPressed( e );
                            break;
                        case KeyEvent.KEY_RELEASED:
                            c.keyReleased( e );
                            break;
                        case KeyEvent.KEY_TYPED:
                            c.keyTyped( e );
                            break;
                        default:
                            System.out.println( "unknown key event type" );
                            break;
                    }
                }
                return false;
            }
        } );
    }
}
