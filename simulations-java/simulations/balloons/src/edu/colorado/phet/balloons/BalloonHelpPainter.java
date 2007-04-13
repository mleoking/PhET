/* Copyright 2004, Sam Reid */
package edu.colorado.phet.balloons;

import edu.colorado.phet.balloons.common.paint.Painter;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 19, 2005
 * Time: 9:49:45 AM
 * Copyright (c) May 19, 2005 by Sam Reid
 */

public class BalloonHelpPainter implements Painter {
    private BalloonsApplication balloonsApplication;

    public BalloonHelpPainter( BalloonsApplication balloonsApplication ) {
        this.balloonsApplication = balloonsApplication;
    }

    public void paint( Graphics2D g ) {
        g.setFont( new Font( "Lucida Sans", Font.BOLD, 14 ) );
        g.setColor( Color.blue );
        g.drawString( "Rub the balloon", balloonsApplication.getSweaterMaxX(), 50 );
        double height = g.getFont().getStringBounds( "Rub", g.getFontRenderContext() ).getHeight();
        g.drawString( "on the sweater", balloonsApplication.getSweaterMaxX(), (int)( 50 + height ) );

        double w = g.getFont().getStringBounds( "Bring the balloon", g.getFontRenderContext() ).getWidth();
        int x = (int)( balloonsApplication.getWallX() - w - 10 );
        int y = (int)( balloonsApplication.getWallHeight() - height * 5 );
        g.drawString( "Bring the balloon", x, y );
        g.drawString( "near the wall", x, (int)( y + height ) );
    }
}
