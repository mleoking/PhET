package edu.colorado.phet.workenergy;

import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.workenergy.model.WorkEnergyModel;

/**
 * @author Sam Reid
 */
public class WorkModule extends WorkEnergyModule<WorkEnergyModel> {
    public WorkModule( PhetFrame phetFrame ) {
        super( phetFrame, "Work", new WorkEnergyModel() );
    }
}
