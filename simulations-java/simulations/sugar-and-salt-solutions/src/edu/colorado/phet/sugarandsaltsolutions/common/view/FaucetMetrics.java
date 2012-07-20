// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.faucet.FaucetNode;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Absolute locations in model coordinates (in meters) of where particles flow to leave the drain pipe, and where they leave when they exit the drain pipe.
 *
 * @author Sam Reid
 */
public class FaucetMetrics {

    //Location where particles enter the drain faucet
    private final Vector2D inputPoint;

    //Location where particles leave the drain faucet
    public final Vector2D outputPoint;

    //The main model is used to obtain the bounds for the solution
    private final SugarAndSaltSolutionModel model;

    //The width of the opening of the faucet where the water comes out, used to create water rectangle of the right dimension
    public final double faucetWidth;

    //Creates a FaucetMetrics given the faucet node and root node
    public FaucetMetrics( ModelViewTransform transform, SugarAndSaltSolutionModel model, PNode rootNode, FaucetNode faucetNode ) {
        this( model,
              transform.viewToModel( new Vector2D( rootNode.globalToLocal( faucetNode.getGlobalInputCenter() ) ) ),
              transform.viewToModel( new Vector2D( rootNode.globalToLocal( faucetNode.getGlobalOutputCenter() ) ) ),
              transform.viewToModelDeltaX( rootNode.globalToLocal( faucetNode.getGlobalOutputSize() ).getWidth() ) );
    }

    //Creates a FaucetMetrics with the previously computed positions
    public FaucetMetrics( SugarAndSaltSolutionModel model, Vector2D inputPoint, Vector2D outputPoint, double faucetWidth ) {
        this.inputPoint = inputPoint;
        this.outputPoint = outputPoint;
        this.model = model;
        this.faucetWidth = faucetWidth;
    }

    //If the center of the drain pipe input is above the water, move the target input point to be within the drain pipe, but at the surface of the water
    //This is so that solutes can continue flowing out as long as water is flowing out
    public Vector2D getInputPoint() {
        Rectangle2D solutionShapeBounds = model.solution.shape.get().getBounds2D();
        if ( solutionShapeBounds.getBounds2D().getMinY() < inputPoint.toPoint2D().getY() && inputPoint.toPoint2D().getY() < solutionShapeBounds.getBounds2D().getMaxY() ) {
            return inputPoint;
        }
        else {
            return new Vector2D( inputPoint.getX(), solutionShapeBounds.getMaxY() );
        }
    }

    //Copies this FaucetMetrics but with the substituted inputX value to ensure output drain input point is within the fluid so particles can reach it
    public FaucetMetrics clampInputWithinFluid( double inputX ) {
        return new FaucetMetrics( model, new Vector2D( inputX, inputPoint.getY() ), outputPoint, faucetWidth );
    }
}