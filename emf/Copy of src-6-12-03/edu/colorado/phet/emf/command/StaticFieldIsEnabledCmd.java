/**
 * Class: StaticFieldIsEnabledCmd
 * Class: edu.colorado.phet.emf.command
 * User: Ron LeMaster
 * Date: Jun 4, 2003
 * Time: 3:47:35 AM
 */
package edu.colorado.phet.emf.command;

import edu.colorado.phet.emf.model.EmfModel;
import edu.colorado.phet.common.model.command.Command;

public class StaticFieldIsEnabledCmd implements Command {

    private boolean isEnabled;

    public StaticFieldIsEnabledCmd( boolean isEnabled ) {
        this.isEnabled = isEnabled;
    }

    public void doIt() {
        EmfModel.instance().setStaticFieldEnabled( isEnabled );
    }

}
