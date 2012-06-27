// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.model.behaviors.PlateBehavior;
import edu.colorado.phet.platetectonics.model.behaviors.RiftingBehavior;
import edu.colorado.phet.platetectonics.model.behaviors.TransformBehavior;
import edu.colorado.phet.platetectonics.model.labels.BoundaryLabel;
import edu.colorado.phet.platetectonics.model.labels.RangeLabel;
import edu.colorado.phet.platetectonics.model.regions.Boundary;
import edu.colorado.phet.platetectonics.model.regions.Region;
import edu.colorado.phet.platetectonics.util.Side;

import static edu.colorado.phet.platetectonics.model.PlateMotionModel.*;
import static edu.colorado.phet.platetectonics.util.Side.LEFT;

public class PlateMotionPlate extends Plate {
    private final PlateMotionModel model;
    private final TextureStrategy textureStrategy;
    private final Side side;

    // main boundary label between the mantle and lithosphere for this plate
    private BoundaryLabel boundaryLabel;

    private PlateType plateType = null;

    private PlateBehavior behavior;

    public PlateMotionPlate( final PlateMotionModel model, final TextureStrategy textureStrategy, final Side side ) {
        this.model = model;
        this.textureStrategy = textureStrategy;
        this.side = side;
        addMantle( new Region( MANTLE_VERTICAL_STRIPS, HORIZONTAL_SAMPLES, new Function2<Integer, Integer, Sample>() {
            public Sample apply( Integer yIndex, Integer xIndex ) {
                return createMantleSample( xIndex, yIndex, MANTLE_VERTICAL_STRIPS,
                                           SIMPLE_MANTLE_TOP_Y, SIMPLE_MANTLE_BOTTOM_Y,
                                           SIMPLE_MANTLE_TOP_TEMP, SIMPLE_MANTLE_BOTTOM_TEMP );
            }
        } ) );
        addTerrain( textureStrategy, TERRAIN_DEPTH_SAMPLES, model.getBounds().getMinZ(), model.getBounds().getMaxZ() );
    }

    private Sample createMantleSample( int xIndex, int yIndex, int verticalSamples, float mantleTopY, float mantleBottomY, float mantleTopTemp, float mantleBottomTemp ) {
        // TODO: consider adding a higher concentration of samples near the top?
        float x = model.getStartingX( side, xIndex );
        final float yRatio = ( (float) yIndex ) / ( (float) verticalSamples );
        float y = mantleTopY + ( mantleBottomY - mantleTopY ) * yRatio;
        float temp = mantleTopTemp + ( mantleBottomTemp - mantleTopTemp ) * yRatio;
        return new Sample( new ImmutableVector3F( x, y, 0 ), temp, SIMPLE_MANTLE_DENSITY,
                           textureStrategy.mapFront( new ImmutableVector2F( x, y ) ) );
    }

