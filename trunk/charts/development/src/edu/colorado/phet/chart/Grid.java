/** Sam Reid*/
package edu.colorado.phet.chart;

import java.awt.*;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * User: Sam Reid
 * Date: Sep 21, 2004
 * Time: 6:25:31 AM
 * Copyright (c) Sep 21, 2004 by Sam Reid
 */
public class Grid extends AbstractGrid {
    GridTicks ticks;

    public Grid( Chart chart, int orientation, Stroke stroke, Color color, double tickSpacing, double crossesOtherAxisAt ) {
        super( chart, orientation, stroke, color, tickSpacing, crossesOtherAxisAt );
        ticks = new GridTicks( chart, orientation, new BasicStroke( 2 ), Color.black, tickSpacing );
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
            if( orientation == VERTICAL ) {
                double[] gridLines = getGridLines( crossesOtherAxisAt, chart.getRange().getMinX(), chart.getRange().getMaxX(), tickSpacing );
                for( int i = 0; i < gridLines.length; i++ ) {
                    double gridLineX = gridLines[i];
                    Point src = chart.transform( gridLineX, chart.getRange().getMinY() );
                    Point dst = chart.transform( gridLineX, chart.getRange().getMaxY() );
                    g.drawLine( src.x, src.y, dst.x, dst.y );
                }
            }
            else if( orientation == HORIZONTAL ) {
                double[] gridLines = getGridLines( crossesOtherAxisAt, chart.getRange().getMinY(), chart.getRange().getMaxY(), tickSpacing );
                for( int i = 0; i < gridLines.length; i++ ) {
                    double gridLineY = gridLines[i];
                    Point src = chart.transform( chart.getRange().getMinX(), gridLineY );
                    Point dst = chart.transform( chart.getRange().getMaxX(), gridLineY );
                    g.drawLine( src.x, src.y, dst.x, dst.y );
                }
            }
            g.setStroke( origStroke );
            g.setColor( origColor );

        }
        ticks.paint( g );
    }

    public void setTicksVisible( boolean visible ) {
        ticks.setVisible( visible );
    }

    /**
     * Lots of repeated code between this and AxisTicks.
     */
    public static class GridTicks extends AbstractGrid {
        private int tickHeight = 6;
        private NumberFormat format = new DecimalFormat( "#.#" );
        private Font font = new Font( "Lucida Sans", 0, 12 );
        private FontMetrics fontMetrics;
        private boolean showLabels = true;

        public GridTicks( Chart chart, int orientation, Stroke stroke, Color color, double tickSpacing ) {
            super( chart, orientation, stroke, color, tickSpacing, 0 );
            fontMetrics = chart.getComponent().getFontMetrics( font );
            setVisible( true );
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
                        int y = chart.transformY( chart.getRange().getMinY() );
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
                        int x = chart.transformX( chart.getRange().getMinX() );
//                        int x = bottom.x;
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
