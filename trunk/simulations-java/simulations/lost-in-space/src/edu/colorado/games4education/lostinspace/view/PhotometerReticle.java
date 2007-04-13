/**
 * Class: PhotometerReticle
 * Class: edu.colorado.games4education.lostinspace.view
 * User: Ron LeMaster
 * Date: Mar 16, 2004
 * Time: 9:25:22 PM
 */
package edu.colorado.games4education.lostinspace.view;

import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.bounds.Boundary;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

public class PhotometerReticle extends DefaultInteractiveGraphic implements Translatable {
    private Point2D.Double location = new Point2D.Double();
    private Container container;
    private AffineTransform atx = new AffineTransform();

    public PhotometerReticle( Container container ) {
        super( null, null );
        this.container = container;
        Reticle reticle = new Reticle();
        setGraphic( reticle );
        setBoundary( reticle );
        this.addCursorHandBehavior();
        this.addTranslationBehavior( this );
    }

    public void translate( double dx, double dy ) {
        setLocation( location.getX() + dx, location.getY() + dy );
    }

    public void setLocation( Point2D.Double location ) {
        setLocation( location.getX(), location.getY() );
    }

    public void setLocation( double x, double y ) {
        this.location.setLocation( x, y );
        container.repaint();
    }

    public void paint( Graphics2D g ) {
        atx.setToIdentity();
        atx.translate( location.getX(), location.getY() );
        AffineTransform orgTx = g.getTransform();
        g.transform( atx );
        super.paint( g );
        g.setTransform( orgTx );
    }

    //
    // Inner classes
    //
    private class Reticle implements Graphic, Boundary {
        private Color color = Color.green;
        private Stroke stroke = new BasicStroke( 2f );
        private float width = 20;
        private GeneralPath path;
        private Point2D.Double testPoint = new Point2D.Double();

        Reticle() {
            path = new GeneralPath();
            path.moveTo( width / 3, 0 );
            path.lineTo( 0, 0 );
            path.lineTo( 0, width );
            path.lineTo( width / 3, width );
            path.moveTo( width * 2 / 3, width );
            path.lineTo( width, width );
            path.lineTo( width, 0 );
            path.lineTo( width * 2 / 3, 0 );
        }

        public void paint( Graphics2D g ) {
            g.setColor( color );
            g.setStroke( stroke );
            g.draw( path );
        }

        public boolean contains( int x, int y ) {
            try {
                atx.inverseTransform( new Point2D.Double( (double)x, (double)y ), testPoint );
            }
            catch( NoninvertibleTransformException e ) {
                e.printStackTrace();
            }
            return path.contains( testPoint );
        }
    }
}
