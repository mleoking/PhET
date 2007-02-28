/**
 * Class: RectangleGraphic
 * Package: edu.colorado.phet.bernoulli.pump
 * Author: Another Guy
 * Date: Sep 26, 2003
 */
package edu.colorado.phet.bernoulli.pump;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class RectangleGraphic implements Graphic, TransformListener {
    ModelViewTransform2d transform;
    private Rectangle rect;
    protected Rectangle2D.Double model;
    private Rectangle inputTube = new Rectangle();
    Stroke stroke = new BasicStroke( 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );


    public RectangleGraphic( Rectangle2D.Double model, ModelViewTransform2d transform ) {
        this( model, transform, new BasicStroke( 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
    }

    public RectangleGraphic( Rectangle2D.Double model, ModelViewTransform2d transform, Stroke stroke ) {
        this.stroke = stroke;
        this.model = model;
        this.transform = transform;
        transform.addTransformListener( this );
        update();
    }

    public void setModelRectangle( Rectangle2D.Double model ) {
        this.model = model;
        update();
    }

    public void update() {
        Rectangle2D.Double modelRect = new Rectangle2D.Double( model.x, model.y, model.width, model.height );
        rect = transform.modelToView( modelRect );
    }

    public void paint( Graphics2D g ) {
        g.setColor( Color.black );
        g.setStroke( stroke );
        g.draw( rect );
    }

    public void transformChanged( ModelViewTransform2d mvt ) {
        update();
    }

    public Rectangle getRectangle() {
        return rect;
    }
}
