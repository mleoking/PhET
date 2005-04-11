/**
 * Class: SetEnergyLevelsVisibleCmd
 * Class: ${PACKAGE}
 * User: Ron LeMaster
 * Date: Mar 28, 2003
 * Time: 8:37:11 AM
 */
package edu.colorado.phet.lasers.controller.command;

import edu.colorado.phet.controller.PhetApplication;
import edu.colorado.phet.controller.command.Command;
import edu.colorado.phet.lasers.view.LaserMainPanel;

public class SetEnergyLevelsVisibleCmd implements Command {

    private boolean isVisible;

    public SetEnergyLevelsVisibleCmd( boolean isVisible ) {
        this.isVisible = isVisible;
    }

    //
    // Interfaces implemented
    //
    public Object doIt() {
        LaserMainPanel mainPanel = (LaserMainPanel)PhetApplication.instance().getPhetMainPanel();
        mainPanel.setEnergyLevelsVisible( isVisible );
        return null;
    }
}
