/**
 * Class: Chart
 * Package: edu.colorado.phet.chart
 * Author: Another Guy
 * Date: Sep 15, 2004
 */
package edu.colorado.phet.chart;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;

import java.awt.*;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

public class Chart implements Graphic {
    private Component component;
    private ArrayList dataSetGraphics = new ArrayList();
    private Axis xAxis;
    private Axis yAxis;
    private GridLineSet verticalGridlines;
    private GridLineSet horizonalGridlines;
    private Range2D range;
    private Rectangle viewBounds;
    private Paint background = Color.white;
    private Stroke outlineStroke = new BasicStroke( 1 );
    private Color outlineColor = Color.black;

    public Chart( Component component, Range2D range, Rectangle viewBounds ) {
        this.component = component;
        this.range = range;
        this.viewBounds = viewBounds;
        this.xAxis = new Axis( this, AbstractGrid.HORIZONTAL );
        this.yAxis = new Axis( this, AbstractGrid.VERTICAL );
        this.verticalGridlines = new GridLineSet( this, AbstractGrid.VERTICAL );
        this.horizonalGridlines = new GridLineSet( this, AbstractGrid.HORIZONTAL );
    }

    public GridLineSet getVerticalGridlines() {
        return verticalGridlines;
    }

    public GridLineSet getHorizonalGridlines() {
        return horizonalGridlines;
    }

    public void setRange( Range2D range ) {
        this.range = range;
        fireTransformChanged();
        repaint();
    }

    public Range2D getDataRange() {
        if( numDataSetGraphics() == 0 ) {
            return null;
        }
        Range2D range = ( (DataSetGraphic)dataSetGraphics.get( 0 ) ).getDataSet().getRange();
        for( int i = 1; i < dataSetGraphics.size(); i++ ) {
            DataSetGraphic dataSetGraphic = (DataSetGraphic)dataSetGraphics.get( i );
            range = range.union( dataSetGraphic.getDataSet().getRange() );
        }
        return range;
    }

    private int numDataSetGraphics() {
        return dataSetGraphics.size();
    }


    public void setBackground( Paint background ) {
        this.background = background;
    }

    public void addDataSetGraphic( DataSetGraphic dataSetGraphic ) {
        dataSetGraphic.setChart( this );
        dataSetGraphics.add( dataSetGraphic );
    }

    Component getComponent() {
        return component;
    }

    /**
     * Takes a point in model coordinates and returns the corresponding view location.
     *
     * @param point
     * @return the Point in view coordinates.
     */
    public Point transform( Point2D point ) {
        if( point == null ) {
            throw new RuntimeException( "Null point" );
        }
        ModelViewTransform2D tx = new ModelViewTransform2D( range.getBounds(), viewBounds, true );
        return tx.modelToView( point );
    }

    public Point transform( double x, double y ) {
        return transform( new Point2D.Double( x, y ) );
    }

    public void paint( Graphics2D graphics2D ) {
        //paint the background
        graphics2D.setPaint( background );
        graphics2D.fill( viewBounds );

        //paint the gridlines
        horizonalGridlines.paint( graphics2D );
        verticalGridlines.paint( graphics2D );

        //paint the axes
        xAxis.paint( graphics2D );
        yAxis.paint( graphics2D );

        //paint the ornaments.

        //paint the datasets
        Shape clip = graphics2D.getClip();
        graphics2D.setClip( viewBounds );
        for( int i = 0; i < dataSetGraphics.size(); i++ ) {
            DataSetGraphic dataSetGraphic = (DataSetGraphic)dataSetGraphics.get( i );
            dataSetGraphic.paint( graphics2D );
        }
        graphics2D.setClip( clip );
        graphics2D.setStroke( outlineStroke );
        graphics2D.setColor( outlineColor );
        graphics2D.draw( viewBounds );
    }

    public Axis getXAxis() {
        return xAxis;
    }

    public Axis getYAxis() {
        return yAxis;
    }

    public void setXAxis( Axis xAxis ) {
        this.xAxis = xAxis;
    }

    public void setYAxis( Axis yAxis ) {
        this.yAxis = yAxis;
    }

    public void setViewBounds( Rectangle viewBounds ) {
        this.viewBounds = viewBounds;
        fireTransformChanged();
        repaint();
    }

    private void repaint() {
        component.repaint( viewBounds.x, viewBounds.y, viewBounds.width, viewBounds.height );
    }

    private void fireTransformChanged() {
        for( int i = 0; i < dataSetGraphics.size(); i++ ) {
            DataSetGraphic dataSetGraphic = (DataSetGraphic)dataSetGraphics.get( i );
            dataSetGraphic.transformChanged();
        }
    }

    private int transformY( double gridLineY ) {
        return transform( new Point2D.Double( 0, gridLineY ) ).y;
    }

    private int transformX( double gridLineX ) {
        return transform( new Point2D.Double( gridLineX, 0 ) ).x;
    }

    //TODO this needs to be rewritten to handle the case of origin outside of the viewport. Current handling is VERY slow.
    public static double[] getGridLines( double origin, double min, double max, double spacing ) {
        ArrayList results = new ArrayList();

        for( double currentPoint = origin; currentPoint <= max; currentPoint += spacing ) {
            if( currentPoint >= min && currentPoint <= max ) {
                results.add( new Double( currentPoint ) );
            }
        }
        for( double currentPoint = origin - spacing; currentPoint >= min; currentPoint -= spacing ) {
            if( currentPoint >= min && currentPoint <= max ) {
                results.add( new Double( currentPoint ) );
            }
        }
        Collections.sort( results );
        double[] output = new double[results.size()];
        for( int i = 0; i < output.length; i++ ) {
            output[i] = ( (Double)results.get( i ) ).doubleValue();
        }
        return output;
    }

