/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas.collision;

/**
 * NullFixupStrategy
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class NullFixupStrategy implements WallFixupStrategy {
    public void fixup( Wall wall, SphericalBody sphere ) {
        // noop
    }
}
