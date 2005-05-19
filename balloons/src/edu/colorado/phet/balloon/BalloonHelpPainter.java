/* Copyright 2004, Sam Reid */
package edu.colorado.phet.balloon;

import phet.paint.Painter;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 19, 2005
 * Time: 9:49:45 AM
 * Copyright (c) May 19, 2005 by Sam Reid
 */

public class BalloonHelpPainter implements Painter {
    private BalloonApplet balloonApplet;

    public BalloonHelpPainter( BalloonApplet balloonApplet ) {
        this.balloonApplet = balloonApplet;
    }

    public void paint( Graphics2D g ) {
        g.setFont( new Font( "Lucida Sans", Font.BOLD, 14 ) );
        g.setColor( Color.blue );
        g.drawString( "Rub the balloon", balloonApplet.getSweaterMaxX(), 50 );
        double height = g.getFont().getStringBounds( "Rub", g.getFontRenderContext() ).getHeight();
        g.drawString( "on the sweater", balloonApplet.getSweaterMaxX(), (int)( 50 + height ) );

        double w = g.getFont().getStringBounds( "Bring the balloon", g.getFontRenderContext() ).getWidth();
        int x = (int)( balloonApplet.getWallX() - w - 10 );
        int y = (int)( balloonApplet.getWallHeight() - height * 5 );
        g.drawString( "Bring the balloon", x, y );
        g.drawString( "near the wall", x, (int)( y + height ) );
    }
}
