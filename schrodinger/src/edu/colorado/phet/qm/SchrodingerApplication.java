/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.util.FrameSetup;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:48:21 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class SchrodingerApplication extends PhetApplication {
    public static String TITLE = "Schrodinger";
    public static String DESCRIPTION = "Schrodinger Wave Propagation";
    public static String VERSION = "0.1";

    public SchrodingerApplication( String[] args ) {
        super( args, TITLE, DESCRIPTION, VERSION, createClock(), true, createFrameSetup() );
        SchrodingerModule singleParticleModel = new SingleParticleModule( getClock() );
        SchrodingerModule intensityModule = new IntensityModule( getClock() );
        setModules( new Module[]{singleParticleModel, intensityModule} );
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
