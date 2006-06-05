/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher;

/**
 * SimulationContainer
 * <p>
 * An object that contains a reference to a particular Simulation. This can be a JList,
 * a JTable, or any object that has a distinguished instance of Simulation.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface SimContainer {
    Simulation getSimulation();
}
