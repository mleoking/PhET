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
 * Persistent
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface Persistent {
    StateDescriptor getState();

    void setState( StateDescriptor stateDescriptor );
}
