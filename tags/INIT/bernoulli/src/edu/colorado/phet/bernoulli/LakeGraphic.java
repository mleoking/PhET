package edu.colorado.phet.bernoulli;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Aug 22, 2003
 * Time: 1:52:09 AM
 * Copyright (c) Aug 22, 2003 by Sam Reid
 */
public class LakeGraphic implements TransformListener, Graphic {
    double x;
    double y;
    double width;
    double height;
    //the bottom half of an ellispe.
    Area area;
    ModelViewTransform2d transform;
    private EarthGraphic earthGraphic;

    public LakeGraphic( double x, double y, double width, double height, ModelViewTransform2d transform, EarthGraphic earthGraphic ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.transform = transform;
        this.earthGraphic = earthGraphic;
        transform.addTransformListener( this );
        transformChanged( transform );
    }

    public void transformChanged( ModelViewTransform2d mvt ) {
        Rectangle2D.Double rect = new Rectangle2D.Double( x, y - height / 2, width, height * 2 );
        Rectangle2D.Double topRect = new Rectangle2D.Double( x, y + height / 2, width, height );
        Rectangle viewRect = transform.modelToView( rect );
        Rectangle topView = transform.modelToView( topRect );
        Ellipse2D.Double ellipse = new Ellipse2D.Double( viewRect.x, viewRect.y, viewRect.width, viewRect.height );
        area = new Area( ellipse );
        area.intersect( earthGraphic.view );
    }

    public void paint( Graphics2D g ) {

        g.setColor( Color.blue );
        g.fill( area );
    }
}
