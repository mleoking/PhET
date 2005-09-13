/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 8, 2005
 * Time: 8:11:05 AM
 * Copyright (c) May 8, 2005 by Sam Reid
 */

public class EarthGraphic extends PNode {
    private PPath phetShapeGraphic;
    private RampPanel rampPanel;
    private RampWorld rampWorld;
    public static final Color earthGreen = new Color( 83, 175, 38 );

    public EarthGraphic( RampPanel rampPanel, RampWorld rampWorld ) {
        this.rampPanel = rampPanel;
        this.rampWorld = rampWorld;
//        phetShapeGraphic = new PhetShapeGraphic( rampPanel, null, earthGreen, new BasicStroke( 1 ), Color.black );
        phetShapeGraphic = new PPath( null );
        phetShapeGraphic.setPaint( earthGreen );
        addChild( phetShapeGraphic );
        update();
    }

    private void update() {
        phetShapeGraphic.setPathTo( createShape() );
    }

    private Shape createShape() {
        int y = rampWorld.getRampBaseY();
        int dw = 10000;
        return new Rectangle( -dw, y, 1000 + dw * 2, 100000 );
    }
}
