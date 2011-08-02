// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;

/**
 * Absolute locations in model coordinates (in meters) of where particles flow to leave the drain pipe, and where they leave when they exit the drain pipe.
 *
 * @author Sam Reid
 */
public class DrainFaucetMetrics {

    //Location where particles enter the drain faucet
    private final ImmutableVector2D inputPoint;

    //Location where particles leave the drain faucet
    public final ImmutableVector2D outputPoint;

    //The main model is used to obtain the bounds for the solution
    private SugarAndSaltSolutionModel model;

    public DrainFaucetMetrics( ImmutableVector2D inputPoint, ImmutableVector2D outputPoint, SugarAndSaltSolutionModel model ) {
        this.inputPoint = inputPoint;
        this.outputPoint = outputPoint;
        this.model = model;
    }

    //If the center of the drain pipe input is above the water, move the target input point to be within the drain pipe, but at the surface of the water
    //This is so that solutes can continue flowing out as long as water is flowing out
    public ImmutableVector2D getInputPoint() {
        Rectangle2D solutionShapeBounds = model.solution.shape.get().getBounds2D();
        if ( solutionShapeBounds.contains( inputPoint.toPoint2D() ) ) {
            return inputPoint;
        }
        else {
            return new ImmutableVector2D( inputPoint.getX(), solutionShapeBounds.getMaxY() );
        }
    }
}