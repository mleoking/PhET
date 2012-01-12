// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import java.util.Arrays;

/**
 * A terrain with a convenience function for setting the elevation all to the same value (i.e. to flatten it)
 */
public class FlatTerrain extends Terrain {
    public FlatTerrain( int numXSamples, int numZSamples ) {
        super( numXSamples, numZSamples );
    }

    public void setElevation( float elevation ) {
        Arrays.fill( elevationData, elevation );
    }
}
