// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.behaviors;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.model.PlateMotionModel;
import edu.colorado.phet.platetectonics.model.PlateMotionPlate;
import edu.colorado.phet.platetectonics.model.PlateType;
import edu.colorado.phet.platetectonics.model.Sample;
import edu.colorado.phet.platetectonics.model.SmokePuff;
import edu.colorado.phet.platetectonics.model.TerrainSample;
import edu.colorado.phet.platetectonics.model.regions.MagmaRegion;
import edu.colorado.phet.platetectonics.model.regions.Region;

public class OverridingBehavior extends PlateBehavior {

    private float magmaCenterX;

    public static final float TOP_MELT_Y = -100000;
    public static final float BOTTOM_MELT_Y = -150000;
    public static final float MELT_PADDING_Y = 10000;

    public static final float MELT_SPEED = 10000f; // pretty slow. like 3mm/year
    public static final float MELT_CHANCE_FACTOR = 0.0002f;

    // melting X positions, determined by commented-out code below. update this if the magma chamber isn't centered properly
    public static final float OLD_MELT_X = 103157.875f;
    public static final float YOUNG_MELT_X = 162105.25f;

    public static final float MAGMA_TUBE_WIDTH = 1000;

    public static final float MOUNTAIN_X_OFFSET = 10000;
    public static final float MOUNTAIN_Z_PERIOD_FACTOR = 10000;

    private float chamberFullness = 0;

    private float minElevationInTimestep;
    private float maxElevationInTimestep;

    private Region magmaTube;

    public OverridingBehavior( PlateMotionPlate plate, PlateMotionPlate otherPlate ) {
        super( plate, otherPlate );

        getLithosphere().moveToFront();
        getCrust().moveToFront();

        magmaCenterX = getSide().getSign() * ( otherPlate.getPlateType() == PlateType.YOUNG_OCEANIC ? YOUNG_MELT_X : OLD_MELT_X );

        magmaTarget = getMagmaChamberTop();
        magmaSpeed = MELT_SPEED;
    }

    private float getMagmaChamberScale() {
        return plate.getPlateType().getCrustThickness() / ( plate.getPlateType().isOceanic() ? 3f : 6f );
    }

    private SubductingBehavior getSubductingBehavior() {
        // cast OK, overriding should only be paired with subducting
        return (SubductingBehavior) getOtherPlate().getBehavior();
    }

