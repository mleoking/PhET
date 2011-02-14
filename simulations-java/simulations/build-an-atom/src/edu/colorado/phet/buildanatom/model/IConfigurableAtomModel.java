/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.model;

/**
 * This interface is implemented by models that contain a single configurable
 * atom.  Through this interface, an user is able to obtain a reference to the
 * atom itself (which is generally done to add a listener) and to set the
 * configuration of the atom.  The configuration of the atom is NOT set by
 * setting the configuration of the atom directly.  This is necessary to allow
 * the models to have different underlying atom implementations.
 *
 * @author John Blanco
 */
public interface IConfigurableAtomModel {
    IDynamicAtom getAtom();
    void setAtomConfiguration( IAtom atom );
}
