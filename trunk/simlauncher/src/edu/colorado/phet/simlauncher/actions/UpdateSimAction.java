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

import edu.colorado.phet.simlauncher.Simulation;
import edu.colorado.phet.simlauncher.SimContainer;
import edu.colorado.phet.simlauncher.resources.SimResourceException;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * UpdateSimAction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class UpdateSimAction extends AbstractAction {
    private SimContainer simContainer;

    public UpdateSimAction( SimContainer simContainer ) {
        this.simContainer = simContainer;
    }

    public UpdateSimAction( Simulation simulation ) {
        this( new DefaultSimContainer( simulation ) );
    }

    public void actionPerformed( ActionEvent e ) {
        Simulation sim = simContainer.getSimulation();
        try {
            if( sim.isInstalled() && !sim.isCurrent() ) {
                sim.update();
            }
        }
        catch( SimResourceException e1 ) {
            e1.printStackTrace();
        }
    }
}