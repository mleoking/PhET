/**
 * Class: DialGauge
 * Class: edu.colorado.phet.sound.view
 * User: Ron LeMaster
 * Date: Sep 8, 2004
 * Time: 7:39:47 AM
 */
package edu.colorado.phet.sound.view;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.phetgraphics.view.util.GraphicsUtil;
import edu.colorado.phet.sound.coreadditions.ScalarObservable;
import edu.colorado.phet.sound.coreadditions.ScalarObserver;

// todo: add min and max lines and legends on face
// todo: add min and max limits to needle
// todo: add ScalarObservable interface and ScalarObserver interface

public class DialGauge extends CompositePhetGraphic implements ScalarObserver {

    private ScalarObservable dataSource;
    private String title;
    private String units;
    private Component component;
    private double x;
    private double y;
    private double diam;
    private double min;
    private double max;
    private NeedleGraphic needleGraphic;

    public DialGauge( ScalarObservable dataSource, Component component,
                      double x, double y, double diam, double min, double max,
                      String title, String units ) {
        this.dataSource = dataSource;
        this.title = title;
        this.units = units;
        dataSource.addObserver( this );
        this.component = component;
        this.x = x;
        this.y = y;
        this.diam = diam;
        this.min = min;
        this.max = max;
        this.addGraphic( new FaceGraphic() );
        needleGraphic = new NeedleGraphic();
        this.addGraphic( needleGraphic );

        update();
    }

    private class NeedleGraphic extends PhetShapeGraphic {
        private Rectangle.Double needle;
        // Ratio of needle on either side of pivot point
        private double r = 0.3;
        private double l;
        private AffineTransform needleTx;
        private Ellipse2D.Double pivot = new Ellipse2D.Double();

        NeedleGraphic() {
            super( component, null, Color.red );
            needle = new Rectangle2D.Double();
            l = diam * .5;
            needle.setRect( x - l * r, y - 1, l, 2 );
            super.setShape( needle );
        }

        public void paint( Graphics2D g ) {
            pushRenderingHints( g );
            AffineTransform orgTx = g.getTransform();
            GraphicsUtil.setAntiAliasingOn( g );
            g.transform( needleTx );
            super.paint( g );
            g.setColor( Color.black );
            g.fill( pivot );
            g.setTransform( orgTx );
            popRenderingHints( g );
        }

        void update( double theta ) {
            needleTx = AffineTransform.getRotateInstance( theta, x, y );
            pivot.setFrameFromCenter( x, y, x + 2, y + 2 );
        }
    }

    private class FaceGraphic extends PhetShapeGraphic {
        private Rectangle2D.Double tickMark;
        private Font font = new PhetFont( Font.BOLD, 8 );

        FaceGraphic() {
            super( component, null, Color.white, new BasicStroke( 5 ), new Color( 80, 80, 40 ) );
            Shape face = new Ellipse2D.Double( x - diam / 2, y - diam / 2, diam, diam );
            super.setShape( face );
            tickMark = new Rectangle2D.Double( x + diam / 6, y - 1, diam / 8, 2 );
        }

        public void paint( Graphics2D g ) {
            pushRenderingHints( g );
            GraphicsUtil.setAntiAliasingOn( g );
            super.paint( g );

            // Paint tick marks
            int numTickMarks = 7;
            g.setColor( Color.black );
            double minTickTheta = Math.PI * 3 / 4;
            double maxTickTheta = Math.PI / 4;
            for( int i = 0; i < numTickMarks; i++ ) {
                AffineTransform orgTx = g.getTransform();
                double theta = minTickTheta + i * Math.PI / 4;
                g.transform( AffineTransform.getRotateInstance( theta, x, y ) );
                g.fill( tickMark );
                g.setTransform( orgTx );
            }

            // Paint values on min and max tick marks
            FontRenderContext frc = g.getFontRenderContext();
            String minStr = Double.toString( min );
            Rectangle2D bounds = font.getStringBounds( minStr, frc );
            g.setFont( font );
            g.drawString( minStr,
                          (float)( x + Math.cos( minTickTheta ) * ( diam / 2 ) * .7 ) - (float)bounds.getWidth() / 2,
                          (float)( y + Math.sin( minTickTheta ) * ( diam / 2 ) * .7 ) + (float)bounds.getHeight() );
            String maxStr = Double.toString( max );
            bounds = font.getStringBounds( maxStr, frc );
            g.drawString( Double.toString( max ),
                          (float)( x + Math.cos( maxTickTheta ) * ( diam / 2 ) * .7 ) - (float)bounds.getWidth() / 2,
                          (float)( y + Math.sin( maxTickTheta ) * ( diam / 2 ) * .7 ) + (float)bounds.getHeight() );

            // Paint units label
            bounds = font.getStringBounds( units, frc );
            g.drawString( units,
                          (float)x - (float)bounds.getWidth() / 2,
                          (float)( y + ( ( diam / 2 ) * .75 ) ) );

            // Paint the title
            bounds = font.getStringBounds( title, frc );
            g.drawString( title,
                          (float)x - (float)bounds.getWidth() / 2,
                          (float)( y - ( ( diam / 2 ) * .75 ) ) );

            popRenderingHints( g );
        }
    }

    public void update() {
        double datum = dataSource.getValue();
        datum = Math.max( Math.min( datum, max ), min );
        double p = ( max - datum ) / ( max - min );
        double theta = -( ( Math.PI * 5 / 4 ) + ( Math.PI * 3 / 2 ) * p ) - Math.PI / 2;
        needleGraphic.update( theta );
    }
}
