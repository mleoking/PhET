package edu.colorado.phet.common.motion.graphs;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.common.jfreechartphet.piccolo.dynamic.BufferedSeriesView;
import edu.colorado.phet.common.jfreechartphet.piccolo.dynamic.DynamicJFreeChartNode;
import edu.colorado.phet.common.motion.model.ISimulationVariable;
import edu.colorado.phet.common.motion.model.ITimeSeries;
import edu.colorado.phet.common.motion.model.TimeData;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.colorado.phet.common.piccolophet.nodes.ZoomControlNode;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * The ControlGraph displays a graph of data for (multiple) time series data,
 * including horziontal and vertical zoom in/out and (optionally) a slider control for changing the data.
 *
 * @author Sam Reid
 */
public class ControlGraph extends PNode {
    private JFreeChart jFreeChart;
    private DynamicJFreeChartNode dynamicJFreeChartNode;

    private GraphTimeControlNode graphTimeControlNode;
    private JFreeChartSliderNode jFreeChartSliderNode;
    private ZoomSuiteNode zoomControl;
    private TitleLayer titleLayer;

    private double maxDomainValue;
    private double ZOOM_FRACTION = 1.1;
    private Layout layout = new FlowLayout();
    private ArrayList series = new ArrayList();
    private ArrayList listeners = new ArrayList();
    private PNode additionalControls;
    private ISimulationVariable simulationVariable;

    public ControlGraph( PhetPCanvas pSwingCanvas, final ISimulationVariable simulationVariable,
                         String title, double minY, double maxY, TimeSeriesModel timeSeriesModel ) {
        this( pSwingCanvas, simulationVariable, title, minY, maxY, new PText( "THUMB" ), timeSeriesModel );
    }

    public ControlGraph( PhetPCanvas pSwingCanvas, final ISimulationVariable simulationVariable,
                         String title, double minY, final double maxY, PNode thumb, TimeSeriesModel timeSeriesModel ) {
        this( pSwingCanvas, simulationVariable, title, minY, maxY, thumb, timeSeriesModel, 1000 );
    }

