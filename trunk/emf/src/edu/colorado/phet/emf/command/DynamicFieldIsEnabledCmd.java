/**
 * Class: DynamicFieldIsEnabledCmd
 * Class: edu.colorado.phet.emf.command
 * User: Ron LeMaster
 * Date: Jun 4, 2003
 * Time: 3:58:12 AM
 */
package edu.colorado.phet.emf.command;

import edu.colorado.phet.common.model.command.Command;
import edu.colorado.phet.emf.model.EmfModel;

public class DynamicFieldIsEnabledCmd implements Command {

    private EmfModel model;
    private boolean isEnabled;

    public DynamicFieldIsEnabledCmd( EmfModel model, boolean isEnabled ) {
        this.model = model;
        this.isEnabled = isEnabled;
    }

    public void doIt() {
        // TODO: rjl 6/23/03
        model.setDynamicFieldEnabled( isEnabled );
//        EmfModel.instance().setDynamicFieldEnabled( isEnabled );
    }
}
