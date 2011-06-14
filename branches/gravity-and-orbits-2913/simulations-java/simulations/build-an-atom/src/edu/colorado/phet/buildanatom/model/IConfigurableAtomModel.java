/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.model;

/**
 * This interface is implemented by models that contain a single configurable
 * atom.  Through this interface, a user is able to obtain a reference to the
 * atom itself, which can be used in order to add a change listener.  This
 * interface also contains a method for setting the atom's configuration.  The
 * configuration of the atom should NOT set by directly configuring the atom
 * itself, since this may bypass notifications.
 *
 * @author John Blanco
 */
public interface IConfigurableAtomModel {
    IDynamicAtom getAtom();
    void setAtomConfiguration( IAtom atom );
}
