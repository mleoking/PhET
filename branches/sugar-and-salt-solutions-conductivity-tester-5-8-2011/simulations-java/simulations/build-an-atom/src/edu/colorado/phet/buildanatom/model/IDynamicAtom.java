/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.model;


/**
 * Interface for atoms that can change their configuration, e.g. can gain an
 * electron.
 *
 * @author John Blanco
 */
public interface IDynamicAtom extends IAtom {

    /**
     * Register for notifications of changes to this atom's configuration,
     * position, or any other dynamic aspects of it.  See the definition of
     * the listener for details of what exactly can change.
     *
     * @param atomListener
     */
    void addAtomListener( AtomListener atomListener );

    /**
     * Get a static snapshot that represents the current configuration of the
     * atom.
     *
     * @return
     */
    ImmutableAtom toImmutableAtom();
}