    @Override public void stepInTime( float millionsOfYears ) {

        // initialize the magma chamber if we haven't already
        if ( magmaChamber == null ) {
            magmaChamber = new MagmaRegion( plate.getTextureStrategy(), getMagmaChamberScale(), (float) ( Math.PI / 2 ), 16,
                                            getMagmaChamberTop() );
            plate.regions.add( magmaChamber );
            magmaChamber.moveToFront();
            magmaChamber.setAllAlphas( 0 );

            magmaTube = new Region( 1, 2, new Function2<Integer, Integer, Sample>() {
                public Sample apply( Integer yIndex, Integer xIndex ) {
                    final float x = magmaCenterX + ( xIndex == 0 ? -0.5f : 0.5f ) * MAGMA_TUBE_WIDTH;
                    final float y = getMagmaChamberTop().y - 500;
                    return new Sample( new ImmutableVector3F( x, y, 0 ),
                                       PlateMotionModel.SIMPLE_MAGMA_TEMP, PlateMotionModel.SIMPLE_MAGMA_DENSITY,
                                       plate.getTextureStrategy().mapFront( new ImmutableVector2F( x, y ) ) );
                }
            } );
            plate.regions.add( magmaTube );
            magmaTube.moveToFront();
            magmaTube.setAllAlphas( 0 );
        }

        animateMagma( millionsOfYears );

        if ( chamberFullness >= 1 ) {
            animateMountains( millionsOfYears );
        }

        // bring the edge down to the other level fairly quickly
        {
            float boundaryElevation = getSubductingBehavior().getBoundaryElevation();

            Sample edgeSample = getTopCrustBoundary().getEdgeSample( getOppositeSide() );
            float currentElevation = edgeSample.getPosition().y;

            float diff = boundaryElevation - currentElevation;
            float delta = (float) ( diff * ( 1 - Math.exp( -millionsOfYears ) ) ); // using exponential to keep timestep-independence

            final int columnIndex = getOppositeSide().getIndex( getNumCrustXSamples() );
            getCrust().layoutColumn( columnIndex,
                                     currentElevation + delta,
                                     getCrust().getBottomElevation( columnIndex ) + delta,
                                     plate.getTextureStrategy(), true );
            getTerrain().shiftColumnElevation( columnIndex, delta );
        }


        /*---------------------------------------------------------------------------*
        * smooth out continental crustal corner
        *----------------------------------------------------------------------------*/

        // NOTE: somewhat copied code from RiftingBehavior, but only changes the crust
        for ( int columnIndex = 0; columnIndex < getNumCrustXSamples(); columnIndex++ ) {
            Sample topSample = getCrust().getTopBoundary().samples.get( columnIndex );

            // blending crust sizes here (and preferably lithosphere too?)
            float fakeNeighborhoodY = topSample.getPosition().y;
            int count = 1;
            if ( columnIndex > 0 ) {
                fakeNeighborhoodY += getCrust().getTopBoundary().samples.get( columnIndex - 1 ).getPosition().y;
                count += 1;
            }
            if ( columnIndex < getNumCrustXSamples() - 1 ) {
                fakeNeighborhoodY += getCrust().getTopBoundary().samples.get( columnIndex + 1 ).getPosition().y;
                count += 1;
            }
            fakeNeighborhoodY /= count;

            float currentCrustTop = getCrust().getTopElevation( columnIndex );
            float currentCrustBottom = getCrust().getBottomElevation( columnIndex );
            float currentCrustWidth = currentCrustTop - currentCrustBottom;

            // try subtracting off top and bottom, and see how much all of this would change
            float resizeFactor = ( currentCrustWidth - 2 * ( currentCrustTop - fakeNeighborhoodY ) ) / currentCrustWidth;

            // don't ever grow the crust height
            if ( resizeFactor > 1 ) {
                resizeFactor = 1;
            }

            { // blend the resizeFactor to 1 the farther we get from the boundary, so we don't affect the mountains
                final float THRESHOLD_X_DIFFERENCE = 50000;
                final float ratio = Math.min( 1, Math.abs( topSample.getPosition().x ) / THRESHOLD_X_DIFFERENCE );
                resizeFactor = ratio + ( 1 - ratio ) * resizeFactor;
            }

            resizeFactor = (float) Math.pow( resizeFactor, millionsOfYears );
            float center = ( currentCrustTop + currentCrustBottom ) / 2;

            final float newCrustTop = ( currentCrustTop - center ) * resizeFactor + center;

            // compute new bottom with the same delta
            final float newCrustBottom = currentCrustBottom + ( newCrustTop - currentCrustTop );

            getCrust().layoutColumn( columnIndex,
                                     newCrustTop,
                                     newCrustBottom,
                                     plate.getTextureStrategy(), true );
            getTerrain().shiftColumnElevation( columnIndex, newCrustTop - currentCrustTop );
        }

        getTerrain().elevationChanged.updateListeners();

        /*---------------------------------------------------------------------------*
        * handle melt
        *----------------------------------------------------------------------------*/

        ImmutableVector2F lowMeltPoint = getSubductingBehavior().getLowestMeltingLocation();
        ImmutableVector2F highMeltPoint = getSubductingBehavior().getHighestMeltingLocation();

        if ( lowMeltPoint != null && highMeltPoint != null ) {
            // melting can start over this region
            float chanceOfMelting = (float) ( 1 - Math.exp( -millionsOfYears * MELT_CHANCE_FACTOR * ( highMeltPoint.y - lowMeltPoint.y ) ) );

            boolean shouldCreateMelt = ( Math.random() < chanceOfMelting );

//            System.out.println( "center melt x: " + lowMeltPoint.plus( highMeltPoint ).times( 0.5f ).x );

            if ( shouldCreateMelt ) {
                // randomly pick a location on the available span
                ImmutableVector2F location = lowMeltPoint.plus( highMeltPoint.minus( lowMeltPoint ).times( (float) Math.random() ) );

                addMagma( location, 0.7f );
            }
        }

        {
            // min, max elevation computations
            minElevationInTimestep = Float.MAX_VALUE;
            maxElevationInTimestep = -Float.MAX_VALUE;
            for ( Sample sample : getTopCrustBoundary().samples ) {
                if ( sample.getPosition().y > maxElevationInTimestep ) {
//                    System.out.println( "max elevation at: " + sample.getPosition().x );
                }
                minElevationInTimestep = Math.min( minElevationInTimestep, sample.getPosition().y );
                maxElevationInTimestep = Math.max( maxElevationInTimestep, sample.getPosition().y );
            }
        }

        /*---------------------------------------------------------------------------*
        * smoke animation
        *----------------------------------------------------------------------------*/
        if ( chamberFullness >= 1 ) {
            float zStep = (float) ( 2 * Math.PI * MOUNTAIN_Z_PERIOD_FACTOR );
            // TODO: remove this. copy/paste helped in a jam
            createSmokeAt( new ImmutableVector3F( magmaCenterX, maxElevationInTimestep, 0 ), millionsOfYears );
            createSmokeAt( new ImmutableVector3F( magmaCenterX - plate.getSign() * MOUNTAIN_X_OFFSET, maxElevationInTimestep - 2000, -zStep ), millionsOfYears );
            createSmokeAt( new ImmutableVector3F( magmaCenterX + plate.getSign() * MOUNTAIN_X_OFFSET, maxElevationInTimestep - 2000, -2 * zStep ), millionsOfYears );

            createSmokeAt( new ImmutableVector3F( magmaCenterX, maxElevationInTimestep - 2000, -3 * zStep ), millionsOfYears );
            createSmokeAt( new ImmutableVector3F( magmaCenterX - plate.getSign() * MOUNTAIN_X_OFFSET, maxElevationInTimestep - 4000, -4 * zStep ), millionsOfYears );
            createSmokeAt( new ImmutableVector3F( magmaCenterX + plate.getSign() * MOUNTAIN_X_OFFSET, maxElevationInTimestep - 4000, -5 * zStep ), millionsOfYears );

            createSmokeAt( new ImmutableVector3F( magmaCenterX, maxElevationInTimestep - 4000, -6 * zStep ), millionsOfYears );
            createSmokeAt( new ImmutableVector3F( magmaCenterX - plate.getSign() * MOUNTAIN_X_OFFSET, maxElevationInTimestep - 6000, -7 * zStep ), millionsOfYears );
            createSmokeAt( new ImmutableVector3F( magmaCenterX + plate.getSign() * MOUNTAIN_X_OFFSET, maxElevationInTimestep - 6000, -8 * zStep ), millionsOfYears );

            createSmokeAt( new ImmutableVector3F( magmaCenterX, maxElevationInTimestep - 6000, -9 * zStep ), millionsOfYears );
            createSmokeAt( new ImmutableVector3F( magmaCenterX - plate.getSign() * MOUNTAIN_X_OFFSET, maxElevationInTimestep - 8000, -10 * zStep ), millionsOfYears );
            createSmokeAt( new ImmutableVector3F( magmaCenterX + plate.getSign() * MOUNTAIN_X_OFFSET, maxElevationInTimestep - 8000, -11 * zStep ), millionsOfYears );

            for ( SmokePuff puff : new ArrayList<SmokePuff>( plate.getModel().smokePuffs ) ) {
                puff.age += millionsOfYears;

                float alpha = ( -puff.age * puff.age / 4 + puff.age );
                if ( alpha < 0 ) {
                    plate.getModel().smokePuffs.remove( puff );
                }
                else {
                    puff.scale.set( (float) Math.sqrt( puff.age ) );
                    puff.alpha.set( alpha / 15 );
                    final ImmutableVector3F heightChange = new ImmutableVector3F( 0, millionsOfYears * 2000f, 0 );

                    // TODO: the adding randoms together actually causes less variation with smaller timesteps
                    final ImmutableVector3F randomness = new ImmutableVector3F(
                            (float) ( puff.age * ( Math.random() - 0.5 ) * 400 ),
                            (float) ( puff.age * ( Math.random() - 0.5 ) * 100 ),
                            0
                    ).times( millionsOfYears * 15 );
                    puff.position.set( puff.position.get().plus( heightChange.plus( randomness )
                    ) );
                }
            }
        }

        /*---------------------------------------------------------------------------*
        * magma tube animation
        *----------------------------------------------------------------------------*/

        magmaTube.setAllAlphas( chamberFullness );
        if ( chamberFullness >= 1 ) {
            // 250m offset down so it doesn't go quite to the top
            float topDelta = maxElevationInTimestep - 250 - magmaTube.getTopElevation( 0 );
            for ( Sample sample : magmaTube.getTopBoundary().samples ) {
                sample.setPosition( sample.getPosition().plus( new ImmutableVector3F( 0, topDelta, 0 ) ) );
                sample.setTextureCoordinates( sample.getTextureCoordinates().plus(
                        plate.getTextureStrategy().mapFrontDelta( new ImmutableVector2F( 0, topDelta ) ) ) );
            }

            float textureDelta = MELT_SPEED * millionsOfYears;
            for ( Sample sample : magmaTube.getSamples() ) {
                sample.setTextureCoordinates( sample.getTextureCoordinates().plus(
                        plate.getTextureStrategy().mapFrontDelta( new ImmutableVector2F( 0, -textureDelta ) ) ) );
            }
        }
    }

