// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model;

/**
 * Base class for all circuits.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractCircuit implements ICircuit {

    private final String displayName;

    protected AbstractCircuit( String displayName ) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
