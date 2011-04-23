// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.fluidpressureandflow.fluidflow.FluidFlowModule;
import edu.colorado.phet.fluidpressureandflow.modules.fluidpressure.FluidPressureModule;
import edu.colorado.phet.fluidpressureandflow.modules.watertower.WaterTowerModule;

/**
 * Main application class for "Fluid Pressure and Flow"
 *
 * @author Sam Reid
 */
public class FluidPressureAndFlowApplication extends PiccoloPhetApplication {
    private static final String NAME = "fluid-pressure-and-flow";
    public static final PhetResources RESOURCES = new PhetResources( NAME );

    public FluidPressureAndFlowApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new FluidPressureModule() );
        addModule( new FluidFlowModule() );
        addModule( new WaterTowerModule() );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, NAME, FluidPressureAndFlowApplication.class );
    }
}
