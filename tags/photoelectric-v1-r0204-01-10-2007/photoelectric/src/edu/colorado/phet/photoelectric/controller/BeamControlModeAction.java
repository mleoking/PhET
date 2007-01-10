/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.controller;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * BeamControlModeAction
 * <p>
 * An action that sets the mode of the beam control. Provided as a separate class so that it will
 * be easy to change the way in which the user controls the mode.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class BeamControlModeAction extends AbstractAction {
    BeamControl beamControl;
    private BeamControl.Mode mode;

    public BeamControlModeAction( BeamControl beamControl, BeamControl.Mode mode, String label ) {
        super( label );
        this.beamControl = beamControl;
        this.mode = mode;
    }

    public void actionPerformed( ActionEvent e ) {
        beamControl.setMode( mode );
    }
}
