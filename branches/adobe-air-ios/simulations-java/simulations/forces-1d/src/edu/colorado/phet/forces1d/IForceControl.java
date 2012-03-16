// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.forces1d;


import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.forces1d.common.ControlPanel;

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
