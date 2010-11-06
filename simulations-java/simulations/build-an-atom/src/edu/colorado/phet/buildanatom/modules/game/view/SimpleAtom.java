package edu.colorado.phet.buildanatom.modules.game.view;

import edu.colorado.phet.buildanatom.model.IAtom;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

/**
 * This is the minimal interface required for atom identity, used in ToElementView and PeriodicTableNode.
 *
 * @author Sam Reid
 */
public class SimpleAtom extends SimpleObservable implements IAtom {
    private int numProtons = 0;

    public int getNumProtons() {
        return numProtons;
    }

    public void setNumProtons( int numProtons ) {
        this.numProtons = numProtons;
        notifyObservers();
    }
}