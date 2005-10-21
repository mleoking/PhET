/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.view;

import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Sep 29, 2005
 * Time: 10:26:39 PM
 * Copyright (c) Sep 29, 2005 by Sam Reid
 */

public class SplineMatch {
    private SplineGraphic target;
    private int index;

    public SplineMatch( SplineGraphic target, int index ) {
        this.target = target;
        this.index = index;
    }

    public PNode getTarget() {
        return target.getControlPointGraphic( index );
    }

    public SplineGraphic getSplineGraphic() {
        return target;
    }

    public AbstractSpline getTopSplineMatch() {
        return target.getSplineSurface().getTop();
    }

    public boolean matchesBeginning() {
        return index == 0;
    }

    public boolean matchesEnd() {
        return index == getTopSplineMatch().numControlPoints() - 1;
    }
}
