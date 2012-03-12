// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.behaviors;

import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.platetectonics.model.PlateMotionPlate;
import edu.colorado.phet.platetectonics.model.PlateType;
import edu.colorado.phet.platetectonics.model.Sample;
import edu.colorado.phet.platetectonics.model.TerrainSample;
import edu.colorado.phet.platetectonics.model.regions.MagmaRegion;

public class OverridingBehavior extends PlateBehavior {

    private float magmaCenterX;

    public static final float TOP_MELT_Y = -100000;
    public static final float BOTTOM_MELT_Y = -150000;
    public static final float MELT_PADDING_Y = 10000;

    public static final float MELT_SPEED = 10000f; // pretty slow. like 3mm/year
    public static final float MELT_CHANCE_FACTOR = 0.0002f;

    // melting X positions, determined by commented-out code below. update this if the magma chamber isn't centered properly
    public static final float OLD_MELT_X = 89500;
    public static final float YOUNG_MELT_X = 148500;

    private float chamberFullness = 0;

    private float minElevationInTimestep;
    private float maxElevationInTimestep;

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

//            System.out.println( "center: " + lowMeltPoint.plus( highMeltPoint ).times( 0.5f ) );

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
                minElevationInTimestep = Math.min( minElevationInTimestep, sample.getPosition().y );
                maxElevationInTimestep = Math.max( maxElevationInTimestep, sample.getPosition().y );
            }
        }
    }

    private void animateMountains( float millionsOfYears ) {
        assert getNumCrustXSamples() == getNumTerrainXSamples();
        for ( int columnIndex = 0; columnIndex < getNumTerrainXSamples(); columnIndex++ ) {
            float x = getTerrain().xPositions.get( columnIndex );

            float delta = (float) Math.exp( -Math.abs( x - magmaCenterX ) / 10000 ) * 400 * millionsOfYears;
            float bottomDelta = (float) Math.exp( -Math.abs( x - magmaCenterX ) / 30000 ) * 500 * millionsOfYears;

            // TODO: for performance, add in a threshold for how large |x-center| can be
            for ( int rowIndex = 0; rowIndex < getTerrain().getNumRows(); rowIndex++ ) {
                final TerrainSample terrainSample = getTerrain().getSample( columnIndex, rowIndex );

                final float upDownFactor = (float) ( Math.cos( getTerrain().zPositions.get( rowIndex ) / 10000 ) + 1 ) / 2;
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
