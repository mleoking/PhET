// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.behaviors;

import java.util.List;

import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.model.PlateMotionModel.PlateType;
import edu.colorado.phet.platetectonics.model.PlateMotionPlate;
import edu.colorado.phet.platetectonics.model.Sample;
import edu.colorado.phet.platetectonics.model.TerrainSample;
import edu.colorado.phet.platetectonics.model.regions.Boundary;
import edu.colorado.phet.platetectonics.model.regions.Region;
import edu.colorado.phet.platetectonics.util.Side;

public class RiftingBehavior extends PlateBehavior {

    private float timeElapsed = 0;

    public static final float RIDGE_TOP_Y = -1500;
    public static final float SPREAD_START_TIME = 10.0f;

    public static final float RIFT_PLATE_SPEED = 30000f / 2;

    public RiftingBehavior( final PlateMotionPlate plate, PlateMotionPlate otherPlate ) {
        super( plate, otherPlate );

        plate.getCrust().moveToFront();
    }

    @Override public void stepInTime( float millionsOfYears ) {
        timeElapsed += millionsOfYears;

        // TODO: why are we having terrain issues with this?
//        removeEarthEdges();

        moveSpreading( millionsOfYears );
    }

    private void moveSpreading( float millionsOfYears ) {
        float idealChunkWidth = plate.getSimpleChunkWidth();

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
            final Sample centerTopSample = getSampleFromCenter( plate.getCrust().getTopBoundary(), 0 );
            final Sample nextSample = getSampleFromCenter( plate.getCrust().getTopBoundary(), 1 );
            final float currentWidth = Math.abs( nextSample.getPosition().x - centerTopSample.getPosition().x );
            if ( currentWidth < idealChunkWidth * 1.001 ) {
                float currentPosition = centerTopSample.getPosition().x;
                float idealPosition = nextSample.getPosition().x - idealChunkWidth * plate.getSide().getSign();

                if ( idealPosition * plate.getSign() < 0 ) {
                    idealPosition = 0;
                }

                shiftColumn( plate.getSide().opposite().getIndex( plate.getCrust().getTopBoundary().samples ), idealPosition - currentPosition );
            }
        }

        /*---------------------------------------------------------------------------*
        * add fresh crust
        *----------------------------------------------------------------------------*/
        while ( getSampleFromCenter( plate.getCrust().getTopBoundary(), 0 ).getPosition().x * plate.getSide().getSign() > 0.0001 ) {
            final Side zeroSide = plate.getSide().opposite();
            plate.addSection( zeroSide, PlateType.YOUNG_OCEANIC );
            { // update the new section positions
                // re-layout that section
                final int newIndex = zeroSide.getIndex( plate.getCrust().getTopBoundary().samples );
                final float crustBottom = RIDGE_TOP_Y - PlateType.YOUNG_OCEANIC.getCrustThickness();
                plate.getCrust().layoutColumn( newIndex,
                                               RIDGE_TOP_Y,
                                               crustBottom,
                                               plate.getTextureStrategy(), true ); // essentially reset the textures

                // make the mantle part of the lithosphere have zero thickness here
                plate.getLithosphere().layoutColumn( newIndex,
                                                     crustBottom, crustBottom,
                                                     plate.getTextureStrategy(), true );

                for ( TerrainSample sample : plate.getTerrain().getColumn( newIndex ) ) {
                    sample.setElevation( RIDGE_TOP_Y );
                }
            }

            // TODO: add in correct different temp/density and lithosphere thickness

            // this will reference the newly created section top are on the same side, then we need to process the heights like normal
            if ( getSampleFromCenter( plate.getCrust().getTopBoundary(), 0 ).getPosition().x * plate.getSide().getSign() > 0 ) {

            }
            else {
                final List<Sample> topSamples = plate.getCrust().getTopBoundary().samples;
                final int index = zeroSide.getIndex( topSamples );

                // shift over the entire column
                shiftColumn( index, -zeroSide.getEnd( topSamples ).getPosition().x );
            }
        }

        /*---------------------------------------------------------------------------*
        * handle oceanic crust changes
        *----------------------------------------------------------------------------*/



        riftPostProcess();
    }

    private void shiftColumn( int columnIndex, float xOffset ) {
        for ( Region region : new Region[] { plate.getCrust(), plate.getLithosphere() } ) {
            for ( Boundary boundary : region.getBoundaries() ) {
                boundary.samples.get( columnIndex ).shiftWithTexture( new ImmutableVector3F( xOffset, 0, 0 ), plate.getTextureStrategy() );
            }
        }

        plate.getTerrain().xPositions.set( columnIndex, plate.getTerrain().xPositions.get( columnIndex ) + xOffset );
        final ImmutableVector2F offset2D = new ImmutableVector2F( xOffset, 0 );

        for ( TerrainSample sample : plate.getTerrain().getColumn( columnIndex ) ) {
            sample.shiftWithTexture( offset2D, plate.getTextureStrategy() );
        }
        plate.getTerrain().columnsModified.updateListeners();
    }

    private void riftPostProcess() {
        getPlate().getTerrain().elevationChanged.updateListeners();

        glueMantleTopToLithosphere( 1000 );
        redistributeMantle();
    }

    private Sample getSampleFromCenter( Boundary boundary, int offsetFromCenter ) {
        return boundary.getEdgeSample( getPlate().getSide().opposite(), offsetFromCenter );
    }
}
