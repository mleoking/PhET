// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.Ray3F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.model.event.ValueNotifier;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;
import edu.colorado.phet.platetectonics.util.Side;

/**
 * A rectangular grid of terrain vertices that represents the terrain on top of the earth. Each column has the same X value and each
 * row has the same Z value for performance needs (simplifies trigonometric calculations for radial transformations from O(rows*cols) to
 * O(rows+cols))
 * <p/>
 * The number of samples in the Z position is fixed, however the number of samples in the X is not (terrain can be added or removed from
 * the sides)
 */
public class Terrain {

    public final List<Float> xPositions = new ArrayList<Float>();
    public final List<Float> zPositions = new ArrayList<Float>();

    // indexed first by column (x), then by z
    private final List<List<TerrainSample>> samples = new ArrayList<List<TerrainSample>>();

    // nothing else besides elevation changed here (at least)
    public final ValueNotifier<Terrain> elevationChanged = new ValueNotifier<Terrain>( this );

    // the x or z values could have changed, so assume the worst (everything changed)
    public final ValueNotifier<Terrain> columnsModified = new ValueNotifier<Terrain>( this );

    // fired when the terrain is permanently removed from the model, so we can detach the necessary listeners
    public final ValueNotifier<Terrain> disposed = new ValueNotifier<Terrain>( this );

    // number of samples in the Z direction
    private final int zSamples;

    public Terrain( int zSamples, float initialMinZ, float initialMaxZ ) {
        this.zSamples = zSamples;
        for ( int i = 0; i < zSamples; i++ ) {
            float ratio = ( (float) ( i ) ) / ( (float) ( zSamples - 1 ) );
            zPositions.add( initialMinZ + ratio * ( initialMaxZ - initialMinZ ) );
        }
    }

    // the index that refers to the front of the terrain (where the cross-section is)
    public int getFrontZIndex() {
        return zSamples - 1;
    }

    public int getNumColumns() {
        return samples.size();
    }

    public int getNumRows() {
        return zSamples;
    }

    public TerrainSample getSample( int xIndex, int zIndex ) {
        return samples.get( xIndex ).get( zIndex );
    }

    public TerrainSample getFrontSample( int xIndex ) {
        return samples.get( xIndex ).get( getFrontZIndex() );
    }

    public int getNumberOfVertices() {
        return getNumColumns() * getNumRows();
    }

    public List<TerrainSample> getColumn( int xIndex ) {
        return samples.get( xIndex );
    }

    // add a column of terrain vertices to a side
    public void addColumn( Side side, float x, List<TerrainSample> column ) {
        assert column.size() == zSamples;
        side.addToList( samples, column );
        side.addToList( xPositions, x );

        columnsModified.updateListeners();
    }

    // remove a column of terran vertices
    public void removeColumn( Side side ) {
        side.removeFromList( samples );
        side.removeFromList( xPositions );

        columnsModified.updateListeners();
    }

    public int getZSamples() {
        return zSamples;
    }

    // whether water should be displayed over this particular section of terrain
    public boolean hasWater() {
        return true;
    }

    // an array of the vertices at z=0
    public Vector2F[] getFrontVertices() {
        Vector2F[] result = new Vector2F[getNumColumns()];
        int zIndex = getFrontZIndex();
        for ( int xIndex = 0; xIndex < getNumColumns(); xIndex++ ) {
            result[xIndex] = new Vector2F( xPositions.get( xIndex ), getSample( xIndex, zIndex ).getElevation() );
        }
        return result;
    }

    public void setToFlatElevation( float elevation ) {
        for ( List<TerrainSample> list : samples ) {
            for ( TerrainSample point : list ) {
                point.setElevation( elevation );
            }
        }
    }

    public void shiftColumnElevation( int columnIndex, float yOffset ) {
        for ( TerrainSample sample : getColumn( columnIndex ) ) {
            sample.setElevation( sample.getElevation() + yOffset );
        }
    }

    public void shiftColumnXWithTexture( TextureStrategy textureStrategy, int columnIndex, float xOffset ) {
        // shift the actual positions
        xPositions.set( columnIndex, xPositions.get( columnIndex ) + xOffset );

        for ( TerrainSample sample : getColumn( columnIndex ) ) {
            // only change the X part of the texture, not the Z portion
            sample.setTextureCoordinates( sample.getTextureCoordinates().plus( textureStrategy.mapTopDelta( new Vector2F( xOffset, 0 ) ) ) );
        }
    }

    public void shiftZ( float offset ) {
        for ( int i = 0; i < zPositions.size(); i++ ) {
            zPositions.set( i, zPositions.get( i ) + offset );
        }
    }

    public void setColumnElevation( int columnIndex, float elevation ) {
        for ( TerrainSample sample : getColumn( columnIndex ) ) {
            sample.setElevation( elevation );
        }
    }

    private Vector3F[] getViewSpaceRow( int zIndex, Vector3F[] xRadials, LWJGLTransform modelViewTransform ) {
        Vector3F[] result = new Vector3F[xPositions.size()];
        Vector3F zRadial = PlateTectonicsModel.getZRadialVector( zPositions.get( zIndex ) );
        for ( int xIndex = 0; xIndex < xPositions.size(); xIndex++ ) {
            float elevation = getSample( xIndex, zIndex ).getElevation();
            Vector3F cartesianModelVector = PlateTectonicsModel.convertToRadial( xRadials[xIndex], zRadial, elevation );
            Vector3F viewVector = modelViewTransform.transformPosition( cartesianModelVector );

            result[xIndex] = viewVector;
        }
        return result;
    }

    // returns the point in "planar" form, not radial. ray is implied as view space
    public Option<Vector3F> intersectWithRay( Ray3F ray, LWJGLTransform modelViewTransform ) {
        // used for faster trig calculations for positioning
        Vector3F[] xRadials = new Vector3F[xPositions.size()];

        for ( int i = 0; i < xPositions.size(); i++ ) {
            xRadials[i] = PlateTectonicsModel.getXRadialVector( xPositions.get( i ) );
        }

        // try intersecting from front to back
        // NOTE: this is reliant on the order of z indices decreasing
        int zIndex = zSamples - 1;
        Vector3F[] lastRow = getViewSpaceRow( zIndex, xRadials, modelViewTransform );
        while ( zIndex >= 1 ) {
            zIndex--;
            Vector3F[] nextRow = getViewSpaceRow( zIndex, xRadials, modelViewTransform );
            for ( int i = 1; i < xPositions.size(); i++ ) {
                Vector3F leftLast = lastRow[i - 1];
                Vector3F rightLast = lastRow[i];
                Vector3F leftNext = nextRow[i - 1];
                Vector3F rightNext = nextRow[i];

                // check two "triangles" for each quad
                Option<Vector3F> hit = ray.intersectWithTriangle( leftNext, rightNext, rightLast );
                if ( hit.isSome() ) {
                    return hit;
                }

                hit = ray.intersectWithTriangle( rightLast, leftLast, leftNext );
                if ( hit.isSome() ) {
                    return hit;
                }
            }
        }

        // no hit found
        return new None<Vector3F>();
    }
}
