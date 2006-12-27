/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2;

import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 23, 2003
 * Time: 3:12:39 AM
 * Copyright (c) Sep 23, 2003 by Sam Reid
 */
public class StemGraphic implements Graphic {
    int x1;
    int y1;
    int x2;
    int y2;
    private boolean init = false;

    public void paint( Graphics2D g ) {
        if( init ) {
            g.setColor( Color.black );
            g.setStroke( new BasicStroke( 2 ) );
            g.drawLine( x1, y1, x2, y2 );
        }
    }

    public void setState( int x1, int y1, int x2, int y2 ) {
        this.x1 = x1;
        this.y1 = y1;
        this.y2 = y2;
        this.x2 = x2;
        this.init = true;
    }
}
