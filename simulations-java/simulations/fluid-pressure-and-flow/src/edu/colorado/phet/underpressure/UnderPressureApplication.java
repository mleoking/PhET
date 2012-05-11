// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.underpressure;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources;
import edu.colorado.phet.fluidpressureandflow.pressure.FluidPressureModule;

/**
 * Main application class for "Under Pressure", which is just the 1st tab from Fluid Pressure and Flow.
 *
 * @author Sam Reid
 */
public class UnderPressureApplication extends PiccoloPhetApplication {
    public UnderPressureApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new FluidPressureModule() );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, FluidPressureAndFlowResources.PROJECT_NAME, "under-pressure", UnderPressureApplication.class );
    }
}