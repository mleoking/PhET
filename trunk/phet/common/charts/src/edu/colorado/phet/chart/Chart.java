/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.chart;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.phetgraphics.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.text.NumberFormat;
import java.util.ArrayList;

public class Chart extends GraphicLayerSet {
    private Component component;
    private Range2D range = new Range2D( 0, 0, 1, 1 );

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
    private PhetGraphic xAxisTitleGraphic;
    private PhetGraphic yAxisTitleGraphic;

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

    private static double getDefaultMinor( double range ) {
        if( range < 20 ) {
            return 1;
        }
        else {
            double defaultNumTicks = 20;
            return range / defaultNumTicks;
        }
    }

    private static double getDefaultMajor( double range ) {
        return getDefaultMinor( range ) * 2;
    }

    public Chart( Component component, Range2D range, Dimension chartSize, double horizMinorSpacing, double horizMajorSpacing, double vertMinorSpacing, double vertMajorSpacing ) {
        super( component );
//        System.out.println( "horizMinorSpacing = " + horizMinorSpacing );
//        System.out.println( "horizMajorSpacing = " + horizMajorSpacing );
//        System.out.println( "vertMinor= " + vertMinorSpacing );
//        System.out.println( "vertMaj= " + vertMajorSpacing );
        this.chartSize = chartSize;
        this.component = component;
        this.range.setRange( range );

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

    /**
     * Sets the title label that appears on the x axis.
     * The title will be place to the right of the chart,
     * and the title's registration point will be aligned 
     * with the x axis.
     * 
     * @param phetGraphic
     */
    public void setXAxisTitle( PhetGraphic phetGraphic ) {
        if( xAxisTitleGraphic != null ) {
            removeGraphic( xAxisTitleGraphic );
        }
        xAxisTitleGraphic = phetGraphic;
        xAxisTitleGraphic.setLocation( chartSize.width + 2, (int) transformYDouble( 0 ) ); // aligned with x axis
        addGraphic( xAxisTitleGraphic, ApparatusPanel.LAYER_TOP - 1 );
    }

    /**
     * Sets the title label that appears on the y axis.
     * The title will be place to the right of the chart,
     * and the title's registration point will be aligned 
     * with the y axis.
     * 
     * @param phetGraphic
     */
    public void setYAxisTitle( PhetGraphic phetGraphic ) {
        if( yAxisTitleGraphic != null ) {
            removeGraphic( yAxisTitleGraphic );
        }
        yAxisTitleGraphic = phetGraphic;
        yAxisTitleGraphic.setLocation( (int) transformXDouble( 0 ), -2 ); // aligned with y axis
        addGraphic( yAxisTitleGraphic, ApparatusPanel.LAYER_TOP - 1 );
    }

    public void removeDataSetGraphic( DataSetGraphic dataSetGraphic ) {
        dataSetGraphics.remove( dataSetGraphic );
        compositeDataSetGraphic.removeGraphic( dataSetGraphic );
    }

    public void removeAllDataSetGraphics() {
        for( int i = 0; i < dataSetGraphics.size(); i++ ) {
            compositeDataSetGraphic.removeGraphic( (DataSetGraphic)dataSetGraphics.get( i ) );
        }
        dataSetGraphics.clear();
    }

    public boolean containsDataSetGraphic( DataSetGraphic dataSetGraphic ) {
        return dataSetGraphics.contains( dataSetGraphic );
    }

    public DataSetGraphic[] getDataSetGraphics() {
        return (DataSetGraphic[])dataSetGraphics.toArray( new DataSetGraphic[0] );
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

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public static class TickMarkSet extends CompositePhetGraphic {
        private GridTicks majorTicks;
        private GridTicks minorTicks;
//        private RangeLabel maxRangeLabel;
//        private RangeLabel minRangeLabel;

        public TickMarkSet( Chart chart, Orientation orientation, double minorTickSpacing, double majorTickSpacing ) {
            super( chart.getComponent() );
            minorTicks = new GridTicks( chart, orientation, new BasicStroke( 2 ), Color.black, minorTickSpacing );
            majorTicks = new GridTicks( chart, orientation, new BasicStroke( 2 ), Color.black, majorTickSpacing );
//            maxRangeLabel = new RangeLabel( getComponent(), chart, majorTicks );
//            minRangeLabel = new RangeLabel( getComponent(), chart, majorTicks );
            minorTicks.setVisible( false );
            addGraphic( minorTicks );
            addGraphic( majorTicks );

//            addGraphic( maxRangeLabel );
//            addGraphic( minRangeLabel );
            setRangeLabelsVisible( false );
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

        public void setMajorLabels( LabelTable labelTable ) {
            majorTicks.setLabels( labelTable );
        }

        public void setMinorLabels( LabelTable labelTable ) {
            minorTicks.setLabels( labelTable );
        }

        public void setRangeLabelsVisible( boolean rangeLabelsVisible ) {
            majorTicks.setRangeLabelsVisible( rangeLabelsVisible );
        }

        public void setMajorNumberFormat( NumberFormat numberFormat ) {
            majorTicks.setNumberFormat( numberFormat );
        }

        public void setRangeLabelsNumberFormat( NumberFormat numberFormat ) {
            majorTicks.setRangeLabelsNumberFormat( numberFormat );
        }
    }

    /**
     * This method is poorly named.
     * It gets the ticks that go along the left (vertical) edge of the chart.
     * It does not get the ticks whose orientation is horizontal.
     * <p/>
     * Note that the Y-axis also has its own set of tick marks that
     * may draw on top of these tick marks if the origin is too close
     * to the left edge of the chart.
     *
     * @return
     */
    public TickMarkSet getVerticalTicks() {
        return verticalTicks;
    }

    /**
     * This method is poorly named.
     * It gets the ticks that go along the bottom (horizontal) edge of the chart.
     * It does not get the ticks whose orientation is horizontal.
     * <p/>
     * Note that the X-axis also has its own set of tick marks that
     * may draw on top of these tick marks if the origin is too close
     * to the bottom edge of the chart.
     *
     * @return
     */
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

    /**
     * Gets the grid lines whose orientation is vertical.
     *
     * @return
     */
    public GridLineSet getVerticalGridlines() {
        return verticalGridlines;
    }

    /**
     * Gets the grid lines whose orientation is horizontal.
     *
     * @return
     */
    public GridLineSet getHorizonalGridlines() {
        return horizonalGridlines;
    }

    /**
     * Sets the chart's range.
     * The chart copies the provided Range object, so that any subsequency
     * changes to that object do not affect the chart.
     *
     * @param range
     */
    public void setRange( Range2D range ) {
        this.range.setRange( range );
        transform.setModelBounds( range.getBounds() );
        fireTransformChanged();
        autorepaint();
    }

    /**
     * Gets a copy of the chart's range.
     *
     * @return the range
     */
    public Range2D getRange() {
        return new Range2D( range );
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

    public int numDataSetGraphics() {
        return dataSetGraphics.size();
    }

    public void setBackground( Paint background ) {
        this.backgroundGraphic.setPaint( background );
    }
    
    public void addDataSetGraphic( DataSetGraphic dataSetGraphic, double layer ) {
        if( dataSetGraphic.getChart() == null || dataSetGraphic.getChart() == this ) {
            dataSetGraphics.add( dataSetGraphic );
            compositeDataSetGraphic.addGraphic( dataSetGraphic, layer );
        }
        else {
            throw new RuntimeException( "DataSetGraphic was associated with the wrong Chart instance." );
        }
    }

    public void addDataSetGraphic( DataSetGraphic dataSetGraphic ) {
        addDataSetGraphic( dataSetGraphic, 0 );
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

    public Point2D transformDouble( Point2D point ) {
        if( point == null ) {
            throw new RuntimeException( "Null point" );
        }
        return transform.modelToViewDouble( point );
    }

    public Point2D transformDouble( double x, double y ) {
        return transformDouble( new Point2D.Double( x, y ) );
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
        
        // Update the axis title after the transform has been adjusted.
        if ( xAxisTitleGraphic != null ) {
            xAxisTitleGraphic.setLocation( chartSize.width + 2, (int) transformYDouble( 0 ) ); // aligned with x axis
        }
        if ( yAxisTitleGraphic != null ) {
            yAxisTitleGraphic.setLocation( (int) transformXDouble( 0 ), -2 ); // aligned with y axis
        }
        
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

    public double transformXDouble( double gridLineX ) {
        return transformDouble( new Point2D.Double( gridLineX, 0 ) ).getX();
    }

    public double transformYDouble( double gridLineY ) {
        return transformDouble( new Point2D.Double( 0, gridLineY ) ).getY();
    }
}