    public void droppedCrust( final PlateType type ) {
        plateType = type;
        final float crustTopY = type.getCrustTopY();
        final float crustBottomY = type.getCrustBottomY();
        final float lithosphereBottomY = type.getLithosphereBottomY();

        final float crustDensity = type.getDensity();

        addLithosphere( new Region( LITHOSPHERE_VERTICAL_STRIPS, HORIZONTAL_SAMPLES, new Function2<Integer, Integer, Sample>() {
            public Sample apply( Integer yIndex, Integer xIndex ) {
                return createMantleSample( xIndex, yIndex, LITHOSPHERE_VERTICAL_STRIPS,
                                           crustBottomY, lithosphereBottomY,
                                           SIMPLE_MANTLE_TOP_TEMP, SIMPLE_LITHOSPHERE_BOUNDARY_TEMP );
            }
        } ) );

        addCrust( new Region( CRUST_VERTICAL_STRIPS, HORIZONTAL_SAMPLES, new Function2<Integer, Integer, Sample>() {
            public Sample apply( Integer yIndex, Integer xIndex ) {
                // start with top first
                float x = model.getStartingX( side, xIndex );

                final float yRatio = ( (float) yIndex ) / ( (float) CRUST_VERTICAL_STRIPS );
                float y = crustTopY + ( crustBottomY - crustTopY ) * yRatio;

                float temp = getCrustTemperatureFromYRatio( yRatio );
                return new Sample( new ImmutableVector3F( x, y, 0 ), temp, crustDensity,
                                   textureStrategy.mapFront( new ImmutableVector2F( x, y ) ) );
            }
        } ) );

        // set the position/texture coordinates to be the same for the mantle top boundary
//        getMantle().getTopBoundary().borrowPositionTemperatureAndTexture( getLithosphere().getBottomBoundary() );

        // essentially re-set all of the mantle samples based on our new information
        for ( int yIndex = 0; yIndex < getMantle().getBoundaries().size(); yIndex++ ) {
            Boundary boundary = getMantle().getBoundaries().get( yIndex );

            for ( int xIndex = 0; xIndex < boundary.samples.size(); xIndex++ ) {
                final Sample sample = boundary.samples.get( xIndex );
                final Sample desiredSample = createMantleSample( xIndex, yIndex, MANTLE_VERTICAL_STRIPS,
                                                                 lithosphereBottomY, SIMPLE_MANTLE_BOTTOM_Y,
                                                                 SIMPLE_LITHOSPHERE_BOUNDARY_TEMP, SIMPLE_MANTLE_BOTTOM_TEMP );

                sample.setPosition( desiredSample.getPosition() );
                sample.setTemperature( desiredSample.getTemperature() );
                sample.setDensity( desiredSample.getDensity() );
                sample.setTextureCoordinates( desiredSample.getTextureCoordinates() );
            }
        }

        final int crustLabelIndex = ( getCrust().getTopBoundary().samples.size() * ( getSide() == LEFT ? 5 : 1 ) ) / 6;
        final int lithosphereLabelIndex = ( getCrust().getTopBoundary().samples.size() * ( getSide() == LEFT ? 4 : 2 ) ) / 6;

        final Property<ImmutableVector3F> crustTop = new Property<ImmutableVector3F>( new ImmutableVector3F() ) {{
            final Sample sample = getCrust().getTopBoundary().getSample( crustLabelIndex );
            assert sample != null;
            model.modelChanged.addUpdateListener(
                    new UpdateListener() {
                        public void update() {
                            set( sample.getPosition() );
                        }
                    }, true );
        }};

        final Property<ImmutableVector3F> crustBottom = new Property<ImmutableVector3F>( new ImmutableVector3F() ) {{
            final Sample sample = getCrust().getBottomBoundary().getSample( crustLabelIndex );
            assert sample != null;
            model.modelChanged.addUpdateListener(
                    new UpdateListener() {
                        public void update() {
                            set( sample.getPosition() );
                        }
                    }, true );
        }};

        final Property<ImmutableVector3F> lithosphereTop = new Property<ImmutableVector3F>( new ImmutableVector3F() ) {{
            final Sample sample = getCrust().getTopBoundary().getSample( lithosphereLabelIndex );
            assert sample != null;
            model.modelChanged.addUpdateListener(
                    new UpdateListener() {
                        public void update() {
                            set( sample.getPosition() );
                        }
                    }, true );
        }};

        final Property<ImmutableVector3F> lithosphereBottom = new Property<ImmutableVector3F>( new ImmutableVector3F() ) {{
            final Sample sample = getLithosphere().getBottomBoundary().getSample( lithosphereLabelIndex );
            assert sample != null;
            model.modelChanged.addUpdateListener(
                    new UpdateListener() {
                        public void update() {
                            set( sample.getPosition() );
                        }
                    }, true );
        }};

        model.rangeLabels.add( new RangeLabel(
                new BoundaryTrackingProperty( getCrust().getTopBoundary(), crustLabelIndex ),
                new BoundaryTrackingProperty( getCrust().getBottomBoundary(), crustLabelIndex ),
                type.getSpecificLabel(), this
        ) );

        model.rangeLabels.add( new RangeLabel(
                new BoundaryTrackingProperty( getCrust().getTopBoundary(), lithosphereLabelIndex ),
                new BoundaryTrackingProperty( getLithosphere().getBottomBoundary(), lithosphereLabelIndex ),
                Strings.LITHOSPHERE, this
        ) );

        boundaryLabel = new BoundaryLabel( getLithosphere().getBottomBoundary(), side ) {
            @Override
            public boolean isReversed() {
                if ( behavior == null ) {
                    return super.isReversed();
                }
                else {
                    // reverse the boundary type if we are rifting apart
                    return behavior instanceof RiftingBehavior;
                }
            }
        };
        model.boundaryLabels.add( boundaryLabel );

        final float mantleX = lithosphereBottom.get().getX();

        // TODO: remove when we decide we don't need the vertical "mantle" label
//        model.rangeLabels.add( new RangeLabel(
//                // calculate the y for a set x value for this
//                new Property<ImmutableVector3F>( new ImmutableVector3F() ) {{
//                    lithosphereBottom.addObserver( new SimpleObserver() {
//                        public void update() {
//                            set( new ImmutableVector3F(
//                                    mantleX,
//                                    getLithosphere().getBottomBoundary().getApproximateYFromX( mantleX ),
//                                    lithosphereBottom.get().z
//                            ) );
//                        }
//                    } );
//                }},
//
//                // and set x/y here, however we need to keep up to date with z for the transform motion
//                new Property<ImmutableVector3F>( new ImmutableVector3F() ) {{
//                    lithosphereBottom.addObserver( new SimpleObserver() {
//                        public void update() {
//                            set( new ImmutableVector3F(
//                                    mantleX,
//                                    ( lithosphereBottom.get().y - lithosphereTop.get().y ) * 20,
//                                    lithosphereBottom.get().z
//                            ) );
//                        }
//                    } );
//                }},
//                Strings.MANTLE, this
//        ) {{
//            setLimitToScreen( true );
//        }} );

    }

