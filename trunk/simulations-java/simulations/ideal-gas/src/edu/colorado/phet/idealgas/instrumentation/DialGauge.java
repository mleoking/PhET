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

import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.phetgraphics.view.util.GraphicsUtil;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.idealgas.coreadditions.ScalarObservable;
import edu.colorado.phet.idealgas.coreadditions.ScalarObserver;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Dial Gauge
 * <p/>
 * A round dial gauge with a red needle. Also displays a numberic value.
 */
public class DialGauge extends CompositePhetGraphic implements ScalarObserver {

    //-----------------------------------------------------------------
    // Class data
    //-----------------------------------------------------------------
    private static Font s_defaultFont = new PhetFont( Font.BOLD, 8 );
    private static NumberFormat s_defaultFormatter = new DecimalFormat( "#0.0" );

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
    private FaceGraphic faceGraphic;
    private Font font;
    private NumberFormat numberFormat;

    private double needleLength = 0.5;
    private double datum = Double.NaN;
    private Color backgroundColor = new Color( 245, 255, 250 );

    /**
     * @param dataSource The ScalarObservable object from which the gauge gets its data
     * @param component
     * @param x          x location of the center of the dial
     * @param y          y location of the center of the dial
     * @param diam
     * @param min        minimum value the dial will display. This corresponds to the most counter-clockwise
     *                   tick mark
     * @param max        maximum value the dial will display. This corresponds to the most clockwise tick mark
     * @param title      String printed near top of dial
     * @param units      String printed after digital readout
     */
    public DialGauge( ScalarObservable dataSource, Component component,
                      double x, double y, double diam, double min, double max,
                      String title, String units ) {
        this( dataSource, component, x, y, diam, min, max, title, units, s_defaultFont, s_defaultFormatter );
    }

    /**
     * @param dataSource The ScalarObservable object from which the gauge gets its data
     * @param component
     * @param x          x location of the center of the dial
     * @param y          y location of the center of the dial
     * @param diam
     * @param min        minimum value the dial will display. This corresponds to the most counter-clockwise
     *                   tick mark
     * @param max        maximum value the dial will display. This corresponds to the most clockwise tick mark
     * @param title      String printed near top of dial
     * @param units      String printed after digital readout
     * @param font
     */
    public DialGauge( ScalarObservable dataSource, Component component,
                      double x, double y, double diam, double min, double max,
                      String title, String units, Font font, NumberFormat numberFormat ) {
        this.dataSource = dataSource;
        this.title = title;
        this.units = units;
        this.font = font;
        this.numberFormat = numberFormat;
        dataSource.addObserver( this );
        this.component = component;
        this.x = x;
        this.y = y;
        this.diam = diam;
        this.min = min;
        this.max = max;
        faceGraphic = new FaceGraphic();
        this.addGraphic( faceGraphic );
        needleGraphic = new NeedleGraphic();
        this.addGraphic( needleGraphic );

        update();
    }

    public void update() {
        double newDatum = dataSource.getValue();
        if( datum != newDatum ) {
            datum = newDatum;
            double needleDatum = Math.max( Math.min( datum, max ), min );
            double p = ( max - needleDatum ) / ( max - min );
            double theta = -( ( Math.PI * 5 / 4 ) + ( Math.PI * 3 / 2 ) * p ) - Math.PI / 2;
            needleGraphic.update( theta );
            faceGraphic.repaint();
        }
    }

    public void setBackground( Color color ) {
        backgroundColor = color;
    }

    private class NeedleGraphic extends PhetShapeGraphic {
        private Rectangle.Double needle;
        // Ratio of needle on either side of pivot point
        private double r = 0.2;
        private double l;
        private AffineTransform needleTx;
        private Ellipse2D.Double pivot = new Ellipse2D.Double();

        NeedleGraphic() {
            super( component, null, Color.red );
            needle = new Rectangle2D.Double();
            l = diam * needleLength;
            needle.setRect( x - l * r, y - 1, l, 2 );
            super.setShape( needle );
        }

        public void paint( Graphics2D g ) {
            saveGraphicsState( g );
            GraphicsUtil.setAntiAliasingOn( g );
            g.transform( needleTx );
            super.paint( g );
            g.setColor( Color.black );
            g.fill( pivot );
            restoreGraphicsState();
        }

        void update( double theta ) {
            needleTx = AffineTransform.getRotateInstance( theta, x, y );
            pivot.setFrameFromCenter( x, y, x + 2, y + 2 );
            repaint();
        }
    }

    private class FaceGraphic extends PhetShapeGraphic {
        private Rectangle2D.Double tickMark;

        FaceGraphic() {
            super( component, null, backgroundColor, new BasicStroke( 5 ), new Color( 80, 80, 40 ) );
            Shape face = new Ellipse2D.Double( x - diam / 2, y - diam / 2, diam, diam );
            super.setShape( face );
            tickMark = new Rectangle2D.Double( x + diam * 3 / 8, y - 1, diam / 16, 2 );
        }


        public void paint( Graphics2D g ) {
            saveGraphicsState( g );
            setBackground( backgroundColor );
            GraphicsUtil.setAntiAliasingOn( g );
            super.paint( g );

            // Paint tick marks
            int numTickMarks = 19;
            g.setColor( Color.black );
            double tickSpace = ( Math.PI * 6 / 4 ) / ( numTickMarks - 1 );
            for( double theta = Math.PI * 3 / 4; theta <= Math.PI * 9 / 4 + tickSpace / 2; theta += tickSpace ) {
                AffineTransform orgTx = g.getTransform();
                g.transform( AffineTransform.getRotateInstance( theta, x, y ) );
                g.fill( tickMark );
                g.setTransform( orgTx );
            }

            // Paint values on min and max tick marks
            FontRenderContext frc = g.getFontRenderContext();
            String minStr = Double.toString( min );
            Rectangle2D bounds = font.getStringBounds( minStr, frc );
            g.setFont( font );
            double radRatio = .6;

            // Paint value, and units label
            RoundRectangle2D rect = new RoundRectangle2D.Double( 0, 0, 0, 0, 3, 3 );
            rect.setFrameFromCenter( x, y + 10, x + 30, y + 17 );
            g.setColor( Color.white );
            g.fill( rect );
            g.setColor( Color.yellow );
            g.setStroke( new BasicStroke( 3f ) );
            g.draw( rect );
            g.setColor( Color.black );
            g.setStroke( new BasicStroke( 0.5f ) );
            g.draw( rect );
            String datumString = numberFormat.format( datum ) + " " + units;
            bounds = font.getStringBounds( datumString, frc );
            g.setColor( Color.black );
            g.drawString( datumString,
                          (float)x - (float)bounds.getWidth() / 2,
                          (float)( y + ( ( diam / 4 ) * radRatio ) ) );

            // Paint the title
            bounds = font.getStringBounds( title, frc );
            g.setColor( Color.black );
            g.drawString( title,
                          (float)x - (float)bounds.getWidth() / 2,
                          (float)( y - ( ( diam / 4 ) * radRatio ) ) );

            restoreGraphicsState();
        }
    }
}
