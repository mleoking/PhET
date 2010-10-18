package edu.colorado.phet.workenergy;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;

/**
 * @author Sam Reid
 */
public class WorkEnergyModule extends Module {
    public WorkEnergyModule( PhetFrame phetFrame, String title ) {
        super( title, new ConstantDtClock( 30, 1.0 ) );
    }

}
