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

/**
 * A Command for invoking in the BaseModel update operation.
 *
 * @author ?
 * @version $Revision$
 */
public interface Command {

    /**
     * Invokes this Command.
     */
    void doIt();
}
