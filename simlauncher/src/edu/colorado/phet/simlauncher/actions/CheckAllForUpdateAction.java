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

import edu.colorado.phet.simlauncher.Catalog;
import edu.colorado.phet.simlauncher.Simulation;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * CheckAllForUpdateAction
 * <p/>
 * Causes all installed simulations to check for updates
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CheckAllForUpdateAction extends AbstractAction {

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------
    public CheckAllForUpdateAction() {
    }

    public void actionPerformed( ActionEvent e ) {
        List installedSims = Catalog.instance().getInstalledSimulations();
        Simulation simulation = null;
        for( int i = 0; i < installedSims.size(); i++ ) {
            simulation = (Simulation)installedSims.get( i );
            if( !simulation.isCurrent() ) {
                simulation.setUpdateAvailable();
            }
        }
    }
}
