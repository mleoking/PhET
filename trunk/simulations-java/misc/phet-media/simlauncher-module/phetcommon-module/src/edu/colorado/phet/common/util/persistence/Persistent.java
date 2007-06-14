/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/phetcommon/src/edu/colorado/phet/common/util/persistence/Persistent.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: samreid $
 * Revision : $Revision: 1.4 $
 * Date modified : $Date: 2005/11/11 23:09:49 $
 */
package edu.colorado.phet.common.util.persistence;

/**
 * Persistent
 *
 * @author Ron LeMaster
 * @version $Revision: 1.4 $
 */
public interface Persistent {
    StateDescriptor getState();

    void setState( StateDescriptor stateDescriptor );
}
