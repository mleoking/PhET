package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.energyskatepark.model.physics.ParametricFunction2D;

import java.awt.geom.Point2D;

/**
 * Author: Sam Reid
 * Mar 27, 2007, 3:14:03 PM
 */
public class TraversalState {
    private ParametricFunction2D parametricFunction2D;
    private boolean top;
    private double alpha;

    public TraversalState( ParametricFunction2D parametricFunction2D, boolean top, double alpha ) {
        this.parametricFunction2D = parametricFunction2D;
        this.top = top;
        this.alpha = alpha;
    }

    public ParametricFunction2D getParametricFunction2D() {
        return parametricFunction2D;
    }

    public boolean isTop() {
        return top;
    }

    public double getAlpha() {
        return alpha;
    }

    public Point2D getPosition() {
        return parametricFunction2D.evaluate( alpha );
    }
}
