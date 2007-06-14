/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/phetcommon/src/edu/colorado/phet/common/model/Command.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.8 $
 * Date modified : $Date: 2006/01/03 23:37:17 $
 */
package edu.colorado.phet.common.model;

/**
 * A Command for invoking in the BaseModel update operation.
 *
 * @author ?
 * @version $Revision: 1.8 $
 */
public interface Command {

    /**
     * Invokes this Command.
     */
    void doIt();
}