    public Range2D getRange() {
        return range;
    }

    public Rectangle getViewBounds() {
        return viewBounds;
    }


    /**
     * Inner classses.
     */
    public static class GridLineSet {
        private Grid minorGrid;
        private Grid majorGrid;

        public GridLineSet( Chart chart, int orientation ) {
            this( chart, orientation, 1, 2, 0 );
        }

        public void setMinorTickSpacing( double minorTickSpacing ) {
            minorGrid.setTickSpacing( minorTickSpacing );
        }

        public void setMajorTickSpacing( double majorTickSpacing ) {
            majorGrid.setTickSpacing( majorTickSpacing );
        }

        public GridLineSet( Chart chart, int orientation, int minorTickSpacing, int majorTickSpacing, int crossesOtherAxisAt ) {
            minorGrid = new Grid( chart, orientation, new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[]{7, 7}, 0 ),
                                  Color.black, minorTickSpacing, crossesOtherAxisAt );
            majorGrid = new Grid( chart, orientation, new BasicStroke( 1 ),
                                  Color.black, majorTickSpacing, crossesOtherAxisAt );
        }

        public void paint( Graphics2D graphics2D ) {
            minorGrid.paint( graphics2D );
            majorGrid.paint( graphics2D );
        }

        public void setVisible( boolean visible ) {
            minorGrid.setVisible( visible );
            majorGrid.setVisible( visible );
        }

        public void setMajorGridlinesVisible( boolean visible ) {
            majorGrid.setVisible( visible );
        }

        public void setMinorGridlinesVisible( boolean visible ) {
            minorGrid.setVisible( visible );
        }

        public void setMajorGridlinesColor( Color color ) {
            majorGrid.setColor( color );
        }

        public void setMinorGridlinesColor( Color color ) {
            minorGrid.setColor( color );
        }

        public void setMajorGridlinesStroke( Stroke stroke ) {
            majorGrid.setStroke( stroke );
        }

        public void setMinorGridlinesStroke( Stroke stroke ) {
            minorGrid.setStroke( stroke );
        }
    }

    public static class Grid extends AbstractGrid {
        public Grid( Chart chart, int orientation, Stroke stroke, Color color, double tickSpacing, double crossesOtherAxisAt ) {
            super( chart, orientation, stroke, color, tickSpacing, crossesOtherAxisAt );
        }

        public void paint( Graphics2D g ) {
            if( isVisible() ) {
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
        }

    }

    public static abstract class AbstractGrid implements Graphic {
        boolean visible = true;
        public final static int HORIZONTAL = 1;
        public final static int VERTICAL = 2;
        Chart chart;
        int orientation;
        Stroke stroke;
        Color color;
        double tickSpacing;
        double crossesOtherAxisAt;

        protected AbstractGrid( Chart chart, int orientation, Stroke stroke, Color color, double tickSpacing, double crossesOtherAxisAt ) {
            this.chart = chart;
            this.orientation = orientation;
            this.stroke = stroke;
            this.color = color;
            this.tickSpacing = tickSpacing;
            this.crossesOtherAxisAt = crossesOtherAxisAt;
        }

        public void setStroke( Stroke stroke ) {
            this.stroke = stroke;
        }

        public void setColor( Color color ) {
            this.color = color;
        }

        public void setTickSpacing( double tickSpacing ) {
            this.tickSpacing = tickSpacing;
        }

        public void setCrossesOtherAxisAt( double crossesOtherAxisAt ) {
            this.crossesOtherAxisAt = crossesOtherAxisAt;
        }

        public void setVisible( boolean visible ) {
            this.visible = visible;
        }

        public boolean isVisible() {
            return visible;
        }
    }

    public static class AxisGrid extends AbstractGrid {
        int tickHeight = 6;
        DecimalFormat formatter = new DecimalFormat( "#.#" );
        Font font = new Font( "Lucida Sans", 0, 12 );
        private FontMetrics fontMetrics;
        boolean showLabels = true;

        public AxisGrid( Chart chart, int orientation, Stroke stroke, Color color, double tickSpacing ) {
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
            this.formatter = formatter;
        }

        public void setTickHeight( int tickHeight ) {
            this.tickHeight = tickHeight;
        }

        public void paint( Graphics2D g ) {
            if( isVisible() ) {
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
                            String string = formatter.format( gridLineX );
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
                            String string = formatter.format( gridLineY );
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
    }

    public static class Axis implements Graphic {
        Chart chart;
        AxisGrid minorGrid;
        AxisGrid majorGrid;
        Stroke stroke;
        Color color;
        int orientation;
        double crossesOtherAxisAt = 0;

        public Axis( Chart chart, int orientation ) {
            this( chart, orientation, new BasicStroke( 2 ), Color.black, 2, 1 );
        }

        public Axis( Chart chart, int orientation, Stroke stroke, Color color, double minorTickSpacing, double majorTickSpacing ) {
            this.minorGrid = new AxisGrid( chart, orientation, stroke, color, minorTickSpacing );
            minorGrid.setShowLabels( false );
            this.majorGrid = new AxisGrid( chart, orientation, stroke, color, majorTickSpacing );
            majorGrid.setShowLabels( true );
            this.stroke = stroke;
            this.orientation = orientation;
            this.chart = chart;
            this.color = color;
        }

        public AxisGrid getMinorGrid() {
            return minorGrid;
        }

        public AxisGrid getMajorGrid() {
            return majorGrid;
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
            minorGrid.paint( g );
            majorGrid.paint( g );
        }
    }
}
