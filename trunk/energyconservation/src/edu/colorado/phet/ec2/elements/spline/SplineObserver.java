package edu.colorado.phet.ec2.elements.spline;

/**
 * User: Sam Reid
 * Date: Jul 26, 2003
 * Time: 9:59:10 AM
 * Copyright (c) Jul 26, 2003 by Sam Reid
 */
public interface SplineObserver {
    public void splineTranslated( Spline source, double dx, double dy );

    public void pointStructureChanged( Spline source );
}
