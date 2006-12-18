/**
 * Class: SetAmplitudeCmd
 * Package: edu.colorado.phet.emf.command
 * Author: Another Guy
 * Date: Jun 25, 2003
 */
package edu.colorado.phet.emf.command;

import edu.colorado.phet.common.model.Command;
import edu.colorado.phet.emf.model.EmfModel;

public class SetAmplitudeCmd implements Command {
    private EmfModel model;
    private float amplitude;

    public SetAmplitudeCmd( EmfModel model, float amplitude ) {
        this.model = model;
        this.amplitude = amplitude;
    }

    public void doIt() {
        model.setTransmittingAmplitude( amplitude );
//        EmfModel.instance().setTransmittingAmplitude( amplitude );
    }
}
