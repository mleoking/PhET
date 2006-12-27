package edu.colorado.phet.bernoulli.common;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Aug 22, 2003
 * Time: 4:09:57 PM
 * Copyright (c) Aug 22, 2003 by Sam Reid
 */
public class CircleGraphic implements Graphic, TransformListener {
    private int x;
    private int y;
    private int radius;
    private Color color;
    Point2D.Double modelPoint;
    private double modelRadius;
    private ModelViewTransform2d transform;
    boolean ready = false;

    public CircleGraphic( Point2D.Double modelPoint, double modelRadius, Color color, ModelViewTransform2d transform ) {
        if( modelPoint == null ) {
            throw new RuntimeException( "Null model point." );
        }
        this.modelPoint = modelPoint;
        this.modelRadius = modelRadius;
        this.transform = transform;
        this.color = color;
        transform.addTransformListener( this );
        transformChanged( transform );
    }

    public void paint( Graphics2D g ) {
        if( !ready ) {
            return;
        }
        g.setColor( color );
        g.fillOval( x - radius / 2, y - radius / 2, radius, radius );
    }

    public void transformChanged( ModelViewTransform2d mvt ) {
        ready = true;
        if( modelPoint == null ) {
            throw new RuntimeException( "Null model point." );
        }
        Point view = transform.modelToView( modelPoint );
        this.x = view.x;
        this.y = view.y;
//        this.radius = transform.modelToViewDifferentialX(modelRadius);
        this.radius = 15;
    }

    public boolean containsPoint( Point point ) {
        double distance = point.distance( x, y );
        if( distance <= radius ) {
            return true;
        }
        return false;
    }

    public void setLocation( Point2D.Double pt ) {
        this.modelPoint = pt;
        transformChanged( transform );
    }

}
