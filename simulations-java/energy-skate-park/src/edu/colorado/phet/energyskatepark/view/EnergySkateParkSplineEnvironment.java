package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.energyskatepark.model.EnergySkateParkSpline;

import java.awt.geom.Point2D;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Mar 16, 2007
 * Time: 12:44:54 PM
 * To change this template use File | Settings | File Templates.
 */
public interface EnergySkateParkSplineEnvironment {
    void removeSpline( SplineNode splineNode );

    SplineMatch proposeMatch( SplineNode splineNode, Point2D toMatch );

    void splineTranslated( EnergySkateParkSpline splineSurface, double dx, double dy );

    void attach( SplineNode splineNode, int index, SplineMatch match );
}
