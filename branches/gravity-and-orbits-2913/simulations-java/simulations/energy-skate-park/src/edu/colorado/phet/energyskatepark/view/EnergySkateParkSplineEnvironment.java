// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.energyskatepark.view.piccolo.SplineNode;

import java.awt.geom.Point2D;

/**
 * Author: Sam Reid
 * Mar 16, 2007, 12:44:54 PM
 */
public interface EnergySkateParkSplineEnvironment {
    void removeSpline( SplineNode splineNode );

    SplineMatch proposeMatch( SplineNode splineNode, Point2D toMatch );

    void attach( SplineNode splineNode, int index, SplineMatch match );
}
