/* Copyright 2004, Sam Reid */
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.energyskatepark.model.Floor;
import edu.colorado.phet.energyskatepark.model.spline.AbstractSpline;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

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
        float offset = 3 * AbstractSpline.SPLINE_THICKNESS / 2;
//        double offset= 0.0;
        PPath earth = new PPath( new Rectangle2D.Double( -100, y - 1000, 10000, 1000 + offset ) );
        earth.setPaint( new Color( 100, 170, 100 ) );
        earth.setStroke( null );
        addChild( earth );

        Line2D.Double line = new Line2D.Double( -100, y + offset, 10000, y + offset );
        PPath path = new PPath( line );
        path.setStroke( new BasicStroke( 0.03f ) );
        path.setStrokePaint( new Color( 0, 130, 0 ) );

        addChild( path );
    }
}
