// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.behaviors;

import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.model.PlateMotionModel;
import edu.colorado.phet.platetectonics.model.PlateMotionPlate;
import edu.colorado.phet.platetectonics.model.PlateType;
import edu.colorado.phet.platetectonics.model.Sample;
import edu.colorado.phet.platetectonics.model.TerrainSample;
import edu.colorado.phet.platetectonics.model.regions.Boundary;
import edu.colorado.phet.platetectonics.model.regions.Region;
import edu.colorado.phet.platetectonics.util.Side;

import static edu.colorado.phet.platetectonics.model.behaviors.OverridingBehavior.*;

public class SubductingBehavior extends PlateBehavior {

    private float timeElapsed = 0;

    private int regionToTerrainOffset = 0; // offset added to region column indices to get the corresponding terrain index

    public static final float PLATE_SPEED = 30000f / 2; // meters per millions of years

    public static final float MAX_HORIZONTAL_OFFSET = 32000;
    public static final float OFFSET_RATE = 0.4f;

    public static final float BLEND_THRESHOLD_Y = -150000;
    public static final float BLEND_RATE_FACTOR = 0.08f;

    public SubductingBehavior( PlateMotionPlate plate, PlateMotionPlate otherPlate ) {
        super( plate, otherPlate );
    }

    @Override public void afterConstructionInit() {
        super.afterConstructionInit();

        // placed here so it happens for sure after the overriding behavior's constructor executes
        getLithosphere().moveToFront();
        getCrust().moveToFront();
        plate.getModel().frontBoundarySideNotifier.updateListeners( plate.getSide() );
    }

    @Override public void stepInTime( float millionsOfYears ) {

        timeElapsed += millionsOfYears;
        createEarthEdges();

        // for determining middle X values for subduction. even though it's not actively used during runtime, leave it so we can recalculate if necessary
        float summedMeltingPositions = 0;
        int quantityOfMeltingPositions = 0;

        /*---------------------------------------------------------------------------*
        * compute positions for the subducting crust
        *----------------------------------------------------------------------------*/
        ColumnResult[] result = new ColumnResult[getNumCrustXSamples()];
        for ( int columnIndex = 0; columnIndex < getNumCrustXSamples(); columnIndex++ ) {
            final ImmutableVector2F offsetVector = new ImmutableVector2F(
                    getOffsetSize(),
                    0
            );
            result[columnIndex] = computeSubductingPosition( getT( columnIndex ), offsetVector );
        }

        /*---------------------------------------------------------------------------*
        * synchronizing terrain
        *----------------------------------------------------------------------------*/
        for ( int regionIndex = 0; regionIndex < getNumCrustXSamples(); regionIndex++ ) {
            int terrainIndex = regionIndex + regionToTerrainOffset;
            if ( terrainIndex < 0 || terrainIndex >= getNumTerrainXSamples() ) {
                continue;
            }
            Sample sample = getCrust().getTopBoundary().samples.get( regionIndex );
            ImmutableVector2F currentPosition = new ImmutableVector2F( sample.getPosition().x, sample.getPosition().y );
            ImmutableVector2F newPosition = result[regionIndex].crustTop;

            // in x-y, how we need to move
            ImmutableVector2F delta = newPosition.minus( currentPosition );

            getTerrain().xPositions.set( terrainIndex, newPosition.x );

            for ( TerrainSample terrainSample : getTerrain().getColumn( terrainIndex ) ) {
                terrainSample.setElevation( terrainSample.getElevation() + delta.y );
            }
        }

        /*---------------------------------------------------------------------------*
        * handle the terrain changes at the boundary
        *----------------------------------------------------------------------------*/
        while ( getOppositeSide().isToSideOf( getTerrain().xPositions.get( getCenterTerrainIndex( 0 ) ), 0 ) ) {
            if ( getOppositeSide().isToSideOf( getTerrain().xPositions.get( getCenterTerrainIndex( 1 ) ), 0 ) ) {
                // remove the section of terrain
                getTerrain().removeColumn( getOppositeSide() );

                // so we can match crust column indices to terrain column indices.
                if ( getSide() == Side.RIGHT ) {
                    // we extracted from the left, so we need to subtract one in the future
                    regionToTerrainOffset -= 1;
                }
            }
            else {
                final float xOffset = -getTerrain().xPositions.get( getCenterTerrainIndex( 0 ) );
                getTerrain().shiftColumnXWithTexture( plate.getTextureStrategy(), getCenterTerrainIndex( 0 ), xOffset );
                break;
            }
        }

        /*---------------------------------------------------------------------------*
        * update all of our lithospheric cross-section sample positions
        *----------------------------------------------------------------------------*/
        for ( Region region : getLithosphereRegions() ) {
            boolean isCrust = region == getCrust();

            for ( int i = 0; i < getNumCrustXSamples(); i++ ) {
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

                    if ( newPosition.y < TOP_MELT_Y && newPosition.y > BOTTOM_MELT_Y ) {
                        summedMeltingPositions += newPosition.x;
                        quantityOfMeltingPositions++;
                    }

                    /*---------------------------------------------------------------------------*
                    * blend the temp/density with surrounding rock
                    *----------------------------------------------------------------------------*/

                    if ( newPosition.y < BLEND_THRESHOLD_Y ) {
                        float currentTemp = sample.getTemperature();
                        float currentDensity = sample.getDensity();

                        float targetTemp = PlateMotionModel.SIMPLE_MANTLE_BOTTOM_TEMP;
                        float targetDensity = PlateMotionModel.SIMPLE_MANTLE_DENSITY;

                        float blendRatio = (float) ( 1 - Math.exp( -millionsOfYears * BLEND_RATE_FACTOR ) );
                        sample.setDensity( currentDensity * ( 1 - blendRatio ) + targetDensity * blendRatio );
                        sample.setTemperature( currentTemp * ( 1 - blendRatio ) + targetTemp * blendRatio );
                    }
                }
            }
        }

