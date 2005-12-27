/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.common;

import edu.umd.cs.piccolo.PNode;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Dec 26, 2005
 * Time: 10:30:05 PM
 * Copyright (c) Dec 26, 2005 by Sam Reid
 */

public class PhetPNode extends PNode {
    public void setOffset( Point2D point ) {
        if( !getOffset().equals( point ) ) {
            super.setOffset( point );
        }
    }

    public void setOffset( double x, double y ) {
        if( getOffset().getX() != x || getOffset().getY() != y ) {
            super.setOffset( x, y );
        }
    }

}
