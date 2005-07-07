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

    public DetectionGraphic( DetectorSheet detectorSheet, int x, int y, int opacity ) {
        super( detectorSheet.getComponent() );
        int width = 6;
        int height = width;
//        PhetShapeGraphic pt = new PhetShapeGraphic( getComponent(), new Ellipse2D.Double( -width / 2, -height / 2, width, height ), new Color( 150,150,255), new BasicStroke( 1 ), Color.black );
//        Color fill = new Color( 100, 100, 255, 200 );
//        Color fill = new Color( 100, 100, 255, 10);
        Color fill = new Color( 100, 100, 255, opacity );//50 per time step at transparency 4 looks good
        PhetShapeGraphic pt = new PhetShapeGraphic( getComponent(), new Ellipse2D.Double( -width / 2, -height / 2, width, height ), fill );
//        , new BasicStroke( 1 ), Color.black );
        addGraphic( pt );
        setLocation( x, y );
    }


}