    private void createSmokeAt( final ImmutableVector3F location, float millionsOfYears ) {
        if ( maxElevationInTimestep < 0 ) {
            return;
        }
        //            float chance = (float) ( 1 - Math.exp( -millionsOfYears * 10 ) );
        float chance = millionsOfYears / 0.16f / 1.5f;
//        System.out.println( millionsOfYears );

        boolean shouldSmoke = ( Math.random() < chance );

        if ( shouldSmoke ) {
            plate.getModel().smokePuffs.add( new SmokePuff() {{
                position.set( location );
                scale.set( 0.1f );
                alpha.set( 0f );
            }} );
        }
    }

    private void animateMountains( float millionsOfYears ) {
        assert getNumCrustXSamples() == getNumTerrainXSamples();
        for ( int columnIndex = 0; columnIndex < getNumTerrainXSamples(); columnIndex++ ) {
            float x = getTerrain().xPositions.get( columnIndex );

            // TODO: for performance, add in a threshold for how large |x-center| can be
            for ( int rowIndex = 0; rowIndex < getTerrain().getNumRows(); rowIndex++ ) {
                final float theta = getTerrain().zPositions.get( rowIndex ) / MOUNTAIN_Z_PERIOD_FACTOR;
                final float upDownFactor = (float) ( Math.cos( theta ) + 1 ) / 2;

                float periodicOffset = (float) Math.abs( theta / Math.PI );

                int integerOffset = ( (int) ( periodicOffset + 0.5f ) % 3 ); // add in 0.5 since we want to switch at valleys

                // add in offset for mountains
                float myX = x + ( integerOffset == 0 ? 0 : -plate.getSign() * MOUNTAIN_X_OFFSET * ( integerOffset == 1 ? 1 : -1 ) );

                float delta = (float) Math.exp( -Math.abs( myX - magmaCenterX ) / 10000 ) * 400 * millionsOfYears;
                float bottomDelta = (float) Math.exp( -Math.abs( myX - magmaCenterX ) / 30000 ) * 500 * millionsOfYears;

                final TerrainSample terrainSample = getTerrain().getSample( columnIndex, rowIndex );

                float myDelta = delta * upDownFactor * upDownFactor * upDownFactor;

                terrainSample.setElevation( terrainSample.getElevation() + myDelta );

                if ( rowIndex == getTerrain().getFrontZIndex() ) {
                    float newCrustTop = getCrust().getTopElevation( columnIndex ) + myDelta;
                    float newCrustBottom = getCrust().getBottomElevation( columnIndex ) - bottomDelta;
                    float newLithosphereBottom = getLithosphere().getBottomElevation( columnIndex ) - bottomDelta * 2;
                    getCrust().layoutColumn( columnIndex,
                                             newCrustTop, newCrustBottom,
                                             plate.getTextureStrategy(), true );
                    getLithosphere().layoutColumn( columnIndex,
                                                   newCrustBottom, newLithosphereBottom,
                                                   plate.getTextureStrategy(), true );
                }
            }
        }
    }

    private ImmutableVector2F getMagmaChamberTop() {
        return new ImmutableVector2F( magmaCenterX, plate.getPlateType().getCrustTopY() );
    }

    @Override protected void onMagmaRemoved( MagmaRegion magma ) {
        super.onMagmaRemoved( magma );

        chamberFullness = Math.min( 1, chamberFullness + ( plate.getPlateType().isContinental() ? 0.05f : 0.25f ) );

        magmaChamber.setAllAlphas( chamberFullness );
    }
}
