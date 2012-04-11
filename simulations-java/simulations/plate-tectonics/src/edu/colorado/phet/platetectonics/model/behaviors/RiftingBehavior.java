// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.behaviors;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.colorado.phet.common.phetcommon.util.FunctionalUtils;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.model.PlateMotionPlate;
import edu.colorado.phet.platetectonics.model.PlateType;
import edu.colorado.phet.platetectonics.model.Sample;
import edu.colorado.phet.platetectonics.model.TerrainSample;
import edu.colorado.phet.platetectonics.model.regions.Boundary;
import edu.colorado.phet.platetectonics.model.regions.MagmaRegion;
import edu.colorado.phet.platetectonics.model.regions.Region;
import edu.colorado.phet.platetectonics.util.Side;

public class RiftingBehavior extends PlateBehavior {

    private float timeElapsed = 0;

    public static final float RIDGE_TOP_Y = -500;
    public static final float SPREAD_START_TIME = 10.0f;

    public static final float RIFT_PLATE_SPEED = 30000f / 2;

    public static final int BLOB_QUANTITY = 50;

    public RiftingBehavior( final PlateMotionPlate plate, PlateMotionPlate otherPlate ) {
        super( plate, otherPlate );

        getLithosphere().moveToFront();
        getCrust().moveToFront();

        moveMantleTopTo( PlateType.OLD_OCEANIC.getCrustTopY() - 1000 );

        magmaTarget = new ImmutableVector2F( 0, RIDGE_TOP_Y );
        magmaSpeed = RIFT_PLATE_SPEED;
    }

    @Override protected void onMagmaRemoved( MagmaRegion magma ) {
        super.onMagmaRemoved( magma );

        addMagmaBlob( true, magma.position.get().minus( magmaTarget ).getMagnitude() );
    }

    public void addMagmaBlob( boolean onlyAtBottom ) {
        addMagmaBlob( onlyAtBottom, 0 );
    }

    public void addMagmaBlob( boolean onlyAtBottom, float additionalMagnitude ) {
        final float spread = 0.5f;
        final float maxDistance = 100000;
        float angle = (float) ( Math.PI / 2 + ( Math.random() - 0.5 ) * spread );
        ImmutableVector2F directionFromEnd = new ImmutableVector2F( Math.cos( angle ), Math.sin( angle ) ).negate();
        ImmutableVector2F position = new ImmutableVector2F( 0, RIDGE_TOP_Y ).plus( directionFromEnd.times(
                onlyAtBottom ? maxDistance - additionalMagnitude : (float) ( Math.random() * maxDistance ) ) );
        addMagma( position );
    }

    @Override public void stepInTime( float millionsOfYears ) {
        timeElapsed += millionsOfYears;

        // trim away things that are outside of our view (performance)
        removeEarthEdges();

        // show the magma chamber once we start animating
        if ( plate.getSide() == Side.LEFT && magmaChamber == null ) {
            if ( plate.getSide() == Side.LEFT ) {
                magmaChamber = new MagmaRegion( plate.getTextureStrategy(), PlateType.YOUNG_OCEANIC.getCrustThickness() / 3f, (float) ( Math.PI / 2 ), 16,
                                                new ImmutableVector2F( 0, RIDGE_TOP_Y ) );
                plate.regions.add( magmaChamber );
                magmaChamber.moveToFront();

                // add in the magma blobs
                FunctionalUtils.repeat( new Runnable() {
                                            public void run() {
                                                addMagmaBlob( false );
                                            }
                                        }, BLOB_QUANTITY );
            }
        }

        moveSpreading( millionsOfYears );

        if ( plate.getSide() == Side.LEFT ) {
            animateMagma( millionsOfYears );
        }
    }

