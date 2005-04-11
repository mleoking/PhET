/**
 * Class: SetMovementSinusoidalCmd
 * Package: edu.colorado.phet.command
 * Author: Another Guy
 * Date: May 27, 2003
 */
package edu.colorado.phet.command;

import edu.colorado.phet.common.model.command.Command;
import edu.colorado.phet.emf.model.EmfModel;
import edu.colorado.phet.emf.model.movement.ManualMovement;

public class SetMovementManualCmd implements Command {

    public void doIt() {
        ManualMovement movementControl = new ManualMovement();
        EmfModel model = EmfModel.instance();
        model.setTransmittingElectronMovementStrategy( movementControl );
    }
}
