/**
 * Class: StarGraphic
 * Class: edu.colorado.games4education.lostinspace.view
 * User: Ron LeMaster
 * Date: Mar 16, 2004
 * Time: 9:57:35 PM
 */
package edu.colorado.games4education.lostinspace.view;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.games4education.lostinspace.model.Star;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class StarGraphic implements Graphic {

    private Paint paint;
    private AffineTransform starTx = new AffineTransform();
    private Ellipse2D.Double circle = new Ellipse2D.Double( );

    public StarGraphic( double radius, Paint paint, Point2D.Double location ) {
        setRadius( radius );
        this.paint = paint;
        setLocation( location );
    }

    public StarGraphic( double radius, Paint paint ) {
        this( radius, paint, new Point2D.Double() );
    }

    public void setRadius( double radius ) {
        circle.setFrameFromCenter( 0, 0, radius, radius );
    }

    public void setLocation( Point2D.Double location ) {
        starTx.setToTranslation( location.getX(), location.getY() );
    }

    public void paint( Graphics2D g ) {
        AffineTransform orgTx = g.getTransform();
        g.transform( starTx );
        g.setPaint( paint );
        g.fill( circle );
        g.setTransform( orgTx );
    }

    public void update( Star star, Point2D.Double location ) {
        setLocation( location );
    }
}
