/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.util.persistence;

/**
 * StateDescriptor
 * <p/>
 * Interface for an object that defines all the persistent information about a Persistent object. Each
 * concrete implementer of type Persistent should have an associated concrete implementer of StateDescriptor.
 * <p/>
 * Concrete subclasses must be Java Beans conformant. They must have a no-arguments constructor, and
 * public getters and setters for the information they hold.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface StateDescriptor {

    /**
     * Sets the state of a PersistentObject based on the information in the StateDescriptor
     * <p/>
     *
     * @param persistentObject
     */
    void setState( Persistent persistentObject );
}
