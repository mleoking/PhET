/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
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
 * @version $Revision$
 */
public interface SelectedSimsContainer extends SimContainer {
    Simulation[] getSelectedSimulations();
}
