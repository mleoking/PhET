/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Sep 30, 2005
 * Time: 7:33:21 AM
 * Copyright (c) Sep 30, 2005 by Sam Reid
 */

public class SkyGraphic extends PNode {
    public SkyGraphic( double y ) {
        PPath sky = new PPath( new Rectangle2D.Double( -100, y - 1000, 10000, 1000 ) );
//        earth.setPaint( Color.green );
        sky.setPaint( new Color( 170, 200, 220 ) );
        addChild( sky );

//        Line2D.Double line = new Line2D.Double( -100, y, 10000, y );
//        PPath path = new PPath( line );
//        path.setStroke( new BasicStroke( 3 ) );
//        path.setStrokePaint( Color.black );
//
//
//        addChild( path );
    }
}