    private void moveSpreading( float millionsOfYears ) {
        float idealChunkWidth = plate.getSimpleChunkWidth();

        final Set<Integer> elevationColumnsChanged = new HashSet<Integer>();

        final float xOffset = RIFT_PLATE_SPEED * (float) plate.getSide().getSign() * millionsOfYears;

        // move all of the lithosphere
        final Region[] mobileRegions = { getPlate().getLithosphere(), getPlate().getCrust() };
        for ( Region region : mobileRegions ) {
            for ( Sample sample : region.getSamples() ) {
                sample.setPosition( sample.getPosition().plus( new ImmutableVector3F( xOffset, 0, 0 ) ) );
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

        // if our "shortened" segment needs to be updated, then we update it
        {
            final Sample centerTopSample = getSampleFromCenter( getCrust().getTopBoundary(), 0 );
            final Sample nextSample = getSampleFromCenter( getCrust().getTopBoundary(), 1 );
            final float currentWidth = Math.abs( nextSample.getPosition().x - centerTopSample.getPosition().x );
            if ( currentWidth < idealChunkWidth * 1.001 ) {
                float currentPosition = centerTopSample.getPosition().x;
                float idealPosition = nextSample.getPosition().x - idealChunkWidth * plate.getSide().getSign();

                if ( idealPosition * plate.getSign() < 0 ) {
                    idealPosition = 0;
                }

                final float offset = idealPosition - currentPosition;
                final int index = plate.getSide().opposite().getIndex( getCrust().getTopBoundary().samples );
                shiftColumn( index, offset );

                if ( idealPosition != 0 && getCrust().getTopBoundary().samples.get( index ).getDensity() == PlateType.YOUNG_OCEANIC.getDensity() ) {
                    sinkOceanicCrust( Math.abs( idealPosition ) / RIFT_PLATE_SPEED, index );
                    elevationColumnsChanged.add( index );
                }
            }
        }

        /*---------------------------------------------------------------------------*
        * add fresh crust
        *----------------------------------------------------------------------------*/
        final Side zeroSide = plate.getSide().opposite();
        while ( getSampleFromCenter( getCrust().getTopBoundary(), 0 ).getPosition().x * plate.getSide().getSign() > 0.0001 ) {
            plate.addSection( zeroSide, PlateType.YOUNG_OCEANIC );

            // if we add to the left, we need to update all of our "already elevation changed" indices
            if ( zeroSide == Side.LEFT ) {
                Set<Integer> copy = new HashSet<Integer>( elevationColumnsChanged );
                elevationColumnsChanged.clear();
                for ( Integer column : copy ) {
                    elevationColumnsChanged.add( column + 1 );
                }
            }

            { // update the new section positions
                // re-layout that section
                final int newIndex = zeroSide.getIndex( getCrust().getTopBoundary().samples );
                final float crustBottom = RIDGE_TOP_Y - PlateType.YOUNG_OCEANIC.getCrustThickness();
                getCrust().layoutColumn( newIndex,
                                         RIDGE_TOP_Y,
                                         crustBottom,
                                         plate.getTextureStrategy(), true ); // essentially reset the textures

                // make the mantle part of the lithosphere have zero thickness here
                getLithosphere().layoutColumn( newIndex,
                                               crustBottom, crustBottom,
                                               plate.getTextureStrategy(), true );

                // correct the Y-texture values that were caused to be somewhat incorrect by the addSection
                for ( Region region : getLithosphereRegions() ) {
                    for ( Boundary boundary : region.getBoundaries() ) {
                        Sample sample = boundary.samples.get( newIndex );
                        ImmutableVector2F staticTextureCoordinates = plate.getTextureStrategy().mapFront( new ImmutableVector2F( sample.getPosition().x, sample.getPosition().y ) );
                        sample.setTextureCoordinates( new ImmutableVector2F( sample.getTextureCoordinates().x, staticTextureCoordinates.y ) );
                    }
                }

                for ( TerrainSample sample : getTerrain().getColumn( newIndex ) ) {
                    sample.setElevation( RIDGE_TOP_Y );
                }
            }

            // this will reference the newly created section top are on the same side, then we need to process the heights like normal
            final float newX = getSampleFromCenter( getCrust().getTopBoundary(), 0 ).getPosition().x;
            if ( newX * plate.getSide().getSign() > 0 ) {
                // our created column is on the correct side (there was a lot of room)
                // we need to compensate for how much time should have passed

                int newIndex = zeroSide.getIndex( getCrust().getTopBoundary().samples );

                // we can actually compute the "passed" years directly here from our position
                sinkOceanicCrust( Math.abs( newX ) / RIFT_PLATE_SPEED, newIndex );
                elevationColumnsChanged.add( newIndex );
            }
            else {
                // our column needs to be put in the exact center (x=0)
                final List<Sample> topSamples = getCrust().getTopBoundary().samples;
                final int index = zeroSide.getIndex( topSamples );

                // shift over the entire column
                shiftColumn( index, -zeroSide.getEnd( topSamples ).getPosition().x );
            }
        }

        /*---------------------------------------------------------------------------*
        * handle plate changes
        *----------------------------------------------------------------------------*/
        {
            for ( int columnIndex = 0; columnIndex < getNumCrustXSamples(); columnIndex++ ) {
                Sample topSample = getCrust().getTopBoundary().samples.get( columnIndex );
                if ( topSample.getDensity() == PlateType.YOUNG_OCEANIC.getDensity() ) {
                    /*---------------------------------------------------------------------------*
                    * oceanic modifications
                    *----------------------------------------------------------------------------*/

                    // sink the crust, advancing us along an arctangent-sloped curve
                    // don't sink the crust on ones that we have already done
                    if ( topSample.getPosition().x != 0 && !elevationColumnsChanged.contains( columnIndex ) ) {
                        sinkOceanicCrust( millionsOfYears, columnIndex );
                    }
                }
            }
        }
        {
            // split the continental modifications if the timestep is too large
            recursiveSplitCall(
                    new VoidFunction1<Float>() {
                        public void apply( Float millionsOfYears ) {
                            for ( int columnIndex = 0; columnIndex < getNumCrustXSamples(); columnIndex++ ) {
                                Sample topSample = getCrust().getTopBoundary().samples.get( columnIndex );
                                if ( topSample.getDensity() == PlateType.CONTINENTAL.getDensity() ) {
                                    /*---------------------------------------------------------------------------*
                                    * continental modifications
                                    *----------------------------------------------------------------------------*/

                                    // blending crust sizes here (and preferably lithosphere too?)
                                    float fakeNeighborhoodY = topSample.getPosition().y - topSample.getRandomTerrainOffset();
                                    int count = 1;
                                    if ( columnIndex > 0 ) {
                                        final Sample neighborSample = getCrust().getTopBoundary().samples.get( columnIndex - 1 );
                                        fakeNeighborhoodY += neighborSample.getPosition().y - neighborSample.getRandomTerrainOffset();
                                        count += 1;
                                    }
                                    if ( columnIndex < getNumCrustXSamples() - 1 ) {
                                        final Sample neighborSample = getCrust().getTopBoundary().samples.get( columnIndex + 1 );
                                        fakeNeighborhoodY += neighborSample.getPosition().y - neighborSample.getRandomTerrainOffset();
                                        count += 1;
                                    }
                                    fakeNeighborhoodY /= count;

                                    // this means any further accesses are relative to the actual position (thus it's a fake neighborhood)
                                    fakeNeighborhoodY += topSample.getRandomTerrainOffset();

                                    float currentCrustTop = getCrust().getTopElevation( columnIndex );
                                    float currentCrustBottom = getCrust().getBottomElevation( columnIndex );
                                    float currentLithosphereBottom = getLithosphere().getBottomElevation( columnIndex );
                                    float currentCrustWidth = currentCrustTop - currentCrustBottom;

                                    // try subtracting off top and bottom, and see how much all of this would change
                                    float resizeFactor = ( currentCrustWidth - 2 * ( currentCrustTop - fakeNeighborhoodY ) ) / currentCrustWidth;

                                    // don't ever grow the crust height
                                    if ( resizeFactor > 1 ) {
                                        resizeFactor = 1;
                                    }
                                    resizeFactor = (float) Math.pow( resizeFactor, 2 * millionsOfYears );
                                    float center = ( currentCrustTop + currentCrustBottom ) / 2;

                                    final float newCrustTop = ( currentCrustTop - center ) * resizeFactor + center;

                                    // x^2 the resizing factor for the bottoms so they shrink faster
                                    final float newCrustBottom = ( currentCrustBottom - center ) * resizeFactor * resizeFactor + center;
                                    final float newLithosphereBottom = ( currentLithosphereBottom - center ) * resizeFactor * resizeFactor + center;
                                    getCrust().layoutColumn( columnIndex,
                                                             newCrustTop,
                                                             newCrustBottom,
                                                             plate.getTextureStrategy(), true );
                                    getLithosphere().layoutColumn( columnIndex,
                                                                   newCrustBottom,
                                                                   newLithosphereBottom,
                                                                   plate.getTextureStrategy(), true );
                                    getTerrain().shiftColumnElevation( columnIndex, newCrustTop - currentCrustTop );
                                }
                            }
                        }
                    }, millionsOfYears, 0.1f
            );
        }

        getPlate().getTerrain().elevationChanged.updateListeners();
    }

    private void sinkOceanicCrust( float millionsOfYears, int columnIndex ) {
        float topY = RIDGE_TOP_Y;
        float bottomY = PlateType.OLD_OCEANIC.getCrustTopY();

        {
            /*---------------------------------------------------------------------------*
            * sink the plate as it moves out
            *----------------------------------------------------------------------------*/

            final float magicConstant1 = 4;

            float currentTopY = getCrust().getTopElevation( columnIndex );
            float currentRatio = ( currentTopY - topY ) / ( bottomY - topY );

            // invert arctanget, offset, then apply normally
            float currentT = (float) Math.tan( currentRatio * ( Math.PI / 2 ) );
            float newT = currentT + millionsOfYears * magicConstant1;
            float newRatio = (float) ( Math.atan( newT ) / ( Math.PI / 2 ) );

            // some necessary assertions for any future debugging
            assert currentRatio >= 0;
            assert currentRatio <= 1;
            assert currentT >= 0;
            assert newRatio >= 0;
            assert newRatio <= 1;

            float newTopY = ( 1 - newRatio ) * topY + ( newRatio ) * bottomY;
            float offsetY = newTopY - currentTopY;
            for ( Region region : getLithosphereRegions() ) {
                for ( Boundary boundary : region.getBoundaries() ) {
                    final Sample sample = boundary.samples.get( columnIndex );
                    sample.setPosition( sample.getPosition().plus( ImmutableVector3F.Y_UNIT.times( offsetY ) ) );
                }
            }
            getTerrain().shiftColumnElevation( columnIndex, offsetY );
        }

        {
            /*---------------------------------------------------------------------------*
            * accrue more lithosphere here
            *----------------------------------------------------------------------------*/
            float currentMantleTop = plate.getLithosphere().getTopElevation( columnIndex );
            float currentLithosphereBottom = plate.getLithosphere().getBottomElevation( columnIndex );

            float magicConstant2 = 1;

            // 0 = thinnest, 1 = thickest
            final float maxThickness = PlateType.YOUNG_OCEANIC.getMantleLithosphereThickness();
            float currentRatio = ( currentMantleTop - currentLithosphereBottom ) / maxThickness;

            // due to randomness of oceanic terrain heights, this is needed. for some reason
            if ( currentRatio > 1 ) {
                currentRatio = 1;
            }
            if ( currentRatio < 0 ) {
                currentRatio = 0;
            }

            // invert arctanget, offset, then apply normally
            float currentT = (float) Math.tan( currentRatio * ( Math.PI / 2 ) );
            float newT = currentT + millionsOfYears * magicConstant2;
            float newRatio = (float) ( Math.atan( newT ) / ( Math.PI / 2 ) );

            float newLithosphereBottom = currentMantleTop - maxThickness * newRatio;
            plate.getLithosphere().layoutColumn( columnIndex,
                                                 currentMantleTop,
                                                 newLithosphereBottom,
                                                 plate.getTextureStrategy(), true );
        }
    }

    private void shiftColumn( int columnIndex, float xOffset ) {
        for ( Region region : getLithosphereRegions() ) {
            for ( Boundary boundary : region.getBoundaries() ) {
                boundary.samples.get( columnIndex ).shiftWithTexture( new ImmutableVector3F( xOffset, 0, 0 ), plate.getTextureStrategy() );
            }
        }

        getTerrain().shiftColumnXWithTexture( plate.getTextureStrategy(), columnIndex, xOffset );
        getTerrain().columnsModified.updateListeners();
    }

    private Sample getSampleFromCenter( Boundary boundary, int offsetFromCenter ) {
        return boundary.getEdgeSample( getPlate().getSide().opposite(), offsetFromCenter );
    }
}
