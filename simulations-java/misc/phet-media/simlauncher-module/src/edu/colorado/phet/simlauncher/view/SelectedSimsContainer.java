/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/view/SelectedSimsContainer.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.1 $
 * Date modified : $Date: 2006/07/25 23:05:14 $
 */
package edu.colorado.phet.simlauncher.view;

import edu.colorado.phet.simlauncher.model.SimContainer;
import edu.colorado.phet.simlauncher.model.Simulation;

/**
 * SelectedSimsContainer
 * <p>
 * A SimContainer that contain simulations selected from a larger
 * collection of simulations. This is useful for things like SimTables
 *
 * @author Ron LeMaster
 * @version $Revision: 1.1 $
 */
public interface SelectedSimsContainer extends SimContainer {
    Simulation[] getSelectedSimulations();
}
