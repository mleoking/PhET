/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.SwingClock;
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
    public static String TITLE = "Quantum Wave Interference";
    public static String DESCRIPTION = "Quantum Wave Interference";
    public static String VERSION = "0.30";

    static {
        PhetLookAndFeel.setLookAndFeel();
    }

    public SchrodingerApplication( String[] args ) {
        super( args, TITLE, DESCRIPTION, VERSION, createFrameSetup() );

        SchrodingerModule intensityModule = new IntensityModule( this, createClock() );
        addModule( intensityModule );

        SchrodingerModule singleParticleModel = new SingleParticleModule( this, createClock() );
        addModule( singleParticleModel );

        SchrodingerModule mandelModule = new MandelModule( this, createClock() );
        addModule( mandelModule );
    }

    private static IClock createClock() {
        return new SwingClock( 30, 1 );
    }

    private static FrameSetup createFrameSetup() {
        return new FrameSetup.MaxExtent( new FrameSetup.CenteredWithInsets( 100, 100 ) );
    }

    public static void main( String[] args ) {
        new PhetLookAndFeel().apply();
        SchrodingerApplication schrodingerApplication = new SchrodingerApplication( args );
        schrodingerApplication.startApplication();
        System.out.println( "SchrodingerApplication.main" );
    }

}
