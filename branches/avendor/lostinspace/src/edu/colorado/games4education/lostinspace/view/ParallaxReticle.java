/**
 * Class: ParallaxReticle
 * Class: edu.colorado.games4education.lostinspace.view
 * User: Ron LeMaster
 * Date: Mar 16, 2004
 * Time: 8:17:53 AM
 */
package edu.colorado.games4education.lostinspace.view;

import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.graphics.bounds.Boundary;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.*;

public class ParallaxReticle extends DefaultInteractiveGraphic implements Translatable {

    private Point2D.Double location = new Point2D.Double( );
    private AffineTransform atx = new AffineTransform( );
    private Reticle reticle;
    private Container container;

    public ParallaxReticle( Container container ) {
        super( null, null );
        this.container = container;
        reticle = new Reticle();
        super.setGraphic( reticle );
        super.setBoundary( reticle );
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

        private Color color = Color.yellow;
        private int length = 200;
        private int height = 40;
        private Stroke stroke = new BasicStroke( 2f );
        private Line2D.Double baseLine = new Line2D.Double( 0, 0, length, 0 );
        private int numMajorTicks = 3;
        private int numMinorTicks = 10;
        private int majorTickIncr = length / ( numMajorTicks - 1 );
        private int minorTickIncr = majorTickIncr / numMinorTicks;
        private Rectangle2D.Double bounds = new Rectangle2D.Double( 0, - height / 2, length, height );
        private Point2D.Double testPoint = new Point2D.Double( );

        public Rectangle2D.Double getBounds() {
            return bounds;
        }

        public void paint( Graphics2D g ) {
            g.setColor( color );
            g.setStroke( stroke );

            // Draw the baseline
            g.draw( baseLine );
            // Draw the tick marks
            int yMajor = height / 2;
            int yMinor = height / 3;
            for( int i = 0; i < numMajorTicks - 1; i++ ) {
                int xMajor = i * majorTickIncr;
                g.drawLine( xMajor, -yMajor, xMajor, yMajor );

                for( int j = 1; j < numMinorTicks; j++ ) {
                    int xMinor = xMajor + j * minorTickIncr;
                    g.drawLine( xMinor, -yMinor, xMinor, yMinor );
                }
            }
            g.drawLine( length, -yMajor, length, yMajor );
        }

        public boolean contains( int x, int y ) {
            try {
                atx.inverseTransform( new Point2D.Double( (double)x, (double)y ), testPoint);
            }
            catch( NoninvertibleTransformException e ) {
                e.printStackTrace();
            }
            return bounds.contains( testPoint ) ;
        }
    }
}
