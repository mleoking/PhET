/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.util.FrameSetup;

import javax.swing.*;
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
        super( args, "nRg Sk8r", "Energy Conservation", "0.18",
//               new SwingTimerClock( 0.2, 30 ), true, new FrameSetup() {
new FrameSetup() {
//               new SwingTimerClock( 0.2/5, 30 ), true, new FrameSetup() {

    public void initialize( JFrame frame ) {
        frame.setSize( Toolkit.getDefaultToolkit().getScreenSize().width - EC3Module.energyFrameWidth,
                       Toolkit.getDefaultToolkit().getScreenSize().height - 100 - EC3Module.chartFrameHeight //for debug
        );
        frame.setLocation( 0, 0 );
    }
} );

        module = new EC3Module( "Module", new SwingClock( 30, 0.03 ), getPhetFrame() );
        setModules( new Module[]{module} );
    }

    public static void main( final String[] args ) {
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
//        PDebug.debugRegionManagement=true;
    }
}
