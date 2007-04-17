/**
 * Class: AddModelElementCmd
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Jan 13, 2004
 */
package edu.colorado.phet.idealgas.controller.command;

import edu.colorado.phet.common.mechanics.Body;
import edu.colorado.phet.common.model.Command;
import edu.colorado.phet.idealgas.model.IdealGasModel;

public class RemoveModelElementCmd implements Command {
    private IdealGasModel model;
    private Body body;

    public RemoveModelElementCmd( IdealGasModel model, Body body ) {
        this.model = model;
        this.body = body;
    }

    public void doIt() {
        model.removeModelElement( body );
    }
}
