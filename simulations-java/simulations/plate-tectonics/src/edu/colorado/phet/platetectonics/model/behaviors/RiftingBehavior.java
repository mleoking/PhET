// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.behaviors;

import java.util.List;

import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.model.PlateMotionModel;
import edu.colorado.phet.platetectonics.model.PlateMotionPlate;
import edu.colorado.phet.platetectonics.model.Sample;
import edu.colorado.phet.platetectonics.model.TerrainSample;
import edu.colorado.phet.platetectonics.model.regions.Boundary;
import edu.colorado.phet.platetectonics.model.regions.Region;

public class RiftingBehavior extends PlateBehavior {

    private Region magma;
    public static final int MAGMA_VERTICAL_SAMPLES = 20;

    private float timeElapsed = 0;

    public static final float RIDGE_TOP_Y = -2500;
    public static final float RIDGE_START_Y = -400000; // 400km
    public static final float SPREAD_START_TIME = 10.0f;

    public static final float MAGMA_PLUME_PADDING = 6000;

    // the height of the magma plume
    public static final float MAGMA_HEIGHT = RIDGE_TOP_Y - RIDGE_START_Y;

    public static final float MAGMA_SPEED = MAGMA_HEIGHT / SPREAD_START_TIME;

    public static final float MAGMA_TEXTURE_COORDINATE_X = 100000;

    public static final float RIFT_PLATE_SPEED = 30000f / 2;

    public RiftingBehavior( final PlateMotionPlate plate, PlateMotionPlate otherPlate ) {
        super( plate, otherPlate );

        magma = new Region( MAGMA_VERTICAL_SAMPLES, 2, new Function2<Integer, Integer, Sample>() {
            public Sample apply( Integer yIndex, Integer xIndex ) {
                float yRatio = ( (float) yIndex ) / ( (float) ( MAGMA_VERTICAL_SAMPLES - 1 ) );
                final float distanceFromPlumeTop = yRatio * MAGMA_HEIGHT;

                final boolean isCenterPoint = plate.isLeftPlate() == ( xIndex == 1 );
                float x = isCenterPoint
                          ? 0
                          : getPlumeXFromTop( -distanceFromPlumeTop ) * getPlateSign();
                final float textureX = isCenterPoint ? 0 : MAGMA_TEXTURE_COORDINATE_X * getPlateSign();
                float y = RIDGE_START_Y - distanceFromPlumeTop;

                final ImmutableVector2F textureCoordinates = plate.getTextureStrategy().mapFront( new ImmutableVector2F( textureX, y ) );
                return new Sample( new ImmutableVector3F( x, y, 0 ),
                                   PlateMotionModel.SIMPLE_MAGMA_TEMP, PlateMotionModel.SIMPLE_MAGMA_DENSITY,
                                   textureCoordinates ); // TODO: x-y coordinates offset based on relative width of plume, so we don't see that "shrinking" as it goes up
            }
        } );

        plate.regions.add( magma );
        magma.moveToFront();
    }

    @Override public void stepInTime( float millionsOfYears ) {
        final float timeBeforeSpreading = Math.max( 0, SPREAD_START_TIME - timeElapsed );
        float stretchTimeDelta = Math.min( timeBeforeSpreading, millionsOfYears );
        float spreadTimeDelta = millionsOfYears - stretchTimeDelta;

        timeElapsed += millionsOfYears;

        if ( stretchTimeDelta > 0 ) {
            moveStretched( stretchTimeDelta );
        }
        if ( spreadTimeDelta > 0 ) {
            moveSpreading( spreadTimeDelta );
        }
    }

    private float getPlateSign() {
        return plate.isLeftPlate() ? -1 : 1;
    }

