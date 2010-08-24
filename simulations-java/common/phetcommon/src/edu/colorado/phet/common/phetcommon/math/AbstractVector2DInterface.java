/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.common.phetcommon.math;

import java.awt.geom.Point2D;

/**
 * High level abstraction of a 2-D Vector data structure.
 * The two main implementations are the Vector2D and ImmutableVector2D.
 *
 * @author Ron LeMaster
 * @author Sam Reid
 * @version $Revision$
 */
public interface AbstractVector2DInterface {

    AbstractVector2DInterface getAddedInstance( AbstractVector2DInterface v );

    AbstractVector2DInterface getSubtractedInstance( AbstractVector2DInterface v );

    AbstractVector2DInterface getAddedInstance( double x, double y );

    AbstractVector2DInterface getSubtractedInstance( double x, double y );

    AbstractVector2DInterface getScaledInstance( double scale );

    AbstractVector2DInterface getNormalVector();

    AbstractVector2DInterface getNormalizedInstance();

    double getY();

    double getX();

    double getMagnitudeSq();

    double getMagnitude();

    double dot( AbstractVector2DInterface v );

    double getAngle();

    AbstractVector2DInterface getInstanceOfMagnitude( double magnitude );

    Point2D toPoint2D();

    double getCrossProductScalar( AbstractVector2DInterface v );

    Point2D getDestination( Point2D startPt );

    AbstractVector2DInterface getRotatedInstance( double angle );


}
