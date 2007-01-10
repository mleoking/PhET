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
    private GraphControlNode graphControlNode;
    private ChartSlider chartSlider;
    private ZoomSuiteNode zoomControl;

    private ArrayList seriesNodes = new ArrayList();
    private double xPad = 0;
    private JFreeChartNode jFreeChartNode;
    private PNode titleNode;
    private PNode seriesLayer;

    private ArrayList listeners = new ArrayList();
    private JFreeChart jFreeChart;
    private int minDomainValue = 1000;
    private double ZOOM_FRACTION = 1.1;

    public ControlGraph( PSwingCanvas pSwingCanvas, final SimulationVariable simulationVariable, String abbr, String title, double min, double max ) {
        this( pSwingCanvas, simulationVariable, abbr, title, min, max, Color.black, new PText( "THUMB" ) );
    }

    public ControlGraph( PSwingCanvas pSwingCanvas, final SimulationVariable simulationVariable, String abbr, String title, double min, final double max, Color color, PNode thumb ) {
        seriesLayer = new PNode();
        addSeries( "default_series", color );

        XYDataset dataset = new XYSeriesCollection( new XYSeries( "dummy series" ) );
        jFreeChart = ChartFactory.createXYLineChart( title + ", " + abbr, null, null, dataset, PlotOrientation.VERTICAL, false, false, false );
        jFreeChart.setTitle( (String)null );
        jFreeChart.getXYPlot().getRangeAxis().setRange( min, max );
        jFreeChart.getXYPlot().getDomainAxis().setRange( 0, minDomainValue );
        jFreeChart.setBackgroundPaint( null );

        jFreeChartNode = new JFreeChartNode( jFreeChart );
        jFreeChartNode.setBuffered( true );
        jFreeChartNode.setBounds( 0, 0, 300, 400 );
        graphControlNode = new GraphControlNode( pSwingCanvas, abbr, simulationVariable, new DefaultGraphTimeSeries(), color );
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

        titleNode = new PNode();
        ShadowPText titlePText = new ShadowPText( title + ", " + abbr );
        titlePText.setFont( new Font( "Lucida Sans", Font.BOLD, 14 ) );
        titlePText.setTextPaint( color );
        titleNode.addChild( new PhetPPath( RectangleUtils.expand( titlePText.getFullBounds(), 2, 2 ), Color.white, new BasicStroke(), Color.black ) );
        titleNode.addChild( titlePText );

        addChild( graphControlNode );
        addChild( chartSlider );
        addChild( jFreeChartNode );
        addChild( zoomControl );
        addChild( titleNode );
        addChild( seriesLayer );

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

    public void addSeries( String title, Color color ) {
        SeriesNode o = new SeriesNode( title + " " + "series_" + seriesNodes.size(), color, this );
        seriesNodes.add( o );
        seriesLayer.addChild( o );
    }

    public double getMaxDataX() {
        return jFreeChart.getXYPlot().getDomainAxis().getUpperBound();
    }

    public void setDomainUpperBound( double maxDataX ) {
        jFreeChart.getXYPlot().getDomainAxis().setUpperBound( maxDataX );
        updateZoomEnabled();
    }

    private static class SeriesNode extends PNode {
        XYSeries xySeries;
        PhetPPath pathNode;
        private ControlGraph controlGraph;
        private PClip pathClip;

        public SeriesNode( String title, Color color, ControlGraph controlGraph ) {
            this.controlGraph = controlGraph;
            xySeries = new XYSeries( title );
            pathNode = new PhetPPath( new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f ), color );
            pathClip = new PClip();
            pathClip.setStrokePaint( null );//set to non-null for debugging clip area
            addChild( pathClip );
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
            return controlGraph.jFreeChartNode.plotToNode( getPoint( i ) );
        }

        public void relayout() {
            pathClip.setPathTo( controlGraph.jFreeChartNode.getDataArea() );
            pathClip.setOffset( controlGraph.jFreeChartNode.getOffset() );
        }

        public void clear() {
            xySeries.clear();
            updateSeriesGraphic();
        }
    }

    public JFreeChartNode getJFreeChartNode() {
        return jFreeChartNode;
    }

    public boolean setBounds( double x, double y, double width, double height ) {
        relayout();
        jFreeChartNode.setBounds( 0, 0, width - xPad, height );
        relayout();
        setOffset( x, y );
        return super.setBounds( x, y, width, height );
    }

    private void relayout() {
        double dx = 5;
        graphControlNode.setOffset( 0, 0 );
        chartSlider.setOffset( graphControlNode.getFullBounds().getMaxX() + dx, 0 );
        jFreeChartNode.setOffset( chartSlider.getFullBounds().getMaxX(), 0 );

        zoomControl.setOffset( jFreeChartNode.getFullBounds().getMaxX(), jFreeChartNode.getFullBounds().getCenterY() - zoomControl.getFullBounds().getHeight() / 2 );
        Rectangle2D.Double r = getDataArea();
        Rectangle2D d = jFreeChartNode.plotToNode( r );
        titleNode.setOffset( d.getX() + jFreeChartNode.getOffset().getX(), d.getY() + jFreeChartNode.getOffset().getY() );

        this.xPad = jFreeChartNode.getFullBounds().getX() + zoomControl.getFullBounds().getWidth();

        jFreeChartNode.updateChartRenderingInfo();
        Rectangle2D dataArea = jFreeChartNode.getDataArea();
        jFreeChartNode.localToGlobal( dataArea );
        globalToLocal( dataArea );

        for( int i = 0; i < seriesNodes.size(); i++ ) {
            SeriesNode seriesNode = (SeriesNode)seriesNodes.get( i );
            seriesNode.relayout();
        }
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
