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

    private boolean isEnabled;
    public DynamicFieldIsEnabledCmd( boolean isEnabled ) {
        this.isEnabled = isEnabled;
    }

    public void doIt() {
        EmfModel.instance().setDynamicFieldEnabled( isEnabled );
    }
}