    public float getSimpleChunkWidth() {
        // TODO: consider a direct model query with this, as if we change to uneven spacing this will break
        return model.getStartingX( LEFT, 1 ) - model.getStartingX( LEFT, 0 );
    }

    public void addSection( final Side side, final PlateType type ) {
        final float width = model.getStartingX( side, 1 ) - model.getStartingX( side, 0 );
        final float xOffset = width * side.getSign();
        final float crustTopY = type.getCrustTopY();
        final float crustMantleBoundaryY = type.getCrustBottomY();
        final float lithosphereBottomY = type.getLithosphereBottomY();

        final float x = getCrust().getTopBoundary().getEdgeSample( side ).getPosition().x + xOffset;

        // TODO: clean up code duplication
        getCrust().addColumn( side, new ArrayList<Sample>() {{
            float topY = crustTopY;
            float bottomY = crustMantleBoundaryY;
            for ( int yIndex = 0; yIndex < getCrust().getBoundaries().size(); yIndex++ ) {
                final Sample mySample = getCrust().getBoundaries().get( yIndex ).getEdgeSample( side );

                final float yRatio = ( (float) yIndex ) / ( (float) CRUST_VERTICAL_STRIPS );
                float y = topY + ( bottomY - topY ) * yRatio;

                float temp = getCrustTemperatureFromYRatio( yRatio );
                final float x = mySample.getPosition().x + xOffset;
                add( new Sample( new ImmutableVector3F( x, y, 0 ), temp, type.getDensity(),
                                 mySample.getTextureCoordinates().plus( textureStrategy.mapFrontDelta( new ImmutableVector2F( xOffset, 0 ) ) ) ) );
            }
        }} );

        getLithosphere().addColumn( side, new ArrayList<Sample>() {{
            float topY = crustMantleBoundaryY;
            float bottomY = lithosphereBottomY;
            for ( int yIndex = 0; yIndex < getLithosphere().getBoundaries().size(); yIndex++ ) {
                final Sample mySample = getLithosphere().getBoundaries().get( yIndex ).getEdgeSample( side );

                final float yRatio = ( (float) yIndex ) / ( (float) LITHOSPHERE_VERTICAL_STRIPS );
                float y = topY + ( bottomY - topY ) * yRatio;

                float temp = getLithosphereTemperatureFromYRatio( yRatio );
                final float x = mySample.getPosition().x + xOffset;
                add( new Sample( new ImmutableVector3F( x, y, 0 ), temp, SIMPLE_MANTLE_DENSITY,
                                 mySample.getTextureCoordinates().plus( textureStrategy.mapFrontDelta( new ImmutableVector2F( xOffset, 0 ) ) ) ) );
            }
        }} );

        getTerrain().addColumn( side, x, new ArrayList<TerrainSample>() {{
            for ( int zIndex = 0; zIndex < getTerrain().getZSamples(); zIndex++ ) {
                final TerrainSample mySample = getTerrain().getSample( side.getIndex( getTerrain().getNumColumns() ), zIndex );
                // elevation to be fixed later
                // TODO: fix texture coordinates on newly added terrain
                add( new TerrainSample( getCrust().getTopBoundary().getEdgeSample( side ).getPosition().y, mySample.getTextureCoordinates().plus( textureStrategy.mapTopDelta( new ImmutableVector2F( xOffset, 0 ) ) ) ) );
            }
        }} );
    }

