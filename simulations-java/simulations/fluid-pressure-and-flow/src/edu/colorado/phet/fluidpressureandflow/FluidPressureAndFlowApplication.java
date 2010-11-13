package edu.colorado.phet.fluidpressureandflow;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.fluidpressureandflow.modules.fluidpressure.FluidPressureModule;

/**
 * @author Sam Reid
 */
public class FluidPressureAndFlowApplication extends PiccoloPhetApplication {
    private static final String NAME = "fluid-pressure-and-flow";

    public FluidPressureAndFlowApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new FluidPressureModule() );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, NAME, FluidPressureAndFlowApplication.class );
    }
}
