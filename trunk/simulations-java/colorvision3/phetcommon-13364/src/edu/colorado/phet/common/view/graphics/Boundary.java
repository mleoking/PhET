/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.graphics;

/**
 * A Boundary represents the area within which a graphic is controllable.
 * This is typically done in view coordinates.
 * 
 * @author ?
 * @version $Revision$
 */
public interface Boundary {
    /**
     * Determine whether this boundary contains the specified point.
     *
     * @param x
     * @param y
     * @return true if this boundary contains the specified point.
     */
    public boolean contains( int x, int y );
}
