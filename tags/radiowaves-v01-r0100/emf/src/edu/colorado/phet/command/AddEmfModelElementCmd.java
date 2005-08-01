/**
 * Class: AddEmfModelElementCmd
 * Package: edu.colorado.phet.command
 * Author: Another Guy
 * Date: May 23, 2003
 */
package edu.colorado.phet.command;

import edu.colorado.phet.common.model.Command;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.emf.model.EmfModel;

public class AddEmfModelElementCmd implements Command {

    private ModelElement element;
    private EmfModel model;

    public AddEmfModelElementCmd( EmfModel model, ModelElement element ) {
        this.model = model;
        this.element = element;
    }

    public void doIt() {
        model.addModelElement( element );
//        EmfModel.instance().addModelElement( element );
    }
}
