/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.actions;

import edu.colorado.phet.simlauncher.SimContainer;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * LaunchSimAction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class LaunchSimAction extends AbstractAction {
    private SimContainer simContainer;

    public LaunchSimAction( SimContainer simContainer ) {
        this.simContainer = simContainer;
    }

    public void actionPerformed( ActionEvent e ) {
        simContainer.getSimulation().launch();
    }
}