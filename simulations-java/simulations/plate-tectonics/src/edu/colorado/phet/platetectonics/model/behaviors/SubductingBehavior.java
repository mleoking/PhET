// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.behaviors;

import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.model.PlateMotionPlate;
import edu.colorado.phet.platetectonics.model.PlateType;
import edu.colorado.phet.platetectonics.model.Sample;
import edu.colorado.phet.platetectonics.model.TerrainSample;
import edu.colorado.phet.platetectonics.model.regions.Boundary;
import edu.colorado.phet.platetectonics.model.regions.Region;
import edu.colorado.phet.platetectonics.util.Side;

public class SubductingBehavior extends PlateBehavior {

    private float timeElapsed = 0;

    private int regionToTerrainOffset = 0; // offset added to region column indices to get the corresponding terrain index

    public static final float PLATE_SPEED = 30000f / 2; // meters per millions of years

    public static final float MAX_HORIZONTAL_OFFSET = 40000;
    public static final float OFFSET_RATE = 0.4f;

    public SubductingBehavior( PlateMotionPlate plate, PlateMotionPlate otherPlate ) {
        super( plate, otherPlate );

        plate.getLithosphere().moveToFront();
        plate.getCrust().moveToFront();
    }

    @Override public void stepInTime( float millionsOfYears ) {

        timeElapsed += millionsOfYears;
        createEarthEdges();

        final int regionSize = getNumCrustXSamples();
        final int terrainSize = getNumTerrainXSamples();

        ColumnResult[] result = new ColumnResult[regionSize];
        for ( int i = 0; i < regionSize; i++ ) {
            final ImmutableVector2F offsetVector = new ImmutableVector2F(
                    getOffsetSize(),
                    0
            );
            result[i] = computeSubductingPosition( getT( i ), offsetVector );
        }

        /*---------------------------------------------------------------------------*
        * synchronizing terrain
        *----------------------------------------------------------------------------*/
        for ( int i = 0; i < regionSize; i++ ) {
            int regionIndex = i;
            int terrainIndex = i + regionToTerrainOffset;
            if ( terrainIndex < 0 || terrainIndex >= terrainSize ) {
                continue;
            }
            Sample sample = plate.getCrust().getTopBoundary().samples.get( i );
            ImmutableVector2F currentPosition = new ImmutableVector2F( sample.getPosition().x, sample.getPosition().y );
            ImmutableVector2F newPosition = result[i].crustTop;

            // in x-y, how we need to move
            ImmutableVector2F delta = newPosition.minus( currentPosition );

            plate.getTerrain().xPositions.set( terrainIndex, newPosition.x );

            for ( TerrainSample terrainSample : plate.getTerrain().getColumn( terrainIndex ) ) {
                terrainSample.setElevation( terrainSample.getElevation() + delta.y );
            }
        }

        /*---------------------------------------------------------------------------*
        * handle the terrain changes at the boundary
        *----------------------------------------------------------------------------*/
        if ( plate.getSide() == Side.RIGHT ) {
            while ( plate.getTerrain().xPositions.get( 0 ) < 0 ) {
                if ( plate.getTerrain().xPositions.get( 1 ) < 0 ) {
                    // remove the section of terrain
                    plate.getTerrain().removeColumn( Side.LEFT );
                    regionToTerrainOffset -= 1;
                }
                else {
                    final float xOffset = -plate.getTerrain().xPositions.get( 0 );
                    plate.getTerrain().shiftColumnXWithTexture( plate.getTextureStrategy(), 0, xOffset );
                    break;
                }
            }
        }
        else {
            while ( plate.getTerrain().xPositions.get( getNumTerrainXSamples() - 1 ) > 0 ) {
                if ( plate.getTerrain().xPositions.get( getNumTerrainXSamples() - 2 ) > 0 ) {
                    // remove the section of terrain
                    plate.getTerrain().removeColumn( Side.RIGHT );
                }
                else {
                    final float xOffset = -plate.getTerrain().xPositions.get( getNumTerrainXSamples() - 1 );
                    plate.getTerrain().shiftColumnXWithTexture( plate.getTextureStrategy(), getNumTerrainXSamples() - 1, xOffset );
                    break;
                }
            }
        }

        for ( Region region : new Region[] { plate.getCrust(), plate.getLithosphere() } ) {
            boolean isCrust = region == plate.getCrust();

            for ( int i = 0; i < regionSize; i++ ) {
                ImmutableVector2F top = isCrust ? result[i].crustTop : result[i].crustBottom;
                ImmutableVector2F bottom = isCrust ? result[i].crustBottom : result[i].lithosphereBottom;

                for ( int boundaryIndex = 0; boundaryIndex < region.getBoundaries().size(); boundaryIndex++ ) {
                    Boundary boundary = region.getBoundaries().get( boundaryIndex );

                    float ratio = ( (float) boundaryIndex ) / ( (float) ( region.getBoundaries().size() - 1 ) );

                    Sample sample = boundary.samples.get( i );

                    ImmutableVector3F newPosition = new ImmutableVector3F(
                            ( 1 - ratio ) * top.x + ( ratio ) * bottom.x,
                            ( 1 - ratio ) * top.y + ( ratio ) * bottom.y,
                            sample.getPosition().z
                    );

                    sample.setPosition( newPosition );
                }
            }
        }

        /*---------------------------------------------------------------------------*
        * fix boundary elevation (now that cross-section samples are correct
        *----------------------------------------------------------------------------*/
        {
            final float boundaryElevation = getBoundaryElevation();
            final int columnIndex = plate.getSide().opposite().getIndex( plate.getTerrain().getNumColumns() );
            plate.getTerrain().setColumnElevation( columnIndex, boundaryElevation );
        }


        getPlate().getTerrain().elevationChanged.updateListeners();
    }

