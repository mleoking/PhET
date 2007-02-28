/**
 * Class: SetMovementSinusoidalCmd
 * Package: edu.colorado.phet.command
 * Author: Another Guy
 * Date: May 27, 2003
 */
package edu.colorado.phet.command;

import edu.colorado.phet.common.model.Command;
import edu.colorado.phet.emf.model.EmfModel;
import edu.colorado.phet.emf.model.movement.ManualMovement;

public class SetMovementManualCmd implements Command {

    private EmfModel model;

    public SetMovementManualCmd( EmfModel model ) {
        this.model = model;
    }

    public void doIt() {
        ManualMovement movementControl = new ManualMovement();
        // TODO: rjl 6/26/03
//        EmfModel model = EmfModel.instance();
        model.setTransmittingElectronMovementStrategy( movementControl );
    }
}
