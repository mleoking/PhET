package edu.colorado.phet.bernoulli;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

/**
 * User: Sam Reid
 * Date: Aug 22, 2003
 * Time: 12:51:08 AM
 * Copyright (c) Aug 22, 2003 by Sam Reid
 */
public class EarthGraphic implements Graphic, TransformListener {
    private double x;
    private double y;
    private double radius;
    private ModelViewTransform2d transform;
    Area view;
    final Color greenEarth = new Color( 40, 230, 45 );

    public EarthGraphic( double x, double y, double radius, ModelViewTransform2d transform ) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.transform = transform;
        transform.addTransformListener( this );
        transformChanged( transform );
    }

    public void paint( Graphics2D g ) {
        if( view != null ) {
            g.setColor( greenEarth );
            g.fill( view );
        }
    }

    public void transformChanged( ModelViewTransform2d mvt ) {
        Shape earthShape = new Ellipse2D.Double( x - radius, y - radius, radius * 2, radius * 2 );
        Shape viewShape = transform.toAffineTransform().createTransformedShape( earthShape );
        this.view = new Area( viewShape );
        view.intersect( new Area( transform.getViewBounds() ) );
    }
}
