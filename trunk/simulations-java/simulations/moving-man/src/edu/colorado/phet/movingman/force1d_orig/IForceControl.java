package edu.colorado.phet.movingman.force1d_orig;

import edu.colorado.phet.movingman.force1d_orig.common_force1d.application.Module;
import edu.colorado.phet.movingman.force1d_orig.common_force1d.view.ControlPanel;

/**
 * User: Sam Reid
 * Date: Mar 17, 2005
 * Time: 6:05:08 AM
 */
public abstract class IForceControl extends ControlPanel {
    /**
     * @param module
     */
    public IForceControl( Module module ) {
        super( module );
    }

    public abstract void handleUserInput();

    public abstract void updateGraphics();
}
