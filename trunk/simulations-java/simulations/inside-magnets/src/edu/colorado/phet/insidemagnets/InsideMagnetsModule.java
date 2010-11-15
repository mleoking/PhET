package edu.colorado.phet.insidemagnets;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * @author Sam Reid
 */
public class InsideMagnetsModule extends Module {
    public InsideMagnetsModule( String name ) {
        super( name, new ConstantDtClock( 30 ) );
        setSimulationPanel( new InsideMagnetsCanvas() );
    }
}
