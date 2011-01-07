// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.model;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

//TODO This is only needed by PeriodicTableNode.  Consider replacing this entire interface with a Property<Integer>.
/**
 * Interface implemented by all atoms.
 *
 * @author Sam Reid
 */
public interface IAtom {
    int getNumProtons();
    void addObserver( SimpleObserver simpleObserver );
}
