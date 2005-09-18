/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.piccolo;

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

public class DetectionGraphic extends PNode {

    public DetectionGraphic( DetectorSheet detectorSheet, int x, int y, int opacity ) {
        super();
        int width = 6;
        int height = width;
        Color fill = new Color( 235, 230, 255, opacity );//50 per time step at transparency 4 looks good
        PPath pt = new PPath( new Ellipse2D.Double( -width / 2, -height / 2, width, height ) );
        pt.setPaint( fill );
        addChild( pt );
        setOffset( x, y );
    }


}
