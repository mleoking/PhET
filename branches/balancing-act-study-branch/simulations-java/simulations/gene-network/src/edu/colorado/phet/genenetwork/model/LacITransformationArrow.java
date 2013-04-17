// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;

public class LacITransformationArrow extends TransformationArrow {

    private static final double ARROW_LENGTH = 5;
    private final LacI lacIToAddToModel;

    public LacITransformationArrow( IGeneNetworkModelControl model, Point2D initialPosition, LacI lacI, double pointingAngle ) {
        super( model, initialPosition, ARROW_LENGTH, true, pointingAngle );
        lacIToAddToModel = lacI;
    }

    @Override
    protected void onTransitionToExistingState() {
        // Time to add our LacI to the model.
        double xOffset = Math.cos( getPointingAngle() ) * getHeadHeight() + 6;
        double yOffset = Math.sin( getPointingAngle() ) * getHeadHeight() + 4;
        lacIToAddToModel.setPosition( getPositionRef().getX() + xOffset, getPositionRef().getY() + yOffset );
        lacIToAddToModel.setMotionStrategy( new LinearMotionStrategy(
                MotionBoundsTrimmer.trim( getModel().getInteriorMotionBoundsAboveDna(), lacIToAddToModel ),
                lacIToAddToModel.getPositionRef(),
                new MutableVector2D( getVelocityRef() ),
                5.0 ) );
        getModel().addLacI( lacIToAddToModel );
    }
}
