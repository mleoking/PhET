/**
 * Class: StarGraphic
 * Class: edu.colorado.phet.distanceladder.view
 * User: Ron LeMaster
 * Date: Mar 16, 2004
 * Time: 9:57:35 PM
 */
package edu.colorado.phet.distanceladder.view;

import edu.colorado.phet.distanceladder.model.Star;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class StarGraphic implements Graphic {

    private Paint paint;
    private AffineTransform starTx = new AffineTransform();
    private Ellipse2D.Double circle = new Ellipse2D.Double();
    private double radius;

    public StarGraphic( double radius, Paint paint, Point2D.Double location ) {
        setRadius( radius );
        this.paint = paint;
        setLocation( location );
    }

    public StarGraphic( double radius, Paint paint ) {
        this( radius, paint, new Point2D.Double() );
    }

    public void setRadius( double radius ) {
        this.radius = ( Double.isNaN( radius ) || Double.isInfinite( radius )
                        || Double.POSITIVE_INFINITY == radius || Double.NEGATIVE_INFINITY == radius ) ? 0 : radius;
        circle.setFrameFromCenter( 0, 0, this.radius, this.radius );
    }

    public void setLocation( Point2D.Double location ) {
        starTx.setToTranslation( location.getX(), location.getY() );
    }

    public void paint( Graphics2D g ) {
        AffineTransform orgTx = g.getTransform();
        GraphicsUtil.setAntiAliasingOn( g );
        g.transform( starTx );
        g.setPaint( paint );

        g.fill( circle );
        g.setTransform( orgTx );
    }

    public void update( Point2D.Double location, double radius ) {
        setRadius( radius );
        setLocation( location );
    }
}
