/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.model;

/**
 * SimulationContainer
 * <p/>
 * This interface is provided so that menu items and actions can determine what Simulation
 * to deal with at event-time. Some such event responders must be created when a Swing
 * control is created, but before that control knows what Simulation a particular event
 * refers to. This is the case, for example, with lists and tables. Other examples are menu
 * items that are used in both global and context menus.
 * <p/>
 * Simulation itself implements this interface, so that all Swing control that work with
 * Simulations can have uniform constructors
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface SimContainer {
    Simulation getSimulation();

    Simulation[] getSimulations();
}
