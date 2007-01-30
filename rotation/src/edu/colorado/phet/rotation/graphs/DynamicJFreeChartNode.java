package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PClip;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * This class extends the functionality of JFreeChartNode by providing different strategies for rendering the data.
 * It is assumed that the chart's plot is XYPlot, and some functionality is lost in rendering, since we
 * have our own rendering strategies here.
 * <p/>
 * Data is added to the chart through addValue() methods, not through the underlying XYSeriesCollection dataset.
 * <p/>
 * The 3 rendering styles are:
 * 1. JFreeChart renderer: This uses the JFreeChart subsystem to do all the data rendering.
 * This looks beautiful and comes built-in to jfreechart but has performance problems during dynamic data display, since the entire region
 * is repainted whenever a single point changes.
 * <p/>
 * 2. Piccolo renderer: This uses a PPath to render the path as a PNode child of the JFreeChartNode
 * This looks fine and reduces the clip region necessary for painting.  When combined with a buffered jfreechartnode, this can improve computation substantially.
 * This renderer is combined with a PClip to ensure no data is drawn outside the chart's data area (which could otherwise change the fullbounds of the jfreechartnode..
 * <p/>
 * 3. Buffered renderer: This draws directly to the buffer in the JFreeChartNode, and only repaints the changed region of the screen.
 */
public class DynamicJFreeChartNode extends JFreeChartNode {
    private ArrayList seriesDataList = new ArrayList();
    private ArrayList seriesViewList = new ArrayList();
    private ArrayList listeners = new ArrayList();
    private PhetPCanvas phetPCanvas;
    private PhetPPath debugBufferRegion;

    private SeriesViewFactory jfreeChartSeriesFactory = new SeriesViewFactory() {
        public SeriesView createSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            return new JFreeChartSeriesView( dynamicJFreeChartNode, seriesData );
        }
    };
    private SeriesViewFactory piccoloSeriesFactory = new SeriesViewFactory() {
        public SeriesView createSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            return new PiccoloSeriesView( dynamicJFreeChartNode, seriesData );
        }
    };
    private SeriesViewFactory bufferedSeriesFactory = new SeriesViewFactory() {
        public SeriesView createSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            return new BufferedSeriesView( dynamicJFreeChartNode, seriesData );
        }
    };
    private SeriesViewFactory viewFactory = jfreeChartSeriesFactory;

    public DynamicJFreeChartNode( PhetPCanvas phetPCanvas, JFreeChart chart ) {
        super( chart );
        this.phetPCanvas = phetPCanvas;
        debugBufferRegion = new PhetPPath( new BasicStroke( 1.0f ), Color.green );
//        addChild( debugBufferRegion );//this can destroy the bounds of the graph
    }

    protected void addPNodeUpdateHandler() {
        super.addPNodeUpdateHandler();
        //avoid adding a PNode property change listener, since some unidentified events from BufferedView are causing buffer clears.
    }

    public void addValue( double x, double y ) {
        addValue( 0, x, y );
    }

    public void addValue( int series, double x, double y ) {
        getSeries( series ).addValue( x, y );
    }

    private SeriesData getSeries( int series ) {
        return (SeriesData)seriesDataList.get( series );
    }

    public void addSeries( String title, Color color ) {
        SeriesData seriesData = new SeriesData( title, color );
        seriesDataList.add( seriesData );
        updateSeriesViews();
    }

    public void clear() {
        for( int i = 0; i < seriesDataList.size(); i++ ) {
            SeriesData seriesData = (SeriesData)seriesDataList.get( i );
            seriesData.clear();
        }
        clearBuffer();
    }

    public static interface Listener {
        void changed();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.changed();
        }
    }

