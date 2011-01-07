/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.model;

import edu.colorado.phet.buildanatom.modules.game.model.AtomValue;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Interface for atoms that can change their configuration, e.g. can gain an
 * electron.
 *
 * @author John Blanco
 */
public interface IDynamicAtom extends IAtom {

    /**
     * Register for notifications of changes to this atom's configuration.
     *
     * @param simpleObserver
     */
    void addObserver( SimpleObserver simpleObserver );

    /**
     * Get a static snapshot that represents the current configuration of the
     * atom.
     *
     * @return
     */
    AtomValue toImmutableAtom();
}
