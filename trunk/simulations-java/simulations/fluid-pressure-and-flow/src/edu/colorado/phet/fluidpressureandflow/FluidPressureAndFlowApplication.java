// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.fluidpressureandflow.flow.FluidFlowModule;
import edu.colorado.phet.fluidpressureandflow.pressure.FluidPressureModule;
import edu.colorado.phet.fluidpressureandflow.watertower.WaterTowerModule;

/**
 * Main application class for "Fluid Pressure and Flow"
 *
 * @author Sam Reid
 */
public class FluidPressureAndFlowApplication extends PiccoloPhetApplication {

    public FluidPressureAndFlowApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new FluidPressureModule() );
        addModule( new FluidFlowModule() );
        addModule( new WaterTowerModule() );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, FluidPressureAndFlowResources.PROJECT_NAME, FluidPressureAndFlowApplication.class );
    }
}