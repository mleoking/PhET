/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005                            ÷
 * Time: 9:57:09 AM
 * Copyright (c) Feb 11, 2005 by Sam Reid
 */

public class TheRampApplication extends PhetApplication {
    private static final String VERSION = "1.01";
    public static final double FORCE_LENGTH_SCALE = 0.1;//1.0;

    private RampModule simpleRampModule;
    private RampModule advancedFeatureModule;
//    private static PhetStartupWindow startupWindow;

    public TheRampApplication( String[] args, IClock clock, FrameSetup frameSetup ) {
        super( args, TheRampStrings.getString( "the.ramp" ), TheRampStrings.getString( "the.ramp.simulation" ),
               VERSION, frameSetup );
        simpleRampModule = new SimpleRampModule( getPhetFrame(), clock );
        advancedFeatureModule = new RampModule( getPhetFrame(), clock );
        setModules( new Module[]{simpleRampModule, advancedFeatureModule} );
//
//        simpleRampModule = new SimpleRampModule( getPhetFrame(), clock );
//        setModules( new Module[]{simpleRampModule} );

//        addClockTickListener( new ClockTickListener() {
//            public void clockTicked( ClockEvent event ) {
//                System.out.println( "surfaceType=: " + simpleRampModule.getRampPhysicalModel().getBlock().getSurface().getName()+", surfaceOffset="+simpleRampModule.getRampPhysicalModel().getBlock().getSurface().getDistanceOffset()+", distInSurface="+simpleRampModule.getRampPhysicalModel().getBlock().getPositionInSurface()+", totalDist="+simpleRampModule.getRampPhysicalModel().getBlock().getPosition());
////                System.out.println( "RampApplication.clockTicked: " + simpleRampModule.getRampPhysicalModel().getBlock().getPosition() );
//            }
//        } );
    }

    public static void main( final String[] args ) {
        TheRampStrings.init( args );
        PhetLookAndFeel phetLookAndFeel = new PhetLookAndFeel();
        phetLookAndFeel.apply();
        PhetLookAndFeel.setLookAndFeel();//todo this misses the better l&f in 1.5
        final SwingClock clock = new SwingClock( 30, 1.0 / 30.0 );
        final FrameSetup frameSetup = new FrameSetup.MaxExtent( new FrameSetup.CenteredWithSize( 800, 600 ) );
        final TheRampApplication applicationThe = new TheRampApplication( args, clock, frameSetup );
        try {
            SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    applicationThe.startApplication();
                    //workaround for 1.4.1, in which applying maxextent to an invisible frame does nothing.
                    new FrameSetup.MaxExtent().initialize( applicationThe.getPhetFrame() );
                    applicationThe.simpleRampModule.getPhetPCanvas().requestFocus();
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
