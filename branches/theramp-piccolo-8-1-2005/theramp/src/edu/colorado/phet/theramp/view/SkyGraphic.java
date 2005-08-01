/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 8, 2005
 * Time: 8:15:23 AM
 * Copyright (c) May 8, 2005 by Sam Reid
 */

public class SkyGraphic extends PNode {
    private RampPanel rampPanel;
    private RampWorld rampWorld;
    private PPath phetShapeGraphic;

    public SkyGraphic( RampPanel rampPanel, RampWorld rampWorld ) {
        this.rampPanel = rampPanel;
        this.rampWorld = rampWorld;
        Color lightBlue = new Color( 165, 220, 252 );
        phetShapeGraphic = new PPath();
        phetShapeGraphic.setPaint( lightBlue );
        addChild( phetShapeGraphic );
        update();
    }

    private void update() {
        phetShapeGraphic.setPathTo( createShape() );
    }

    private Shape createShape() {
        int dw = 10000;
        int dh = 10000;
        Rectangle skyRect = new Rectangle( -dw, -dh, 1000 + dw * 2, rampWorld.getRampBaseY() + dh );
        return skyRect;
    }
}
