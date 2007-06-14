/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/phetcommon/src/edu/colorado/phet/common/model/ModelElement.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.10 $
 * Date modified : $Date: 2006/01/03 23:37:18 $
 */
package edu.colorado.phet.common.model;


/**
 * An element of the BaseModel, notified when the simulation time changes.
 *
 * @author ?
 * @version $Revision: 1.10 $
 */
public interface ModelElement {
    public void stepInTime( double dt );
}
