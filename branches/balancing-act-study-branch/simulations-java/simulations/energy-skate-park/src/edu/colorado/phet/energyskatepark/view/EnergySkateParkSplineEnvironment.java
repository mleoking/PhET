// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.energyskatepark.model.EnergySkateParkSpline;
import edu.colorado.phet.energyskatepark.view.piccolo.SplineNode;

/**
 * Author: Sam Reid
 * Mar 16, 2007, 12:44:54 PM
 */
public interface EnergySkateParkSplineEnvironment {
    void removeSpline( SplineNode splineNode );

    SplineMatch proposeMatch( SplineNode splineNode, Point2D toMatch );

    EnergySkateParkSpline attach( SplineNode splineNode, int index, SplineMatch match );

    double getMinDragY();

    double getMaxDragY();

    double getMinDragX();

    double getMaxDragX();

    void notifySplineDeletedByUser();

    void setRollerCoasterMode( boolean rollerCoasterMode );
}
