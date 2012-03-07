// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.behaviors;

import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.model.PlateMotionPlate;
import edu.colorado.phet.platetectonics.model.Sample;
import edu.colorado.phet.platetectonics.model.TerrainSample;
import edu.colorado.phet.platetectonics.model.regions.Boundary;
import edu.colorado.phet.platetectonics.model.regions.Region;

public class RiftingBehavior extends PlateBehavior {

    private float timeElapsed = 0;

    public static final float RIDGE_TOP_Y = -2500;
    public static final float SPREAD_START_TIME = 10.0f;

    public static final float RIFT_PLATE_SPEED = 30000f / 2;

    public RiftingBehavior( final PlateMotionPlate plate, PlateMotionPlate otherPlate ) {
        super( plate, otherPlate );
    }

    @Override public void stepInTime( float millionsOfYears ) {
        timeElapsed += millionsOfYears;

        // TODO: why are we having terrain issues with this?
//        removeEarthEdges();

        moveSpreading( millionsOfYears );
    }

    private void moveSpreading( float millionsOfYears ) {
        float chunkWidth = plate.getSimpleChunkWidth();

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

        if ( getSampleFromCenter( plate.getCrust().getTopBoundary(), 0 ).getPosition().x * plate.getSide().getSign() > 0 ) {
            plate.addSection( plate.getSide().opposite() );
        }

        riftPostProcess();
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
