/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 8, 2005
 * Time: 8:11:05 AM
 * Copyright (c) May 8, 2005 by Sam Reid
 */

public class EarthGraphic extends CompositePhetGraphic {
    private PhetShapeGraphic phetShapeGraphic;
    private RampPanel rampPanel;

    public EarthGraphic( RampPanel rampPanel ) {
        this.rampPanel = rampPanel;
        Color earthGreen = new Color( 83, 175, 38 );
        phetShapeGraphic = new PhetShapeGraphic( rampPanel, null, earthGreen, new BasicStroke( 1 ), Color.black );
        addGraphic( phetShapeGraphic );
        update();
    }

    private void update() {
        phetShapeGraphic.setShape( createShape() );
    }

    private Shape createShape() {
        int y = rampPanel.getRampBaseY();
        int dw = 10000;
        return new Rectangle( -dw, y, 1000 + dw * 2, 100000 );
    }
}