    public ControlGraph( PhetPCanvas pSwingCanvas, final ISimulationVariable simulationVariable,
                         String title, double minY, final double maxY, PNode thumb,
                         TimeSeriesModel timeSeriesModel, double maxDomainTime ) {
        this.simulationVariable = simulationVariable;
        this.maxDomainValue = maxDomainTime;
        titleLayer = createTitleLayer();
        XYDataset dataset = new XYSeriesCollection( new XYSeries( "dummy series" ) );
        jFreeChart = ChartFactory.createXYLineChart( title, null, null, dataset, PlotOrientation.VERTICAL, false, false, false );
        jFreeChart.setTitle( (String)null );
        jFreeChart.getXYPlot().getRangeAxis().setRange( minY, maxY );
        jFreeChart.getXYPlot().getDomainAxis().setRange( 0, maxDomainValue );
        jFreeChart.setBackgroundPaint( null );

        dynamicJFreeChartNode = new DynamicJFreeChartNode( pSwingCanvas, jFreeChart );
        dynamicJFreeChartNode.setBuffered( true );
        dynamicJFreeChartNode.setBounds( 0, 0, 300, 400 );
//        dynamicJFreeChartNode.setPiccoloSeries();
//        dynamicJFreeChartNode.setJFreeChartSeries();
//        dynamicJFreeChartNode.setBufferedSeries();
        dynamicJFreeChartNode.setBufferedImmediateSeries();

        graphTimeControlNode = new GraphTimeControlNode( timeSeriesModel );
        additionalControls = new PNode();

//        addSeries( title, color, abbr, simulationVariable, observableTimeSeries, stroke );
        jFreeChartSliderNode = new JFreeChartSliderNode( dynamicJFreeChartNode, thumb );
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

        addChild( graphTimeControlNode );
        addChild( additionalControls );
        addChild( jFreeChartSliderNode );
        addChild( dynamicJFreeChartNode );
        addChild( zoomControl );
        addChild( titleLayer );

        simulationVariable.addListener( new ISimulationVariable.Listener() {
            public void valueChanged() {
                jFreeChartSliderNode.setValue( simulationVariable.getData().getValue() );
            }
        } );
        jFreeChartSliderNode.addListener( new JFreeChartSliderNode.Listener() {
            public void valueChanged() {
                handleValueChanged();
            }

            public void sliderThumbGrabbed() {
                notifyControlGrabbed();
            }

        } );

        dynamicJFreeChartNode.updateChartRenderingInfo();
        relayout();
        updateZoomEnabled();

        //for debugging, attach listeners that allow change of rendering style.
        addInputEventListener( new PBasicInputEventHandler() {
            public void keyPressed( PInputEvent event ) {
                if( event.getKeyCode() == KeyEvent.VK_1 ) {
                    dynamicJFreeChartNode.setJFreeChartSeries();
                }
                else if( event.getKeyCode() == KeyEvent.VK_2 ) {
                    dynamicJFreeChartNode.setPiccoloSeries();
                }
                else if( event.getKeyCode() == KeyEvent.VK_3 ) {
                    dynamicJFreeChartNode.setBufferedSeries();
                }
                else if( event.getKeyCode() == KeyEvent.VK_4 ) {
                    dynamicJFreeChartNode.setBufferedImmediateSeries();
                }
            }
        } );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                event.getInputManager().setKeyboardFocus( event.getPath() );
            }
        } );
    }

    protected TitleLayer createTitleLayer() {
        return new TitleLayer();
    }

    protected void handleValueChanged() {
        simulationVariable.setValue( getSliderValue() );
    }

    protected double getSliderValue() {
        return jFreeChartSliderNode.getValue();
    }

    public DynamicJFreeChartNode getDynamicJFreeChartNode() {
        return dynamicJFreeChartNode;
    }

    private void notifyControlGrabbed() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.controlFocusGrabbed();
        }
    }

    public void addHorizontalZoomListener( ZoomControlNode.ZoomListener zoomListener ) {
        zoomControl.addHorizontalZoomListener( zoomListener );
    }

    //Todo: current implementation only supports one additional control
    public void addControl( JComponent component ) {
        additionalControls.addChild( new PSwing( component ) );
    }

    static class TitleNode extends PNode {
        public TitleNode( String title, String abbr, Color color ) {
//            ShadowPText titlePText = new ShadowPText( title + ", " + abbr );
            ShadowPText titlePText = new ShadowPText( title );
            titlePText.setFont( new Font( "Lucida Sans", Font.BOLD, 14 ) );
            titlePText.setTextPaint( color );
            addChild( new PhetPPath( RectangleUtils.expand( titlePText.getFullBounds(), 2, 2 ), Color.white, new BasicStroke(), Color.black ) );
            addChild( titlePText );
        }
    }

    public static class ReadoutTitleNode extends PNode {
        private ShadowPText titlePText;
        private ControlGraphSeries series;
        private String title;
        private String abbr;
        private ISimulationVariable simulationVariable;
        private String units;
        private DecimalFormat decimalFormat = new DefaultDecimalFormat( "0.00" );
        private PhetPPath background;

        //todo: remove all params except series
        public ReadoutTitleNode( ControlGraphSeries series, String title, String abbr, Color color, ISimulationVariable simulationVariable, String units ) {
            this.series = series;
            this.title = title;
            this.abbr = abbr;
            this.simulationVariable = simulationVariable;
            this.units = units;
            titlePText = new ShadowPText();
            titlePText.setFont( new Font( "Lucida Sans", Font.BOLD, 14 ) );
            titlePText.setTextPaint( color );
            background = new PhetPPath( null, Color.white, new BasicStroke(), Color.black );
            addChild( background );
            addChild( titlePText );
            simulationVariable.addListener( new ISimulationVariable.Listener() {
                public void valueChanged() {
                    updateText();
                }
            } );
            updateText();
        }

        public ControlGraphSeries getSeries() {
            return series;
        }

        private void updateText() {
//            titlePText.setText( title + ", " + abbr + " = " + decimalFormat.format( simulationVariable.getValue() ) );
//            titlePText.setText( abbr + " = " + decimalFormat.format( simulationVariable.getValue() ) );
            String valueText = decimalFormat.format( simulationVariable.getValue() );
            while( valueText.length() < "-10.00".length() ) {
                valueText = " " + valueText;
            }
            titlePText.setText( title + " " + valueText + " " + units );
            background.setPathTo( RectangleUtils.expand( titlePText.getFullBounds(), 2, 2 ) );//todo: avoid setting identical shapes here for performance considerations
        }
    }

    private void zoomHorizontal( double v ) {
        double currentValue = jFreeChart.getXYPlot().getDomainAxis().getUpperBound();
        double newValue = currentValue * v;
        if( newValue > maxDomainValue ) {
            newValue = maxDomainValue;
        }
        setDomainUpperBound( newValue );
    }

    private void notifyZoomChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.zoomChanged();
        }
    }

    private void zoomVertical( double v ) {
        double currentRange = jFreeChart.getXYPlot().getRangeAxis().getUpperBound() - jFreeChart.getXYPlot().getRangeAxis().getLowerBound();
        double newRange = currentRange * v;
        double diff = newRange - currentRange;
        jFreeChart.getXYPlot().getRangeAxis().setRange( jFreeChart.getXYPlot().getRangeAxis().getLowerBound() - diff / 2, jFreeChart.getXYPlot().getRangeAxis().getUpperBound() + diff / 2 );
        updateZoomEnabled();
        notifyZoomChanged();
    }

    private void updateZoomEnabled() {
        zoomControl.setHorizontalZoomOutEnabled( jFreeChart.getXYPlot().getDomainAxis().getUpperBound() != maxDomainValue );
    }

    public void addSeries( String title, Color color, String abbr, String units, ISimulationVariable simulationVariable, ITimeSeries observableTimeSeries ) {
        addSeries( title, color, abbr, units, simulationVariable, observableTimeSeries, BufferedSeriesView.DEFAULT_STROKE );
    }

    public void addSeries( String title, Color color, String abbr, String units, ISimulationVariable simulationVariable, ITimeSeries observableTimeSeries, Stroke stroke ) {
        addSeries( new ControlGraphSeries( title, color, abbr, units, simulationVariable, observableTimeSeries, stroke ) );
    }

    public ControlGraphSeries getControlGraphSeries( int i ) {
        return (ControlGraphSeries)series.get( i );
    }

    public int getSeriesCount() {
        return this.series.size();
    }

    public static class TitleLayer extends PhetPNode {
        public TitleLayer() {
        }

        public void addReadoutNode( ReadoutTitleNode titleNode ) {
            titleNode.setOffset( getFullBounds().getWidth(), 0 );
            addChild( titleNode );
        }

        public ReadoutTitleNode getReadoutNode( ControlGraphSeries series ) {
            for( int i = 0; i < getChildrenCount(); i++ ) {
                if( getChild( i ) instanceof ReadoutTitleNode ) {
                    ReadoutTitleNode readoutTitleNode = (ReadoutTitleNode)getChild( i );
                    if( readoutTitleNode.getSeries() == series ) {
                        return readoutTitleNode;
                    }
                }
            }
            return null;
        }
    }

    public void addSeries( final ControlGraphSeries series ) {
        this.series.add( series );
        dynamicJFreeChartNode.addSeries( series.getTitle(), series.getColor(), series.getStroke() );

        final ReadoutTitleNode titleNode = new ReadoutTitleNode( series, series.getTitle(), series.getAbbr(), series.getColor(), series.getSimulationVariable(), series.getUnits() );
        titleLayer.addReadoutNode( titleNode );

        GraphTimeControlNode.SeriesNode seriesNode = null;
        if( series.isEditable() ) {
            seriesNode = graphTimeControlNode.addVariable( series.getTitle(), series.getAbbr(), series.getColor(), series.getSimulationVariable() );
        }
        series.getObservableTimeSeries().addListener( new ITimeSeries.Listener() {
            public void dataAdded( TimeData timeData ) {
                addValue( getSeriesIndex( series.getSimulationVariable() ), timeData.getTime(), timeData.getValue() );
            }

            public void dataCleared() {
                clear();
            }
        } );

        final GraphTimeControlNode.SeriesNode seriesNodeTemp = seriesNode;
        series.addListener( new ControlGraphSeries.Listener() {
            public void visibilityChanged() {
                dynamicJFreeChartNode.setSeriesVisible( series.getTitle(), series.isVisible() );
                titleNode.setVisible( series.isVisible() );
                if( seriesNodeTemp != null ) {
                    seriesNodeTemp.setVisible( series.isVisible() );
                }
                getDynamicJFreeChartNode().forceUpdateAll();
            }
        } );
    }

    public int getSeriesIndex( ISimulationVariable simulationVariable ) {
        for( int i = 0; i < getSeriesCount(); i++ ) {
            if( getControlGraphSeries( i ).getSimulationVariable() == simulationVariable ) {
                return i;
            }
        }
        return -1;
    }

    public double getMaxDataX() {
        return jFreeChart.getXYPlot().getDomainAxis().getUpperBound();
    }

    public void setDomainUpperBound( double maxDataX ) {
        if( jFreeChart.getXYPlot().getDomainAxis().getUpperBound() != maxDataX ) {
            jFreeChart.getXYPlot().getDomainAxis().setUpperBound( maxDataX );
            updateZoomEnabled();
            notifyZoomChanged();
        }
    }

    public void setFlowLayout() {
        setLayout( new FlowLayout() );
    }

    public void setAlignedLayout( MinimizableControlGraph[] minimizableControlGraphs ) {
        setLayout( new AlignedLayout( minimizableControlGraphs ) );
    }

    public JFreeChartNode getJFreeChartNode() {
        return dynamicJFreeChartNode;
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
            graphTimeControlNode.setOffset( 0, 0 );
            additionalControls.setOffset( 0, graphTimeControlNode.getFullBounds().getMaxY() );
            jFreeChartSliderNode.setOffset( graphTimeControlNode.getFullBounds().getMaxX() + dx, 0 );

            //putting everything in setBounds fails, for some reason setOffset as a separate operation succeeds
            dynamicJFreeChartNode.setBounds( 0, 0, getBounds().getWidth() - zoomControl.getFullBounds().getWidth() - jFreeChartSliderNode.getFullBounds().getMaxX(), getBounds().getHeight() );
            dynamicJFreeChartNode.setOffset( jFreeChartSliderNode.getFullBounds().getMaxX(), 0 );
            dynamicJFreeChartNode.updateChartRenderingInfo();
            zoomControl.setOffset( dynamicJFreeChartNode.getFullBounds().getMaxX(), dynamicJFreeChartNode.getFullBounds().getCenterY() - zoomControl.getFullBounds().getHeight() / 2 );
            Rectangle2D d = dynamicJFreeChartNode.plotToNode( getDataArea() );
            titleLayer.setOffset( d.getX() + dynamicJFreeChartNode.getOffset().getX(), d.getY() + dynamicJFreeChartNode.getOffset().getY() );

            setOffset( getBounds().getX(), getBounds().getY() );
        }
    }

    static interface LayoutFunction {
        double getValue( MinimizableControlGraph minimizableControlGraph );
    }

    public class AlignedLayout implements Layout {
        private MinimizableControlGraph[] minimizableControlGraphs;

        public AlignedLayout( MinimizableControlGraph[] minimizableControlGraphs ) {
            this.minimizableControlGraphs = minimizableControlGraphs;
        }

        public double[] getValues( LayoutFunction layoutFunction ) {
            ArrayList values = new ArrayList();
            for( int i = 0; i < minimizableControlGraphs.length; i++ ) {
                if( !minimizableControlGraphs[i].isMinimized() ) {
                    values.add( new Double( layoutFunction.getValue( minimizableControlGraphs[i] ) ) );
                }
            }
            double[] val = new double[values.size()];
            for( int i = 0; i < val.length; i++ ) {
                val[i] = ( (Double)values.get( i ) ).doubleValue();
            }
            return val;
        }

        public void layout() {
//            System.out.println( "ControlGraph$AlignedLayout.layout: " + ControlGraph.this );
            double dx = 5;
            graphTimeControlNode.setOffset( 0, 0 );
            additionalControls.setOffset( 0, graphTimeControlNode.getFullBounds().getMaxY() );
            LayoutFunction controlNodeMaxX = new LayoutFunction() {
                public double getValue( MinimizableControlGraph minimizableControlGraph ) {
                    return minimizableControlGraph.getControlGraph().graphTimeControlNode.getFullBounds().getWidth();
                }
            };
            if( getNumberMaximized() == 0 ) {
                return;
            }
            jFreeChartSliderNode.setOffset( max( getValues( controlNodeMaxX ) ) + dx, 0 );

            //compact the jfreechart node in the x direction by distance from optimal.

            LayoutFunction chartInset = new LayoutFunction() {
                public double getValue( MinimizableControlGraph minimizableControlGraph ) {
                    return getInsetX( minimizableControlGraph.getControlGraph().getJFreeChartNode() );
                }
            };
            double maxInset = max( getValues( chartInset ) );
//            System.out.println( "maxInset = " + maxInset );
            //todo: this layout code looks like it depends on layout getting called twice for each graph
            double diff = maxInset - getInsetX( getJFreeChartNode() );

            //putting everything in setBounds fails, for some reason setOffset as a separate operation succeeds
            dynamicJFreeChartNode.setBounds( 0, 0, getBounds().getWidth() - zoomControl.getFullBounds().getWidth() - jFreeChartSliderNode.getFullBounds().getMaxX() - diff, getBounds().getHeight() );
            dynamicJFreeChartNode.setOffset( jFreeChartSliderNode.getFullBounds().getMaxX() + diff, 0 );
            dynamicJFreeChartNode.updateChartRenderingInfo();
            zoomControl.setOffset( dynamicJFreeChartNode.getFullBounds().getMaxX(), dynamicJFreeChartNode.getFullBounds().getCenterY() - zoomControl.getFullBounds().getHeight() / 2 );
            Rectangle2D d = dynamicJFreeChartNode.plotToNode( getDataArea() );
            titleLayer.setOffset( d.getX() + dynamicJFreeChartNode.getOffset().getX(), d.getY() + dynamicJFreeChartNode.getOffset().getY() );
        }

        private int getNumberMaximized() {
            int count = 0;
            for( int i = 0; i < minimizableControlGraphs.length; i++ ) {
                MinimizableControlGraph minimizableControlGraph = minimizableControlGraphs[i];
                if( !minimizableControlGraph.isMinimized() ) {
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

    public void relayout() {
        layout.layout();
    }

    private Rectangle2D.Double getDataArea() {
        double xMin = dynamicJFreeChartNode.getChart().getXYPlot().getDomainAxis().getLowerBound();
        double xMax = dynamicJFreeChartNode.getChart().getXYPlot().getDomainAxis().getUpperBound();
        double yMin = dynamicJFreeChartNode.getChart().getXYPlot().getRangeAxis().getLowerBound();
        double yMax = dynamicJFreeChartNode.getChart().getXYPlot().getRangeAxis().getUpperBound();
        Rectangle2D.Double r = new Rectangle2D.Double();
        r.setFrameFromDiagonal( xMin, yMin, xMax, yMax );
        return r;
    }

    public void clear() {
        dynamicJFreeChartNode.clear();
    }

    public void addValue( double time, double value ) {
        addValue( 0, time, value );
    }

    public void addValue( int series, double time, double value ) {
        dynamicJFreeChartNode.addValue( series, time, value );
//        System.out.println( "series = " + series + " time=" + time + ", value=" + value );
    }

    public void setEditable( boolean editable ) {
        jFreeChartSliderNode.setVisible( editable );
        jFreeChartSliderNode.setPickable( editable );
        jFreeChartSliderNode.setChildrenPickable( editable );

        graphTimeControlNode.setEditable( editable );
    }

    public static interface Listener {

        //This method is called when the user makes an input event that indicates
        //that this component should be "in control" of the simulation
        void controlFocusGrabbed();

        void zoomChanged();
    }

    public static class Adapter implements Listener {
        public void controlFocusGrabbed() {
        }

        public void zoomChanged() {
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

}
