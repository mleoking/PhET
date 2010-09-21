/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas.instrumentation;

import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.util.GraphicsState;
import edu.colorado.phet.common.phetgraphics.view.util.GraphicsUtil;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.IdealGasResources;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Thermometer extends PhetGraphic {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------

    private static Color s_color = Color.red;
    private static Color s_outlineColor = IdealGasConfig.COLOR_SCHEME.thermometerOutline;

    //----------------------------------------------------------------
    // Instance data and methods
    //----------------------------------------------------------------

    private BarGauge gauge;
    private Ellipse2D.Double bulb;
    private NumberFormat formatter = new DecimalFormat( "#0" );
    private Point2D location;
    private double scale;
    private double maxScreenLevel;
    private double thickness;
    private double value;
    private Rectangle2D boundingRect;
    private Font font = new PhetFont( Font.BOLD, 10 );
    private FontMetrics fontMetrics;
    private double rectBorderThickness = 2;
    private RoundRectangle2D.Double readoutRect = new RoundRectangle2D.Double();
    private RoundRectangle2D.Double innerRect = new RoundRectangle2D.Double();
    private BasicStroke rectStroke = new BasicStroke( 3 );
    private float columnStrokeWidth = 1.5f;
    private BasicStroke columnStroke = new BasicStroke( columnStrokeWidth );
    private Color rectColor = Color.yellow;
    private int readoutWidth;
    private float readoutRectStrokeWidth = 0.5f;
    private BasicStroke readoutRectStroke = new BasicStroke( readoutRectStrokeWidth );
    private BasicStroke oneStroke;


    /**
     * @param component
     * @param location
     * @param maxScreenLevel
     * @param thickness
     * @param isVertical
     * @param minLevel
     * @param maxLevel
     */
    public Thermometer( Component component, Point2D.Double location, double maxScreenLevel, double thickness,
                        boolean isVertical, double minLevel, double maxLevel ) {
        super( component );
        gauge = new BarGauge( location, maxScreenLevel, s_color, thickness, isVertical,
                              minLevel, maxLevel );
        gauge.setOutlineColor( s_outlineColor );
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

        super.setIgnoreMouse( true );
    }

    /**
     * @param location
     */
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
                             (int)( location.getY() - readoutHeight ) );

        readoutRect.setRoundRect( location.getX() + thickness + columnStrokeWidth * 2,
                                  yLoc - rectBorderThickness,
                                  readoutWidth + rectBorderThickness * 2,
                                  readoutHeight + rectBorderThickness * 2,
                                  4, 4 );
        innerRect.setRoundRect( location.getX() + thickness + columnStrokeWidth * 2 + rectBorderThickness,
                                yLoc,
                                readoutWidth,
                                readoutHeight,
                                4, 4 );

        g.setColor( rectColor );
        g.setStroke( rectStroke );
        g.draw( readoutRect );
        g.setColor( s_outlineColor );
        g.setStroke( readoutRectStroke );
        g.draw( readoutRect );
        g.setColor( rectColor );
        g.fill( readoutRect );

        g.setColor( Color.white );
        g.fill( innerRect );
        oneStroke = new BasicStroke( 1 );
        g.setStroke( oneStroke );
        g.setColor( Color.black );
        g.draw( innerRect );

        double v = Double.isNaN( value ) ? 0 : value / 1000;
        String temperatureStr = formatter.format( v ) + IdealGasResources.getString( "temperature.units.abbreviation" );
        g.setColor( Color.black );
        int strLocY = (int)innerRect.getMinY() + fontMetrics.getHeight();
        g.drawString( temperatureStr, (int)innerRect.getMaxX() - 5 - fontMetrics.stringWidth( temperatureStr ), strLocY );

        GraphicsUtil.setAntiAliasingOn( g );
        g.setStroke( columnStroke );
        gauge.paint( g );
        g.setColor( s_color );
        g.fill( bulb );
        g.setColor( s_outlineColor );
        g.draw( bulb );
        Rectangle2D r = new Rectangle.Double( gauge.getBounds().getMinX() + columnStrokeWidth, gauge.getBounds().getMaxY() - 2,
                                              gauge.getBounds().getWidth() - columnStrokeWidth, 4 );
        g.setColor( s_color );
        g.fill( r );

        // Debug
//        g.setColor( Color.green );
//        g.draw( determineBounds() );

        gs.restoreGraphics();
    }

    protected Rectangle determineBounds() {
//        boundingRect = new Rectangle2D.Double( location.getX(), gauge.getBounds().getMinY(),
        boundingRect = new Rectangle2D.Double( location.getX(), location.getY(),
                                               readoutWidth + rectBorderThickness,
                                               maxScreenLevel + bulb.getHeight() );


        double minX = Math.min( boundingRect.getMinX(), readoutRect.getMinX() );
        double minY = Math.min( boundingRect.getMinY(), readoutRect.getMinY() - readoutRectStrokeWidth );
        double w = Math.max( boundingRect.getMaxX(), readoutRect.getMaxX() ) - minX + 4 * readoutRectStrokeWidth;
        double h = Math.max( boundingRect.getMaxY(), readoutRect.getMaxY() ) - minY;
        boundingRect.setRect( minX, minY, w, h );
        return RectangleUtils.toRectangle( boundingRect );
    }
}
