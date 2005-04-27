/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.flourescent;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.controller.LaserConfig;

import java.awt.*;

/**
 * FuorescentLightsApp
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class FluorescentLightsApp extends PhetApplication {

    public FluorescentLightsApp( ApplicationModel descriptor, String args[] ) {
        super( descriptor, args );
    }

//    public FluorescentLightsApp( String args[] ) {
//        super( args,
//               new SwingTimerClock( 12, 25, AbstractClock.FRAMES_PER_SECOND ),
//               "Fluorescent Lights",
//               "Fluorescent Lights",
//               "0.01" );
//
//        // Determine the resolution of the screen
//        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
//        FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 1024, 750 );
//        if( dim.getWidth() == 1024 || dim.getHeight() == 768 ) {
//            frameSetup = new FrameSetup.MaxExtent( new FrameSetup.CenteredWithSize( 1024, 750 ) );
//        }
//        setFrameSetup( frameSetup );
//
//        DischargeLampModule singleAtomModule = new DischargeLampModule( SimStrings.get( "ModuleTitle.SingleAtomModule" ),
//                                                                        getClock() );
//
//        double maxSpeed = 0.1;
//        DischargeLampModule multipleAtomModule = new DischargeLampModule( SimStrings.get( "ModuleTitle.MultipleAtomModule" ),
//                                                                          getClock() );
//        addModule( singleAtomModule );
//        addModule( multipleAtomModule );
//        setInitialModule( singleAtomModule );
//
//        getPhetFrame().pack();
//    }

    private static class AppDesc extends ApplicationModel {
        public AppDesc() {
            super( SimStrings.get("FluorescentLightsApplication.title"),
                   SimStrings.get("FluorescentLightsApplication.title"),
                   "0.01" );

            setClock( new SwingTimerClock( FluorescentLightsConfig.DT, FluorescentLightsConfig.FPS, AbstractClock.FRAMES_PER_SECOND ) );

            // Determine the resolution of the screen
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 1024, 768 );
            if( dim.getWidth() == 1024 || dim.getHeight() == 768 ) {
                frameSetup = new FrameSetup.MaxExtent( new FrameSetup.CenteredWithSize( 1024, 768 ) );
            }
            setFrameSetup( frameSetup );

            DischargeLampModule singleAtomModule = new SingleAtomModule( SimStrings.get( "ModuleTitle.SingleAtomModule" ),
                                                                            getClock(),
                                                                            FluorescentLightsConfig.NUM_ENERGY_LEVELS );

            double maxSpeed = 0.1;
            DischargeLampModule multipleAtomModule = new MultipleAtomModule( SimStrings.get( "ModuleTitle.MultipleAtomModule" ),
                                                                              getClock(), 30,
                                                                              FluorescentLightsConfig.NUM_ENERGY_LEVELS,
                                                                              maxSpeed );
            setModules( new Module[]{
                singleAtomModule,
                multipleAtomModule} );
            setInitialModule( singleAtomModule );
        }
    }

    public static void main( String[] args ) {
        SimStrings.setStrings( FluorescentLightsConfig.localizedStringsPath );
        SimStrings.setStrings( LaserConfig.localizedStringsPath );

        FluorescentLightsApp app = new FluorescentLightsApp( new AppDesc(), args );
        app.startApplication();
    }

}
