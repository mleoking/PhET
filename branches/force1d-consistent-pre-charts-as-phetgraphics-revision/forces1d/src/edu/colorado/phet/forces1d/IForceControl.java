package edu.colorado.phet.forces1d;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.ControlPanel;

/**
 * User: Sam Reid
 * Date: Mar 17, 2005
 * Time: 6:05:08 AM
 * Copyright (c) Mar 17, 2005 by Sam Reid
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
