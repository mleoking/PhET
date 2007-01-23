package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.colorado.phet.piccolo.nodes.PhetPPath;
import edu.colorado.phet.piccolo.nodes.ShadowPText;
import edu.colorado.phet.rotation.model.SimulationVariable;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PClip;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 10:38:00 PM
 * Copyright (c) Dec 28, 2006 by Sam Reid
 */

public class ControlGraph extends PNode {
    private PSwingCanvas pSwingCanvas;

    private GraphControlNode graphControlNode;
    private ChartSlider chartSlider;
    private ZoomSuiteNode zoomControl;

    private ArrayList seriesNodes = new ArrayList();
    private JFreeChartNode jFreeChartNode;
    private PNode titleLayer = new PNode();

    private ArrayList listeners = new ArrayList();
    private JFreeChart jFreeChart;
    private int minDomainValue = 1000;
    private double ZOOM_FRACTION = 1.1;
    private Layout layout = new FlowLayout();

    private ArrayList seriesDataList = new ArrayList();

    public ControlGraph( PSwingCanvas pSwingCanvas, final SimulationVariable simulationVariable, String abbr, String title, double min, double max ) {
        this( pSwingCanvas, simulationVariable, abbr, title, min, max, Color.black, new PText( "THUMB" ) );
    }

    public ControlGraph( PSwingCanvas pSwingCanvas, final SimulationVariable simulationVariable, String abbr, String title, double min, final double max, Color color, PNode thumb ) {
        this.pSwingCanvas = pSwingCanvas;
        XYDataset dataset = new XYSeriesCollection( new XYSeries( "dummy series" ) );
        jFreeChart = ChartFactory.createXYLineChart( title + ", " + abbr, null, null, dataset, PlotOrientation.VERTICAL, false, false, false );
        jFreeChart.setTitle( (String)null );
        jFreeChart.getXYPlot().getRangeAxis().setRange( min, max );
        jFreeChart.getXYPlot().getDomainAxis().setRange( 0, minDomainValue );
        jFreeChart.setBackgroundPaint( null );

        jFreeChartNode = new JFreeChartNode( jFreeChart ) {

            public boolean setBounds( double x, double y, double width, double height ) {
                boolean ok = super.setBounds( x, y, width, height );
                updateChartRenderingInfo();
                return ok;
            }
        };
        jFreeChartNode.setBuffered( true );
        jFreeChartNode.setBounds( 0, 0, 300, 400 );

        graphControlNode = new GraphControlNode( pSwingCanvas, new DefaultGraphTimeSeries() );
        addSeries( title, color, abbr, simulationVariable );
        chartSlider = new ChartSlider( jFreeChartNode, thumb );
        zoomControl = new ZoomSuiteNode();
        zoomControl.addVerticalZoomListener( new ZoomControlNode.ZoomListener() {
            public void zoomedOut() {
                zoomVertical( ZOOM_FRACTION );
            }

            public void zoomedIn() {
                zoomVertical( 1.0 / ZOOM_FRACTION );
            }
        } );
        zoomControl.addHorizontalZoomListener( new ZoomControlNode.ZoomListener() {
            public void zoomedOut() {
                zoomHorizontal( ZOOM_FRACTION );
            }

            public void zoomedIn() {
                zoomHorizontal( 1.0 / ZOOM_FRACTION );
            }
        } );

        addChild( graphControlNode );
        addChild( chartSlider );
        addChild( jFreeChartNode );
        addChild( zoomControl );
        addChild( titleLayer );

        simulationVariable.addListener( new SimulationVariable.Listener() {
            public void valueChanged() {
                chartSlider.setValue( simulationVariable.getValue() );
            }
        } );
        chartSlider.addListener( new ChartSlider.Listener() {
            public void valueChanged() {
                simulationVariable.setValue( chartSlider.getValue() );
            }
        } );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                notifyListeners();
            }
        } );
        jFreeChartNode.updateChartRenderingInfo();
        relayout();
        updateZoomEnabled();
    }

    public void addHorizontalZoomListener( ZoomControlNode.ZoomListener zoomListener ) {
        zoomControl.addHorizontalZoomListener( zoomListener );
    }

    static class TitleNode extends PNode {

        public TitleNode( String title, String abbr, Color color ) {
            ShadowPText titlePText = new ShadowPText( title + ", " + abbr );
            titlePText.setFont( new Font( "Lucida Sans", Font.BOLD, 14 ) );
            titlePText.setTextPaint( color );
            addChild( new PhetPPath( RectangleUtils.expand( titlePText.getFullBounds(), 2, 2 ), Color.white, new BasicStroke(), Color.black ) );
            addChild( titlePText );
        }
    }

    private void zoomHorizontal( double v ) {
        double currentValue = jFreeChart.getXYPlot().getDomainAxis().getUpperBound();
        double newValue = currentValue * v;
        if( newValue > minDomainValue ) {
            newValue = minDomainValue;
        }
        jFreeChart.getXYPlot().getDomainAxis().setUpperBound( newValue );
        updateZoomEnabled();
    }

    private void zoomVertical( double v ) {
        double currentRange = jFreeChart.getXYPlot().getRangeAxis().getUpperBound() - jFreeChart.getXYPlot().getRangeAxis().getLowerBound();
        double newRange = currentRange * v;
        double diff = newRange - currentRange;
        jFreeChart.getXYPlot().getRangeAxis().setRange( jFreeChart.getXYPlot().getRangeAxis().getLowerBound() - diff / 2, jFreeChart.getXYPlot().getRangeAxis().getUpperBound() + diff / 2 );
        updateZoomEnabled();
    }

    private void updateZoomEnabled() {
        zoomControl.setHorizontalZoomOutEnabled( jFreeChart.getXYPlot().getDomainAxis().getUpperBound() != minDomainValue );
    }

    public static class SeriesData {
        String title;
        Color color;
        XYSeries series;

        public SeriesData( String title, Color color ) {
            this( title, color, new XYSeries( title ) );
        }

        public SeriesData( String title, Color color, XYSeries series ) {
            this.title = title;
            this.color = color;
            this.series = series;
        }

        public String getTitle() {
            return title;
        }

        public Color getColor() {
            return color;
        }

        public XYSeries getSeries() {
            return series;
        }
    }

    public void addSeries( String title, Color color, String abbr, SimulationVariable simulationVariable ) {
        SeriesData seriesData = new SeriesData( title, color );
        seriesDataList.add( seriesData );
        updateSeriesPlotViews();

        TitleNode titleNode = new TitleNode( title, abbr, color );
        titleNode.setOffset( titleLayer.getFullBounds().getWidth(), 0 );
        titleLayer.addChild( titleNode );

        graphControlNode.addVariable( abbr, color, simulationVariable, pSwingCanvas );
    }

    private void updateSeriesPlotViews( String title, Color color, XYSeries series ) {

        SeriesNode o = new SeriesNode( title + " " + "series_" + seriesNodes.size(), color, this, series );
        seriesNodes.add( o );
        jFreeChartNode.addChild( o );
    }

    public double getMaxDataX() {
        return jFreeChart.getXYPlot().getDomainAxis().getUpperBound();
    }

    public void setDomainUpperBound( double maxDataX ) {
        jFreeChart.getXYPlot().getDomainAxis().setUpperBound( maxDataX );
        updateZoomEnabled();
    }

    public void setFlowLayout() {
        setLayout( new FlowLayout() );
    }

    public void setAlignedLayout( GraphComponent[] graphComponents ) {
        setLayout( new AlignedLayout( graphComponents ) );
    }

    //The primary ways of drawing the data are:
    //1. Allow jfreechart to do all the drawing.
    //2. Buffer the chart background and draw the series as a PNode.
    //3. Buffer the chart background and draw the series directly into the background.
    private static interface SeriesGraphic {
    }

    private static class SeriesBuffer implements SeriesGraphic {
    }

    private static class JFreeChartSeries implements SeriesGraphic {
    }

    private static class SeriesNode extends PNode implements SeriesGraphic {
        private XYSeries xySeries;

        private ControlGraph controlGraph;
        private PhetPPath pathNode;
        private PClip pathClip;

        public SeriesNode( String title, Color color, ControlGraph controlGraph, XYSeries xySeries ) {
            this.controlGraph = controlGraph;

            pathClip = new PClip();
            pathClip.setStrokePaint( null );//set to non-null for debugging clip area
//            pathClip.setStrokePaint( Color.blue );//set to non-null for debugging clip area
            addChild( pathClip );

            pathNode = new PhetPPath( new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f ), color );
            pathClip.addChild( pathNode );
        }

        public void addValue( double time, double value ) {
            xySeries.add( time, value );
            updateSeriesGraphic();
        }

        private void updateSeriesGraphic() {
            GeneralPath path = new GeneralPath();
            if( xySeries.getItemCount() > 0 ) {
                Point2D d = getNodePoint( 0 );
                path.moveTo( (float)d.getX(), (float)d.getY() );
                for( int i = 1; i < xySeries.getItemCount(); i++ ) {
                    Point2D nodePoint = getNodePoint( i );
                    path.lineTo( (float)nodePoint.getX(), (float)nodePoint.getY() );
                }
            }
            pathNode.setPathTo( path );
        }

        public Point2D.Double getPoint( int i ) {
            return new Point2D.Double( xySeries.getX( i ).doubleValue(), xySeries.getY( i ).doubleValue() );
        }

        public Point2D getNodePoint( int i ) {
//            return controlGraph.jFreeChartNode.plotToNode( getPoint( i ) );
            return controlGraph.jFreeChartNode.plotToNode( getPoint( i ) );
        }

        public void setClip( Rectangle2D clip ) {
            pathClip.setPathTo( clip );
        }

        public void clear() {
            xySeries.clear();
            updateSeriesGraphic();
        }
    }

    public JFreeChartNode getJFreeChartNode() {
        return jFreeChartNode;
    }

    protected void internalUpdateBounds( double x, double y, double width, double height ) {
        super.internalUpdateBounds( x, y, width, height );
        relayout();
    }

    public interface Layout {
        void layout();
    }

    public class FlowLayout implements Layout {
        public void layout() {
            double dx = 5;
            graphControlNode.setOffset( 0, 0 );
            chartSlider.setOffset( graphControlNode.getFullBounds().getMaxX() + dx, 0 );

//            jFreeChartNode.setBounds( chartSlider.getFullBounds().getMaxX(), 0, getBounds().getWidth() - zoomControl.getFullBounds().getWidth() - chartSlider.getFullBounds().getMaxX(), getBounds().getHeight() );
            //todo: putting everything in setBounds fails, for some reason setOffset as a separate operation succeeds
            jFreeChartNode.setBounds( 0, 0, getBounds().getWidth() - zoomControl.getFullBounds().getWidth() - chartSlider.getFullBounds().getMaxX(), getBounds().getHeight() );
            jFreeChartNode.setOffset( chartSlider.getFullBounds().getMaxX(), 0 );
            jFreeChartNode.updateChartRenderingInfo();
            zoomControl.setOffset( jFreeChartNode.getFullBounds().getMaxX(), jFreeChartNode.getFullBounds().getCenterY() - zoomControl.getFullBounds().getHeight() / 2 );
            Rectangle2D d = jFreeChartNode.plotToNode( getDataArea() );
            titleLayer.setOffset( d.getX() + jFreeChartNode.getOffset().getX(), d.getY() + jFreeChartNode.getOffset().getY() );

            for( int i = 0; i < seriesNodes.size(); i++ ) {
                SeriesNode seriesNode = (SeriesNode)seriesNodes.get( i );
                seriesNode.setClip( jFreeChartNode.getDataArea() );
                seriesNode.updateSeriesGraphic();
            }
        }
    }

    static interface LayoutFunction {
        double getValue( GraphComponent graphComponent );
    }

    public class AlignedLayout implements Layout {
        private GraphComponent[] graphComponents;

        public AlignedLayout( GraphComponent[] graphComponents ) {
            this.graphComponents = graphComponents;
        }

        public double[] getValues( LayoutFunction layoutFunction ) {
            ArrayList values = new ArrayList();
            for( int i = 0; i < graphComponents.length; i++ ) {
                if( !graphComponents[i].isMinimized() ) {
                    values.add( new Double( layoutFunction.getValue( graphComponents[i] ) ) );
                }
            }
            double[] val = new double[values.size()];
            for( int i = 0; i < val.length; i++ ) {
                val[i] = ( (Double)values.get( i ) ).doubleValue();
            }
            return val;
        }

        public void layout() {
            double dx = 5;
            graphControlNode.setOffset( 0, 0 );
            LayoutFunction controlNodeMaxX = new LayoutFunction() {
                public double getValue( GraphComponent graphComponent ) {
                    return graphComponent.getControlGraph().graphControlNode.getFullBounds().getWidth();
                }
            };
            if( getNumberMaximized() == 0 ) {
                return;
            }
            chartSlider.setOffset( max( getValues( controlNodeMaxX ) ) + dx, 0 );

            //compact the jfreechart node in the x direction by distance from optimal.

            LayoutFunction chartInset = new LayoutFunction() {
                public double getValue( GraphComponent graphComponent ) {
                    return getInsetX( graphComponent.getControlGraph().getJFreeChartNode() );
                }
            };
            double maxInset = max( getValues( chartInset ) );
//            System.out.println( "maxInset = " + maxInset );
            //todo: this layout code looks like it depends on layout getting called twice for each graph
            double diff = maxInset - getInsetX( getJFreeChartNode() );
            //todo: putting everything in setBounds fails, for some reason setOffset as a separate operation succeeds
            jFreeChartNode.setBounds( 0, 0, getBounds().getWidth() - zoomControl.getFullBounds().getWidth() - chartSlider.getFullBounds().getMaxX() - diff, getBounds().getHeight() );
            jFreeChartNode.setOffset( chartSlider.getFullBounds().getMaxX() + diff, 0 );
            jFreeChartNode.updateChartRenderingInfo();
            zoomControl.setOffset( jFreeChartNode.getFullBounds().getMaxX(), jFreeChartNode.getFullBounds().getCenterY() - zoomControl.getFullBounds().getHeight() / 2 );
            Rectangle2D d = jFreeChartNode.plotToNode( getDataArea() );
            titleLayer.setOffset( d.getX() + jFreeChartNode.getOffset().getX(), d.getY() + jFreeChartNode.getOffset().getY() );

            for( int i = 0; i < seriesNodes.size(); i++ ) {
                SeriesNode seriesNode = (SeriesNode)seriesNodes.get( i );
                seriesNode.setClip( jFreeChartNode.getDataArea() );
                seriesNode.updateSeriesGraphic();
            }
        }

        private int getNumberMaximized() {
            int count = 0;
            for( int i = 0; i < graphComponents.length; i++ ) {
                GraphComponent graphComponent = graphComponents[i];
                if( !graphComponent.isMinimized() ) {
                    count++;
                }
            }
            return count;
        }

        private double max( double[] values ) {
            double max = values[0];
            for( int i = 1; i < values.length; i++ ) {
                if( values[i] > max ) {
                    max = values[i];
                }
            }
            return max;
        }
    }

    private static double getInsetX( JFreeChartNode jFreeChartNode ) {
        Rectangle2D bounds = jFreeChartNode.getBounds();
        Rectangle2D dataBounds = jFreeChartNode.getDataArea();
        return dataBounds.getX() - bounds.getX();
    }

    public void setLayout( Layout layout ) {
        this.layout = layout;
        relayout();
    }

    void relayout() {
        layout.layout();
    }

    private Rectangle2D.Double getDataArea() {
        double xMin = jFreeChartNode.getChart().getXYPlot().getDomainAxis().getLowerBound();
        double xMax = jFreeChartNode.getChart().getXYPlot().getDomainAxis().getUpperBound();
        double yMin = jFreeChartNode.getChart().getXYPlot().getRangeAxis().getLowerBound();
        double yMax = jFreeChartNode.getChart().getXYPlot().getRangeAxis().getUpperBound();
        Rectangle2D.Double r = new Rectangle2D.Double();
        r.setFrameFromDiagonal( xMin, yMin, xMax, yMax );
        return r;
    }

    public void clear() {
        for( int i = 0; i < seriesNodes.size(); i++ ) {
            SeriesNode seriesNode = (SeriesNode)seriesNodes.get( i );
            seriesNode.clear();
        }
    }

    public void addValue( double time, double value ) {
        addValue( 0, time, value );
    }

    public void addValue( int series, double time, double value ) {
        getSeriesNode( series ).addValue( time, value );
    }

    private SeriesNode getSeriesNode( int series ) {
        return (SeriesNode)seriesNodes.get( series );
    }

    public void setEditable( boolean editable ) {
        chartSlider.setVisible( editable );
        chartSlider.setPickable( editable );
        chartSlider.setChildrenPickable( editable );

        graphControlNode.setEditable( editable );
    }

    public static interface Listener {
        void mousePressed();

        void valueChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.mousePressed();
        }
    }
}
