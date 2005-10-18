/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common;

import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.ShadowHTMLGraphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 1, 2005
 * Time: 9:23:38 AM
 * Copyright (c) Jun 1, 2005 by Sam Reid
 */

public class CurlyLabel extends GraphicLayerSet {
    private String label;
    public PhetImageGraphic phetImageGraphic;
    public ShadowHTMLGraphic textGraphic;

    public CurlyLabel( Component component, String label ) {
        super( component );
        this.label = label;
        phetImageGraphic = new PhetImageGraphic( component, "images/curly-label-200x50.gif" );
        addGraphic( phetImageGraphic );
        Font font = new Font( "Lucida Sans", Font.BOLD, 34 );
//        phetShadowTextGraphic = new PhetShadowTextGraphic( component, font, label, Color.blue, 1, 1, Color.black );

        textGraphic = new ShadowHTMLGraphic( component, label, font, Color.green, 1, 1, Color.black );
        addGraphic( textGraphic );
        textGraphic.scale( 1.3, 1.3 );
        int offsetDY = 2;
        textGraphic.setLocation( phetImageGraphic.getWidth() / 2 - textGraphic.getWidth() / 2, phetImageGraphic.getHeight() + offsetDY );
    }

    public void rescaleToWidth( int width ) {
        double currentWidth = getWidth();
        double scale = width / currentWidth;
        scale( scale, scale );
    }
}
