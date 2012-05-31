// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.event.ValueNotifier;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;
import edu.colorado.phet.lwjglphet.math.Ray3F;
import edu.colorado.phet.platetectonics.util.Side;

public class Terrain {

    public final List<Float> xPositions = new ArrayList<Float>();
    public final List<Float> zPositions = new ArrayList<Float>();

    // indexed first by column (x), then by z
    private final List<List<TerrainSample>> samples = new ArrayList<List<TerrainSample>>();

    // nothing else besides elevation changed here (at least)
    public final ValueNotifier<Terrain> elevationChanged = new ValueNotifier<Terrain>( this );

    public final ValueNotifier<Terrain> columnsModified = new ValueNotifier<Terrain>( this );

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

    public void addColumn( Side side, float x, List<TerrainSample> column ) {
        assert column.size() == zSamples;
        side.addToList( samples, column );
        side.addToList( xPositions, x );

        columnsModified.updateListeners();
    }

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

    public ImmutableVector2F[] getFrontVertices() {
        ImmutableVector2F[] result = new ImmutableVector2F[getNumColumns()];
        int zIndex = getFrontZIndex();
        for ( int xIndex = 0; xIndex < getNumColumns(); xIndex++ ) {
            result[xIndex] = new ImmutableVector2F( xPositions.get( xIndex ), getSample( xIndex, zIndex ).getElevation() );
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
            sample.setTextureCoordinates( sample.getTextureCoordinates().plus( textureStrategy.mapTopDelta( new ImmutableVector2F( xOffset, 0 ) ) ) );
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

    private ImmutableVector3F[] getViewSpaceRow( int zIndex, ImmutableVector3F[] xRadials, LWJGLTransform modelViewTransform ) {
        ImmutableVector3F[] result = new ImmutableVector3F[xPositions.size()];
        ImmutableVector3F zRadial = PlateModel.getZRadialVector( zPositions.get( zIndex ) );
        for ( int xIndex = 0; xIndex < xPositions.size(); xIndex++ ) {
            float elevation = getSample( xIndex, zIndex ).getElevation();
            ImmutableVector3F cartesianModelVector = PlateModel.convertToRadial( xRadials[xIndex], zRadial, elevation );
            ImmutableVector3F viewVector = modelViewTransform.transformPosition( cartesianModelVector );

            result[xIndex] = viewVector;
        }
        return result;
    }

    // returns the point in "planar" form, not radial. ray is implied as view space
    public Option<ImmutableVector3F> intersectWithRay( Ray3F ray, LWJGLTransform modelViewTransform ) {
        // used for faster trig calculations for positioning
        ImmutableVector3F[] xRadials = new ImmutableVector3F[xPositions.size()];

        for ( int i = 0; i < xPositions.size(); i++ ) {
            xRadials[i] = PlateModel.getXRadialVector( xPositions.get( i ) );
        }

        // try intersecting from front to back
        // NOTE: this is reliant on the order of z indices decreasing
        int zIndex = zSamples - 1;
        ImmutableVector3F[] lastRow = getViewSpaceRow( zIndex, xRadials, modelViewTransform );
        while ( zIndex >= 1 ) {
            zIndex--;
            ImmutableVector3F[] nextRow = getViewSpaceRow( zIndex, xRadials, modelViewTransform );
            for ( int i = 1; i < xPositions.size(); i++ ) {
                ImmutableVector3F leftLast = lastRow[i - 1];
                ImmutableVector3F rightLast = lastRow[i];
                ImmutableVector3F leftNext = nextRow[i - 1];
                ImmutableVector3F rightNext = nextRow[i];

                // check two "triangles" for each quad
                Option<ImmutableVector3F> hit = ray.intersectWithTriangle( leftNext, rightNext, rightLast );
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
        return new None<ImmutableVector3F>();
    }
}
