// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.capacitorlab.developer.EFieldShapesDebugMenuItem;
import edu.colorado.phet.capacitorlab.developer.VoltageShapesDebugMenuItem;
import edu.colorado.phet.capacitorlab.module.dielectric.DielectricModule;
import edu.colorado.phet.capacitorlab.module.introduction.IntroductionModule;
import edu.colorado.phet.capacitorlab.module.multiplecapacitors.MultipleCapacitorsModule;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * Main application for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CapacitorLabApplication extends PiccoloPhetApplication {

    /**
     * Sole constructor.
     *
     * @param config the configuration for this application
     */
    public CapacitorLabApplication( PhetApplicationConfig config ) {
        super( config );
        initModules();
        initMenuBar();
    }

    /*
     * Initializes the modules.
     */
    private void initModules() {

        Frame parentFrame = getPhetFrame();
        boolean dev = isDeveloperControlsEnabled();

        // add modules
        addModule( new IntroductionModule( parentFrame, dev ) );
        addModule( new DielectricModule( parentFrame, dev ) );
        addModule( new MultipleCapacitorsModule( parentFrame, dev ) );

        // make all control panels the same width
        int maxWidth = 0;
        for ( Module module : getModules() ) {
            maxWidth = Math.max( maxWidth, module.getControlPanel().getPreferredSize().width );
        }
        for ( Module module : getModules() ) {
            module.getControlPanel().addControlFullWidth( Box.createHorizontalStrut( maxWidth ) );
        }

        // start with Multiple Capacitor module for development
        if ( dev ) {
            setStartModule( getModule( 1 ) );
        }
    }

    private void initMenuBar() {
        // Developer menu items
        JMenu developerMenu = getPhetFrame().getDeveloperMenu();
        developerMenu.add( new EFieldShapesDebugMenuItem( this ) );
        developerMenu.add( new VoltageShapesDebugMenuItem( this ) );
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) {
        new PhetApplicationLauncher().launchSim( args, CLConstants.PROJECT_NAME, CapacitorLabApplication.class );
    }
}
