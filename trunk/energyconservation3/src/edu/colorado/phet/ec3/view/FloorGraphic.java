/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.view;

import edu.colorado.phet.ec3.model.Floor;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * User: Sam Reid
 * Date: Sep 26, 2005
 * Time: 12:59:47 PM
 * Copyright (c) Sep 26, 2005 by Sam Reid
 */

public class FloorGraphic extends PNode {
    private Floor floor;

    public FloorGraphic( Floor floor ) {
        this.floor = floor;
        double y = floor.getY();
        Line2D.Double line = new Line2D.Double( -100, y, 10000, y );
        PPath path = new PPath( line );
        path.setStroke( new BasicStroke( 3 ) );
        path.setStrokePaint( Color.black );
        addChild( path );
    }
}
