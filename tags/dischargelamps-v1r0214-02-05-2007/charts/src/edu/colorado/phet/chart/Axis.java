/** Sam Reid*/
package edu.colorado.phet.chart;

import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.text.NumberFormat;

/**
 * User: Sam Reid
 * Date: Sep 21, 2004
 * Time: 6:21:43 AM
 * Copyright (c) Sep 21, 2004 by Sam Reid
 */
public class Axis extends GraphicLayerSet {
    private Chart chart;
    private AxisTicks minorTicks;
    private AxisTicks majorTicks;
    private Orientation orientation;
    private double crossesOtherAxisAt = 0;
    private GraphicLayerSet lineGraphic;
    private Stroke stroke;
    private Color color;
    private boolean inBounds = true;

    public Axis( Chart chart, Orientation orientation ) {
        this( chart, orientation, new BasicStroke( 2 ), Color.black, 2, 1 );
    }

    public Axis( Chart chart, Orientation orientation, Stroke stroke, Color color, double minorTickSpacing, double majorTickSpacing ) {
        minorTicks = new AxisTicks( chart, orientation.opposite(), stroke, color, minorTickSpacing );
        minorTicks.setShowLabels( false );
        majorTicks = new AxisTicks( chart, orientation.opposite(), stroke, color, majorTickSpacing );
        majorTicks.setShowLabels( true );
        this.stroke = stroke;
        this.orientation = orientation;
        this.chart = chart;
        this.color = color;
        minorTicks.setVisible( false );
        lineGraphic = new GraphicLayerSet();
        addGraphic( lineGraphic );
        addGraphic( minorTicks );
        addGraphic( majorTicks );
        chart.addListener( new Chart.Listener() {
            public void transformChanged( Chart chart ) {
                update();
            }
        } );
        update();
    }

    public void update() {
        lineGraphic.clear();
        if( orientation.isHorizontal() ) {
            Point2D.Double leftEndOfAxis = new Point2D.Double( chart.getRange().getMinX(), crossesOtherAxisAt );
            Point left = chart.transform( leftEndOfAxis );
            Point2D.Double rightEndOfAxis = new Point2D.Double( chart.getRange().getMaxX(), crossesOtherAxisAt );
            Point right = chart.transform( rightEndOfAxis );
            Line2D.Double line = new Line2D.Double( left.x, left.y, right.x, right.y );
            PhetShapeGraphic phetShapeGraphic = new PhetShapeGraphic( chart.getComponent(), line, stroke, color );
            lineGraphic.addGraphic( phetShapeGraphic );
        }
        else if( orientation.isVertical() ) {
            Point2D.Double bottomEndOfAxis = new Point2D.Double( crossesOtherAxisAt, chart.getRange().getMinY() );
            Point bottom = chart.transform( bottomEndOfAxis );
            Point2D.Double topEndOfAxis = new Point2D.Double( crossesOtherAxisAt, chart.getRange().getMaxY() );
            Point top = chart.transform( topEndOfAxis );
            Line2D.Double line = new Line2D.Double( bottom.x, bottom.y, top.x, top.y );
            PhetShapeGraphic phetShapeGraphic = new PhetShapeGraphic( chart.getComponent(), line, stroke, color );
            lineGraphic.addGraphic( phetShapeGraphic );
        }

        majorTicks.update();
        minorTicks.update();
        inBounds = isInBounds();
//        System.out.println( "inBounds = " + inBounds );
        setBoundsDirty();
    }

    protected Rectangle determineBounds() {
        if( !inBounds ) {
            return null;
        }
        else {
            return super.determineBounds();
        }
    }

    private boolean isInBounds() {
        if( orientation.isHorizontal() ) {
            return chart.getRange().containsY( crossesOtherAxisAt );
        }
        else if( orientation.isVertical() ) {
            return chart.getRange().containsX( crossesOtherAxisAt );
        }
        else {
            throw new RuntimeException( "Illegal orientation" );
        }
    }

    public void paint( Graphics2D g2 ) {
        if( inBounds ) {
            super.paint( g2 );
        }
    }

    protected AxisTicks getMinorTicks() {
        return minorTicks;
    }

    protected AxisTicks getMajorTicks() {
        return majorTicks;
    }

    public void setColor( Color color ) {
        this.color = color;
        update();
    }

    public void setStroke( Stroke stroke ) {
        this.stroke = stroke;
        update();
    }

    public void setMajorTickFont( Font font ) {
        majorTicks.setFont( font );
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

    public void setMajorTickSpacing( double x ) {
        majorTicks.setSpacing( x );
    }

    public void setMinorTickFont( Font font ) {
        minorTicks.setFont( font );
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

    public void setMinorTickStroke( Stroke stroke ) {
        minorTicks.setStroke( stroke );
    }

    public void setMajorGridlines( double[] lines ) {
        majorTicks.setGridlines( lines );
    }

    public void setMinorTickSpacing( double x ) {
        minorTicks.setSpacing( x );
    }

    public void setMajorLabels( LabelTable labelTable ) {
        majorTicks.setLabels( labelTable );
    }

    public void setMinorLabels( LabelTable labelTable ) {
        minorTicks.setLabels( labelTable );
    }

    public static class AxisTicks extends AbstractTicks {

        public AxisTicks( Chart chart, Orientation orientation, Stroke stroke, Color color, double tickSpacing ) {
            super( chart, orientation, stroke, color, tickSpacing, 0 );
        }

        public int getVerticalTickX() {
            Point2D.Double bottomEndOfAxis = new Point2D.Double( getCrossesOtherAxisAt(), getChart().getRange().getMinY() );
            Point bottom = getChart().transform( bottomEndOfAxis );
            return bottom.x;
        }

        private double getCrossesOtherAxisAt() {
            if( getGridStrategy() instanceof GridStrategy.Relative ) {
                return ( (GridStrategy.Relative)getGridStrategy() ).getCrossesOtherAxisAt();
            }
            else {
                throw new RuntimeException( "Axes must use Relative grid strategy." );
            }
        }

        public int getHorizontalTickY() {
            Point2D.Double leftEndOfAxis = new Point2D.Double( getChart().getRange().getMinX(), getCrossesOtherAxisAt() );
            Point left = getChart().transform( leftEndOfAxis );
            return left.y;
        }
    }
}
