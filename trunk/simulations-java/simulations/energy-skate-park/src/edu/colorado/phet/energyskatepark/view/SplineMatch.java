// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.energyskatepark.model.EnergySkateParkSpline;
import edu.colorado.phet.energyskatepark.view.piccolo.SplineNode;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Sep 29, 2005
 * Time: 10:26:39 PM
 */

public class SplineMatch {
    private SplineNode target;
    private int index;

    public SplineMatch( SplineNode target, int index ) {
        this.target = target;
        this.index = index;
    }

    public PNode getTarget() {
        return target.getControlPointGraphic( index );
    }

    public SplineNode getSplineGraphic() {
        return target;
    }

    public EnergySkateParkSpline getEnergySkateParkSpline() {
        return target.getSpline();
    }

    public boolean matchesBeginning() {
        return index == 0;
    }

    public boolean matchesEnd() {
        return index == getEnergySkateParkSpline().numControlPoints() - 1;
    }
}
