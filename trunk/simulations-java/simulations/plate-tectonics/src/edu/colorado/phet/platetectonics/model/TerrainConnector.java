// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

/**
 * Describes a simple terrain as connecting the edges of two other terrains
 * <p/>
 * Left and right terrains to conenct should have the same number of Z samples,
 * and the Z samples should be equal to each other
 */
public class TerrainConnector extends Terrain {
    private final Terrain left;
    private final Terrain right;
    private final int samples;
    private final int leftXIndex;
    private final int rightXIndex = 0;

    public TerrainConnector( Terrain left, Terrain right, int samples ) {
        super( samples, left.numZSamples );
        this.left = left;
        this.right = right;
        this.samples = samples;

        // left and right need to have the same number of samples here
        assert left.numZSamples == right.numZSamples;

        leftXIndex = left.xData.length - 1;

        update();
    }

    public void update() {
        // get the X-data correct from each side
        setXBounds( left.xData[leftXIndex], right.xData[rightXIndex] );

        // copy over the Z data
        System.arraycopy( left.zData, 0, zData, 0, numZSamples );

        // copy over elevation data
        for ( int zIndex = 0; zIndex < numZSamples; zIndex++ ) {
            float leftElevation = left.getElevation( leftXIndex, zIndex );
            float rightElevation = right.getElevation( rightXIndex, zIndex );
            for ( int xIndex = 0; xIndex < numXSamples; xIndex++ ) {
                float elevation = leftElevation + ( (float) xIndex ) / ( (float) ( numXSamples - 1 ) ) * ( rightElevation - leftElevation );
                setElevation( elevation, xIndex, zIndex );
            }
        }
    }

    // don't display water over the connectors
    @Override public boolean hasWater() {
        return false;
    }
}
