/** Sam Reid*/
package edu.colorado.phet.chart;

import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * User: Sam Reid
 * Date: Sep 21, 2004
 * Time: 6:21:43 AM
 * Copyright (c) Sep 21, 2004 by Sam Reid
 */
public class Axis implements Graphic {
    private Chart chart;
    private AxisTicks minorTicks;
    private AxisTicks majorTicks;
    private Stroke stroke;
    private Color color;
    private int orientation;
    private double crossesOtherAxisAt = 0;

    public Axis( Chart chart, int orientation ) {
        this( chart, orientation, new BasicStroke( 2 ), Color.black, 2, 1 );
    }

    public Axis( Chart chart, int orientation, Stroke stroke, Color color, double minorTickSpacing, double majorTickSpacing ) {
        this.minorTicks = new AxisTicks( chart, orientation, stroke, color, minorTickSpacing );
        minorTicks.setShowLabels( false );
        this.majorTicks = new AxisTicks( chart, orientation, stroke, color, majorTickSpacing );
        majorTicks.setShowLabels( true );
        this.stroke = stroke;
        this.orientation = orientation;
        this.chart = chart;
        this.color = color;
    }

    protected AxisTicks getMinorTicks() {
        return minorTicks;
    }

    protected AxisTicks getMajorTicks() {
        return majorTicks;
    }

    public void setVisible( boolean visible ) {
        majorTicks.setVisible( visible );
        minorTicks.setVisible( visible );
    }

    public void paint( Graphics2D g ) {
        Stroke origStroke = g.getStroke();
        Color origColor = g.getColor();
        g.setStroke( stroke );
        g.setColor( color );
        if( orientation == AbstractGrid.HORIZONTAL ) {
            Point2D.Double leftEndOfAxis = new Point2D.Double( chart.getRange().getMinX(), crossesOtherAxisAt );
            Point left = chart.transform( leftEndOfAxis );
            Point2D.Double rightEndOfAxis = new Point2D.Double( chart.getRange().getMaxX(), crossesOtherAxisAt );
            Point right = chart.transform( rightEndOfAxis );
            g.drawLine( left.x, left.y, right.x, right.y );
        }
        else if( orientation == AbstractGrid.VERTICAL ) {
            Point2D.Double bottomEndOfAxis = new Point2D.Double( crossesOtherAxisAt, chart.getRange().getMinY() );
            Point bottom = chart.transform( bottomEndOfAxis );
            Point2D.Double topEndOfAxis = new Point2D.Double( crossesOtherAxisAt, chart.getRange().getMaxX() );
            Point top = chart.transform( topEndOfAxis );
            g.drawLine( bottom.x, bottom.y, top.x, top.y );
        }
        g.setStroke( origStroke );
        g.setColor( origColor );
        minorTicks.paint( g );
        majorTicks.paint( g );
    }

    public void setMajorTickFont( Font font ) {
        majorTicks.setFont( font );
    }

    public void setMajorTickSpacing( double spacing ) {
        majorTicks.setSpacing( spacing );
    }

    public void setMajorTickColor( Color color ) {
        majorTicks.setColor( color );
    }

    public void setMajorTickHeight( int tickHeight ) {
        majorTicks.setTickHeight( tickHeight );
    }

    public void setMajorTicksVisible( boolean visible ) {
        majorTicks.setVisible( visible );
    }

    public void setMajorTickLabelsVisible( boolean visible ) {
        majorTicks.setLabelsVisible( visible );
    }

    public void setMajorTickStroke( Stroke stroke ) {
        majorTicks.setStroke( stroke );
    }

    public void setMinorTickFont( Font font ) {
        minorTicks.setFont( font );
    }

    public void setMinorTickSpacing( double spacing ) {
        minorTicks.setSpacing( spacing );
    }

    public void setMinorTickColor( Color color ) {
        minorTicks.setColor( color );
    }

    public void setMinorTickHeight( int tickHeight ) {
        minorTicks.setTickHeight( tickHeight );
    }

    public void setMinorTicksVisible( boolean visible ) {
        minorTicks.setVisible( visible );
    }

    public void setMinorTickLabelsVisible( boolean visible ) {
        minorTicks.setLabelsVisible( visible );
    }

