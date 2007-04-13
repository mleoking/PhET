/**
 * Class: AddModelElementCmd
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Jan 13, 2004
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.model.Command;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.mechanics.Body;

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
