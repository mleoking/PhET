/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShadowTextGraphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Feb 12, 2005
 * Time: 9:47:54 AM
 * Copyright (c) Feb 12, 2005 by Sam Reid
 */

public class VerticalTextGraphic extends CompositePhetGraphic {
    private Font font;
    private String text;
    private Paint color;

    public VerticalTextGraphic( Component component, Font font, String text, Color color ) {
        super( component );
        this.font = font;
        this.text = text;
        this.color = color;
//        PhetTextGraphic phetTextGraphic = new PhetTextGraphic( component, font, text, color );
//        phetTextGraphic.rotate( -Math.PI / 2 );
        PhetShadowTextGraphic phetTextGraphic = new PhetShadowTextGraphic( component, font, text, color, 1, 1, Color.yellow );

        phetTextGraphic.rotate( -Math.PI / 2 );
        addGraphic( phetTextGraphic );
    }
}
