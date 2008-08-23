package edu.colorado.phet.forces1d.charts;

//import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;
import java.awt.geom.Point2D;
import java.text.NumberFormat;

/**
 * User: Sam Reid
 * Date: Sep 21, 2004
 * Time: 6:21:43 AM
 */
public class Axis {
    private Chart chart;
    private AxisTicks minorTicks;
    private AxisTicks majorTicks;
    private Stroke stroke;
    private Color color;
    private int orientation;
    private double crossesOtherAxisAt = 0;
    private boolean visible = true;

    public Axis( Chart chart, int orientation ) {
        this( chart, orientation, new BasicStroke( 2 ), Color.black, 2, 1 );
    }

    public Axis( Chart chart, int orientation, Stroke stroke, Color color, double minorTickSpacing, double majorTickSpacing ) {
        minorTicks = new AxisTicks( chart, orientation, stroke, color, minorTickSpacing );
        minorTicks.setShowLabels( false );
        majorTicks = new AxisTicks( chart, orientation, stroke, color, majorTickSpacing );
        majorTicks.setShowLabels( true );
        this.stroke = stroke;
        this.orientation = orientation;
        this.chart = chart;
        this.color = color;
        minorTicks.setVisible( false );
    }

    protected AxisTicks getMinorTicks() {
        return minorTicks;
    }

    protected AxisTicks getMajorTicks() {
        return majorTicks;
    }

    public void setVisible( boolean visible ) {
//        majorTicks.setVisible( visible );
//        minorTicks.setVisible( visible );
        this.visible = visible;
    }

    public void paint( Graphics2D g ) {
        if ( isInRange() && isVisible() ) {
            Stroke origStroke = g.getStroke();
            Color origColor = g.getColor();
            g.setStroke( stroke );
            g.setColor( color );
            if ( orientation == AbstractGrid.HORIZONTAL ) {
                Point2D.Double leftEndOfAxis = new Point2D.Double( chart.getRange().getMinX(), crossesOtherAxisAt );
                Point left = chart.transform( leftEndOfAxis );
                Point2D.Double rightEndOfAxis = new Point2D.Double( chart.getRange().getMaxX(), crossesOtherAxisAt );
                Point right = chart.transform( rightEndOfAxis );
                g.drawLine( left.x, left.y, right.x, right.y );
            }
            else if ( orientation == AbstractGrid.VERTICAL ) {
                Point2D.Double bottomEndOfAxis = new Point2D.Double( crossesOtherAxisAt, chart.getRange().getMinY() );
                Point bottom = chart.transform( bottomEndOfAxis );
                Point2D.Double topEndOfAxis = new Point2D.Double( crossesOtherAxisAt, chart.getRange().getMaxY() );
                Point top = chart.transform( topEndOfAxis );
                g.drawLine( bottom.x, bottom.y, top.x, top.y );
            }
            g.setStroke( origStroke );
            g.setColor( origColor );
            minorTicks.paint( g );
            majorTicks.paint( g );
        }
    }

    private boolean isInRange() {
        if ( orientation == Grid.HORIZONTAL ) {
            return chart.getRange().containsY( crossesOtherAxisAt );
        }
        else if ( orientation == Grid.VERTICAL ) {
            return chart.getRange().containsX( crossesOtherAxisAt );
        }
        else {
            throw new RuntimeException( "Illegal orientation" );
        }
    }

    private boolean isVisible() {
        return visible;
    }

    public void setColor( Color color ) {
        this.color = color;
    }

    public void setStroke( Stroke stroke ) {
        this.stroke = stroke;
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

    public void setMajorGridlines( double[] lines ) {
        majorTicks.setGridlines( lines );
    }

    public static class AxisTicks extends AbstractTicks {

        public AxisTicks( Chart chart, int orientation, Stroke stroke, Color color, double tickSpacing ) {
            super( chart, orientation, stroke, color, tickSpacing, 0 );
        }

        public int getVerticalTickX() {
            Point2D.Double bottomEndOfAxis = new Point2D.Double( getCrossesOtherAxisAt(), getChart().getRange().getMinY() );
            Point bottom = getChart().transform( bottomEndOfAxis );
            return bottom.x;
        }

        public int getHorizontalTickY() {
            Point2D.Double leftEndOfAxis = new Point2D.Double( getChart().getRange().getMinX(), getCrossesOtherAxisAt() );
            Point left = getChart().transform( leftEndOfAxis );
            return left.y;
        }
    }
}
