// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark;

import javax.swing.JApplet;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;

/**
 * Demonstration of how to deploy a phet sim as an applet.
 *
 * @author Sam Reid
 */
public class EnergySkateParkApplet extends JApplet implements IProguardKeepClass {
    boolean inited = false;
    private boolean applicationStarted = false;

    @Override
    public void init() {
        super.init();    //To change body of overridden methods use File | Settings | File Templates.
        if ( !inited ) {
            EnergySkateParkApplication movingManApplication = new EnergySkateParkApplication( new PhetApplicationConfig( new String[0], "energy-skate-park" ) ) {
                @Override
                public void startApplication() {
                    if ( !applicationStarted ) {
                        applicationStarted = true;

                        setActiveModule( getStartModule() );

                        updateLogoVisibility();
                    }
                    else {
                        //TODO: put into standard logging framework
                        System.out.println( "WARNING: PhetApplication.startApplication was called more than once." );
                    }
                }
            };
            movingManApplication.startApplication();
            setContentPane( movingManApplication.getPhetFrame().getContentPane() );
            setJMenuBar( movingManApplication.getPhetFrame().getJMenuBar() );
            inited = true;
        }
    }
}