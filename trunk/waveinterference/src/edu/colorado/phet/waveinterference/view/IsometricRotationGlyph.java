/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.view.util.DoubleGeneralPath;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Mar 23, 2006
 * Time: 12:19:43 AM
 * Copyright (c) Mar 23, 2006 by Sam Reid
 */

public class IsometricRotationGlyph extends RotationGlyph {
    public void update() {
        Function.LinearFunction heightFunction = new Function.LinearFunction( 0, Math.PI / 2, 1, 0 );
        Function.LinearFunction widthFunction = new Function.LinearFunction( 0, Math.PI / 2, 0, getExpansionWidth() );
        double dx = widthFunction.evaluate( getAngle() );//distance from normal edge to tilted edge
        double h = getPrimaryHeight() * heightFunction.evaluate( getAngle() ) / 2;//distance from center to top

        DoubleGeneralPath generalPath = new DoubleGeneralPath();
        generalPath.moveTo( dx * 2, h );
        generalPath.lineToRelative( getPrimaryWidth() - dx * 4, 0 );
        generalPath.lineTo( getPrimaryWidth(), generalPath.getCurrentPoint().getY() + h * 2 );
        Point2D pin1 = generalPath.getCurrentPoint();
        generalPath.lineTo( 0, generalPath.getCurrentPoint().getY() );
        Point2D pin2 = generalPath.getCurrentPoint();
        generalPath.lineTo( dx * 2, h );
        generalPath.closePath();
        getSurface().setPathTo( generalPath.getGeneralPath() );

        DoubleGeneralPath depthPath = new DoubleGeneralPath( pin1 );
        depthPath.lineToRelative( 0, 200 );
        depthPath.lineToRelative( pin2.getX() - pin1.getX(), 0 );
        depthPath.lineTo( pin2 );
        depthPath.closePath();
        getDepth().setPathTo( depthPath.getGeneralPath() );
    }
}
