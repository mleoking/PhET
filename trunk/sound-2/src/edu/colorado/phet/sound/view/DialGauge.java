/**
 * Class: DialGauge
 * Class: edu.colorado.phet.sound.view
 * User: Ron LeMaster
 * Date: Sep 8, 2004
 * Time: 7:39:47 AM
 */
package edu.colorado.phet.sound.view;

import edu.colorado.phet.common.view.CompositeGraphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.graphics.ShapeGraphic;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.*;

// todo: add min and max lines and legends on face
// todo: add min and max limits to needle
// todo: add ScalarObservable interface and ScalarObserver interface
public class DialGauge extends CompositeGraphic {

    private ShapeGraphic border;
    private ShapeGraphic needle;
    private Component component;
    private double x;
    private double y;
    private double diam;
    private double min;
    private double max;

    public DialGauge( Component component, double x, double y, double diam, double min, double max ) {
        this.component = component;
        this.x = x;
        this.y = y;
        this.diam = diam;
        this.min = min;
        this.max = max;
        this.addGraphic( new FaceGraphic() );
        this.addGraphic( new NeedleGraphic() );
    }

    private class NeedleGraphic extends PhetShapeGraphic {
        private Rectangle.Double needle;
        // Ratio of needle on either side of pivot point
        private double r = 0.3;
        private double l;
        private AffineTransform needleTx;

        NeedleGraphic() {
            super( component, null, Color.red );
            needle = new Rectangle2D.Double();
            l = diam * .6;
            needle.setRect( x - l * r, y - 1, l, 2 );
            super.setShape( needle );
        }

        public void paint( Graphics2D g ) {
            pushRenderingHints( g );
            AffineTransform orgTx = g.getTransform();
            update();
            GraphicsUtil.setAntiAliasingOn( g );
            g.transform( needleTx );
            super.paint( g );
            g.setTransform( orgTx );
            popRenderingHints( g );
        }

        private void update() {
            double theta = Math.toRadians( -1200 );
            needleTx = AffineTransform.getRotateInstance( theta, x, y );
        }
    }

    private class FaceGraphic extends PhetShapeGraphic {
        FaceGraphic( ) {
            super( component, null, Color.white, new BasicStroke( 5 ), new Color( 80, 80, 40 ) );
            Shape face = new Ellipse2D.Double( x - diam / 2, y - diam / 2, diam, diam );
            super.setShape( face );
        }

        public void paint( Graphics2D g ) {
            pushRenderingHints( g );
            GraphicsUtil.setAntiAliasingOn( g );
            super.paint( g );
            popRenderingHints( g );
        }
    }
}
