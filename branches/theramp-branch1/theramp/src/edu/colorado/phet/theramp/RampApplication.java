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
//    private static PhetStartupWindow startupWindow;

    public RampApplication( String[] args, AbstractClock clock, FrameSetup frameSetup ) {
        super( args, TITLE, DESCRIPTION, VERSION, clock, false, frameSetup );
        simpleRampModule = new SimpleRampModule( getPhetFrame(), clock );
        advancedFeatureModule = new RampModule( getPhetFrame(), clock );
        setModules( new Module[]{simpleRampModule, advancedFeatureModule} );
//
//        simpleRampModule = new SimpleRampModule( getPhetFrame(), clock );
//        setModules( new Module[]{simpleRampModule} );

//        addClockTickListener( new ClockTickListener() {
//            public void clockTicked( ClockTickEvent event ) {
//                System.out.println( "surfaceType=: " + simpleRampModule.getRampPhysicalModel().getBlock().getSurface().getName()+", surfaceOffset="+simpleRampModule.getRampPhysicalModel().getBlock().getSurface().getDistanceOffset()+", distInSurface="+simpleRampModule.getRampPhysicalModel().getBlock().getPositionInSurface()+", totalDist="+simpleRampModule.getRampPhysicalModel().getBlock().getPosition());
////                System.out.println( "RampApplication.clockTicked: " + simpleRampModule.getRampPhysicalModel().getBlock().getPosition() );
//            }
//        } );
    }

    public static void main( final String[] args ) {
        PhetLookAndFeel phetLookAndFeel = new PhetLookAndFeel();
        phetLookAndFeel.apply();
        PhetLookAndFeel.setLookAndFeel();//todo this misses the better l&f in 1.5
        final SwingTimerClock clock = new SwingTimerClock( 1.0 / 30.0, 30 );
        final FrameSetup frameSetup = new FrameSetup.MaxExtent( new FrameSetup.CenteredWithSize( 800, 600 ) );
        final RampApplication application = new RampApplication( args, clock, frameSetup );
        try {
            SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    application.startApplication();
                    //workaround for 1.4.1, in which applying maxextent to an invisible frame does nothing.
                    new FrameSetup.MaxExtent().initialize( application.getPhetFrame() );
                    application.simpleRampModule.getPhetPCanvas().requestFocus();
//        System.out.println( "getSize() = " + application.simpleRampModule.getPhetPCanvas().getSize() );
//        new DebugPiccoloTree().printTree( application.advancedFeatureModule.getRampPanel().getRoot() );

//        application.getModuleManager().getActiveModule().getPhetPCanvas().requestFocus();
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
