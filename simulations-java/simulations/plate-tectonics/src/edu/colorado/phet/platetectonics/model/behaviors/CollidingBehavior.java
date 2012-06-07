// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.behaviors;

import java.util.List;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.model.PlateMotionPlate;
import edu.colorado.phet.platetectonics.model.Sample;
import edu.colorado.phet.platetectonics.model.TerrainSample;
import edu.colorado.phet.platetectonics.model.regions.Boundary;
import edu.colorado.phet.platetectonics.model.regions.Region;
import edu.colorado.phet.platetectonics.util.Side;

public class CollidingBehavior extends PlateBehavior {

    private float timeElapsed = 0;

    public CollidingBehavior( PlateMotionPlate plate, PlateMotionPlate otherPlate ) {
        super( plate, otherPlate );

        getLithosphere().moveToFront();
        getCrust().moveToFront();

        moveMantleTopTo( 0 );
    }

    @Override public void stepInTime( float millionsOfYears ) {
        timeElapsed += millionsOfYears;
        createEarthEdges();

        float sign = -plate.getSide().getSign();
        final List<Sample> topSamples = getTopCrustBoundary().samples;
        final Boundary lithosphereBottomBoundary = getLithosphere().getBottomBoundary();
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
            newXes[i] = computeNewX( millionsOfYears, sign, oldXes[i] );
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
        for ( Region region : new Region[] { getLithosphere(), getPlate().getCrust() } ) {
            for ( int i = 0; i < getCrust().getTopBoundary().samples.size(); i++ ) {
                float centerY = ( getCrust().getTopBoundary().samples.get( i ).getPosition().y
                                  + getCrust().getBottomBoundary().samples.get( i ).getPosition().y ) / 2;
                for ( Boundary boundary : region.getBoundaries() ) {

                    Sample sample = boundary.samples.get( i );

                    final ImmutableVector3F currentPosition = sample.getPosition();
                    final float currentX = currentPosition.x;
                    final float currentY = currentPosition.y;
                    float newX = currentX == 0 ? 0 : computeNewX( millionsOfYears, sign, currentX );
                    float newY = ( currentY - centerY ) / scales[i] + centerY;
                    final float yOffset = newY - currentY;
                    final ImmutableVector3F offset3d = new ImmutableVector3F( newX - currentX, yOffset, 0 );
                    sample.setPosition( currentPosition.plus( offset3d ) );

                    // kind of a weird hack, but it helps us store less amounts of massive information
                    if ( boundary == getCrust().getTopBoundary() ) {
                        getTerrain().xPositions.set( i, newX );
                        for ( int row = 0; row < getTerrain().getNumRows(); row++ ) {
                            final TerrainSample terrainSample = getPlate().getTerrain().getSample( i, row );
                            terrainSample.setElevation( terrainSample.getElevation() + yOffset );
                        }
                    }
                }
            }
        }

        // create some mountains!
        for ( int col = 0; col < getCrust().getTopBoundary().samples.size(); col++ ) {
            for ( int row = 0; row < getTerrain().getNumRows(); row++ ) {
                final TerrainSample terrainSample = getPlate().getTerrain().getSample( col, row );
                float mountainRatio = (float) MathUtil.clamp( 0, ( terrainSample.getElevation() - 6000 ) / ( 13000 - 6000 ), 1 );
                float elevationOffset = mountainRatio * ( terrainSample.getRandomElevationOffset() );
                elevationOffset *= millionsOfYears / 5;
                terrainSample.setElevation( terrainSample.getElevation() + elevationOffset );
                if ( row == getPlate().getTerrain().getFrontZIndex() ) {
                    final Sample sample = getCrust().getTopBoundary().samples.get( col );
                    sample.setPosition( sample.getPosition().plus( new ImmutableVector3F( 0, elevationOffset, 0 ) ) );
                }
            }
        }

        // copy elevation from left plate to right plate on the center line
        if ( getSide() == Side.RIGHT ) {
            for ( int row = 0; row < getPlate().getTerrain().getNumRows(); row++ ) {
                final float elevation = getOtherPlate().getTerrain().getSample( getOtherPlate().getTerrain().getNumColumns() - 1, row ).getElevation();
                getTerrain().getSample( 0, row ).setElevation(
                        elevation );
                final Sample sample = getPlate().getCrust().getTopBoundary().samples.get( 0 );
                sample.setPosition( new ImmutableVector3F( sample.getPosition().x, elevation, sample.getPosition().z ) );
            }
        }

        getPlate().getTerrain().elevationChanged.updateListeners();

//        glueMantleTopToLithosphere( 750 );
//        redistributeMantle();

        // TODO: different terrain sync so we can handle height differences
//        getPlate().fullSyncTerrain();
    }

    // we actually slow the continental speed down logarithmically, only starting at 3cm/year
    private float timeFormula( float time ) {
        float howFastToSlow = 10;
        return (float) ( howFastToSlow * Math.log( time / howFastToSlow + 1 ) );
    }

    private float timeModification( float millionsOfYears ) {
        float before = timeElapsed - millionsOfYears;
        float after = timeElapsed;

        return timeFormula( after ) - timeFormula( before );
    }

    private float computeNewX( float millionsOfYears, float sign, float currentX ) {
        millionsOfYears = timeModification( millionsOfYears );
        assert !Float.isNaN( millionsOfYears );
        final int exponentialFactor = 30;
        float newX = (float) ( currentX * Math.exp( -millionsOfYears / exponentialFactor ) );
        final float maxXDelta = sign * 30000f / 2 * millionsOfYears;
        final float delta = newX - currentX;
        float ratio = Math.min( 1, Math.abs( currentX / 600000 ) );
        ratio *= 0.75;

        newX = currentX + ( 1 - ratio ) * delta + ratio * maxXDelta;

        assert !Float.isNaN( newX );
        return newX;
    }
}