    public void removeSection( Side side ) {
        getCrust().removeColumn( side );
        getLithosphere().removeColumn( side );
        getTerrain().removeColumn( side );
    }

    public void fullSyncTerrain() {
        for ( int column = 0; column < getTerrain().getNumColumns(); column++ ) {
            // left side
            ImmutableVector3F position = ( plateType != null ? getCrust().getTopBoundary() : getMantle().getTopBoundary() ).samples.get( column ).getPosition();
            for ( int row = 0; row < TERRAIN_DEPTH_SAMPLES; row++ ) {
                // set the elevation for the whole column
                getTerrain().getSample( column, row ).setElevation( position.y );
            }
            getTerrain().xPositions.set( column, position.x );
        }
        getTerrain().elevationChanged.updateListeners();
    }

    private static float getCrustTemperatureFromYRatio( float ratioFromTop ) {
        // TODO: young/old oceanic crust differences!
        return SIMPLE_CRUST_TOP_TEMP + ( SIMPLE_CRUST_BOTTOM_TEMP - SIMPLE_CRUST_TOP_TEMP ) * ratioFromTop;
    }

    private static float getLithosphereTemperatureFromYRatio( float ratioFromTop ) {
        return SIMPLE_MANTLE_TOP_TEMP + ( SIMPLE_LITHOSPHERE_BOUNDARY_TEMP - SIMPLE_MANTLE_TOP_TEMP ) * ratioFromTop;
    }

