package edu.colorado.phet.workenergy.module;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.workenergy.model.WorkEnergyModel;

/**
 * @author Sam Reid
 */
public class WorkModule extends WorkEnergyModule<WorkEnergyModel> {
    public WorkModule( PhetFrame phetFrame, IClock clock ) {
        super( phetFrame, "Work", new WorkEnergyModel( clock ) );
    }
}