    private void moveStretched( float millionsOfYears ) {
        float sign = plate.isLeftPlate() ? 1 : -1;
        final List<Sample> topSamples = getPlate().getCrust().getTopBoundary().samples;
        final Boundary lithosphereBottomBoundary = getPlate().getLithosphere().getBottomBoundary();
        final List<Sample> bottomSamples = lithosphereBottomBoundary.samples;
        float[] oldXes = new float[topSamples.size()];
        float[] newXes = new float[topSamples.size()];
        float[] oldTopYs = new float[topSamples.size()];
        float[] oldBottomYs = new float[bottomSamples.size()];
        float[] oldAreas = new float[topSamples.size() - 1];
        float[] scales = new float[topSamples.size()];
        for ( int i = 0; i < oldXes.length; i++ ) {
            oldXes[i] = topSamples.get( i ).getPosition().x;
            oldTopYs[i] = topSamples.get( i ).getPosition().y;
            oldBottomYs[i] = bottomSamples.get( i ).getPosition().y;
            newXes[i] = computeNewStretchedX( millionsOfYears, sign, oldXes[i] );
            if ( i != 0 ) {
                // width times average height
                oldAreas[i - 1] = ( oldXes[i] - oldXes[i - 1] ) * ( ( oldTopYs[i - 1] + oldTopYs[i] ) / 2 - ( oldBottomYs[i - 1] + oldBottomYs[i] ) / 2 );
            }
        }
        scales[0] = ( newXes[1] - newXes[0] ) / ( oldXes[1] - oldXes[0] );
        scales[scales.length - 1] = ( newXes[scales.length - 1] - newXes[scales.length - 2] ) / ( oldXes[scales.length - 1] - oldXes[scales.length - 2] );
        for ( int i = 1; i < scales.length - 1; i++ ) {
            float leftScale = ( newXes[i] - newXes[i - 1] ) / ( oldXes[i] - oldXes[i - 1] );
            float rightScale = ( newXes[i + 1] - newXes[i] ) / ( oldXes[i + 1] - oldXes[i] );
            scales[i] = ( leftScale + rightScale ) / 2;
        }
        final Region[] mobileRegions = { getPlate().getLithosphere(), getPlate().getCrust() };
        for ( Region region : mobileRegions ) {
            for ( int i = 0; i < getPlate().getCrust().getTopBoundary().samples.size(); i++ ) {
                float centerY = ( getPlate().getCrust().getTopBoundary().samples.get( i ).getPosition().y
                                  + getPlate().getCrust().getBottomBoundary().samples.get( i ).getPosition().y ) / 2;
                float targetY = RIDGE_TOP_Y;
                for ( Boundary boundary : region.getBoundaries() ) {

                    Sample sample = boundary.samples.get( i );

                    final ImmutableVector3F currentPosition = sample.getPosition();
                    final float currentX = currentPosition.x;
                    final float currentY = currentPosition.y;
                    float newX = currentX == 0 ? 0 : computeNewStretchedX( millionsOfYears, sign, currentX );
                    float newY = ( currentY - targetY ) / scales[i] + targetY;
                    final float yOffset = newY - currentY;
                    final ImmutableVector3F offset3d = new ImmutableVector3F( newX - currentX, yOffset, 0 );
                    sample.setPosition( currentPosition.plus( offset3d ) );

                    // kind of a weird hack, but it helps us store less amounts of massive information
                    if ( boundary == getPlate().getCrust().getTopBoundary() ) {
                        getPlate().getTerrain().xPositions.set( i, newX );
                        for ( int row = 0; row < getPlate().getTerrain().getNumRows(); row++ ) {
                            final TerrainSample terrainSample = getPlate().getTerrain().getSample( i, row );
                            terrainSample.setElevation( terrainSample.getElevation() + yOffset );
                        }
                    }
                }
            }
        }

        // push the earth away from the magma plume
        for ( Region region : new Region[] { getPlate().getLithosphere(), getPlate().getCrust(), getPlate().getMantle() } ) {
            for ( Boundary boundary : region.getBoundaries() ) {
                Sample centerSample = getCenterSample( boundary );

                float y = centerSample.getPosition().y;
                float currentX = centerSample.getPosition().x;
                float newX = getPaddedPlumeX( y, MAGMA_PLUME_PADDING ) * ( getPlate().isLeftPlate() ? -1 : 1 );
                float deltaX = newX - currentX;

                if ( deltaX != 0 ) {
                    for ( Sample sample : boundary.samples ) {
                        sample.setPosition( sample.getPosition().plus( new ImmutableVector3F( deltaX, 0, 0 ) ) );
                    }
                }
            }
        }

        // move the magma plume up
        for ( Sample magmaSample : magma.getSamples() ) {
            magmaSample.setPosition( magmaSample.getPosition().plus( new ImmutableVector3F( 0, MAGMA_SPEED * millionsOfYears, 0 ) ) );
        }

        riftPostProcess();
    }

