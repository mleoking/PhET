/**
 * Class: SetFieldCurveEnabledCmd
 * Package: edu.colorado.phet.emf.command
 * Author: Another Guy
 * Date: Jun 4, 2003
 */
package edu.colorado.phet.emf.command;

import edu.colorado.phet.common.model.command.Command;
import edu.colorado.phet.emf.view.AntennaPanel;

public class SetFieldCurveEnabledCmd implements Command {
    private boolean isEnabled;

    public SetFieldCurveEnabledCmd( boolean isEnabled ) {
        this.isEnabled = isEnabled;
    }

    public void doIt() {
        AntennaPanel.instance().setFieldCurvesVisible( isEnabled );
    }
}
