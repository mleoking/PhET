/* Copyright 2003, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.model;

import edu.colorado.phet.common.model.clock.ClockEvent;

import java.util.ArrayList;

/**
 * This class encompasses model elements in a physical system.
 * It also executes commands in the model thread.
 * <p/>
 * Typically, each Module in a PhetApplication will have its own instance of this type.
 *
 * @author Sam and Ron
 * @version $Revision$
 */
public class BaseModel {

    private CommandQueue commandList = new CommandQueue();
    private ArrayList modelElements = new ArrayList();

    /**
     * Add a ModelElement to this BaseModel
     *
     * @param aps
     */
    public void addModelElement( ModelElement aps ) {
        modelElements.add( aps );
    }

    /**
     * Get the ModelElement at the specified index.
     *
     * @param i
     * @return the ModelElement at the specified index.
     */
    public ModelElement modelElementAt( int i ) {
        return (ModelElement)modelElements.get( i );
    }

    /**
     * Determine whether this BaseModel contains the specified ModelElement.
     *
     * @param modelElement
     * @return true if this BaseModel contains the specified ModelElement.
     */
    public boolean containsModelElement( ModelElement modelElement ) {
        return modelElements.contains( modelElement );
    }

    /**
     * Gets the number of ModelElements in this BaseModel.
     *
     * @return the number of ModelElements in this BaseModel.
     */
    public int numModelElements() {
        return modelElements.size();
    }

    /**
     * Remove the specified ModelElement from this BaseModel
     *
     * @param m
     */
    public void removeModelElement( ModelElement m ) {
        modelElements.remove( m );
    }

    /**
     * Clears all ModelElements from this BaseModel.
     */
    public void removeAllModelElements() {
        modelElements.clear();
    }

    /**
     * Enqueue a command which will be executed on the next update.
     *
     * @param cmd
     */
    public synchronized void execute( Command cmd ) {
        commandList.addCommand( cmd );
    }

    /**
     * Update this BaseModel, running any commands on the queue, and stepping all the ModelElements.
     *
     * @param event the source event for this update.
     */
    public void update( ClockEvent event ) {
        commandList.doIt();
        double simulationTimeChange = event.getSimulationTimeChange();
        for( int i = 0; i < numModelElements(); i++ ) {
            modelElementAt( i ).stepInTime( simulationTimeChange );
        }
    }

}
