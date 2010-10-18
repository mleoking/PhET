package edu.colorado.phet.workenergy;

import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.workenergy.model.WorkEnergyModel;

/**
 * @author Sam Reid
 */
public class EnergyModule extends WorkEnergyModule<WorkEnergyModel> {
    public EnergyModule( PhetFrame phetFrame ) {
        super( phetFrame, "Energy", new WorkEnergyModel() );
    }
}
