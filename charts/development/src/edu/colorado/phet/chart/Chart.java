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
import java.util.ArrayList;

public class Chart implements Graphic {
    private Component component;
    private Range2D range;
    private Rectangle viewBounds;

    private ArrayList dataSetGraphics = new ArrayList();
    private Axis xAxis;
    private Axis yAxis;
    private GridLineSet verticalGridlines;
    private GridLineSet horizonalGridlines;

    private TickMarkSet verticalTicks;
    private TickMarkSet horizontalTicks;

    private Paint background = Color.white;
    private Stroke outlineStroke = new BasicStroke( 1 );
    private Color outlineColor = Color.black;
    private ModelViewTransform2D transform;

    public Chart( Component component, Range2D range, Rectangle viewBounds ) {
        this.component = component;
        this.range = range;
        this.viewBounds = viewBounds;
        this.xAxis = new Axis( this, AbstractGrid.HORIZONTAL );
        this.yAxis = new Axis( this, AbstractGrid.VERTICAL );
        this.verticalGridlines = new GridLineSet( this, AbstractGrid.VERTICAL );
        this.horizonalGridlines = new GridLineSet( this, AbstractGrid.HORIZONTAL );
        this.verticalTicks = new TickMarkSet( this, AbstractGrid.VERTICAL, 1, 2 );
        this.horizontalTicks = new TickMarkSet( this, AbstractGrid.HORIZONTAL, 1, 2 );
        this.transform = new ModelViewTransform2D( range.getBounds(), viewBounds );
    }

    public static class TickMarkSet {
        private GridTicks majorTicks;
        private GridTicks minorTicks;

        public TickMarkSet( Chart chart, int orientation, double minorTickSpacing, double majorTickSpacing ) {
            minorTicks = new GridTicks( chart, orientation, new BasicStroke( 2 ), Color.black, minorTickSpacing );
            majorTicks = new GridTicks( chart, orientation, new BasicStroke( 2 ), Color.black, majorTickSpacing );
            minorTicks.setVisible( false );
        }

        public void paint( Graphics2D graphics2D ) {
            minorTicks.paint( graphics2D );
            majorTicks.paint( graphics2D );
        }

        public void setMajorTickSpacing( double majorTickSpacing ) {
            majorTicks.setSpacing( majorTickSpacing );
        }

        public void setMinorTickSpacing( double minorTickSpacing ) {
            minorTicks.setSpacing( minorTickSpacing );
        }

        public void setVisible( boolean visible ) {
            majorTicks.setVisible( visible );
            minorTicks.setVisible( visible );
        }

        public void setMajorGridlines( double[] lines ) {
            majorTicks.setGridlines( lines );
        }

        public void setMajorOffset( int dx, int dy ) {
            majorTicks.setOffset( dx, dy );
        }
    }

    public TickMarkSet getVerticalTicks() {
        return verticalTicks;
    }

    public TickMarkSet getHorizontalTicks() {
        return horizontalTicks;
    }

    public static class GridTicks extends AbstractTicks {
        private int dx = 0;
        private int dy = 0;

        public GridTicks( Chart chart, int orientation, Stroke stroke, Color color, double tickSpacing ) {
            super( chart, orientation, stroke, color, tickSpacing );
        }

        public void setOffset( int dx, int dy ) {
            this.dx = dx;
            this.dy = dy;
        }

        public int getVerticalTickX() {
            Chart chart = getChart();
            return chart.transformX( chart.getRange().getMinX() ) + dx;
        }

        public int getHorizontalTickY() {
            Chart chart = getChart();
            return chart.transformY( chart.getRange().getMinY() ) + dy;
        }
    }

    public ModelViewTransform2D getTransform() {
        return transform;
    }

    public GridLineSet getVerticalGridlines() {
        return verticalGridlines;
    }

    public GridLineSet getHorizonalGridlines() {
        return horizonalGridlines;
    }

    public void setRange( Range2D range ) {
        this.range = range;
        transform.setModelBounds( range.getBounds() );
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
            Range2D nextRange = dataSetGraphic.getDataSet().getRange();
            if( range == null ) {
                range = nextRange;
            }
            else if( nextRange != null ) {
                range = range.union( nextRange );
            }
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
        if( dataSetGraphic.getChart() == null || dataSetGraphic.getChart() == this ) {
            dataSetGraphic.setChart( this );
            dataSetGraphics.add( dataSetGraphic );
        }
        else {
            throw new RuntimeException( "DataSetGraphic was associated with the wrong Chart instance." );
        }
    }

    public Component getComponent() {
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

        return transform.modelToView( point );
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

        horizontalTicks.paint( graphics2D );
        verticalTicks.paint( graphics2D );

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
        transform.setViewBounds( viewBounds );
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

    public int transformY( double gridLineY ) {
        return transform( new Point2D.Double( 0, gridLineY ) ).y;
    }

    public int transformX( double gridLineX ) {
        return transform( new Point2D.Double( gridLineX, 0 ) ).x;
    }

    public Range2D getRange() {
        return range;
    }

    public Rectangle getViewBounds() {
        return viewBounds;
    }


}