    // TODO: bump these up a level and make extensive use of them!
    private int getNumCrustXSamples() {
        return plate.getCrust().getTopBoundary().samples.size();
    }

    private int getNumTerrainXSamples() {
        return plate.getTerrain().getNumColumns();
    }

    public float getBoundaryElevation() {
        Sample lastSample = null;
        for ( Sample sample : plate.getCrust().getTopBoundary().samples ) {
            // if we hit the boundary head-on, just return the elevation (prevents degenerate cases later on)
            if ( sample.getPosition().x == 0 ) {
                return sample.getPosition().y;
            }

            if ( lastSample != null ) {
                if ( sample.getPosition().x * lastSample.getPosition().x < 0 ) {
                    // sign change detected
                    return yInterceptAtX0BetweenPoints( sample.getPosition(), lastSample.getPosition() );
                }
            }
            lastSample = sample;
        }

        // bail with the default case of nothing has happened yet: return the inner-most sample's Y value
        System.out.println( "WARNING: using overridden y-intercept value for boundary elevation in subduction case" );
        return plate.getSide().opposite().getEnd( plate.getCrust().getTopBoundary().samples ).getPosition().y;
    }

    private static float yInterceptAtX0BetweenPoints( ImmutableVector3F a, ImmutableVector3F b ) {
        return a.y - a.x * ( b.y - a.y ) / ( b.x - a.x );
    }

    // NOTE: relies on slices not getting removed
    public float getT( int columnIndex ) {
        float pieceWidth = plate.getSimpleChunkWidth();
        float staticT = pieceWidth * ( plate.getSide() == Side.LEFT

                                       // for the left, we want the right-most column to have 0, then descending to the left
                                       ? -( getNumCrustXSamples() - 1 - columnIndex )

                                       // simplier for the right
                                       : -columnIndex );
        return staticT + timeElapsed * PLATE_SPEED + getOffsetSize();
    }

    private float getOffsetSize() {
        // the "old" crust is pushed back the fastest
        float base = timeElapsed * PLATE_SPEED * OFFSET_RATE * ( plate.getPlateType() == PlateType.OLD_OCEANIC ? 1.7f : 1 );
        if ( base > MAX_HORIZONTAL_OFFSET ) {
            return MAX_HORIZONTAL_OFFSET;
        }
        else {
            // add a quadratic blend from 0 to max over the first amount of time
            float ratioBefore = base / MAX_HORIZONTAL_OFFSET;
            float ratioAfter = -ratioBefore * ratioBefore + 2 * ratioBefore;
            return ratioAfter * MAX_HORIZONTAL_OFFSET;
        }
    }

    /*---------------------------------------------------------------------------*
    * new method of computing the subducting crust's position
    * see shapes.nb in the assets directory for more information and graphics
    *----------------------------------------------------------------------------*/

    public static class ColumnResult {
        public final ImmutableVector2F crustTop;
        public final ImmutableVector2F crustBottom;
        public final ImmutableVector2F lithosphereBottom;
        public final ImmutableVector2F lithosphereCenter;

        public ColumnResult( ImmutableVector2F crustTop, ImmutableVector2F crustBottom, ImmutableVector2F lithosphereBottom, ImmutableVector2F lithosphereCenter ) {
            this.crustTop = crustTop;
            this.crustBottom = crustBottom;
            this.lithosphereBottom = lithosphereBottom;
            this.lithosphereCenter = lithosphereCenter;
        }
    }