    public void setNumberFormat( NumberFormat numberFormat ) {
        majorTicks.setNumberFormat( numberFormat );
        minorTicks.setNumberFormat( numberFormat );
    }

    public void setCrossesOtherAxisAt( double crossesOtherAxisAt ) {
        this.crossesOtherAxisAt = crossesOtherAxisAt;
        majorTicks.setCrossesOtherAxisAt( crossesOtherAxisAt );
        minorTicks.setCrossesOtherAxisAt( crossesOtherAxisAt );
    }

    public void setMinorTickStroke( Stroke stroke ) {
        minorTicks.setStroke( stroke );
    }

    public static class AxisTicks extends AbstractGrid {
        private int tickHeight = 6;
        private NumberFormat format = new DecimalFormat( "#.#" );
        private Font font = new Font( "Lucida Sans", 0, 12 );
        private FontMetrics fontMetrics;
        private boolean showLabels = true;

        public AxisTicks( Chart chart, int orientation, Stroke stroke, Color color, double tickSpacing ) {
            super( chart, orientation, stroke, color, tickSpacing, 0 );
            fontMetrics = chart.getComponent().getFontMetrics( font );
        }

        public boolean isShowLabels() {
            return showLabels;
        }

        public void setShowLabels( boolean showLabels ) {
            this.showLabels = showLabels;
        }

        public void setFont( Font font ) {
            this.font = font;
        }

        public void setFormatter( DecimalFormat formatter ) {
            this.format = formatter;
        }

        public void setTickHeight( int tickHeight ) {
            this.tickHeight = tickHeight;
        }

        public void paint( Graphics2D g ) {

            if( isVisible() ) {
                Stroke stroke = super.getStroke();
                int orientation = super.getOrientation();
                Color color = super.getColor();
                double crossesOtherAxisAt = super.getCrossesOtherAxisAt();
                Chart chart = super.getChart();
                double tickSpacing = super.getSpacing();
                Stroke origStroke = g.getStroke();
                Color origColor = g.getColor();
                g.setStroke( stroke );
                g.setColor( color );
                g.setFont( font );

                if( orientation == HORIZONTAL ) {
                    Point2D.Double leftEndOfAxis = new Point2D.Double( chart.getRange().getMinX(), crossesOtherAxisAt );
                    Point left = chart.transform( leftEndOfAxis );
                    double[] gridLines = getGridLines( crossesOtherAxisAt, chart.getRange().getMinX(), chart.getRange().getMaxX(), tickSpacing );
                    for( int i = 0; i < gridLines.length; i++ ) {
                        double gridLineX = gridLines[i];
                        int x = chart.transformX( gridLineX );
                        int y = left.y;
                        g.drawLine( x, y - tickHeight / 2, x, y + tickHeight / 2 );
                        if( isShowLabels() ) {
                            String string = format.format( gridLineX );
                            int width = fontMetrics.stringWidth( string );
                            int height = fontMetrics.getHeight();
                            g.drawString( string, x - width / 2, y + tickHeight / 2 + height );
                        }
                    }

                }
                else if( orientation == VERTICAL ) {
                    Point2D.Double bottomEndOfAxis = new Point2D.Double( crossesOtherAxisAt, chart.getRange().getMinY() );
                    Point bottom = chart.transform( bottomEndOfAxis );
                    double[] gridLines = getGridLines( crossesOtherAxisAt, chart.getRange().getMinY(), chart.getRange().getMaxY(), tickSpacing );
                    for( int i = 0; i < gridLines.length; i++ ) {
                        double gridLineY = gridLines[i];
                        int x = bottom.x;
                        int y = chart.transformY( gridLineY );
                        g.drawLine( x - tickHeight / 2, y, x + tickHeight / 2, y );
                        if( isShowLabels() ) {
                            String string = format.format( gridLineY );
                            int width = fontMetrics.stringWidth( string );
                            int height = fontMetrics.getHeight();
                            g.drawString( string, x - tickHeight / 2 - width, y + height / 2 );
                        }
                    }
                }
                g.setStroke( origStroke );
                g.setColor( origColor );
            }
        }

        public void setNumberFormat( NumberFormat numberFormat ) {
            this.format = numberFormat;
        }

        public void setLabelsVisible( boolean visible ) {
            showLabels = visible;
        }
    }
}
