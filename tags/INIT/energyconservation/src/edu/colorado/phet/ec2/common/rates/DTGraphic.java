/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.common.rates;

import edu.colorado.phet.common.view.graphics.ObservingGraphic;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Observable;

/**
 * User: Sam Reid
 * Date: Jul 24, 2003
 * Time: 3:43:21 PM
 * Copyright (c) Jul 24, 2003 by Sam Reid
 */
public class DTGraphic implements ObservingGraphic {
    private DecimalFormat format;
    private DTObserver dto;
    private int x;
    private int y;
    private String displayString;
    Font font = new Font( "Times New Roman", 0, 14 );
    Color color = Color.black;
    private boolean visible = true;

    public DTGraphic( DTObserver dto, int x, int y ) {
        this.dto = dto;
        this.x = x;
        this.y = y;
        format = new DecimalFormat( "0.00" );
        dto.addObserver( this );
    }

    public void paint( Graphics2D g ) {
        if( displayString != null && visible ) {
            g.setColor( Color.black );
            g.setFont( font );
            g.drawString( displayString, x, y );
        }
    }

    public void update( Observable o, Object arg ) {
        displayString = "max=" + format.format( dto.getMax() ) + ", min=" + format.format( dto.getMin() ) + ", dt=" + format.format( dto.getDt() ) + ", average dt=" + format.format( dto.getAverage() );
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }
}
