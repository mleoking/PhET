package edu.colorado.phet.bernoulli.common;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Aug 23, 2003
 * Time: 3:15:26 PM
 * Copyright (c) Aug 23, 2003 by Sam Reid
 */
public class SegmentGraphic implements Graphic, TransformListener {
    private ModelViewTransform2d transform;
    double x;
    double y;
    double x2;
    double y2;
    private Color color;
    private Stroke stroke;
    Point start;
    Point end;

    public SegmentGraphic( ModelViewTransform2d transform, double x, double y, double x2, double y2, Color color, Stroke stroke ) {
        this.transform = transform;
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
        this.stroke = stroke;
        transform.addTransformListener( this );
        update();
    }

    private void update() {
        start = transform.modelToView( x, y );
        end = transform.modelToView( x2, y2 );
    }

    public void paint( Graphics2D g ) {
        if( start != null ) {
            g.setColor( color );
            g.setStroke( stroke );
            g.drawLine( start.x, start.y, end.x, end.y );
        }
    }

    public void transformChanged( ModelViewTransform2d mvt ) {
        update();
    }

    public void setState( double x, double y, double x2, double y2 ) {
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
        update();
    }
}
