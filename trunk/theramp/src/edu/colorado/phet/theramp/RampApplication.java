/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.util.FrameSetup;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005
 * Time: 9:57:09 AM
 * Copyright (c) Feb 11, 2005 by Sam Reid
 */

public class RampApplication extends PhetApplication {

    public static final double FORCE_LENGTH_SCALE = 0.1;//1.0;
    private static final String TITLE = "The Ramp";
    private static final String DESCRIPTION = "The Ramp Simulation";
    private static final String VERSION = "1.0";
    private RampModule simpleRampModule;
    private RampModule advancedFeatureModule;

    public RampApplication( String[] args, AbstractClock clock, FrameSetup frameSetup ) {
        super( args, TITLE, DESCRIPTION, VERSION, clock, false, frameSetup );
        simpleRampModule = new SimpleRampModule( getPhetFrame(), clock );
        advancedFeatureModule = new RampModule( getPhetFrame(), clock );
//        try {
//            SwingUtilities.invokeAndWait( new Runnable() {
//                public void run() {
        simpleRampModule.doReset();
        advancedFeatureModule.doReset();
//                }
//            } );
//        }
//        catch( InterruptedException e ) {
//            e.printStackTrace();
//        }
//        catch( InvocationTargetException e ) {
//            e.printStackTrace();
//        }
        setModules( new Module[]{simpleRampModule, advancedFeatureModule} );
    }

    public static void main( final String[] args ) {
        try {
            SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    PhetLookAndFeel phetLookAndFeel = new PhetLookAndFeel();
                    phetLookAndFeel.apply();
                    PhetLookAndFeel.setLookAndFeel();
                    SwingTimerClock clock = new SwingTimerClock( 1.0 / 30.0, 30 );
                    FrameSetup frameSetup = new FrameSetup.MaxExtent( new FrameSetup.CenteredWithSize( 600, 600 ) );
//                    FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 1024,768);
//                    FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 1024, 768 );
                    final RampApplication application = new RampApplication( args, clock, frameSetup );
                    application.startApplication();
                    System.out.println( "getSize() = " + application.simpleRampModule.getPhetPCanvas().getSize( ));
                }
            } );
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
        catch( InvocationTargetException e ) {
            e.printStackTrace();
        }

    }

}
