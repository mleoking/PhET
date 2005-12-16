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
 * This class is encompasses all the model elements in a physical system. It provides
 * an architecture for executing commands in the model's thread.
 * <p/>
 * Typically, each Module in an application will have its own instance of this
 * class, or a subclass. The application's single ApplicationModel instance will
 * be told which BaseModel is active when Modules are activated.
 *
 * @author ?
 * @version $Revision$
 */
public class BaseModel {

    private CommandQueue commandList = new CommandQueue();
    private ArrayList modelElements = new ArrayList();

    public void addModelElement( ModelElement aps ) {
        modelElements.add( aps );
    }

    public ModelElement modelElementAt( int i ) {
        return (ModelElement)modelElements.get( i );
    }

    public boolean containsModelElement( ModelElement modelElement ) {
        return modelElements.contains( modelElement );
    }

    public int numModelElements() {
        return modelElements.size();
    }

    public void removeModelElement( ModelElement m ) {
        modelElements.remove( m );
    }

    public void removeAllModelElements() {
        modelElements.clear();
    }

    //Not allowed to mess with the way we call our abstract method.
    public void stepInTime( double dt ) {
        commandList.doIt();
        for( int i = 0; i < numModelElements(); i++ ) {
            modelElementAt( i ).stepInTime( dt );
        }
    }

    /**
     * Executes a command on the model. If the model's clock is running, the command
     * is placed on its command queue so that it will be executed the next time
     * the model thread ticks.
     */
    public synchronized void execute( Command cmd ) {
        commandList.addCommand( cmd );
    }

    public void clockTicked( ClockEvent event ) {
        stepInTime( event.getSimulationTimeChange() );
    }

}