    private final float y0 = plate.getPlateType().getCrustTopY() - plate.getPlateType().getLithosphereThickness() / 2;
    private final float m = plate.getPlateType().getLithosphereThickness() / 2;
    private final float cb = plate.getPlateType().getLithosphereThickness() / 2 - plate.getPlateType().getCrustThickness();
    private final float totalAngle = (float) ( ( Math.PI / 4 ) * ( plate.getPlateType() == PlateType.OLD_OCEANIC ? 1.2 : 0.8 ) );

    private final float theta0 = totalAngle * 0.25f;
    private final float theta1 = totalAngle * 0.5f;
    private final float theta2 = totalAngle * 0.25f;

    private final float radius0 = 90000;
    private final float radius1 = 40000;
    private final float radius2 = 90000;

    private final float t0 = 0;
    private final float t1 = theta0 * radius0;
    private final float t2 = t1 + theta1 * radius1;
    private final float t3 = t2 + theta2 * radius2;

    private final ImmutableVector2F center0 = new ImmutableVector2F( 0, y0 - radius0 );

    private ImmutableVector2F p0( float t ) {
        return new ImmutableVector2F( -t, y0 );
    }

    private static final ImmutableVector2F value_pd0 = new ImmutableVector2F( -1, 0 );

    // TODO: refactor
    private ImmutableVector2F pd0( float t ) {
        return value_pd0;
    }

    private ImmutableVector2F p1( float t ) {
        float theta = (float) ( Math.PI / 2 + ( t - t0 ) / radius0 );
        return center0.plus( vectorFromAngle( theta ).times( radius0 ) );
    }

    private ImmutableVector2F pd1( float t ) {
        return bottomFromTangent( p1( t ).minus( center0 ).normalized() );
    }

    private final ImmutableVector2F center1 = p1( t1 ).plus( center0.minus( p1( t1 ) ).normalized().times( radius1 ) );

    private ImmutableVector2F p2( float t ) {
        float theta = (float) ( Math.PI / 2 + theta0 + ( t - t1 ) / radius1 );
        return center1.plus( vectorFromAngle( theta ).times( radius1 ) );
    }

    private ImmutableVector2F pd2( float t ) {
        return bottomFromTangent( p2( t ).minus( center1 ).normalized() );
    }

    private final ImmutableVector2F center2 = p2( t2 ).plus( center1.minus( p2( t2 ) ).normalized().times( radius2 ) );

    private ImmutableVector2F p3( float t ) {
        float theta = (float) ( Math.PI / 2 + theta0 + theta1 + ( t - t2 ) / radius2 );
        return center2.plus( vectorFromAngle( theta ).times( radius2 ) );
    }

    private ImmutableVector2F pd3( float t ) {
        return bottomFromTangent( p3( t ).minus( center2 ).normalized() );
    }

    private final ImmutableVector2F dir4 = vectorFromAngle( (float) ( totalAngle + Math.PI ) );

    private final ImmutableVector2F p3oft3 = p3( t3 );

    private ImmutableVector2F p4( float t ) {
        return p3oft3.plus( dir4.times( t - t3 ) );
    }

    private ImmutableVector2F pd4( float t ) {
        return dir4;
    }

    public ColumnResult computeSubductingPosition( float t, ImmutableVector2F offset ) {
        ImmutableVector2F position;
        ImmutableVector2F derivative;

        if ( t < t0 ) {
            position = p0( t );
            derivative = pd0( t );
        }
        else if ( t < t1 ) {
            position = p1( t );
            derivative = pd1( t );
        }
        else if ( t < t2 ) {
            position = p2( t );
            derivative = pd2( t );
        }
        else if ( t < t3 ) {
            position = p3( t );
            derivative = pd3( t );
        }
        else {
            position = p4( t );
            derivative = pd4( t );
        }

        ImmutableVector2F tangent = topFromTangent( derivative );

        // add in the offset
        position = position.plus( offset );

        // if the plate is the left side, we actually switch it here
        if ( plate.getSide() == Side.LEFT ) {
            position = new ImmutableVector2F( -position.x, position.y );
            tangent = new ImmutableVector2F( -tangent.x, tangent.y );
        }

        return new ColumnResult(
                position.plus( tangent.times( m ) ),
                position.plus( tangent.times( cb ) ),
                position.plus( tangent.times( -m ) ),
                position
        );
    }

    private static ImmutableVector2F topFromTangent( ImmutableVector2F v ) {
        return new ImmutableVector2F( v.y, -v.x );
    }

    private static ImmutableVector2F bottomFromTangent( ImmutableVector2F v ) {
        return topFromTangent( v ).negate();
    }

    private static ImmutableVector2F vectorFromAngle( float angle ) {
        return new ImmutableVector2F( Math.cos( angle ), Math.sin( angle ) );
    }
}
