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
 * WallFixupStrategy
 * <p/>
 * A way to fixup problems with a wall and sphere collision. Intended to be used as a last-ditch
 * effort to fix problems that can't be addressed through physically accurate model features.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface WallFixupStrategy {
    void fixup( Wall wall, SphericalBody sphere );
}
