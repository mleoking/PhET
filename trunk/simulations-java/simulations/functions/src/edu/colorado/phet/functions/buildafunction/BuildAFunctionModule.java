package edu.colorado.phet.functions.buildafunction;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.functions.FunctionsApplication;

/**
 * @author Sam Reid
 */
public class BuildAFunctionModule extends PiccoloModule {
    public BuildAFunctionModule() {
        super( "Build a Function II", new ConstantDtClock( 30 ) );
        setSimulationPanel( new BuildAFunctionCanvas() );
        setClockControlPanel( null );
        setLogoPanelVisible( false );
    }

    public static void main( String[] args ) { FunctionsApplication.runModule( args, new BuildAFunctionModule() ); }
}