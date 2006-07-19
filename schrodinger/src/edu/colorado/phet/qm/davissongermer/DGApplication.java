/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.PhetFrameWorkaround;
import edu.colorado.phet.common.view.TabbedModulePane;
import edu.colorado.phet.piccolo.PiccoloPhetApplication;
import edu.colorado.phet.qm.QWIFrameSetup;
import edu.colorado.phet.qm.QWIPhetLookAndFeel;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:48:21 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class DGApplication extends PiccoloPhetApplication {
    public static String VERSION = "0.00.18";
    public static String TITLE = "Davisson-Germer: Electron Diffraction";
    public static String DESCRIPTION = "Simulate the original experiment that proved that electrons can behave as waves.  \n" +
                                       "Watch electrons diffract off a crystal of atoms, interfering with themselves \n" +
                                       "to create peaks and troughs of probability.";

    public DGApplication( String[] args ) {
        super( args, TITLE, DESCRIPTION, VERSION, new QWIFrameSetup() );
        addModule( new DGModule( this, createClock() ) );
    }

    private static IClock createClock() {
        return new SwingClock( 30, 1 );
    }

    protected PhetFrame createPhetFrame( PhetApplication phetApplication ) {
        return new PhetFrameWorkaround( phetApplication );
    }

    class MyTabbedModulePane extends TabbedModulePane {
        public MyTabbedModulePane( PhetApplication application, Module[] modules ) {
            super( application, modules );
        }
    }

    public static void main( String[] args ) {
//        PhetLookAndFeel.setLookAndFeel();
//        new PhetLookAndFeel().apply();
        new QWIPhetLookAndFeel() .initLookAndFeel();
        DGApplication schrodingerApplication = new DGApplication( args );
        schrodingerApplication.startApplication();
//        System.out.println( "SchrodingerApplication.main" );
    }

}