    // only for the transform boundary, so we can simplify a few things here
    public void addMiddleSide( final Plate otherPlate ) {
        assert getCrust() != null && getMantle() != null;

        /*---------------------------------------------------------------------------*
        * mantle
        *----------------------------------------------------------------------------*/
        regions.add( new Region( MANTLE_VERTICAL_STRIPS, getTerrain().getNumRows(), new Function2<Integer, Integer, Sample>() {
            public Sample apply( Integer yIndex, Integer xIndex ) {
                // we're in the center exactly
                float x = 0;

                // grab the y location from the mantle cross section boundary
                final Sample sample = getMantle().getBoundaries().get( yIndex ).samples.get( 0 );
                float y = sample.getPosition().y;

                // grab the Z from the terrain. we also reverse the index for the side of the left plate, so the sidedness is correct
                float z = getTerrain().zPositions.get( side == LEFT ? getTerrain().getNumRows() - xIndex - 1 : xIndex );

                // sample copied from other one. we reverse texture coordinates also for the left plate, so it wraps over the edge nicely
                return new Sample( new ImmutableVector3F( x, y, z ), sample.getTemperature(), sample.getDensity(),
                                   textureStrategy.mapFront( new ImmutableVector2F( z * side.getSign(), y ) ) );
            }
        } ) );

        /*---------------------------------------------------------------------------*
        * lithosphere
        *----------------------------------------------------------------------------*/
        regions.add( new Region( LITHOSPHERE_VERTICAL_STRIPS, getTerrain().getNumRows(), new Function2<Integer, Integer, Sample>() {
            public Sample apply( Integer yIndex, Integer xIndex ) {
                // we're in the center exactly
                float x = 0;

                // grab the y location from the lithosphere cross section boundary
                final Sample sample = getLithosphere().getBoundaries().get( yIndex ).samples.get( 0 );
                float y = sample.getPosition().y;

                // grab the Z from the terrain. we also reverse the index for the side of the left plate, so the sidedness is correct
                float z = getTerrain().zPositions.get( side == LEFT ? getTerrain().getNumRows() - xIndex - 1 : xIndex );

                // sample copied from other one. we reverse texture coordinates also for the left plate, so it wraps over the edge nicely
                return new Sample( new ImmutableVector3F( x, y, z ), sample.getTemperature(), sample.getDensity(),
                                   textureStrategy.mapFront( new ImmutableVector2F( z * side.getSign(), y ) ) );
            }
        } ) );

        /*---------------------------------------------------------------------------*
        * crust
        *----------------------------------------------------------------------------*/

        final int myTerrainIndex = getSide().opposite().getIndex( getTerrain().getNumColumns() );
        final int otherTerrainIndex = getSide().getIndex( getTerrain().getNumColumns() );
        final float myBottomY = getCrust().getBottomBoundary().samples.get( 0 ).getPosition().y;

        // do a few calculations so we don't cover up the exposed-to-air part of the earth
        regions.add( new Region( CRUST_VERTICAL_STRIPS, getTerrain().getNumRows(), createMiddleCrustSampleFactory( otherPlate ) ) {{

            // update heights whenever the model changes in this mode
            model.modelChanged.addUpdateListener(
                    new UpdateListener() {
                        public void update() {
                            final Function2<Integer, Integer, Sample> factory = createMiddleCrustSampleFactory( otherPlate );
                            for ( int yIndex = 0; yIndex < getBoundaries().size(); yIndex++ ) {
                                Boundary boundary = getBoundaries().get( yIndex );
                                for ( int xIndex = 0; xIndex < getTopBoundary().samples.size(); xIndex++ ) {
                                    Sample sample = boundary.getSample( xIndex );
                                    Sample recomputedSample = factory.apply( yIndex, xIndex );
                                    sample.setPosition( new ImmutableVector3F( sample.getPosition().x,
                                                                               recomputedSample.getPosition().y,
                                                                               sample.getPosition().z ) );
                                }
                            }
                        }
                    }, false );
        }} );
    }

    public Function2<Integer, Integer, Sample> createMiddleCrustSampleFactory( final Plate otherPlate ) {
        final int myTerrainIndex = getSide().opposite().getIndex( getTerrain().getNumColumns() );
        final int otherTerrainIndex = getSide().getIndex( getTerrain().getNumColumns() );
        final float myBottomY = getCrust().getBottomBoundary().samples.get( 0 ).getPosition().y;

        return new Function2<Integer, Integer, Sample>() {
            public Sample apply( Integer yIndex, Integer xIndex ) {

                int mappedZIndex = getSide().getFromIndex( getTerrain().getNumRows(), xIndex );

                final float myTopY = getTerrain().getSample( myTerrainIndex, mappedZIndex ).getElevation();
                final float otherTopY = otherPlate.getTerrain().getSample( otherTerrainIndex, mappedZIndex ).getElevation();

                float topEarthY = Math.min( myTopY, otherTopY );
                final float ratioSpread = ( topEarthY - myBottomY ) / ( myTopY - myBottomY );

                // we're in the center exactly
                float x = 0;

                // grab the y location from the mantle cross section boundary
                final Sample sample = getCrust().getBoundaries().get( yIndex ).samples.get( 0 );

                float yRatio = ( (float) yIndex ) / ( (float) CRUST_VERTICAL_STRIPS );
                yRatio = ( yRatio * ratioSpread + ( 1 - ratioSpread ) ); // compensate for the difference
                float y = myTopY + ( myBottomY - myTopY ) * yRatio;

                // grab the Z from the terrain. we also reverse the index for the side of the left plate, so the sidedness is correct
                float z = getTerrain().zPositions.get( mappedZIndex );

                float temp = getCrustTemperatureFromYRatio( yRatio );
                // sample copied from other one. we reverse texture coordinates also for the left plate, so it wraps over the edge nicely
                return new Sample( new ImmutableVector3F( x, y, z ), temp, sample.getDensity(),
                                   textureStrategy.mapFront( new ImmutableVector2F( z * side.getSign(), y ) ) );
            }
        };
    }

