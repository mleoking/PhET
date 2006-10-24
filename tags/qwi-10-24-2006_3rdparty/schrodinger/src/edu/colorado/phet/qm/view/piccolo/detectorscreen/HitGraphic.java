/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.piccolo.detectorscreen;

import edu.colorado.phet.qm.view.colormaps.ColorData;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 1:07:29 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class HitGraphic extends PNode {

    public HitGraphic( double x, double y, int opacity ) {
        this( x, y, opacity, new Color( 235, 230, 255 ) );
    }

    public HitGraphic( double x, double y, int opacity, ColorData rootColor ) {
        this( x, y, opacity, rootColor.toColor( 1.0 ) );
    }

    public HitGraphic( double x, double y, int opacity, Color sourceColor ) {
        double width = 6;
        double height = width;
        Color fill = new Color( sourceColor.getRed(), sourceColor.getGreen(), sourceColor.getBlue(), opacity );
        //50 per time step at transparency 4 looks good
        PPath pt = new PPath( new Ellipse2D.Double( -width / 2, -height / 2, width, height ) );
        pt.setStroke( null );
        pt.setPaint( fill );
        addChild( pt );
        setOffset( x, y );
    }
}
