/**
 * Class: AddBodyCmd
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Jan 13, 2004
 */
package edu.colorado.phet.idealgas.controller;

//import edu.colorado.phet.controller.command.Command;
//import edu.colorado.phet.physics.body.Body;
//import edu.colorado.phet.physics.PhysicalSystem;
import edu.colorado.phet.mechanics.Body;
import edu.colorado.phet.common.model.Command;
import edu.colorado.phet.common.model.BaseModel;

public class AddBodyCmd implements Command {
    private Body body;
    private BaseModel model;

    public AddBodyCmd( Body body, BaseModel model ) {
        this.body = body;
        this.model = model;
    }

    public void doIt() {
//    public Object doIt() {
        model.addModelElement( body );
//        PhysicalSystem.instance().addBody( body );
//        return null;
    }
}
