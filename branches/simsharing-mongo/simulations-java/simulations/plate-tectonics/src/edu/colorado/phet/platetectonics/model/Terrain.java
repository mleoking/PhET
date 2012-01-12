// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;

/**
 * Represents a terrain within the pseudospherical coordinate frame. Specifically,
 * it is a grid of points in the x-z plane where row has the same x coordinate, each
 * column has the same z coordinate, and each point has its own height data.
 */
public class Terrain {
    // how many vertices are in a row
    public final int numXSamples;

    // how many vertices are in a column
    public final int numZSamples;

    // the X positions of each column
    public final float[] xData;

    // the Z positions of each row
    public final float[] zData;

    // the Y positions for the entire grid (row-major order)
    public final float[] elevationData;

    public Terrain( int numXSamples, int numZSamples ) {
        this.numXSamples = numXSamples;
        this.numZSamples = numZSamples;

        xData = new float[numXSamples];
        zData = new float[numZSamples];
        elevationData = new float[numXSamples * numZSamples];
    }

    // the index that refers to the front of the terrain (where the cross-section is)
    public int getFrontZIndex() {
        return zData.length - 1;
    }

    // sets the elevation for a particular position in the grid
    public void setElevation( float elevation, int xIndex, int zIndex ) {
        elevationData[zIndex * numXSamples + xIndex] = elevation;
    }

    // gets the elevation for a particular position in the grid
    public float getElevation( int xIndex, int zIndex ) {
        return elevationData[zIndex * numXSamples + xIndex];
    }

    // get the pseudospherical position for a vertex in the grid (before handling the roundness of earth)
    public ImmutableVector3F getSphericalPoint( int xIndex, int zIndex ) {
        return new ImmutableVector3F( xData[xIndex], getElevation( xIndex, zIndex ), zData[zIndex] );
    }

    // get the cartesian position for a vertex in the grid (after handling the roundness of the earth)
    public ImmutableVector3F getCartesianPoint( int xIndex, int zIndex ) {
        return PlateModel.convertToRadial( getSphericalPoint( xIndex, zIndex ) );
    }

    // set the X coordinates to linearly interpolate between the min and max
    public void setXBounds( float xMin, float xMax ) {
        // simple interpolation
        for ( int i = 0; i < numXSamples; i++ ) {
            xData[i] = xMin + ( (float) i ) / ( (float) ( numXSamples - 1 ) ) * ( xMax - xMin );
        }
    }

    // set the Z coordinates to linearly interpolate between the min and max
    public void setZBounds( float zMin, float zMax ) {
        // simple interpolation
        for ( int i = 0; i < numZSamples; i++ ) {
            zData[i] = zMin + ( (float) i ) / ( (float) ( numZSamples - 1 ) ) * ( zMax - zMin );
        }
    }

    // whether water should be displayed over this particular section of terrain
    public boolean hasWater() {
        return true;
    }

    public ImmutableVector2F[] getFrontVertices() {
        ImmutableVector2F[] result = new ImmutableVector2F[numXSamples];
        int zIndex = getFrontZIndex();
        for ( int xIndex = 0; xIndex < numXSamples; xIndex++ ) {
            result[xIndex] = new ImmutableVector2F( xData[xIndex], getElevation( xIndex, zIndex ) );
        }
        return result;
    }
}
