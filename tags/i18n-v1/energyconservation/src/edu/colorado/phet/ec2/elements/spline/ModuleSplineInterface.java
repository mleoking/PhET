package edu.colorado.phet.ec2.elements.spline;


/**
 * User: Sam Reid
 * Date: Jul 28, 2003
 * Time: 12:02:59 AM
 * Copyright (c) Jul 28, 2003 by Sam Reid
 */
public interface ModuleSplineInterface {
//    void remove(ModelElement me,Graphic g);

    void removeSpline( SplineGraphic splineGraphic );

    void addSpline( Spline spline );
}
