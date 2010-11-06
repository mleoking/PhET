package edu.colorado.phet.buildanatom.model;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
* @author Sam Reid
*/
public interface IAtom {
    int getNumProtons();
    void addObserver( SimpleObserver simpleObserver );
}
