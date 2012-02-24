// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.behaviors;

import java.util.List;

import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.model.PlateMotionPlate;
import edu.colorado.phet.platetectonics.model.Sample;
import edu.colorado.phet.platetectonics.model.TerrainSample;
import edu.colorado.phet.platetectonics.model.regions.Boundary;
import edu.colorado.phet.platetectonics.model.regions.Region;

public class RiftingBehavior extends PlateBehavior {

    private float timeElapsed = 0;

    public static final float RIDGE_TOP_Y = -2500;
    public static final float RIDGE_START_Y = -400000; // 400km
    public static final float SPREAD_START_TIME = 10.0f;

    public RiftingBehavior( PlateMotionPlate plate, PlateMotionPlate otherPlate ) {
        super( plate, otherPlate );
    }

    @Override public void stepInTime( float millionsOfYears ) {
        final float timeBeforeSpreading = Math.max( 0, SPREAD_START_TIME - timeElapsed );
        float stretchTimeDelta = Math.min( timeBeforeSpreading, millionsOfYears );
        float spreadTimeDelta = millionsOfYears - stretchTimeDelta;

        timeElapsed += millionsOfYears;
        System.out.println( "elapsed: " + timeElapsed );

        if ( stretchTimeDelta > 0 ) {
            moveStretched( stretchTimeDelta );
        }
        if ( spreadTimeDelta > 0 ) {
            // TODO: do spreading animation here
        }
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

        for ( Region region : new Region[] { getPlate().getLithosphere(), getPlate().getCrust(), getPlate().getMantle() } ) {
            for ( Boundary boundary : region.getBoundaries() ) {
                Sample centerSample = getCenterSample( boundary );

                float y = centerSample.getPosition().y;
                float currentX = centerSample.getPosition().x;
                float newX = getPlumeX( y ) * ( getPlate().isLeftPlate() ? -1 : 1 );
                float deltaX = newX - currentX;

                if ( deltaX != 0 ) {
                    for ( Sample sample : boundary.samples ) {
                        sample.setPosition( sample.getPosition().plus( new ImmutableVector3F( deltaX, 0, 0 ) ) );
                    }
                }
            }
        }

        getPlate().getTerrain().elevationChanged.updateListeners();

        glueMantleTopToLithosphere( 1000 );
        redistributeMantle();
    }

    private float computeNewStretchedX( float millionsOfYears, float sign, float currentX ) {
        assert !Float.isNaN( millionsOfYears );
        final int exponentialFactor = 10;
        final float maxXDelta = -sign * 20000f / 2 * millionsOfYears;
        float delta = (float) ( ( 1 / Math.exp( -millionsOfYears / exponentialFactor ) ) - 1 ) * currentX;

        delta *= 2.0;

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

    private float getPlumeX( float y ) {
        float fromTop = y - getPlumeTop();

        // above plume, it has no width
        if ( fromTop > 0 ) {
            return 0;
        }

//        return ( y * y - y ) * 0.000001f;
        return Math.abs( fromTop / 3 );
    }

    private Sample getCenterSample( Boundary boundary ) {
        return getPlate().isLeftPlate() ? boundary.getLastSample() : boundary.getFirstSample();
    }
}
