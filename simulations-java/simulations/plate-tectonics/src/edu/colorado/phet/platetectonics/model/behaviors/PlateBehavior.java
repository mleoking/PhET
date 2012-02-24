// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.behaviors;

import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.model.PlateMotionPlate;
import edu.colorado.phet.platetectonics.model.Sample;
import edu.colorado.phet.platetectonics.model.regions.Boundary;
import edu.colorado.phet.platetectonics.model.regions.Region;
import edu.colorado.phet.platetectonics.util.Side;

import static edu.colorado.phet.platetectonics.util.Side.LEFT;
import static edu.colorado.phet.platetectonics.util.Side.RIGHT;

public abstract class PlateBehavior {
    public final PlateMotionPlate plate;
    public final PlateMotionPlate otherPlate;

    public static final float PLATE_X_LIMIT = 700000;

    public PlateBehavior( PlateMotionPlate plate, PlateMotionPlate otherPlate ) {
        this.plate = plate;
        this.otherPlate = otherPlate;
    }

    public abstract void stepInTime( float millionsOfYears );

    public PlateMotionPlate getOtherPlate() {
        return otherPlate;
    }

    public PlateMotionPlate getPlate() {
        return plate;
    }

    protected void createEarthEdges() {
        while ( getPlate().isLeftPlate() && getPlate().getCrust().getTopBoundary().getEdgeSample( Side.LEFT ).getPosition().x > -PlateBehavior.PLATE_X_LIMIT ) {
            getPlate().addSection( LEFT );
//        final float width = model.getStartingX( LEFT, 1 ) - model.getStartingX( LEFT, 0 );
//        final float xOffset = width * LEFT.getSign();
//        final float crustTopY = getFreshCrustTop( plateType );
//        final float crustMantleBoundaryY = getFreshCrustBottom( plateType );
//        final float lithosphereBottomY = getFreshLithosphereBottom( plateType );
//
//        final float x = getCrust().getTopBoundary().getEdgeSample( LEFT ).getPosition().x + xOffset;
//
//        // TODO: clean up code duplication
//        getCrust().addColumn( LEFT, new ArrayList<Sample>() {{
//            float topY = crustTopY;
//            float bottomY = crustMantleBoundaryY;
//            for ( int yIndex = 0; yIndex < getCrust().getBoundaries().size(); yIndex++ ) {
//                final Sample mySample = getCrust().getBoundaries().get( yIndex ).getEdgeSample( LEFT );
//
//                final float yRatio = ( (float) yIndex ) / ( (float) CRUST_VERTICAL_STRIPS );
//                float y = topY + ( bottomY - topY ) * yRatio;
//
//                float temp = getCrustTemperatureFromYRatio( yRatio );
//                final float x = mySample.getPosition().x + xOffset;
//                add( new Sample( new ImmutableVector3F( x, y, 0 ), temp, getFreshDensity( plateType ),
//                                 mySample.getTextureCoordinates().plus( textureStrategy.mapFrontDelta( new ImmutableVector2F( xOffset, 0 ) ) ) ) );
//            }
//        }} );
//
//        getLithosphere().addColumn( LEFT, new ArrayList<Sample>() {{
//            float topY = crustMantleBoundaryY;
//            float bottomY = lithosphereBottomY;
//            for ( int yIndex = 0; yIndex < getLithosphere().getBoundaries().size(); yIndex++ ) {
//                final Sample mySample = getLithosphere().getBoundaries().get( yIndex ).getEdgeSample( LEFT );
//
//                final float yRatio = ( (float) yIndex ) / ( (float) LITHOSPHERE_VERTICAL_STRIPS );
//                float y = topY + ( bottomY - topY ) * yRatio;
//
//                float temp = getLithosphereTemperatureFromYRatio( yRatio );
//                final float x = mySample.getPosition().x + xOffset;
//                add( new Sample( new ImmutableVector3F( x, y, 0 ), temp, SIMPLE_MANTLE_DENSITY,
//                                 mySample.getTextureCoordinates().plus( textureStrategy.mapFrontDelta( new ImmutableVector2F( xOffset, 0 ) ) ) ) );
//            }
//        }} );
//
//        getTerrain().addColumn( LEFT, x, new ArrayList<TerrainSample>() {{
//            for ( int zIndex = 0; zIndex < getTerrain().getZSamples(); zIndex++ ) {
//                final TerrainSample mySample = getTerrain().getSample( LEFT.getIndex( getTerrain().getNumColumns() ), zIndex );
//                // elevation to be fixed later
//                // TODO: fix texture coordinates on newly added terrain
//                add( new TerrainSample( getCrust().getTopBoundary().getEdgeSample( LEFT ).getPosition().y, mySample.getTextureCoordinates().plus( textureStrategy.mapTopDelta( new ImmutableVector2F( xOffset, 0 ) ) ) ) );
//            }
//        }} );
        }
        while ( !getPlate().isLeftPlate() && getPlate().getCrust().getTopBoundary().getEdgeSample( Side.RIGHT ).getPosition().x < PlateBehavior.PLATE_X_LIMIT ) {
            getPlate().addSection( RIGHT );
//        final float width = model.getStartingX( RIGHT, 1 ) - model.getStartingX( RIGHT, 0 );
//        final float xOffset = width * RIGHT.getSign();
//        final float crustTopY = getFreshCrustTop( plateType );
//        final float crustMantleBoundaryY = getFreshCrustBottom( plateType );
//        final float lithosphereBottomY = getFreshLithosphereBottom( plateType );
//
//        final float x = getCrust().getTopBoundary().getEdgeSample( RIGHT ).getPosition().x + xOffset;
//
//        // TODO: clean up code duplication
//        getCrust().addColumn( RIGHT, new ArrayList<Sample>() {{
//            float topY = crustTopY;
//            float bottomY = crustMantleBoundaryY;
//            for ( int yIndex = 0; yIndex < getCrust().getBoundaries().size(); yIndex++ ) {
//                final Sample mySample = getCrust().getBoundaries().get( yIndex ).getEdgeSample( RIGHT );
//
//                final float yRatio = ( (float) yIndex ) / ( (float) CRUST_VERTICAL_STRIPS );
//                float y = topY + ( bottomY - topY ) * yRatio;
//
//                float temp = getCrustTemperatureFromYRatio( yRatio );
//                final float x = mySample.getPosition().x + xOffset;
//                add( new Sample( new ImmutableVector3F( x, y, 0 ), temp, getFreshDensity( plateType ),
//                                 mySample.getTextureCoordinates().plus( textureStrategy.mapFrontDelta( new ImmutableVector2F( xOffset, 0 ) ) ) ) );
//            }
//        }} );
//
//        getLithosphere().addColumn( RIGHT, new ArrayList<Sample>() {{
//            float topY = crustMantleBoundaryY;
//            float bottomY = lithosphereBottomY;
//            for ( int yIndex = 0; yIndex < getLithosphere().getBoundaries().size(); yIndex++ ) {
//                final Sample mySample = getLithosphere().getBoundaries().get( yIndex ).getEdgeSample( RIGHT );
//
//                final float yRatio = ( (float) yIndex ) / ( (float) LITHOSPHERE_VERTICAL_STRIPS );
//                float y = topY + ( bottomY - topY ) * yRatio;
//
//                float temp = getLithosphereTemperatureFromYRatio( yRatio );
//                final float x = mySample.getPosition().x + xOffset;
//                add( new Sample( new ImmutableVector3F( x, y, 0 ), temp, SIMPLE_MANTLE_DENSITY,
//                                 mySample.getTextureCoordinates().plus( textureStrategy.mapFrontDelta( new ImmutableVector2F( xOffset, 0 ) ) ) ) );
//            }
//        }} );
//
//        getTerrain().addColumn( RIGHT, x, new ArrayList<TerrainSample>() {{
//            for ( int zIndex = 0; zIndex < getTerrain().getZSamples(); zIndex++ ) {
//                final TerrainSample mySample = getTerrain().getSample( RIGHT.getIndex( getTerrain().getNumColumns() ), zIndex );
//                // elevation to be fixed later
//                // TODO: fix texture coordinates on newly added terrain
//                add( new TerrainSample( getCrust().getTopBoundary().getEdgeSample( RIGHT ).getPosition().y, mySample.getTextureCoordinates().plus( textureStrategy.mapTopDelta( new ImmutableVector2F( xOffset, 0 ) ) ) ) );
//            }
//        }} );
        }
    }

