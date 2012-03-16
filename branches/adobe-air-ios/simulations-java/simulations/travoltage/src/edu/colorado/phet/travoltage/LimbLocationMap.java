// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.travoltage;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jul 1, 2006
 * Time: 11:02:16 PM
 */

public class LimbLocationMap implements LocationMap {
    private LegNode legNode;
    private ArmNode armNode;
    private LocationMap defaultMap;
    private TravoltageBodyNode bodyNode;

    public LimbLocationMap( LegNode legNode, ArmNode armNode, LocationMap defaultMap, TravoltageBodyNode bodyNode ) {
        this.legNode = legNode;
        this.armNode = armNode;
        this.defaultMap = defaultMap;
        this.bodyNode = bodyNode;
    }

    public Point2D getLocation( Point2D pt ) {
        Rectangle legRect = new Rectangle();
        legRect.setFrameFromDiagonal( 128.0, 237.0, 279.0, 399.0 );
        if( legRect.contains( pt ) ) {
            Point2D pivot = legNode.getGlobalPivot();
            pivot = bodyNode.globalToLocal( pivot );
            AffineTransform tx = AffineTransform.getRotateInstance( legNode.getAngle(), pivot.getX(), pivot.getY() );
            return defaultMap.getLocation( tx.transform( pt, null ) );
        }

        Rectangle armRect = new Rectangle();
        armRect.setFrameFromDiagonal( 198.0, 118.0, 330.0, 200.0 );
        if( armRect.contains( pt ) ) {
            Point2D pivot = armNode.getGlobalPivot();
            pivot = bodyNode.globalToLocal( pivot );
            AffineTransform tx = AffineTransform.getRotateInstance( armNode.getAngle(), pivot.getX(), pivot.getY() );
            return defaultMap.getLocation( tx.transform( pt, null ) );
        }
        return defaultMap.getLocation( pt );
    }

}
