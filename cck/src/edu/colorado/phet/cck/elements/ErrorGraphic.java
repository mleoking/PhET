/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck.elements;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Dec 6, 2003
 * Time: 9:31:00 PM
 * Copyright (c) Dec 6, 2003 by Sam Reid
 */
public class ErrorGraphic implements Graphic {
    public static boolean errorTextIsVisible = false;
    private Font errorFont = new Font( "Lucida Sans", Font.ITALIC, 28 );
    ApparatusPanel apparatusPanel;
    String errString;

    public ErrorGraphic( ApparatusPanel apparatusPanel ) {
        this.apparatusPanel = apparatusPanel;
        this.errString = "Internal Error Detected:  Simulation Unreliable";
    }

    public void paint( Graphics2D g ) {
        if( errorTextIsVisible ) {
            g.setColor( Color.red );
            g.setFont( errorFont );
            float x = 10;
            float y = ( (float)( apparatusPanel.getHeight() - errorFont.getStringBounds( errString, g.getFontRenderContext() ).getHeight() ) );
            g.drawString( errString, x, y );
            x = x + .5F;
            y = y + .5F;
            g.setColor( Color.black );
            g.drawString( errString, x, y );
        }
    }

}
