/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common;

import edu.colorado.phet.piccolo.ShadowHTMLGraphic;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Feb 12, 2005
 * Time: 9:47:54 AM
 * Copyright (c) Feb 12, 2005 by Sam Reid
 */

public class VerticalTextGraphic extends PNode {
    private Font font;
    private String text;
//    private Paint color;

    public VerticalTextGraphic( Font font, String text, Color color, Color outline ) {
        super();
        this.font = font;
        this.text = text;
//        this.color = color;
//        PhetShadowTextGraphic phetTextGraphic = new PhetShadowTextGraphic( component, font, text, color, 1, 1, outline );
//        ShadowHTMLGraphic phetTextGraphic = new ShadowHTMLGraphic( component, text, font, color, 1, 1, outline );
        ShadowHTMLGraphic phetTextGraphic = new ShadowHTMLGraphic( text );//, font, color, 1, 1, outline );
        phetTextGraphic.setColor( color );
        phetTextGraphic.setShadowColor( outline );
        phetTextGraphic.setFont( font );
//        PhetOutlineTextGraphic phetTextGraphic = new PhetOutlineTextGraphic( component, font, text, color, new BasicStroke( 1 ), outline );

        double h = phetTextGraphic.getFullBounds().getHeight();
//        System.out.println( "h = " + h );
//        phetTextGraphic.translate( -h / 2 + 4, -10 );
//        phetTextGraphic.translate( -h/2+8, -10 );
        phetTextGraphic.translate( -3, -10 );
//        phetTextGraphic.translate( -h / 2 + 4, -50);
//        phetTextGraphic.translate( -h / 2 + 4, -13 );
        phetTextGraphic.rotate( -Math.PI / 2 );

        addChild( phetTextGraphic );
    }

    public String getText() {
        return text;
    }
}
