/**
 * Class: AddFieldElementCmd
 * Package: edu.colorado.phet.command
 * Author: Another Guy
 * Date: May 29, 2003
 */
package edu.colorado.phet.command;

import edu.colorado.phet.common.model.command.Command;
import edu.colorado.phet.emf.model.EmfModel;
import edu.colorado.phet.emf.model.FieldElement;

public class AddFieldElementCmd implements Command {

    private FieldElement fieldElement;

    public AddFieldElementCmd( FieldElement fieldElement ) {
        this.fieldElement = fieldElement;
    }

    public void doIt() {
        EmfModel.instance().addModelElement( fieldElement );
    }
}
