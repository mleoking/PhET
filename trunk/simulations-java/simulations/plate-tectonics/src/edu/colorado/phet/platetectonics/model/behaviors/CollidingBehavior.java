// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.behaviors;

import java.util.List;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.platetectonics.model.PlateMotionPlate;
import edu.colorado.phet.platetectonics.model.Sample;
import edu.colorado.phet.platetectonics.model.TerrainSample;
import edu.colorado.phet.platetectonics.model.regions.Boundary;
import edu.colorado.phet.platetectonics.model.regions.Region;
import edu.colorado.phet.platetectonics.util.Side;

/**
 * Plate behavior for continental collision (plates pushing towards each other and creating mountains)
 */
public class CollidingBehavior extends PlateBehavior {

    private float timeElapsed = 0;

    public CollidingBehavior( PlateMotionPlate plate, PlateMotionPlate otherPlate ) {
        super( plate, otherPlate );

        // move all parts of lithosphere in front of the mantle
        getLithosphere().moveToFront();
        getCrust().moveToFront();
        plate.getModel().frontBoundarySideNotifier.updateListeners( plate.getSide() );

        moveMantleTopTo( 0 );
    }

    @Override public void stepInTime( float millionsOfYears ) {
        timeElapsed += millionsOfYears;

        // create new earth at the far edge (if there is room)
        createEarthEdges();

        /*---------------------------------------------------------------------------*
        * Essentially what we do here is use a function (computeNewX) to map from old X values to new X values to determine how
        * our crust moves and compresses (does not pass x=0 boundary). We shift all x values based on the function's value, but then
        * we also preserve the area of each quadrant by increasing its height. The mountains grow out of this increased height, and we add
        * in some randomness to create that peaks and valleys look.
        *----------------------------------------------------------------------------*/
        float sign = -plate.getSide().getSign();
        final List<Sample> topSamples = getTopCrustBoundary().samples;
        final Boundary lithosphereBottomBoundary = getLithosphere().getBottomBoundary();
        final List<Sample> bottomSamples = lithosphereBottomBoundary.samples;

        // current X values for each column
        float[] oldXes = new float[topSamples.size()];

        // new X values for each column
        float[] newXes = new float[topSamples.size()];

        // current high/low points of the lithosphere
        float[] oldTopYs = new float[topSamples.size()];
        float[] oldBottomYs = new float[bottomSamples.size()];

        // and respective area of this slice (currently)
        float[] oldAreas = new float[topSamples.size() - 1];

        // and how much the X-change scales this current slice
        float[] scales = new float[topSamples.size()];

        // compute the above values
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

        // apply the transformations to the lithosphere and crust
        for ( Region region : new Region[]{getLithosphere(), getCrust()} ) {
            for ( int i = 0; i < getCrust().getTopBoundary().samples.size(); i++ ) {
                float centerY = ( getCrust().getTopBoundary().samples.get( i ).getPosition().y
                                  + getCrust().getBottomBoundary().samples.get( i ).getPosition().y ) / 2;
                for ( Boundary boundary : region.getBoundaries() ) {

                    Sample sample = boundary.samples.get( i );

                    final Vector3F currentPosition = sample.getPosition();
                    final float currentX = currentPosition.x;
                    final float currentY = currentPosition.y;
                    float newX = currentX == 0 ? 0 : computeNewX( millionsOfYears, sign, currentX );
                    float newY = ( currentY - centerY ) / scales[i] + centerY;
                    final float yOffset = newY - currentY;
                    final Vector3F offset3d = new Vector3F( newX - currentX, yOffset, 0 );
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

        // create some mountains! (magnify local terrain variation)
        for ( int col = 0; col < getCrust().getTopBoundary().samples.size(); col++ ) {
            for ( int row = 0; row < getTerrain().getNumRows(); row++ ) {
                final TerrainSample terrainSample = getPlate().getTerrain().getSample( col, row );
                float mountainRatio = (float) MathUtil.clamp( 0, ( terrainSample.getElevation() - 6000 ) / ( 13000 - 6000 ), 1 );

                // don't compute randomness each time, instead magnify the initial "random" terrain offsets
                float elevationOffset = mountainRatio * ( terrainSample.getRandomElevationOffset() );
                elevationOffset *= millionsOfYears / 5;
                terrainSample.setElevation( terrainSample.getElevation() + elevationOffset );
                if ( row == getPlate().getTerrain().getFrontZIndex() ) {
                    final Sample sample = getCrust().getTopBoundary().samples.get( col );
                    sample.setPosition( sample.getPosition().plus( new Vector3F( 0, elevationOffset, 0 ) ) );
                }
            }
        }

        // copy elevation from left plate to right plate on the center line (center needs to match up)
        if ( getSide() == Side.RIGHT ) {
            for ( int row = 0; row < getPlate().getTerrain().getNumRows(); row++ ) {
                final float elevation = getOtherPlate().getTerrain().getSample( getOtherPlate().getTerrain().getNumColumns() - 1, row ).getElevation();
                getTerrain().getSample( 0, row ).setElevation(
                        elevation );
                final Sample sample = getPlate().getCrust().getTopBoundary().samples.get( 0 );
                sample.setPosition( new Vector3F( sample.getPosition().x, elevation, sample.getPosition().z ) );
            }
        }

        getPlate().getTerrain().elevationChanged.updateListeners();

        // for now, mantle movement has been removed. may be back soon!
//        glueMantleTopToLithosphere( 750 );
//        redistributeMantle();
    }

    // we actually slow the continental speed down logarithmically, only starting at 3cm/year
    private float timeFormula( float time ) {
        float howFastToSlow = 10;
        return (float) ( howFastToSlow * Math.log( time / howFastToSlow + 1 ) );
    }

    // the difference in our nonlinear time shift
    private float timeModification( float millionsOfYears ) {
        float before = timeElapsed - millionsOfYears;
        float after = timeElapsed;

        return timeFormula( after ) - timeFormula( before );
    }

    // compute the new x position after millionsOfYears from current position
    private float computeNewX( float millionsOfYears, float sign, float currentX ) {
        millionsOfYears = timeModification( millionsOfYears );
        assert !Float.isNaN( millionsOfYears );

        // exponentials in this case are timestep-independent
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
