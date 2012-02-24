// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.behaviors;

import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.model.PlateMotionPlate;
import edu.colorado.phet.platetectonics.model.Sample;
import edu.colorado.phet.platetectonics.model.regions.Boundary;
import edu.colorado.phet.platetectonics.model.regions.Region;
import edu.colorado.phet.platetectonics.util.Side;

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

    public Side getSide() {
        return getPlate().getSide();
    }

    protected void createEarthEdges() {
        while ( getSide().opposite().isToSideOf(
                getPlate().getCrust().getTopBoundary().getEdgeSample( getSide() ).getPosition().x,
                PlateBehavior.PLATE_X_LIMIT * getSide().getSign() ) ) {
            getPlate().addSection( getSide() );
        }
    }

    protected void removeEarthEdges() {
        while ( getSide().isToSideOf(
                getPlate().getCrust().getTopBoundary().getEdgeSample( getSide() ).getPosition().x,
                PlateBehavior.PLATE_X_LIMIT * getSide().getSign() ) ) {
            getPlate().removeSection( getSide() );
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
