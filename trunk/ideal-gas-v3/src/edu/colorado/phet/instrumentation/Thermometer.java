/**
 * Class: Thermometer
 * Package: edu.colorado.phet.instrumentation
 * Author: Another Guy
 * Date: Sep 29, 2004
 */
package edu.colorado.phet.instrumentation;

import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.GraphicsUtil;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class Thermometer extends AbstractGauge {
    private static Color color = Color.red;
    private BarGauge gauge;
    private Ellipse2D.Double bulb;

    public Thermometer( Point2D.Double location, double maxScreenLevel, double thickness,
                        boolean isVertical, double minLevel, double maxLevel ) {
        gauge = new BarGauge( location, maxScreenLevel, color, thickness, isVertical,
                              minLevel, maxLevel );
        bulb = new Ellipse2D.Double( location.x - thickness / 2, location.y + maxScreenLevel - thickness * 0.1,
                                     thickness * 2, thickness * 2 );
    }

    public void setValue( double value ) {
        gauge.setLevel( Double.isNaN( value ) ? 0 : value );
    }

    public void paint( Graphics2D g ) {
        GraphicsState gs = new GraphicsState( g );
        GraphicsUtil.setAntiAliasingOn( g );
        gauge.paint( g );
        g.setColor( color );
        g.fill( bulb );
        g.setColor( Color.black );
        g.draw( bulb );
        gs.restoreGraphics();
    }
}
