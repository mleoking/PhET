/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 8, 2005
 * Time: 8:15:23 AM
 * Copyright (c) May 8, 2005 by Sam Reid
 */

public class SkyGraphic extends CompositePhetGraphic {
    private RampPanel rampPanel;
    private PhetShapeGraphic phetShapeGraphic;

    public SkyGraphic( RampPanel rampPanel ) {
        this.rampPanel = rampPanel;
        Color lightBlue = new Color( 165, 220, 252 );
        phetShapeGraphic = new PhetShapeGraphic( rampPanel, null, lightBlue );
        addGraphic( phetShapeGraphic );
        update();
    }

    private void update() {
        phetShapeGraphic.setShape( createShape() );
    }

    private Shape createShape() {
        Rectangle skyRect = new Rectangle( 0, 0, 1000, rampPanel.getRampBaseY() );
        return skyRect;
    }
}