    protected void removeEarthEdges() {
        while ( getPlate().isLeftPlate() && getPlate().getCrust().getTopBoundary().getEdgeSample( Side.LEFT ).getPosition().x < -PlateBehavior.PLATE_X_LIMIT ) {
            getPlate().removeSection( LEFT );
        }
        while ( !getPlate().isLeftPlate() && getPlate().getCrust().getTopBoundary().getEdgeSample( Side.RIGHT ).getPosition().x > PlateBehavior.PLATE_X_LIMIT ) {
            getPlate().removeSection( RIGHT );
        }
    }

    protected void glueMantleTopToLithosphere( float verticalPadding ) {
        int xIndex = 0;
        final Boundary lithosphereBottomBoundary = getPlate().getLithosphere().getBottomBoundary();
        Sample leftSample = lithosphereBottomBoundary.getEdgeSample( Side.LEFT );
        for ( Sample mantleSample : getPlate().getMantle().getTopBoundary().samples ) {
            // too far to the left
            if ( leftSample.getPosition().x > mantleSample.getPosition().x ) {
                continue;
            }

            int rightIndex = xIndex + 1;

            // too far to the right
            if ( rightIndex > lithosphereBottomBoundary.samples.size() - 1 ) {
                break;
            }
            Sample rightSample = lithosphereBottomBoundary.samples.get( rightIndex );
            while ( rightSample.getPosition().x < mantleSample.getPosition().x && rightIndex + 1 < lithosphereBottomBoundary.samples.size() ) {
                rightIndex++;
                rightSample = lithosphereBottomBoundary.samples.get( rightIndex );
            }

            // couldn't go far enough
            if ( rightSample.getPosition().x < mantleSample.getPosition().x ) {
                break;
            }
            leftSample = lithosphereBottomBoundary.samples.get( rightIndex - 1 );

            // how leftSample and rightSample surround our x
            assert leftSample.getPosition().x <= mantleSample.getPosition().x;
            assert rightSample.getPosition().x >= mantleSample.getPosition().x;

            // interpolate between their y values
            float ratio = ( mantleSample.getPosition().x - leftSample.getPosition().x ) / ( rightSample.getPosition().x - leftSample.getPosition().x );
            assert ratio >= 0;
            assert ratio <= 1;
            mantleSample.setPosition( new ImmutableVector3F( mantleSample.getPosition().x,
                                                             verticalPadding + leftSample.getPosition().y * ( 1 - ratio ) + rightSample.getPosition().y * ratio,
                                                             mantleSample.getPosition().z ) );
        }
    }

    protected void redistributeMantle() {
        Region mantle = getPlate().getMantle();
        // evenly distribute the asthenosphere mantle samples from top to bottom
        for ( int xIndex = 0; xIndex < mantle.getTopBoundary().samples.size(); xIndex++ ) {
            float topY = mantle.getTopBoundary().samples.get( xIndex ).getPosition().y;
            float bottomY = mantle.getBottomBoundary().samples.get( xIndex ).getPosition().y;

            // iterate over the interior boundaries (not including the top and bottom)
            for ( int yIndex = 1; yIndex < mantle.getBoundaries().size() - 1; yIndex++ ) {
                float ratioToBottom = ( (float) yIndex ) / ( (float) ( mantle.getBoundaries().size() - 1 ) );
                final Sample sample = mantle.getBoundaries().get( yIndex ).samples.get( xIndex );

                // interpolate Y between top and bottom
                sample.setPosition( new ImmutableVector3F( sample.getPosition().x,
                                                           topY * ( 1 - ratioToBottom ) + bottomY * ratioToBottom,
                                                           sample.getPosition().z ) );
            }
        }
    }
}
