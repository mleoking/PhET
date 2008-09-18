/**
 * Class: StaticFieldIsEnabledCmd Class: edu.colorado.phet.emf.command User: Ron
 * LeMaster Date: Jun 4, 2003 Time: 3:47:35 AM
 */

package edu.colorado.phet.radiowaves.command;

import edu.colorado.phet.common_1200.model.Command;
import edu.colorado.phet.radiowaves.model.EmfModel;

public class StaticFieldIsEnabledCmd implements Command {

    private EmfModel model;
    private boolean isEnabled;

    public StaticFieldIsEnabledCmd( EmfModel model, boolean isEnabled ) {
        this.model = model;
        this.isEnabled = isEnabled;
    }

    public void doIt() {
        // todo: rjl 6/26/03
        model.setStaticFieldEnabled( isEnabled );
        //        EmfModel.instance().setStaticFieldEnabled( isEnabled );
    }

}
