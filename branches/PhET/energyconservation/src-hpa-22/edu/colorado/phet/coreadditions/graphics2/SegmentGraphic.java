/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.graphics2;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.coreadditions.controllers.AbstractShape;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * User: Sam Reid
 * Date: Sep 16, 2003
 * Time: 10:05:22 PM
 * Copyright (c) Sep 16, 2003 by Sam Reid
 */
public class SegmentGraphic implements Graphic, AbstractShape {
    int x0;
    int y0;
    int x1;
    int y1;
    Shape segment;
    private Stroke stroke;
    private Color color;

    public SegmentGraphic( Stroke stroke, Color color ) {
        this.stroke = stroke;
        this.color = color;
    }

    public void setStroke( Stroke stroke ) {
        this.stroke = stroke;
    }

    public void setColor( Color color ) {
        this.color = color;
    }

    public void paint( Graphics2D g ) {
        if( segment != null ) {
            g.setColor( color );
            g.setStroke( stroke );
            g.fill( segment );
        }
    }

    public boolean contains( Point pt ) {
        return segment.contains( pt );
    }

    public void setState( int x, int y, int x1, int y1 ) {
        this.x0 = x;
        this.y0 = y;
        this.x1 = x1;
        this.y1 = y1;
        segment = stroke.createStrokedShape( new Line2D.Double( x0, y0, x1, y1 ) );
    }

    public boolean containsPoint( Point pt ) {
        return segment.contains( pt );
    }
}
