/**
 * Class: AddTransmittingElectronCmd
 * Package: edu.colorado.phet.command
 * Author: Another Guy
 * Date: May 27, 2003
 */
package edu.colorado.phet.command;

import edu.colorado.phet.common.model.command.Command;
import edu.colorado.phet.emf.model.EmfModel;
import edu.colorado.phet.emf.model.Electron;

public class AddTransmittingElectronCmd implements Command {

    private Electron electron;

    public AddTransmittingElectronCmd( Electron electron ) {
        this.electron = electron;
    }

    public void doIt() {
        EmfModel model = EmfModel.instance();
        model.addTransmittingElectrons( electron );
        new AddEmfModelElementCmd( electron ).doIt();
    }
}
