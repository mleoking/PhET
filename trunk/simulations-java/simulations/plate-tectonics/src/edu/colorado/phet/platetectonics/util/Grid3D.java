// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.util;

//REVIEW lots of unused stuff in here, looks like only getBounds is used. Do you think other stuff will be useful in the future?
/**
 * 3D grid sampling handling for bounds and precomputation of samples
 */
public class Grid3D {
    private Bounds3D bounds;
    private int numXSamples;
    private int numYSamples;
    private int numZSamples;

    private float[] xSamples;
    private float[] ySamples;
    private float[] zSamples;

    /**
     * A sampling strategy that tries to get approximately cubic-shaped samples
     *
     * @param bounds                Bounds
     * @param approximateSampleSize Approximate width/height/depth of a sample
     */
    public Grid3D( Bounds3D bounds, float approximateSampleSize ) {
        this( bounds,
              (int) Math.ceil( bounds.getWidth() / approximateSampleSize ),
              (int) Math.ceil( bounds.getHeight() / approximateSampleSize ),
              (int) Math.ceil( bounds.getDepth() / approximateSampleSize )
        );
    }

    public Grid3D( Bounds3D bounds, int numXSamples, int numYSamples, int numZSamples ) {
        this.bounds = bounds;
        this.numXSamples = numXSamples;
        this.numYSamples = numYSamples;
        this.numZSamples = numZSamples;

        xSamples = new float[numXSamples];
        ySamples = new float[numYSamples];
        zSamples = new float[numZSamples];

        for ( int x = 0; x < numXSamples; x++ ) {
            float ratio = ( (float) x ) / ( (float) ( numXSamples - 1 ) ); // 0 to 1 inclusive, from min to max
            xSamples[x] = bounds.getMinX() + ratio * bounds.getWidth();
        }

        for ( int y = 0; y < numYSamples; y++ ) {
            float ratio = ( (float) y ) / ( (float) ( numYSamples - 1 ) ); // 0 to 1 inclusive, from min to max
            ySamples[y] = bounds.getMinY() + ratio * bounds.getHeight();
        }

        for ( int z = 0; z < numZSamples; z++ ) {
            float ratio = ( (float) z ) / ( (float) ( numZSamples - 1 ) ); // 0 to 1 inclusive, from min to max
            zSamples[z] = bounds.getMinZ() + ratio * bounds.getDepth();
        }
    }

    public Grid3D withSamples( int numXSamples, int numYSamples, int numZSamples ) {
        return new Grid3D( getBounds(), numXSamples, numYSamples, numZSamples );
    }

    public Bounds3D getBounds() {
        return bounds;
    }

    public int getNumXSamples() {
        return numXSamples;
    }

    public int getNumYSamples() {
        return numYSamples;
    }

    public int getNumZSamples() {
        return numZSamples;
    }

    public float[] getXSamples() {
        return xSamples;
    }

    public float[] getYSamples() {
        return ySamples;
    }

    public float[] getZSamples() {
        return zSamples;
    }

    public float getXSample( int index ) {
        return xSamples[index];
    }

    public float getYSample( int index ) {
        return ySamples[index];
    }

    public float getZSample( int index ) {
        return zSamples[index];
    }
}
