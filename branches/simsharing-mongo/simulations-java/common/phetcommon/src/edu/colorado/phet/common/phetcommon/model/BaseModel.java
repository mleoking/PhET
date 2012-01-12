// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.phetcommon.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;

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
        return (ModelElement) modelElements.get( i );
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
        stepInTime( simulationTimeChange );
    }

    /**
     * Steps all the model elements. This needs to be provided as a protected method for backward compatibility
     * for older simulations
     *
     * @param dt
     */
    protected void stepInTime( double dt ) {
        for ( int i = 0; i < numModelElements(); i++ ) {
            modelElementAt( i ).stepInTime( dt );
        }
    }

    /**
     * Selects for elements assignable from ANY of the specified classes.
     *
     * @param modelElementClasses The classes or interfaces to select for.
     * @return All elements assignable from any of the specified classes.
     */
    public List selectForAny( Class[] modelElementClasses ) {
        List elements = new ArrayList();

        for ( int i = 0; i < modelElements.size(); i++ ) {
            for ( int j = 0; j < modelElementClasses.length; j++ ) {
                Class c = modelElementClasses[j];

                Object element = modelElements.get( i );

                if ( c.isAssignableFrom( element.getClass() ) ) {
                    elements.add( element );
                }
            }
        }

        return elements;
    }

    /**
     * Selects for all elements assignable from the specified class.
     *
     * @param modelElementClass The class of the element to select for.
     * @return All elements assignable from the specified class.
     */
    public List selectFor( Class modelElementClass ) {
        return selectFor( modelElements, modelElementClass );
    }

    /**
     * Selects for classes assignable from ALL of the specified interfaces.
     *
     * @param classes The classes.
     * @return All elements assignable from ALL of the specified classes.
     */
    public List selectFor( Class[] classes ) {
        List elements = new ArrayList( modelElements );

        for ( int i = 0; i < classes.length; i++ ) {
            Class c = classes[i];

            elements = selectFor( elements, c );
        }

        return elements;
    }

    private static List selectFor( List modelElements, Class modelElementClass ) {
        List elements = new ArrayList();

        for ( int i = 0; i < modelElements.size(); i++ ) {
            Object element = modelElements.get( i );

            if ( modelElementClass.isAssignableFrom( element.getClass() ) ) {
                elements.add( element );
            }
        }

        return elements;
    }
}
