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
        sky.setPaint( new Color( 170, 200, 220 ) );
        addChild( sky );
    }
}
