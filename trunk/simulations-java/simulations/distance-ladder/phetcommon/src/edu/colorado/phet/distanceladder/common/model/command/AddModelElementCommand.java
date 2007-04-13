/**
 * Class: AddModelElementCommand
 * Package: edu.colorado.phet.common.examples.hellophet.application
 * Author: Another Guy
 * Date: May 28, 2003
 */
// TODO: move this up one level in the package structure
package edu.colorado.phet.distanceladder.common.model.command;

import edu.colorado.phet.distanceladder.common.model.BaseModel;
import edu.colorado.phet.distanceladder.common.model.ModelElement;

public class AddModelElementCommand implements Command {
    ModelElement me;
    BaseModel m;

    public AddModelElementCommand( BaseModel m, ModelElement me ) {
        this.m = m;
        this.me = me;
    }

    public void doItLater() {
        m.execute( this );
    }

    public void doIt() {
        m.addModelElement( me );
    }
}