//    private void repaintPanel( Rectangle2D bounds ) {
//        
//    }

    static interface SeriesViewFactory {
        SeriesView createSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData );
    }

    public void setJFreeChartSeries() {
        setViewFactory( jfreeChartSeriesFactory );
    }

    public void setPiccoloSeries() {
        setViewFactory( piccoloSeriesFactory );
    }

    public void setBufferedSeries() {
        setViewFactory( bufferedSeriesFactory );
    }

    private void setViewFactory( SeriesViewFactory factory ) {
        viewFactory = factory;
        updateSeriesViews();
    }

    private void updateSeriesViews() {
        removeAllSeriesViews();
        clearBuffer();
        addAllSeriesViews();
        updateChartRenderingInfo();
    }

    private void addAllSeriesViews() {
        for( int i = 0; i < seriesDataList.size(); i++ ) {
            SeriesData seriesData = (SeriesData)seriesDataList.get( i );
            SeriesView seriesDataView = viewFactory.createSeriesView( this, seriesData );
            seriesDataView.install();
            seriesViewList.add( seriesDataView );
        }
    }

    private void removeAllSeriesViews() {
        while( seriesViewList.size() > 0 ) {
            SeriesView seriesView = (SeriesView)seriesViewList.get( 0 );
            seriesView.uninstall();
            seriesViewList.remove( seriesView );
        }
    }

    static abstract class SeriesView {
        DynamicJFreeChartNode dynamicJFreeChartNode;
        SeriesData seriesData;
        private SeriesData.Listener listener = new SeriesData.Listener() {
            public void dataAdded() {
                SeriesView.this.dataAdded();
            }
        };

        public SeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            this.dynamicJFreeChartNode = dynamicJFreeChartNode;
            this.seriesData = seriesData;
        }

        public abstract void dataAdded();

        public void uninstall() {
            seriesData.removeListener( listener );
        }

        public void install() {
            seriesData.addListener( listener );
        }

        public DynamicJFreeChartNode getDynamicJFreeChartNode() {
            return dynamicJFreeChartNode;
        }

        public XYSeries getSeries() {
            return seriesData.getSeries();
        }

        public SeriesData getSeriesData() {
            return seriesData;
        }
    }

    static class JFreeChartSeriesView extends SeriesView {

        public JFreeChartSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            super( dynamicJFreeChartNode, seriesData );
        }

        public void dataAdded() {
            //painting happens automatically due to changes in the JFreeChart
        }

        public void uninstall() {
            super.uninstall();
            XYSeriesCollection xySeriesCollection = (XYSeriesCollection)dynamicJFreeChartNode.getChart().getXYPlot().getDataset();
            xySeriesCollection.removeSeries( seriesData.getSeries() );
        }

        public void install() {
            super.install();
            XYSeriesCollection xySeriesCollection = (XYSeriesCollection)dynamicJFreeChartNode.getChart().getXYPlot().getDataset();
            xySeriesCollection.addSeries( seriesData.getSeries() );
        }
    }

    static class PiccoloSeriesView extends SeriesView {

        private PNode root = new PNode();
        private PhetPPath pathNode;
        private PClip pathClip;
        private DynamicJFreeChartNode.Listener listener = new Listener() {
            public void changed() {
                updateClip();
            }
        };

        public PiccoloSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            super( dynamicJFreeChartNode, seriesData );

            pathClip = new PClip();
            pathClip.setStrokePaint( null );//set to non-null for debugging clip area
//            pathClip.setStrokePaint( Color.blue );//set to non-null for debugging clip area
            root.addChild( pathClip );

            pathNode = new PhetPPath( new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f ), seriesData.getColor() );
            pathClip.addChild( pathNode );

            updateClip();
        }

        private void updateClip() {
            pathClip.setPathTo( dynamicJFreeChartNode.getDataArea() );
        }

        public void updateSeriesGraphic() {
            GeneralPath path = new GeneralPath();
            if( super.getSeries().getItemCount() > 0 ) {
                Point2D d = getNodePoint( 0 );
                path.moveTo( (float)d.getX(), (float)d.getY() );
                for( int i = 1; i < getSeries().getItemCount(); i++ ) {
                    Point2D nodePoint = getNodePoint( i );
                    path.lineTo( (float)nodePoint.getX(), (float)nodePoint.getY() );
                }
            }
            pathNode.setPathTo( path );
            if( dynamicJFreeChartNode.isBuffered() ) {
//                pathClip.setOffset( dynamicJFreeChartNode.getBounds().getX(), dynamicJFreeChartNode.getBounds().getY() );
                pathNode.setOffset( dynamicJFreeChartNode.getBounds().getX(), dynamicJFreeChartNode.getBounds().getY() );
            }
            else {
//                pathClip.setOffset( 0, 0 );
                pathNode.setOffset( 0, 0 );
            }
        }

        public Point2D.Double getPoint( int i ) {
            return new Point2D.Double( getSeries().getX( i ).doubleValue(), getSeries().getY( i ).doubleValue() );
        }

        public Point2D getNodePoint( int i ) {
            return getDynamicJFreeChartNode().plotToNode( getPoint( i ) );
        }

        public void setClip( Rectangle2D clip ) {
            pathClip.setPathTo( clip );
        }

        public void uninstall() {
            super.uninstall();
            super.getDynamicJFreeChartNode().removeChild( root );
            dynamicJFreeChartNode.removeListener( listener );
        }

        public void install() {
            super.install();
            getDynamicJFreeChartNode().addChild( root );
            dynamicJFreeChartNode.addListener( listener );
            updateClip();
            updateSeriesGraphic();
        }

        public void dataAdded() {
            updateSeriesGraphic();
        }

    }

    private void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    //todo:  move this to parent class
    public void updateChartRenderingInfo() {
        super.updateChartRenderingInfo();
        if( listeners != null ) {
            notifyListeners();
        }
    }

    static class BufferedSeriesView extends SeriesView {
        private BufferedImage lastFullPaint = null;
        private boolean origStateBuffered;

        public BufferedSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            super( dynamicJFreeChartNode, seriesData );
        }

        public void dataAdded() {
            if( getSeries().getItemCount() >= 2 ) {
                BufferedImage image = dynamicJFreeChartNode.getBuffer();
                if( image != null ) {
                    if( image != lastFullPaint ) {
                        paintAll();
                        lastFullPaint = image;
                    }
                    Graphics2D graphics2D = image.createGraphics();
                    graphics2D.setPaint( getSeriesData().getColor() );
                    BasicStroke stroke = new BasicStroke( 2.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f );
                    graphics2D.setStroke( stroke );
                    int itemCount = getSeries().getItemCount();
                    Line2D.Double viewLine = new Line2D.Double( getViewPoint( itemCount - 2 ), getViewPoint( itemCount - 1 ) );
                    graphics2D.draw( viewLine );

                    Shape sh = stroke.createStrokedShape( viewLine );
                    Rectangle2D bounds = sh.getBounds2D();
                    if( dynamicJFreeChartNode.isBuffered() ) {
                        bounds = new Rectangle2D.Double( bounds.getX() + dynamicJFreeChartNode.getBounds().getX(), bounds.getY() + dynamicJFreeChartNode.getBounds().getY(), bounds.getWidth(), bounds.getHeight() );
                    }
                    dynamicJFreeChartNode.localToGlobal( bounds );
                    dynamicJFreeChartNode.phetPCanvas.getPhetRootNode().globalToScreen( bounds );
                    repaintPanel( bounds );
                }
            }
        }

        private Point2D getViewPoint( int index ) {
            return dynamicJFreeChartNode.plotToNode( getPoint( index ) );
        }

        private Point2D.Double getPoint( int index ) {
            return new Point2D.Double( getSeries().getX( index ).doubleValue(), getSeries().getY( index ).doubleValue() );
        }

        public void uninstall() {
            super.uninstall();
            if( dynamicJFreeChartNode.isBuffered() != origStateBuffered ) {
                dynamicJFreeChartNode.setBuffered( origStateBuffered );
            }
        }

        public void install() {
            super.install();
            paintAll();
            this.origStateBuffered = dynamicJFreeChartNode.isBuffered();
            if( !origStateBuffered ) {
                dynamicJFreeChartNode.setBuffered( true );
            }
        }

        private void paintAll() {
            BufferedImage image = dynamicJFreeChartNode.getBuffer();
            if( image != null ) {
                Graphics2D graphics2D = image.createGraphics();
                graphics2D.setPaint( getSeriesData().getColor() );
                BasicStroke stroke = new BasicStroke( 2.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f );
                graphics2D.setStroke( stroke );
                GeneralPath path = toGeneralPath();
                graphics2D.draw( path );
                repaintPanel( new Rectangle2D.Double( 0, 0, dynamicJFreeChartNode.phetPCanvas.getWidth(), dynamicJFreeChartNode.phetPCanvas.getHeight() ) );
            }
        }

        protected void repaintPanel( Rectangle2D bounds ) {
            dynamicJFreeChartNode.phetPCanvas.repaint( new PBounds( bounds ) );
            dynamicJFreeChartNode.debugBufferRegion.setPathTo( bounds );
        }

        private GeneralPath toGeneralPath() {
            GeneralPath path = new GeneralPath();
            if( getSeries().getItemCount() > 0 ) {
                path.moveTo( (float)getViewPoint( 0 ).getX(), (float)getViewPoint( 0 ).getY() );
            }
            for( int i = 1; i < getSeries().getItemCount(); i++ ) {
                path.lineTo( (float)getViewPoint( i ).getX(), (float)getViewPoint( i ).getY() );
            }
            return path;
        }
    }

    public static class SeriesData {
        private String title;
        private Color color;
        private XYSeries series;
        private ArrayList listeners = new ArrayList();
        private static int index = 0;

        public SeriesData( String title, Color color ) {
            this( title, color, new XYSeries( title + " " + ( index++ ) ) );
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

        public void addValue( double time, double value ) {
            series.add( time, value );
            notifyDataChanged();
        }

        public void removeListener( Listener listener ) {
            listeners.remove( listener );
        }

        public void clear() {
            series.clear();
            notifyDataChanged();
        }

        public static interface Listener {
            void dataAdded();
        }

        public void addListener( Listener listener ) {
            listeners.add( listener );
        }

        public void notifyDataChanged() {
            for( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener)listeners.get( i );
                listener.dataAdded();
            }
        }
    }

    //Todo: provide support for this in the parent class.
    public void clearBuffer() {
        super.chartChanged( null );
    }

    //todo: move this to parent class?
    protected void internalUpdateBounds( double x, double y, double width, double height ) {
        super.internalUpdateBounds( x, y, width, height );
        updateChartRenderingInfo();
    }

    public void setBuffered( boolean buffered ) {
        super.setBuffered( buffered );
        updateChartRenderingInfo();
        updateSeriesViews();
        notifyListeners();
    }

}