        // print out melting X positions for use in OverridingBehavior
//        System.out.println( plate.getPlateType() + " => " + ( summedMeltingPositions / quantityOfMeltingPositions ) );

        /*---------------------------------------------------------------------------*
        * fix boundary elevation (now that cross-section samples are correct
        *----------------------------------------------------------------------------*/
        {
            final float boundaryElevation = getBoundaryElevation();
            final int columnIndex = getOppositeSide().getIndex( getNumTerrainXSamples() );
            getTerrain().setColumnElevation( columnIndex, boundaryElevation );
        }


        getPlate().getTerrain().elevationChanged.updateListeners();
    }

    // NOTE: will return null if there is no point
    public ImmutableVector2F getLowestMeltingLocation() {
        float bestY = Float.MAX_VALUE;
        ImmutableVector2F location = null;
        for ( Sample sample : getTopCrustBoundary().samples ) {
            float y = sample.getPosition().y;

            // melt padding added so we don't start creating melt from a single point, but only across an area
            if ( y < TOP_MELT_Y - MELT_PADDING_Y && y > BOTTOM_MELT_Y && y < bestY ) {
                bestY = y;
                location = new ImmutableVector2F( sample.getPosition().x, sample.getPosition().y );
            }
        }
        return location;
    }

    // NOTE: will return null if there is no point
    public ImmutableVector2F getHighestMeltingLocation() {
        float bestY = -Float.MAX_VALUE;
        ImmutableVector2F location = null;
        for ( Sample sample : getTopCrustBoundary().samples ) {
            float y = sample.getPosition().y;

            if ( y < TOP_MELT_Y && y > BOTTOM_MELT_Y && y > bestY ) {
                bestY = y;
                location = new ImmutableVector2F( sample.getPosition().x, sample.getPosition().y );
            }
        }
        return location;
    }

    private int getCenterTerrainIndex( int offset ) {
        return getOppositeSide().getFromIndex( getNumTerrainXSamples(), offset );
    }

    public float getBoundaryElevation() {
        Sample lastSample = null;
        for ( Sample sample : getCrust().getTopBoundary().samples ) {
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
        return getOppositeSide().getEnd( getCrust().getTopBoundary().samples ).getPosition().y;
    }

    private static float yInterceptAtX0BetweenPoints( ImmutableVector3F a, ImmutableVector3F b ) {
        return a.y - a.x * ( b.y - a.y ) / ( b.x - a.x );
    }

    // NOTE: relies on slices not getting removed
    public float getT( int columnIndex ) {
        float pieceWidth = plate.getSimpleChunkWidth();
        float staticT = pieceWidth * ( getSide() == Side.LEFT

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
        if ( getSide() == Side.LEFT ) {
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