    private void moveSpreading( float millionsOfYears ) {
        // animate magma plume texture
        for ( Sample magmaSample : magma.getSamples() ) {
            magmaSample.setTextureCoordinates( magmaSample.getTextureCoordinates().plus(
                    plate.getTextureStrategy().mapFrontDelta( new ImmutableVector2F( 0, -MAGMA_SPEED * millionsOfYears ) ) ) );
        }

        // move all of the lithosphere
        final Region[] mobileRegions = { getPlate().getLithosphere(), getPlate().getCrust() };
        for ( Region region : mobileRegions ) {
            for ( Sample sample : region.getSamples() ) {
                sample.setPosition( sample.getPosition().plus( new ImmutableVector3F( RIFT_PLATE_SPEED * getPlateSign() * millionsOfYears, 0, 0 ) ) );
            }
        }

        // synchronize the terrain with the crust top
        for ( int i = 0; i < getPlate().getCrust().getTopBoundary().samples.size(); i++ ) {
            Sample crustSample = getPlate().getCrust().getTopBoundary().samples.get( i );
            TerrainSample frontTerrainSample = getPlate().getTerrain().getSample( i, getPlate().getTerrain().getFrontZIndex() );

            float oldXPosition = getPlate().getTerrain().xPositions.get( i );
            ImmutableVector3F delta = crustSample.getPosition().minus( new ImmutableVector3F( oldXPosition,
                                                                                              frontTerrainSample.getElevation(), 0 ) );

            for ( int row = 0; row < getPlate().getTerrain().getNumRows(); row++ ) {
                final TerrainSample terrainSample = getPlate().getTerrain().getSample( i, row );
                terrainSample.setElevation( terrainSample.getElevation() + delta.y );
            }

            getPlate().getTerrain().xPositions.set( i, oldXPosition + delta.x );
        }

        riftPostProcess();
    }

    private void riftPostProcess() {
        getPlate().getTerrain().elevationChanged.updateListeners();

        glueMantleTopToLithosphere( 1000 );
        redistributeMantle();
    }

    // fun function full of magical constants!
    private float computeNewStretchedX( float millionsOfYears, float sign, float currentX ) {
        assert !Float.isNaN( millionsOfYears );
        final int exponentialFactor = 10;
        final float maxXDelta = -sign * RIFT_PLATE_SPEED * millionsOfYears;
        float delta = (float) ( ( 1 / Math.exp( -millionsOfYears / exponentialFactor ) ) - 1 ) * currentX;

        delta *= 1.3;

        // essentially blend the two together
        float power = 0.7f;
        delta = (float) ( Math.pow( Math.abs( delta ), power ) * Math.signum( delta ) * Math.pow( Math.abs( maxXDelta ), 1 - power ) );

        float newX = Math.abs( maxXDelta ) > Math.abs( delta ) ? currentX + delta : currentX + maxXDelta;

        assert !Float.isNaN( newX );
        return newX;
    }

    private float getPlumeTop() {
        if ( timeElapsed > SPREAD_START_TIME ) {
            return RIDGE_TOP_Y;
        }
        else {
            float ratio = timeElapsed / SPREAD_START_TIME;
            return RIDGE_START_Y * ( 1 - ratio ) + RIDGE_TOP_Y * ratio;
        }
    }

    // returns the absolute value x from an absolutely positioned y
    private float getPlumeX( float y ) {
        return getPlumeXFromTop( y - getPlumeTop() );
    }

    private float getPaddedPlumeX( float y, float xPadding ) {
        return Math.max( getPlumeX( y ) - xPadding, 0 );
    }

    // y from the top of the magma plume
    private float getPlumeXFromTop( float yFromTop ) {
        float max = getSimplifiedPlumeX( -MAGMA_HEIGHT );
        float x = getSimplifiedPlumeX( yFromTop );

        // add a quadratic curvature to it
        return x * x / max;
    }

    // a "linear" magma plume, which will be used in multiple ways later
    private float getSimplifiedPlumeX( float yFromTop ) {
        if ( yFromTop > 0 ) {
            return 0;
        }
        return -yFromTop / 3;
    }

    private Sample getCenterSample( Boundary boundary ) {
        return getPlate().isLeftPlate() ? boundary.getLastSample() : boundary.getFirstSample();
    }
}
