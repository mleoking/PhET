/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.SwingClock;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:07:25 AM
 * Copyright (c) Sep 21, 2005 by Sam Reid
 */

public class EC3Application extends PhetApplication {
    private EC3Module module;
    private static final String VERSION = "0.00.24";

    public EC3Application( String[] args ) {
        super( args, "Energy Skate Park", "Energy Conservation", VERSION, new EC3FrameSetup() );

        module = new EC3Module( "Module", new SwingClock( 30, 0.03 ), getPhetFrame() );
        setModules( new Module[]{module} );
    }

    public static void main( final String[] args ) {
        EC3LookAndFeel.initLookAndFeel();
        new EC3Application( args ).start();
    }

    private void start() {
        super.startApplication();
        module.getPhetPCanvas().requestFocus();
        final EC3Canvas c = module.getEnergyConservationCanvas();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher( new KeyEventDispatcher() {
            public boolean dispatchKeyEvent( KeyEvent e ) {
                if( !c.hasFocus() ) {
                    int id = e.getID();
                    switch( id ) {
                        case KeyEvent.KEY_PRESSED:
//                            System.out.println( "KEY_PRESSED" );
                            c.keyPressed( e );
                            break;
                        case KeyEvent.KEY_RELEASED:
//                            System.out.println( "KEY_RELEASED" );
                            c.keyReleased( e );
                            break;
                        case KeyEvent.KEY_TYPED:
//                            System.out.println( "KEY_TYPED" );
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
//        PDebug.debugRegionManagement=true;
    }
}
