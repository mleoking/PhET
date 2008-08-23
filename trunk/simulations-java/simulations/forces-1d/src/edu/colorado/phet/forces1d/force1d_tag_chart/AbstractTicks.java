package edu.colorado.phet.forces1d.force1d_tag_chart;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * User: Sam Reid
 * Date: Oct 8, 2004
 * Time: 11:37:47 PM
 */
public abstract class AbstractTicks extends AbstractGrid {
    private int tickHeight = 6;
    private NumberFormat format = new DecimalFormat( "#.#" );
    private Font font = new Font( PhetFont.getDefaultFontName(), 0, 12 );
    private FontMetrics fontMetrics;
    private boolean showLabels = true;

    public AbstractTicks( Chart chart, int orientation, Stroke stroke, Color color, double tickSpacing ) {
        this( chart, orientation, stroke, color, tickSpacing, 0 );
    }

    public AbstractTicks( Chart chart, int orientation, Stroke stroke, Color color, double tickSpacing, double crossesOtherAxisAt ) {
        super( chart, orientation, stroke, color, tickSpacing, crossesOtherAxisAt );
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
        if ( isVisible() ) {
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
            if ( orientation == HORIZONTAL ) {
                double[] gridLines = getGridLines( crossesOtherAxisAt, chart.getRange().getMinX(), chart.getRange().getMaxX(), tickSpacing );
                for ( int i = 0; i < gridLines.length; i++ ) {
                    double gridLineX = gridLines[i];
                    int x = chart.transformX( gridLineX );
                    int y = getHorizontalTickY();
                    g.drawLine( x, y - tickHeight / 2, x, y + tickHeight / 2 );
                    if ( isShowLabels() ) {
                        String string = format.format( gridLineX );
                        int width = fontMetrics.stringWidth( string );
                        int height = fontMetrics.getHeight();
                        g.drawString( string, x - width / 2, y + tickHeight / 2 + height );
                    }
                }
            }
            else if ( orientation == VERTICAL ) {
                double[] gridLines = getGridLines( crossesOtherAxisAt, chart.getRange().getMinY(), chart.getRange().getMaxY(), tickSpacing );
                for ( int i = 0; i < gridLines.length; i++ ) {
                    double gridLineY = gridLines[i];
                    int x = getVerticalTickX();
                    int y = chart.transformY( gridLineY );
                    g.drawLine( x - tickHeight / 2, y, x + tickHeight / 2, y );
                    if ( isShowLabels() ) {
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

    public Rectangle[] getTextBounds() {
        ArrayList result = new ArrayList();
        Rectangle bounds = null;
        int orientation = super.getOrientation();
        double crossesOtherAxisAt = super.getCrossesOtherAxisAt();
        Chart chart = super.getChart();
        double tickSpacing = super.getSpacing();
        if ( orientation == HORIZONTAL ) {
            double[] gridLines = getGridLines( crossesOtherAxisAt, chart.getRange().getMinX(), chart.getRange().getMaxX(), tickSpacing );
            for ( int i = 0; i < gridLines.length; i++ ) {
                double gridLineX = gridLines[i];
                int x = chart.transformX( gridLineX );
                int y = getHorizontalTickY();
                if ( isShowLabels() ) {
                    String string = format.format( gridLineX );
                    int width = fontMetrics.stringWidth( string );
                    int height = fontMetrics.getHeight();
                    int textX = x - width / 2;
                    int textY = y + tickHeight / 2 + height;
                    Rectangle rect = new Rectangle( textX, textY, width, height );//TODO this is untested
                    result.add( rect );
                }
            }
        }
        else if ( orientation == VERTICAL ) {
            double[] gridLines = getGridLines( crossesOtherAxisAt, chart.getRange().getMinY(), chart.getRange().getMaxY(), tickSpacing );
            for ( int i = 0; i < gridLines.length; i++ ) {
                double gridLineY = gridLines[i];
                int x = getVerticalTickX();
                int y = chart.transformY( gridLineY );
                if ( isShowLabels() ) {
                    String string = format.format( gridLineY );
                    int width = fontMetrics.stringWidth( string );
                    int height = fontMetrics.getHeight();
                    int textX = x - tickHeight / 2 - width;
                    int textY = y - height / 2;
                    Rectangle strBounds = new Rectangle( textX, textY, width, height );
                    result.add( strBounds );
                }
            }
        }
        return (Rectangle[]) result.toArray( new Rectangle[0] );
    }

    public void setNumberFormat( NumberFormat numberFormat ) {
        this.format = numberFormat;
    }

    public void setLabelsVisible( boolean visible ) {
        showLabels = visible;
    }

    public abstract int getVerticalTickX();

    public abstract int getHorizontalTickY();

}
