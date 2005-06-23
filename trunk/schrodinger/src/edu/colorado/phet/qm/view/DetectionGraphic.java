/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 1:07:29 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class DetectionGraphic extends GraphicLayerSet {
    private DetectorSheet detectorSheet;
    private int x;
    private int y;

    public DetectionGraphic( DetectorSheet detectorSheet, int x, int y ) {
        super( detectorSheet.getComponent() );
        this.detectorSheet = detectorSheet;
        this.x = x;
        this.y = y;
        int width = 6;
        int height = 6;
        PhetShapeGraphic pt = new PhetShapeGraphic( getComponent(), new Ellipse2D.Double( -width / 2, -height / 2, width, height ), Color.blue, new BasicStroke( 1 ), Color.black );
        addGraphic( pt );
        setLocation( x, y );
    }
}
