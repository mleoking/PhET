/**
 * Class: AddEmfModelElementCmd
 * Package: edu.colorado.phet.command
 * Author: Another Guy
 * Date: May 23, 2003
 */
package edu.colorado.phet.command;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.command.Command;
import edu.colorado.phet.emf.model.EmfModel;

public class AddEmfModelElementCmd implements Command {

    private ModelElement element;

    public AddEmfModelElementCmd( ModelElement element ) {
        this.element = element;
    }

    public void doIt() {
        EmfModel.instance().addModelElement( element );
    }
}
