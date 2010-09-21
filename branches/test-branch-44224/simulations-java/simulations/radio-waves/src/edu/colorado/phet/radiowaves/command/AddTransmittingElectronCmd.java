/**
 * Class: AddTransmittingElectronCmd Package: edu.colorado.phet.command Author:
 * Another Guy Date: May 27, 2003
 */

package edu.colorado.phet.radiowaves.command;

import edu.colorado.phet.common.phetcommon.model.Command;
import edu.colorado.phet.radiowaves.model.Electron;
import edu.colorado.phet.radiowaves.model.EmfModel;

public class AddTransmittingElectronCmd implements Command {

    private EmfModel model;
    private Electron electron;

    public AddTransmittingElectronCmd( EmfModel model, Electron electron ) {
        this.model = model;
        this.electron = electron;
    }

    public void doIt() {
        // TODO: rjl 6/26/03
        //        EmfModel model = EmfModel.instance();
        model.addTransmittingElectrons( electron );
        new AddEmfModelElementCmd( model, electron ).doIt();
        //        new AddEmfModelElementCmd( electron ).doIt();
    }
}
