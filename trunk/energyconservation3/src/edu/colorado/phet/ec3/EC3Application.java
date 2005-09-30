/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.SwingTimerClock;

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

    public EC3Application( String[] args ) {
        super( args, "EC3", "Energy Conservation", "0.1", new SwingTimerClock( 1.0, 25 ), true, new LeftSideOfScreen( 200, 100 ) );
        module = new EC3Module( "Module", getClock() );
        setModules( new Module[]{module} );
    }

    public static void main( String[] args ) {
        new EC3Application( args ).start();
    }

    private void start() {
        super.startApplication();
        module.getPhetPCanvas().requestFocus();
//        getPhetFrame().setGlassPane( new InterceptingGlassPane( module.getEnergyConservationCanvas() ) );
        final EC3Canvas c = module.getEnergyConservationCanvas();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher( new KeyEventDispatcher() {
            public boolean dispatchKeyEvent( KeyEvent e ) {
                if( !c.hasFocus() ) {
                    int id = e.getID();
                    switch( id ) {
                        case KeyEvent.KEY_PRESSED:
                            System.out.println( "KEY_PRESSED" );
                            c.keyPressed( e );
                            break;
                        case KeyEvent.KEY_RELEASED:
                            System.out.println( "KEY_RELEASED" );
                            c.keyReleased( e );
                            break;
                        case KeyEvent.KEY_TYPED:
                            System.out.println( "KEY_TYPED" );
                            c.keyTyped( e );
                            break;
                        default:
                            System.out.println( "unknown type" );
                            break;
                    }

                }
                return false;
            }
        } );
    }
}
