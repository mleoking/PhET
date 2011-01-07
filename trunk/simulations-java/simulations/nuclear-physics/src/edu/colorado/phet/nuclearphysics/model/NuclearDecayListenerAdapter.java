// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.nuclearphysics.common.model.NuclearDecayModelListener;

/**
 * Adapter pattern for the AlphaDecayListener.
 * 
 * @author John Blanco
 */
public class NuclearDecayListenerAdapter implements NuclearDecayModelListener {
    public void modelElementAdded(Object modelElement){}
    public void modelElementRemoved(Object modelElement){}
    public void nucleusTypeChanged(){}
    public void halfLifeChanged(){}
}
