/**
 * Class: PhotometerReticle
 * Class: edu.colorado.phet.distanceladder.view
 * User: Ron LeMaster
 * Date: Mar 16, 2004
 * Time: 9:25:22 PM
 */
package edu.colorado.phet.distanceladder.view;

import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.bounds.Boundary;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.coreadditions.Body;

import java.awt.*;
import java.awt.geom.*;

public class PhotometerReticle extends DefaultInteractiveGraphic implements Translatable {
    private Point2D.Double location = new Point2D.Double();
    private Container container;
    private AffineTransform atx = new AffineTransform();
    private AffineTransform hitTx = new AffineTransform();
    private Reticle reticle;
    private Photometer photometer = new Photometer();

    public PhotometerReticle( Container container ) {
        super( null, null );
        this.container = container;
        reticle = new Reticle();
        setGraphic( reticle );
        setBoundary( reticle );
        this.addCursorHandBehavior();
        this.addTranslationBehavior( this );
    }

    public void translate( double dx, double dy ) {
        setLocation( location.getX() + dx / hitTx.getScaleX(), location.getY() + dy / hitTx.getScaleY() );
    }

    public void setLocation( Point2D.Double location ) {
        setLocation( location.getX(), location.getY() );
    }

    public void setLocation( double x, double y ) {
        this.location.setLocation( x, y );
        photometer.setLocation( location );
        container.repaint();
    }

    public void paint( Graphics2D g ) {
        atx.setToIdentity();
        atx.translate( location.getX(), location.getY() );
        AffineTransform orgTx = g.getTransform();
        hitTx.setTransform( orgTx );
        hitTx.translate( location.getX(), location.getY() );
        g.transform( atx );

        super.paint( g );
        g.setTransform( orgTx );
    }

    public boolean contains( Point2D.Double pt ) {
        return reticle.containsModelPt( pt );
    }

    public Photometer getPhotometer() {
        return photometer;
    }

    //
    // Inner classes
    //
    private class Reticle implements Graphic, Boundary {
        private Color color = Color.green;
        private Stroke stroke = new BasicStroke( 2f );
        private float width = 20;
        private GeneralPath path;
        private GeneralPath hitTestPath;
        private Point2D.Double testPoint = new Point2D.Double();

        Reticle() {
            path = new GeneralPath();
            path.moveTo( -width / 6, -width / 2 );
            path.lineTo( -width / 2, -width / 2 );
            path.lineTo( -width / 2, width / 2 );
            path.lineTo( -width / 6, width / 2 );
            path.moveTo( width / 6, width / 2 );
            path.lineTo( width / 2, width / 2 );
            path.lineTo( width / 2, -width / 2 );
            path.lineTo( width / 6, -width / 2 );

            hitTestPath = new GeneralPath( new Rectangle2D.Double( -width / 2, -width / 2, width, width ) );
        }

        public void paint( Graphics2D g ) {
            g.setColor( color );
            g.setStroke( stroke );
            g.draw( path );
        }

        public boolean contains( int x, int y ) {
            try {
                hitTx.inverseTransform( new Point2D.Double( (double)x, (double)y ), testPoint );
            }
            catch( NoninvertibleTransformException e ) {
                e.printStackTrace();
            }
            return hitTestPath.contains( testPoint );
        }

        public Shape getHitTestPath() {
            return hitTestPath;
        }

        boolean containsModelPt( Point2D pt ) {
            boolean result = photometer.getLocation().getX() - hitTestPath.getBounds2D().getWidth() / 2 <= pt.getX()
                             && photometer.getLocation().getY() - hitTestPath.getBounds2D().getHeight() / 2 <= pt.getY()
                             && photometer.getLocation().getX() + hitTestPath.getBounds2D().getWidth() / 2 >= pt.getX()
                             && photometer.getLocation().getY() + hitTestPath.getBounds2D().getHeight() / 2 >= pt.getY();
            return result;
        }
    }

    public class Photometer extends Body {
        public Point2D.Double getCM() {
            return getLocation();
        }

        public double getMomentOfInertia() {
            return 0;
        }

        public void setLocation( Point2D.Double location ) {
            super.setLocation( location );
            updateObservers();
        }

        public void setLocation( double x, double y ) {
            super.setLocation( x, y );
            updateObservers();
        }
    }
}
