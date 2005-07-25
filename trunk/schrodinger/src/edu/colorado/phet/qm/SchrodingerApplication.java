/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.qm.modules.intensity.IntensityModule;
import edu.colorado.phet.qm.modules.mandel.MandelModule;
import edu.colorado.phet.qm.modules.single.SingleParticleModule;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:48:21 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class SchrodingerApplication extends PhetApplication {
    public static String TITLE = "Quantum Interference";
    public static String DESCRIPTION = "Quantum Interference";
    public static String VERSION = "0.1";

    public SchrodingerApplication( String[] args ) {
        super( args, TITLE, DESCRIPTION, VERSION, createClock(), true, createFrameSetup() );
        PhetLookAndFeel.setLookAndFeel();
        SchrodingerModule singleParticleModel = new SingleParticleModule( getClock() );
        SchrodingerModule intensityModule = new IntensityModule( getClock() );
        SchrodingerModule mandelModule = new MandelModule( getClock() );
        setModules( new Module[]{singleParticleModel, intensityModule, mandelModule} );
//        setModules( new Module[]{mandelModule} );
    }

    private static AbstractClock createClock() {
        return new SwingTimerClock( 1.0, 30 );
    }

    private static FrameSetup createFrameSetup() {
        return new FrameSetup.MaxExtent( new FrameSetup.CenteredWithInsets( 100, 100 ) );
    }

    public static void main( String[] args ) {
        SchrodingerApplication schrodingerApplication = new SchrodingerApplication( args );
        schrodingerApplication.startApplication();
    }

}
