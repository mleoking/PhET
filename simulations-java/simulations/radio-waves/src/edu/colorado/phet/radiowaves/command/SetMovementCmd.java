/**
 * Class: SetMovementCmd Package: edu.colorado.phet.emf.command Author: Another
 * Guy Date: Jun 12, 2003
 */

package edu.colorado.phet.radiowaves.command;

import edu.colorado.phet.common_1200.model.Command;
import edu.colorado.phet.radiowaves.model.EmfModel;
import edu.colorado.phet.radiowaves.model.movement.MovementType;

public class SetMovementCmd implements Command {

    private EmfModel model;
    private MovementType movementType;

    public SetMovementCmd( EmfModel model, MovementType movementType ) {
        this.model = model;
        this.movementType = movementType;
    }

    public void doIt() {
        model.setTransmittingElectronMovementStrategy( movementType );
    }
}
