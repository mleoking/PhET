// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MutableVector2D;

public class LacYTransformationArrow extends TransformationArrow {

    private static final double ARROW_LENGTH = 5;
    private final LacY lacYToAddToModel;

    public LacYTransformationArrow( IGeneNetworkModelControl model, Point2D initialPosition, LacY lacY, double pointingAngle ) {
        super( model, initialPosition, ARROW_LENGTH, true, pointingAngle );
        lacYToAddToModel = lacY;
    }

    @Override
    protected void onTransitionToFadingOutState() {
        // Time to add our LacY to the model.
        double xOffset = Math.cos( getPointingAngle() ) * getHeadHeight() + 6;
        double yOffset = Math.sin( getPointingAngle() ) * getHeadHeight() + 4;
        lacYToAddToModel.setPosition( getPositionRef().getX() + xOffset, getPositionRef().getY() + yOffset );
        lacYToAddToModel.setMotionStrategy( new LinearMotionStrategy( getModel().getInteriorMotionBoundsAboveDna(),
                                                                      lacYToAddToModel.getPositionRef(), new MutableVector2D( getVelocityRef() ), 5.0 ) );
        getModel().addLacY( lacYToAddToModel );
    }
}
