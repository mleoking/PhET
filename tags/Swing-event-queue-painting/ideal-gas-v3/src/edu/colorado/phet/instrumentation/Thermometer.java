/**
 * Class: Thermometer
 * Package: edu.colorado.phet.instrumentation
 * Author: Another Guy
 * Date: Sep 29, 2004
 */
package edu.colorado.phet.instrumentation;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.RectangleUtils;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Thermometer extends PhetGraphic {
    private static Color color = Color.red;
    private BarGauge gauge;
    private Ellipse2D.Double bulb;
    private NumberFormat formatter = new DecimalFormat( "#0" );
    private Point2D location;
    private double scale;
    private double maxScreenLevel;
    private double thickness;
    private double value;
    private Rectangle2D boundingRect;
    private Font font = new Font( "Lucida Sans", Font.BOLD, 10 );
    private FontMetrics fontMetrics;
    private int rectBorderThickness = 3;
    private RoundRectangle2D.Double readoutRect = new RoundRectangle2D.Double();
    private RoundRectangle2D.Double innerRect = new RoundRectangle2D.Double();
    private BasicStroke rectStroke = new BasicStroke( 3 );
    private BasicStroke columnStroke = new BasicStroke( 1 );
    private Color rectColor = Color.yellow;
    private int readoutWidth;


    public Thermometer( Component component, Point2D.Double location, double maxScreenLevel, double thickness,
                        boolean isVertical, double minLevel, double maxLevel ) {
        super( component );
        gauge = new BarGauge( location, maxScreenLevel, color, thickness, isVertical,
                              minLevel, maxLevel );
        bulb = new Ellipse2D.Double( location.x - thickness / 2, location.y + maxScreenLevel - thickness * 0.1,
                                     thickness * 2, thickness * 2 );
        this.location = location;
        this.thickness = thickness;
        scale = maxScreenLevel / maxLevel;
        this.maxScreenLevel = maxScreenLevel;
        fontMetrics = component.getFontMetrics( font );
        readoutWidth = fontMetrics.stringWidth( "XXXXXXX" );
        boundingRect = new Rectangle2D.Double( location.getX(), location.getY(),
                                               readoutWidth + rectBorderThickness,
                                               maxScreenLevel + bulb.getHeight() );
    }

    public void setLocation( Point2D.Double location ) {
        gauge.setLocation( location );
        this.location.setLocation( location );
        int readoutWidth = fontMetrics.stringWidth( "XXXXXXX" );
        boundingRect = new Rectangle2D.Double( location.getX(), location.getY(),
                                               readoutWidth + rectBorderThickness,
                                               maxScreenLevel + bulb.getHeight() );
    }

    public void setValue( double value ) {
        this.value = Double.isNaN( value ) ? 0 : value;
        gauge.setLevel( this.value );
    }

    public void paint( Graphics2D g ) {
        GraphicsState gs = new GraphicsState( g );
        GraphicsUtil.setAntiAliasingOn( g );
        g.setFont( font );

        int readoutHeight = fontMetrics.getHeight() + fontMetrics.getMaxDescent();
        int readoutWidth = fontMetrics.stringWidth( "XXXXXXX" );
        int yLoc = Math.max( (int)( location.getY() + maxScreenLevel - readoutHeight - value * scale ),
                             (int)( location.getY() - readoutHeight ));

        readoutRect.setRoundRect( location.getX() + thickness,
                           yLoc - rectBorderThickness,
                           readoutWidth + rectBorderThickness * 2,
                           readoutHeight + rectBorderThickness * 2,
                           4, 4 );
        innerRect.setRoundRect( location.getX() + thickness + 3,
                                yLoc,
                                readoutWidth, readoutHeight,
                                4, 4 );

        g.setColor( rectColor );
        g.setStroke( rectStroke );
        g.draw( readoutRect );
        g.setColor( Color.black );
        g.setStroke( new BasicStroke( 0.5f ));
        g.draw( readoutRect );
        g.setColor( rectColor );
        g.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.3f ) );
        g.fill( readoutRect );
        g.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1 ) );
        g.setColor( Color.white );
        g.fill( innerRect );

        double v = Double.isNaN( value ) ? 0 : value / 1000;
        String temperatureStr = formatter.format( v ) + '\u00b0' + "K";
        g.setColor( Color.black );
        int strLocY = (int)innerRect.getMinY() + fontMetrics.getHeight();
        g.drawString( temperatureStr, (int)innerRect.getMaxX() - 5 - fontMetrics.stringWidth( temperatureStr ), strLocY );

        g.setStroke( columnStroke );
        gauge.paint( g );
        g.setColor( color );
        g.fill( bulb );
        g.setColor( Color.black );
        g.draw( bulb );
        gs.restoreGraphics();
    }

    protected Rectangle determineBounds() {
        boundingRect = new Rectangle2D.Double( location.getX(), location.getY(),
                                               readoutWidth + rectBorderThickness,
                                               maxScreenLevel + bulb.getHeight() );

        double minX = Math.min( boundingRect.getMinX(), readoutRect.getMinX() );
        double minY = Math.min( boundingRect.getMinY(), readoutRect.getMinY() );
        double w = Math.max( boundingRect.getMaxX(), readoutRect.getMaxX() ) - minX;
        double h = Math.max( boundingRect.getMaxY(), readoutRect.getMaxY() ) - minY;
        boundingRect.setRect( minX, minY, w, h );
//        System.out.println( "boundingRect = " + boundingRect );
        return RectangleUtils.toRectangle( boundingRect );
    }
}
