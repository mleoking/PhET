/**
 * Class: Chart
 * Package: edu.colorado.phet.chart
 * Author: Another Guy
 * Date: Sep 15, 2004
 */
package edu.colorado.phet.chart;

import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.phetgraphics.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Chart extends GraphicLayerSet {
    private Component component;
    private Range2D range;

    private ArrayList dataSetGraphics = new ArrayList();
    private Axis xAxis;
    private Axis yAxis;
    private GridLineSet verticalGridlines;
    private GridLineSet horizonalGridlines;

    private TickMarkSet verticalTicks;
    private TickMarkSet horizontalTicks;

    private Paint background = Color.white;
    private ModelViewTransform2D transform;
    private ArrayList listeners = new ArrayList();

    private GraphicLayerSet compositeDataSetGraphic;
    private PhetShapeGraphic backgroundGraphic;
    private PhetShapeGraphic frameGraphic;
    private HTMLGraphic title;
    private Dimension chartSize;

    /**
     * @param component
     * @param range
     * @param viewBounds
     * @deprecated, viewBounds should be a Dimension only.  Set the location with setLocation().
     */
    public Chart( Component component, Range2D range, Rectangle viewBounds ) {
        this( component, range, viewBounds.getSize() );
    }

    public Chart( Component component, Range2D range, Dimension chartSize ) {
        this( component, range, chartSize, getDefaultMinor( range.getWidth() ), getDefaultMajor( range.getWidth() ), getDefaultMinor( range.getHeight() ), getDefaultMajor( range.getHeight() ) );
    }

    private static int getDefaultMinor( double range ) {
        if( range < 20 ) {
            return 1;
        }
        else {
            double defaultNumTicks = 20;
            return (int)( range / defaultNumTicks );
        }
    }

    private static int getDefaultMajor( double range ) {
        return getDefaultMinor( range ) * 2;
    }

    public Chart( Component component, Range2D range, Dimension chartSize, int horizMinorSpacing, int horizMajorSpacing, int vertMinorSpacing, int vertMajorSpacing ) {
        super( component );
        System.out.println( "horizMinorSpacing = " + horizMinorSpacing );
        System.out.println( "horizMajorSpacing = " + horizMajorSpacing );
        System.out.println( "vertMinor= " + vertMinorSpacing );
        System.out.println( "vertMaj= " + vertMajorSpacing );
        this.chartSize = chartSize;
        this.component = component;
        this.range = range;

        this.transform = new ModelViewTransform2D( range.getBounds(), new Rectangle( chartSize ) );

        this.yAxis = new Axis( this, Orientation.VERTICAL, new BasicStroke( 2 ), Color.black, vertMinorSpacing, vertMajorSpacing );
        this.verticalTicks = new TickMarkSet( this, Orientation.HORIZONTAL, vertMinorSpacing, vertMajorSpacing );
        this.verticalGridlines = new GridLineSet( this, Orientation.VERTICAL, horizMinorSpacing, horizMajorSpacing, 0 );

        this.xAxis = new Axis( this, Orientation.HORIZONTAL, new BasicStroke( 2 ), Color.black, horizMinorSpacing, horizMajorSpacing );
        this.horizontalTicks = new TickMarkSet( this, Orientation.VERTICAL, horizMinorSpacing, horizMajorSpacing );
        this.horizonalGridlines = new GridLineSet( this, Orientation.HORIZONTAL, vertMinorSpacing, vertMajorSpacing, 0 );

        backgroundGraphic = new PhetShapeGraphic( component, getChartBounds(), background );
        compositeDataSetGraphic = new GraphicLayerSet( component );
        frameGraphic = new PhetShapeGraphic( component, getChartBounds(), new BasicStroke( 1 ), Color.black );
        title = new HTMLGraphic( component, component.getFont(), "Title", Color.black );
        title.setVisible( false );

        addGraphic( backgroundGraphic );
        addGraphic( verticalGridlines );
        addGraphic( horizonalGridlines );
        addGraphic( verticalTicks );
        addGraphic( horizontalTicks );

        addGraphic( xAxis );
        addGraphic( yAxis );
        addGraphic( compositeDataSetGraphic );
        addGraphic( frameGraphic );
        addGraphic( title );
    }

    public void removeDataSetGraphic( DataSetGraphic dataSetGraphic ) {
        dataSetGraphics.remove( dataSetGraphic );
        compositeDataSetGraphic.removeGraphic( dataSetGraphic );
    }

    public boolean containsDataSetGraphic( DataSetGraphic dataSetGraphic ) {
        return dataSetGraphics.contains( dataSetGraphic );
    }

    public void setVerticalTitle( String title, Color color, Font verticalTitleFont ) {
        //todo implement me.
        this.title.setHtml( title );
        this.title.setColor( color );
        this.title.setFont( verticalTitleFont );
    }

    public PhetGraphic getTitle() {
        return title;
    }

    public Dimension getChartSize() {
        return new Dimension( chartSize );
    }

    public int getDecorationInsetX() {
        return -(int)( verticalTicks.getBounds().getMinX() - backgroundGraphic.getBounds().getMinX() );
    }

    public double[] getLinesInBounds( Orientation orientation, double[] gridLines ) {
        ArrayList bounded = new ArrayList();
        if( orientation.isHorizontal() ) {
            for( int i = 0; i < gridLines.length; i++ ) {
                double gridLine = gridLines[i];
                if( range.containsY( gridLine ) ) {
                    bounded.add( new Double( gridLine ) );
                }
            }
        }
        else {
            for( int i = 0; i < gridLines.length; i++ ) {
                double gridLine = gridLines[i];
                if( range.containsX( gridLine ) ) {
                    bounded.add( new Double( gridLine ) );
                }
            }
        }
        double[] out = new double[bounded.size()];
        for( int i = 0; i < bounded.size(); i++ ) {
            java.lang.Double aDouble = (java.lang.Double)bounded.get( i );
            out[i] = aDouble.doubleValue();
        }
        return out;
    }

    public interface Listener {
        void transformChanged( Chart chart );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public static class TickMarkSet extends CompositePhetGraphic {
        private GridTicks majorTicks;
        private GridTicks minorTicks;

        public TickMarkSet( Chart chart, Orientation orientation, double minorTickSpacing, double majorTickSpacing ) {
            minorTicks = new GridTicks( chart, orientation, new BasicStroke( 2 ), Color.black, minorTickSpacing );
            majorTicks = new GridTicks( chart, orientation, new BasicStroke( 2 ), Color.black, majorTickSpacing );
            minorTicks.setVisible( false );
            addGraphic( minorTicks );
            addGraphic( majorTicks );
        }

        public void setMajorTicksVisible( boolean visible ) {
           majorTicks.setVisible( visible );    
        }
        
        public void setMinorTicksVisible( boolean visible ) {
            minorTicks.setVisible( visible );
        }
        
        public void setMajorTickLabelsVisible( boolean visible ) {
            majorTicks.setLabelsVisible( visible );
        }
        
        public void setMinorTickLabelsVisible( boolean visible ) {
            minorTicks.setLabelsVisible( visible );
        }
        
        public void setMajorTickSpacing( double majorTickSpacing ) {
            majorTicks.setSpacing( majorTickSpacing );
        }

        public void setMinorTickSpacing( double minorTickSpacing ) {
            minorTicks.setSpacing( minorTickSpacing );
        }

        public void setMajorGridlines( double[] lines ) {
            majorTicks.setGridlines( lines );
        }

        public void setMajorOffset( int dx, int dy ) {
            majorTicks.setOffset( dx, dy );
        }

        public void setMajorTickFont( Font font ) {
            majorTicks.setFont( font );
        }
        
        public void setMinorTickFont( Font font ) {
            minorTicks.setFont( font );
        }
        
        public void setMajorTickStroke( Stroke stroke ) {
            majorTicks.setStroke( stroke );
        }
        
        public void setMinorTickStroke( Stroke stroke ) {
            minorTicks.setStroke( stroke );
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

        public GridTicks( Chart chart, Orientation orientation, Stroke stroke, Color color, double tickSpacing ) {
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

    public ModelViewTransform2D getModelViewTransform() {
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
        autorepaint();
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
        this.backgroundGraphic.setPaint( background );
    }

    public void addDataSetGraphic( DataSetGraphic dataSetGraphic ) {
        if( dataSetGraphic.getChart() == null || dataSetGraphic.getChart() == this ) {
            dataSetGraphics.add( dataSetGraphic );
            compositeDataSetGraphic.addGraphic( dataSetGraphic );
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

    public void setChartSize( int width, int height ) {
        this.chartSize = new Dimension( width, height );
        Rectangle viewBounds = new Rectangle( chartSize );
        backgroundGraphic.setShape( viewBounds );
        frameGraphic.setShape( viewBounds );
        transform.setViewBounds( viewBounds );
        fireTransformChanged();
        setBoundsDirty();
        autorepaint();
    }

    /**
     * @param viewBounds
     */
    public void setViewBounds( Rectangle viewBounds ) {
        setChartSize( viewBounds.width, viewBounds.height );
        setLocation( viewBounds.x, viewBounds.y );
    }

    public Rectangle getChartBounds() {
        return getNetTransform().createTransformedShape( new Rectangle( chartSize ) ).getBounds();
    }

    private void fireTransformChanged() {
        for( int i = 0; i < dataSetGraphics.size(); i++ ) {
            DataSetGraphic dataSetGraphic = (DataSetGraphic)dataSetGraphics.get( i );
            dataSetGraphic.transformChanged();
        }
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.transformChanged( this );
        }
    }

    public DataSetGraphic dataSetGraphicAt( int i ) {
        return (DataSetGraphic)dataSetGraphics.get( i );
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

}
