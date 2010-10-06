/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab;

import java.awt.Frame;

import javax.swing.Box;

import edu.colorado.phet.capacitorlab.module.dielectric.DielectricModule;
import edu.colorado.phet.capacitorlab.module.introduction.IntroductionModule;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * Main application for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * 
 */
public class CapacitorLabApplication extends PiccoloPhetApplication {

    /**
     * Sole constructor.
     *
     * @param config the configuration for this application
     */
    public CapacitorLabApplication( PhetApplicationConfig config )
    {
        super( config );
        initModules();
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
//        addModule( new MultipleCapacitorsModule( parentFrame, dev ) );
        
        // make all control panels the same width
        int maxWidth = 0;
        for ( Module module : getModules() ) {
            maxWidth = Math.max( maxWidth, module.getControlPanel().getPreferredSize().width );
        }
        for ( Module module : getModules() ) {
            module.getControlPanel().addControlFullWidth( Box.createHorizontalStrut( maxWidth ) );
        }
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) {
        new PhetApplicationLauncher().launchSim( args, CLConstants.PROJECT_NAME, CapacitorLabApplication.class );
    }
}
