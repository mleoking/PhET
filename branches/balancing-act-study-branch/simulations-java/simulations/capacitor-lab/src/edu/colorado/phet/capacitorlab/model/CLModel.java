// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.model;

/**
 * Base class for all models in the "Capacitor Lab" sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CLModel {

    private final WorldBounds worldBounds;

    public CLModel() {
        worldBounds = new WorldBounds();
    }

    public WorldBounds getWorldBounds() {
        return worldBounds;
    }
}
