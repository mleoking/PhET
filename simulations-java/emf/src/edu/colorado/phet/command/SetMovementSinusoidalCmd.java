/**
 * Class: SetMovementSinusoidalCmd
 * Package: edu.colorado.phet.command
 * Author: Another Guy
 * Date: May 27, 2003
 */
package edu.colorado.phet.command;

import edu.colorado.phet.common.model.Command;
import edu.colorado.phet.emf.model.EmfModel;

public class SetMovementSinusoidalCmd implements Command {

    private EmfModel model;
    private float frequency;
    private float amplitude;

    public SetMovementSinusoidalCmd( EmfModel model, float frequency, float amplitude ) {
        this.model = model;
        this.frequency = frequency;
        this.amplitude = amplitude;
    }

    public void doIt() {
        edu.colorado.phet.emf.model.movement.SinusoidalMovement movementControl = new edu.colorado.phet.emf.model.movement.SinusoidalMovement( frequency, amplitude );
//        EmfModel model = EmfModel.instance();
        model.setTransmittingElectronMovementStrategy( movementControl );
    }
}