    public PlateBehavior getBehavior() {
        return behavior;
    }

    public void setBehavior( PlateBehavior behavior ) {
        this.behavior = behavior;
    }

    public Side getSide() {
        return side;
    }

    public int getSign() {
        return side.getSign();
    }

    public TextureStrategy getTextureStrategy() {
        return textureStrategy;
    }

    public PlateType getPlateType() {
        return plateType;
    }

    public PlateMotionModel getModel() {
        return model;
    }

    public BoundaryLabel getBoundaryLabel() {
        return boundaryLabel;
    }

    public void randomizeTerrain() {
        // always the same seed, allows us to have seamless random elevations across different plates
        Random rand = new Random( 0 );
        float[] boundaryValues = new float[getTerrain().getNumRows()];
        for ( int i = 0; i < getTerrain().getNumRows(); i++ ) {
            boundaryValues[i] = rand.nextFloat();
        }

        float hillMagnitude = getPlateType().isContinental() ? 2000 : 500;
        float hillOffset = getPlateType().isContinental() ? -1000 : 0; // raise and lower continental crust, but NEVER lower oceanic crust
        for ( int columnIndex = 0; columnIndex < getTerrain().getNumColumns(); columnIndex++ ) {
            float x = getTerrain().xPositions.get( columnIndex );

            boolean isBoundaryColumn = columnIndex == getSide().opposite().getIndex( getTerrain().getNumColumns() );

            for ( int rowIndex = 0; rowIndex < getTerrain().getNumRows(); rowIndex++ ) {
                float z = getTerrain().zPositions.get( rowIndex );

                final float randomValue = isBoundaryColumn ? boundaryValues[rowIndex] : rand.nextFloat();
                float delta = ( randomValue ) * hillMagnitude + hillOffset;
                final TerrainSample terrainSample = getTerrain().getSample( columnIndex, rowIndex );
                terrainSample.setElevation( terrainSample.getElevation() + delta );
                terrainSample.setRandomElevationOffset( delta );

                // if at the front, update the crust
                if ( rowIndex == getTerrain().getFrontZIndex() ) {
                    float newCrustTop = getCrust().getTopElevation( columnIndex ) + delta;
                    getCrust().getTopBoundary().samples.get( columnIndex ).setRandomTerrainOffset( delta );
                    getCrust().layoutColumn( columnIndex,
                                             newCrustTop, getCrust().getBottomElevation( columnIndex ),
                                             getTextureStrategy(), true );
                }
            }
        }

        getTerrain().elevationChanged.updateListeners();
    }

    public class BoundaryTrackingProperty extends Property<ImmutableVector3F> {
        public BoundaryTrackingProperty( final Boundary boundary, int labelIndex ) {
            super( new ImmutableVector3F() );

            final Sample sample = boundary.getSample( labelIndex );
            assert sample != null;
            final float initialX = sample.getPosition().x;

            model.modelChanged.addUpdateListener(
                    new UpdateListener() {
                        public void update() {
                            if ( behavior instanceof RiftingBehavior || behavior instanceof TransformBehavior ) {
                                set( sample.getPosition() );
                            }
                            else {
                                set( new ImmutableVector3F( initialX, boundary.getApproximateYFromX( initialX ), 0 ) );
                            }
                        }
                    }, true );
        }
    }
}
