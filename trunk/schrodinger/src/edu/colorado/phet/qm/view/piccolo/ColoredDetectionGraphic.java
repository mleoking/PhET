/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.piccolo;

import edu.colorado.phet.qm.view.colormaps.PhotonColorMap;
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

public class ColoredDetectionGraphic extends PNode {

    public ColoredDetectionGraphic( int x, int y, int opacity, PhotonColorMap.ColorData rootColor ) {
        int width = 6;
        int height = width;
        Color sourceColor = rootColor.toColor( 1.0 );
//        System.out.println( "opacity = " + opacity );
        Color fill = new Color( sourceColor.getRed(), sourceColor.getGreen(), sourceColor.getBlue(), opacity );
        //50 per time step at transparency 4 looks good
        PPath pt = new PPath( new Ellipse2D.Double( -width / 2, -height / 2, width, height ) );
        pt.setStroke( null );
        pt.setPaint( fill );
        addChild( pt );
        setOffset( x, y );
    }

}
