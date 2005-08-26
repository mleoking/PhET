/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.controller.LaserConfig;

import java.awt.*;

/**
 * DischargeLampsApp
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DischargeLampsApp extends PhetApplication {
    static private FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 1024, 768 );
//    static private FrameSetup frameSetup = new FrameSetup.MaxExtent( new FrameSetup.CenteredWithSize( 1024, 768 ) );

    /**
     * @param args
     */
    public DischargeLampsApp( String[] args ) {
        super( args, SimStrings.get( "DischargeLampsApplication.title" ),
               SimStrings.get( "DischargeLampsApplication.title" ),
               "0.01",
               new SwingTimerClock( DischargeLampsConfig.DT, DischargeLampsConfig.FPS, AbstractClock.FRAMES_PER_SECOND ),
               true,
               frameSetup );

        // Determine the resolution of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
//        frameSetup = new FrameSetup.CenteredWithSize( 1024, 768 );
//        if( dim.getWidth() == 1024 || dim.getHeight() == 768 ) {
//            frameSetup = new FrameSetup.MaxExtent( new FrameSetup.CenteredWithSize( 1024, 768 ) );
//        }
//        createPhetFrame( frameSetup );

        DischargeLampModule singleAtomModule = new SingleAtomModule( SimStrings.get( "ModuleTitle.SingleAtomModule" ),
                                                                     getClock(),
                                                                     DischargeLampsConfig.NUM_ENERGY_LEVELS );

        double maxSpeed = 0.1;
//        DischargeLampModule multipleAtomModule = new MultipleAtomModule( SimStrings.get( "ModuleTitle.MultipleAtomModule" ),
//                                                                         getClock(), 30,
//                                                                         DischargeLampsConfig.NUM_ENERGY_LEVELS,
//                                                                         maxSpeed );
        setModules( new Module[]{singleAtomModule,
                                 /*multipleAtomModule*/} );
        setInitialModule( singleAtomModule );
    }

    /**
     * @param args
     */
    public static void main( String[] args ) {

        // Tell SimStrings where the simulations-specific strings are
        SimStrings.setStrings( DischargeLampsConfig.localizedStringsPath );
        SimStrings.setStrings( LaserConfig.localizedStringsPath );

        long t1 = System.currentTimeMillis();
        DischargeLampsApp app = new DischargeLampsApp( args );
        long t2 = System.currentTimeMillis();
        app.startApplication();
        long t3 = System.currentTimeMillis();
        System.out.println( "t1 = " + t1 );
        System.out.println( "t2 = " + t2 );
        System.out.println( "t3 = " + t3 );
    }

}
