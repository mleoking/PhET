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
import edu.colorado.phet.common.view.util.GraphicsUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.geom.*;
import java.util.logging.Formatter;
import java.text.DecimalFormat;
import java.text.Format;

public class ParallaxReticle extends DefaultInteractiveGraphic implements Translatable {
    private Point2D.Double location = new Point2D.Double();
    private AffineTransform atx = new AffineTransform();
    private AffineTransform hitTx = new AffineTransform();
    private Rectangle2D.Double bounds;
    private double viewAngle;
    private Container container;

    public ParallaxReticle( Container container, double viewAngle ) {
        super( null, null );
        this.container = container;
        this.bounds = new Rectangle2D.Double( container.getBounds().getX(),
                                              container.getBounds().getY(),
                                              container.getBounds().getWidth(),
                                              container.getBounds().getHeight() );
        this.viewAngle = viewAngle;
        Reticle reticle = new Reticle();
        setGraphic( reticle );
        setBoundary( reticle );
        this.addCursorHandBehavior();
        this.addTranslationBehavior( this );
        this.setLocation( 0, 0 );
    }

    public void translate( double dx, double dy ) {
        setLocation( location.getX(), location.getY() + dy / hitTx.getScaleY() );
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
        hitTx.setTransform( orgTx );
        hitTx.translate( location.getX(), location.getY() );
        g.transform( atx );
        super.paint( g );
        g.setTransform( orgTx );
    }

    //
    // Inner classes
    //
    private class Reticle implements Graphic, Boundary, ImageObserver {
        private Color color = Color.orange;
        private Stroke stroke = new BasicStroke( 1f );
        private float width = 20;
        private GeneralPath path;
        private GeneralPath hitTestPath;
        private Point2D.Double testPoint = new Point2D.Double();
        private float minorTickHeight = 10;
        private float majorTickHeight = 20;
        private int majorTickEvery = 10;
        private BufferedImage reticleBI;

        Reticle() {
            DecimalFormat numFormatter = new DecimalFormat( "##" );
            reticleBI = new BufferedImage( (int)bounds.getWidth(), (int)bounds.getHeight(),
                                           BufferedImage.TYPE_INT_ARGB );
            Graphics2D g = (Graphics2D)reticleBI.getGraphics();
            g.translate( bounds.getWidth() / 2, bounds.getHeight() / 2 );
            g.setColor( color );
            g.setStroke( stroke );
            GraphicsUtil.setAntiAliasingOn( g );

            // Base line
            path = new GeneralPath();
            path.moveTo( (float)-bounds.getWidth() / 2, 0 );
            path.lineTo( (float)bounds.getWidth() / 2, 0 );
            double d = ( bounds.getWidth() / 2 ) / Math.tan( viewAngle / 2 );

            int tickCnt = 0;
            for( double beta = 0; beta <= viewAngle / 2; beta += viewAngle / 90 ) {
                double e = d * Math.sin( beta );
//                double e = d * Math.tan( beta );
                float tickHeight = 0;
                if( tickCnt++ % majorTickEvery == 0 ) {
                    tickHeight = majorTickHeight;
                    String s = numFormatter.format( Math.toDegrees( beta ));
                    g.drawString( s, (float)e, -30 );
                    if( beta != 0 ) {
                        g.drawString( "-" + s, (float)-e, -30 );
                    }
                }
                else {
                    tickHeight = minorTickHeight;
                }
                path.moveTo( (float)e, tickHeight );
                path.lineTo( (float)e, -tickHeight );
                path.moveTo( -(float)e, tickHeight );
                path.lineTo( -(float)e, -tickHeight );
            }

            // Create the path that will detect mouse hits
            hitTestPath = new GeneralPath( new Rectangle2D.Double( (float)-bounds.getWidth() / 2, -minorTickHeight,
                                                                   (float)bounds.getWidth(), minorTickHeight * 2 ) );
        }

        public void paint( Graphics2D g ) {
            Object interpolationHint = g.getRenderingHint(  RenderingHints.KEY_INTERPOLATION );
//            g.setRenderingHint(  RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
            g.setColor( color );
            g.setStroke( stroke );
            g.draw( path );
            g.drawImage( reticleBI, -reticleBI.getWidth() / 2, -reticleBI.getHeight() / 2, this );

//            g.setRenderingHint(  RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
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

        public boolean imageUpdate( Image img, int infoflags, int x, int y, int width, int height ) {
            return false;
        }
    }
}
