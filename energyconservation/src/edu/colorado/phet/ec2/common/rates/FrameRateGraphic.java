/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.common.rates;

import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

/**
 * User: Sam Reid
 * Date: Jul 24, 2003
 * Time: 3:28:21 PM
 * Copyright (c) Jul 24, 2003 by Sam Reid
 */
public class FrameRateGraphic implements Observer, Graphic {
    private FrameRate fr;
    private DecimalFormat format;
    private String displayString;
    private Font font = new Font( "Lucida", 0, 12 );
    private Color color = Color.black;
    private int x;
    private int y;

    public FrameRateGraphic( FrameRate fr, int x, int y ) {
        this.x = x;
        this.y = y;
        fr.addObserver( this );
        this.fr = fr;
        format = new DecimalFormat( "#0.0#" );
    }

    public void update( Observable o, Object arg ) {
        double frameRate = fr.getFrameRate();
        this.displayString = format.format( frameRate ) + " fps";
    }

    public void paint( Graphics2D g ) {
        if( displayString != null ) {
            g.setFont( font );
            g.setColor( color );
            g.drawString( displayString, x, y );
        }
    }
}
