// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.jfreechartphet.piccolo;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.event.SwingPropertyChangeSupport;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeEventType;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.CombinedRangeXYPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;


/**
 * JFreeChartNode is a Piccolo node for displaying a JFreeChart.
 * <p/>
 * The bounds of the node determine the size of the chart.
 * The node registers with the chart to receive notification
 * of changes to any component of the chart.  The chart is
 * redrawn automatically whenever this notification is received.
 * <p/>
 * The node can be buffered or unbuffered. If buffered, the
 * chart is drawn using an off-screen image that is updated
 * whenever the chart or node changes.  If unbuffered, the
 * chart is drawn directly to the screen whenever paint is
 * called; this can be unnecessarily costly if the paint request
 * is not the result of changes to the chart or node.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class JFreeChartNode extends PNode implements ChartChangeListener {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private JFreeChart _chart; // chart associated with the node
    private ChartRenderingInfo _info; // the chart's rendering info
    private boolean _buffered; // draws the chart to an offscreen buffer
    private BufferedImage _chartImage; // buffered chart image
    private AffineTransform _imageTransform; // transform used in with buffered image
    private int _bufferedImageType;

    /*Support for property changes*/
    private SwingPropertyChangeSupport _changeSupport = new SwingPropertyChangeSupport( this );
    private static final String PROPERTY_CHART_RENDERING_INFO = "chartRenderingInfo";
    private static final String PROPERTY_BUFFERED = "buffered";
    private static final String PROPERTY_BUFFERED_IMAGE_TYPE = "bufferedImageType";
    private static final String PROPERTY_BUFFERED_IMAGE = "bufferedImage";

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructs a node that displays the specified chart.
     * The chart is not buffered.
     *
     * @param chart
     */
    public JFreeChartNode( JFreeChart chart ) {
        this( chart, false /* buffered */ );
    }

    /**
     * Constructs a node that displays the specified chart.
     * You can specify whether the chart's image should be buffered.
     * A default buffer type of BufferedImage.TYPE_INT_ARGB is used.
     *
     * @param chart
     * @param buffered
     */
    public JFreeChartNode( JFreeChart chart, boolean buffered ) {
        this( chart, buffered, BufferedImage.TYPE_INT_ARGB );
    }

    /**
     * Constructs a node that displays the specified chart.
     * You can specify whether the chart's image should be buffere,
     * and (if so) what type of buffering should be used.
     *
     * @param chart
     * @param buffered
     * @param bufferedImageType one of types defined for BufferedImage
     */
    public JFreeChartNode( JFreeChart chart, boolean buffered, int bufferedImageType ) {
        super();

        _bufferedImageType = bufferedImageType;
        _chart = chart;
        _chart.addChangeListener( this );
        _info = new ChartRenderingInfo();

        _buffered = buffered;
        _chartImage = null;
        _imageTransform = new AffineTransform();

        addPNodeUpdateHandler();

        updateAll();
    }

    //----------------------------------------------------------------------------
    // Misc updaters
    //----------------------------------------------------------------------------

    /**
     * Forces an update of the chart's ChartRenderingInfo,
     * normally not updated until the next call to paint.
     * Call this directly if you have made changes to the
     * chart and need to calculate something that is based
     * on the ChartRenderingInfo before the next paint occurs.
     */
    public void updateChartRenderingInfo() {
        BufferedImage image = new BufferedImage( 1, 1, _bufferedImageType );
        Graphics2D g2 = image.createGraphics();
        _chart.draw( g2, getBounds(), _info );
        //In order to use this change support, we have to pass different (according to equals()) objects for old and new
        //so clients should not use the getPreviousValue() method on the property change event. 
        _changeSupport.firePropertyChange( PROPERTY_CHART_RENDERING_INFO, null, getChartRenderingInfo() );
    }

    /**
     * This initialization method has been made protected so that clients can override it to perform no-op if necessary.
     * (Functionality is unchanged from previous version.)
     * In DynamicJFreeChartNode, sometimes the BufferedView is causing unidentified bounds change events.
     * <p/>
     * Improved handling of the chart buffer in future versions could alleviate the need for this kind of workaround.
     */
    protected void addPNodeUpdateHandler() {
        addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateAll();
            }
        } );
    }

    /**
     * Rebuilds the buffer and updates the chart rendering info.
     */
    protected void updateAll() {
        rebuildBuffer();
        updateChartRenderingInfo();
        repaint();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Gets the chart that is associated with this node.
     *
     * @return JFreeChart
     */
    public JFreeChart getChart() {
        return _chart;
    }

    /**
     * Gets the chart's rendering info.
     * Changes to the chart are not reflected in the rendering info
     * until after the chart has been painted.
     * You can force the rendering info to be updated by calling
     * updateChartRenderingInfo.
     *
     * @return ChartRenderingInfo
     */
    public ChartRenderingInfo getChartRenderingInfo() {
        return _info;
    }

    /**
     * Returns the type used for the internal buffered image.
     *
     * @return the type used for the internal buffered image.
     */
    public int getBufferedImageType() {
        return _bufferedImageType;
    }

    /**
     * Sets the image type for the internal buffer, e.g. BufferedImage.TYPE_INT_RGB.
     *
     * @param bufferedImageType
     */
    public void setBufferedImageType( int bufferedImageType ) {
        if ( _bufferedImageType != bufferedImageType ) {
            int oldType = _bufferedImageType;
            _bufferedImageType = bufferedImageType;
            updateAll();
            _changeSupport.firePropertyChange( PROPERTY_BUFFERED_IMAGE_TYPE, oldType, _bufferedImageType );
        }
    }

    /**
     * Gets the data area of the primary plot.
     *
     * @return
     */
    public Rectangle2D getDataArea() {
        ChartRenderingInfo chartInfo = getChartRenderingInfo();
        PlotRenderingInfo plotInfo = chartInfo.getPlotInfo();
        // Careful! getDataArea returns a direct reference!
        Rectangle2D dataAreaRef = plotInfo.getDataArea();
        Rectangle2D dataArea = new Rectangle2D.Double();
        dataArea.setRect( dataAreaRef );
        return dataArea;
    }

    /**
     * Gets the data area of a subplot.
     * Only combined charts have subplots.
     *
     * @param subplotIndex
     * @return
     * @throws IndexOutOfBoundsException if subplotIndex is out of bounds
     */
    public Rectangle2D getDataArea( int subplotIndex ) {
        ChartRenderingInfo chartInfo = getChartRenderingInfo();
        PlotRenderingInfo plotInfo = chartInfo.getPlotInfo();
        if ( subplotIndex >= plotInfo.getSubplotCount() ) {
            throw new IndexOutOfBoundsException( "subplotIndex is out of range: " + subplotIndex );
        }
        PlotRenderingInfo subplotInfo = plotInfo.getSubplotInfo( subplotIndex );
        // Careful! getDataArea returns a direct reference!
        Rectangle2D dataAreaRef = subplotInfo.getDataArea();
        Rectangle2D dataArea = new Rectangle2D.Double();
        dataArea.setRect( dataAreaRef );
        return dataArea;
    }

    /*
    * Gets the XYPlot associated with this chart.
    * 
    * @throws UnsupportedOperationException if the primary plot is not an XYPlot
    * @return
    */
    private XYPlot getXYPlot() {
        XYPlot plot = null;
        if ( _chart.getPlot() instanceof XYPlot ) {
            plot = (XYPlot) _chart.getPlot();
        }
        else {
            throw new UnsupportedOperationException(
                    "only works for charts whose primary plot is an XYPlot" );
        }
        return plot;
    }

    /*
    * Gets the XYSubplot of a combined chart.
    * 
    * @param subplotIndex
    * @throws UnsupportedOperationException if the primary plot is not a combined XY plot
    * @throws IndexOutOfBoundsException if subplotIndex is out of bounds
    * @return
    */
    private XYPlot getXYSubplot( int subplotIndex ) {
        XYPlot subplot = null;

        List subplots = null;
        Plot plot = _chart.getPlot();
        if ( plot instanceof CombinedDomainXYPlot ) {
            CombinedDomainXYPlot combinedPlot = (CombinedDomainXYPlot) plot;
            subplots = combinedPlot.getSubplots();
        }
        else if ( plot instanceof CombinedRangeXYPlot ) {
            CombinedRangeXYPlot combinedPlot = (CombinedRangeXYPlot) plot;
            subplots = combinedPlot.getSubplots();
        }
        else {
            throw new UnsupportedOperationException(
                    "only works for for charts whose primary plot is a CombinedDomainXYPlot or CombinedRangeXYPlot" );
        }
        if ( subplotIndex >= subplots.size() ) {
            throw new IndexOutOfBoundsException( "subplotIndex is out of range: " + subplotIndex );
        }
        subplot = (XYPlot) subplots.get( subplotIndex );

        return subplot;
    }

    /**
     * Determines whether the chart is rendered to a buffered image
     * before being drawn to the paint method's graphics context.
     * The chart's image is generated only when the chart changes.
     *
     * @param buffered true or false
     */
    public void setBuffered( boolean buffered ) {
        if ( _buffered != buffered ) {
            _buffered = buffered;
            updateAll();
            _changeSupport.firePropertyChange( PROPERTY_BUFFERED, !_buffered, _buffered );
        }
    }

    /**
     * Is the chart's image buffered?
     *
     * @return true or false
     */
    public boolean isBuffered() {
        return _buffered;
    }

    /*
     * Returns the BufferedImage this JFreeChartNode is using to render the underlying JFreeChart if buffered, or null otherwise.
     * This method is currently for feasibility testing for improved performance during dynamic data display;
     * the interface or implementation may change soon.
     * <p>
     * NOTE: If this method is ever made public, then also make 
     * addBufferedImagePropertyChangeListener and removeBufferedImagePropertyChangeListener public.
     *
     * @return the BufferedImage used to render this chart, possibly null.
     */
    protected BufferedImage getBuffer() {
        return _chartImage;
    }

    //----------------------------------------------------------------------------
    // Property change notification
    //----------------------------------------------------------------------------

    /**
     * Adds a listener that is notified when the chart's isBuffered property changes.
     *
     * @param listener
     */
    public void addBufferedPropertyChangeListener( PropertyChangeListener listener ) {
        _changeSupport.addPropertyChangeListener( PROPERTY_BUFFERED, listener );
    }

    /**
     * Removes a listener that is notified when the chart's isBuffered property changes.
     *
     * @param listener
     */
    public void removeBufferedPropertyChangeListener( PropertyChangeListener listener ) {
        _changeSupport.removePropertyChangeListener( PROPERTY_BUFFERED, listener );
    }

    /*
     * Adds a listener that is notified when the underlying buffered image is (re)constructed.
     * This is protected because getBufferedImage is protected.
     *
     * @param listener
     */
    protected void addBufferedImagePropertyChangeListener( PropertyChangeListener listener ) {
        _changeSupport.addPropertyChangeListener( PROPERTY_BUFFERED_IMAGE, listener );
    }

    /*
     * Removes a listener that is notified when the underlying buffered image is (re)constructed.
     * This is protected because getBufferedImage is protected.
     *
     * @param listener
     */
    protected void removeBufferedImagePropertyChangeListener( PropertyChangeListener listener ) {
        _changeSupport.removePropertyChangeListener( PROPERTY_BUFFERED_IMAGE, listener );
    }

    /**
     * Adds a listener that is notified when the buffered image type is changed.
     *
     * @param listener
     */
    protected void addBufferedImageTypePropertyChangeListener( PropertyChangeListener listener ) {
        _changeSupport.addPropertyChangeListener( PROPERTY_BUFFERED_IMAGE_TYPE, listener );
    }

    /**
     * Adds a listener that is notified when the buffered image type is changed.
     *
     * @param listener
     */
    protected void removeBufferedImageTypePropertyChangeListener( PropertyChangeListener listener ) {
        _changeSupport.removePropertyChangeListener( PROPERTY_BUFFERED_IMAGE_TYPE, listener );
    }

    /**
     * Adds a listener that is notified when the associated chart's rendering info changes.
     *
     * @param propertyChangeListener
     */
    public void addChartRenderingInfoPropertyChangeListener( PropertyChangeListener propertyChangeListener ) {
        _changeSupport.addPropertyChangeListener( PROPERTY_CHART_RENDERING_INFO, propertyChangeListener );
    }

    /**
     * Removes a listener that is notified when the associated chart's rendering info changes.
     *
     * @param propertyChangeListener
     */
    public void removeChartRenderingInfoPropertyChangeListener( PropertyChangeListener propertyChangeListener ) {
        _changeSupport.removePropertyChangeListener( PROPERTY_CHART_RENDERING_INFO, propertyChangeListener );
    }

    //----------------------------------------------------------------------------
    // Coordinate transforms
    //----------------------------------------------------------------------------

    /**
     * Converts a point in the node's local coordinate system
     * to a point in the primary plot's coordinate system.
     * The primary plot must be an XYPlot.
     *
     * @param nodePoint
     * @return
     */
    public Point2D nodeToPlot( Point2D nodePoint ) {
        XYPlot plot = getXYPlot();
        Rectangle2D dataArea = getDataArea();
        Point2D plotPoint = nodeToPlot( nodePoint, plot, dataArea );
        return plotPoint;
    }

    /**
     * Converts a rectangle in the node's local coordinate system
     * to a rectangle in the primary plot's coordinate system.
     * The primary plot must be an XYPlot.
     *
     * @param nodeRect the rectangle to transform.
     * @return the rectangle in this node's coordinate frame.
     */
    public Rectangle2D nodeToPlot( Rectangle2D nodeRect ) {
        Point2D p1 = nodeToPlot( new Point2D.Double( nodeRect.getX(), nodeRect.getY() ) );
        Point2D p2 = nodeToPlot( new Point2D.Double( nodeRect.getX() + nodeRect.getWidth(), nodeRect.getY() + nodeRect.getHeight() ) );
        Rectangle2D.Double rect = new Rectangle2D.Double();
        rect.setFrameFromDiagonal( p1, p2 );
        return rect;
    }

    /**
     * Converts a point in the node's local coordinate system
     * to a point in a subplot's coordinate system.
     * The primary plot must be a combined XY plot,
     * which implies that all subplots are XYPlots.
     *
     * @param nodePoint
     * @param subplotIndex
     * @return
     */
    public Point2D nodeToSubplot( Point2D nodePoint, int subplotIndex ) {
        XYPlot subplot = getXYSubplot( subplotIndex );
        Rectangle2D dataArea = getDataArea( subplotIndex );
        Point2D subplotPoint = nodeToPlot( nodePoint, subplot, dataArea );
        return subplotPoint;
    }

    /**
     * Converts a Rectangle2D in the primary plot's coordinate system
     * to a point in the node's local coordinate system.
     * The primary plot must be an XYPlot.
     *
     * @param plotRect
     * @return
     */
    public Rectangle2D plotToNode( Rectangle2D plotRect ) {
        Point2D a = plotToNode( new Point2D.Double( plotRect.getX(), plotRect.getY() ) );
        Point2D b = plotToNode( new Point2D.Double( plotRect.getMaxX(), plotRect.getMaxY() ) );
        Rectangle2D.Double rect = new Rectangle2D.Double();
        rect.setFrameFromDiagonal( a, b );
        return rect;
    }

    /**
     * Converts a point in the primary plot's coordinate system
     * to a point in the node's local coordinate system.
     * The primary plot must be an XYPlot.
     *
     * @param plotPoint
     * @return
     */
    public Point2D plotToNode( Point2D plotPoint ) {
        XYPlot plot = getXYPlot();
        Rectangle2D dataArea = getDataArea();
        Point2D nodePoint = plotToNode( plotPoint, plot, dataArea );
        return nodePoint;
    }

    /**
     * Converts a point in a subplot's coordinate system
     * to a point in the node's local coordinate system.
     * The primary plot must be a combined XY plot,
     * which implies that all subplots are XYPlots.
     *
     * @param plotPoint
     * @param subplotIndex
     * @return
     */
    public Point2D subplotToNode( Point2D plotPoint, int subplotIndex ) {
        XYPlot subplot = getXYSubplot( subplotIndex );
        Rectangle2D dataArea = getDataArea( subplotIndex );
        Point2D nodePoint = plotToNode( plotPoint, subplot, dataArea );
        return nodePoint;
    }

    /*
    * Converts a node point to a plot point.
    * 
    * @param nodePoint
    * @param plot
    * @param dataArea
    * @return plot point, in model coordinates
    */
    private Point2D nodeToPlot( Point2D nodePoint, XYPlot plot, Rectangle2D dataArea ) {
        final double plotX = plot.getDomainAxis().java2DToValue( nodePoint.getX(), dataArea, plot.getDomainAxisEdge() );
        final double plotY = plot.getRangeAxis().java2DToValue( nodePoint.getY(), dataArea, plot.getRangeAxisEdge() );
        return new Point2D.Double( plotX, plotY );
    }

    /*
    * Converts a plot point to a node point.
    * 
    * @param plotPoint
    * @param plot
    * @param dataArea
    * @return node point, in local coordinates
    */
    private Point2D plotToNode( Point2D plotPoint, XYPlot plot, Rectangle2D dataArea ) {
        final double nodeX = plot.getDomainAxis().valueToJava2D( plotPoint.getX(), dataArea, plot.getDomainAxisEdge() );
        final double nodeY = plot.getRangeAxis().valueToJava2D( plotPoint.getY(), dataArea, plot.getRangeAxisEdge() );
        return new Point2D.Double( nodeX, nodeY );
    }

    //----------------------------------------------------------------------------
    // PNode overrides
    //----------------------------------------------------------------------------

    /*
     * When the node's bounds are changed, this updates the chart rendering info
     */

    protected void internalUpdateBounds( double x, double y, double width, double height ) {
        super.internalUpdateBounds( x, y, width, height );
        updateAll();
    }

    /*
    * Paints the node.
    * The node's bounds (in the node's local coordinate system)
    * are used to determine the size and location of the chart.
    * Painting the node updates the chart's rendering info.
    */
    protected void paint( PPaintContext paintContext ) {
        if ( _buffered ) {
            paintBuffered( paintContext );
        }
        else {
            paintDirect( paintContext );
        }
    }

    /*
    * Paints the node directly to the specified graphics context.
    * 
    * @param paintContext
    */
    private void paintDirect( PPaintContext paintContext ) {
        Graphics2D g2 = paintContext.getGraphics();
        _chart.draw( g2, getBoundsReference(), _info );
    }

    /*
    * Paints the node to a buffered image that is
    * then drawn to the specified graphics context.
    * 
    * @param paintContext
    */
    private void paintBuffered( PPaintContext paintContext ) {
        Rectangle2D bounds = getBoundsReference();

        if ( _chartImage == null ) {
            rebuildBuffer();
        }

        Graphics2D g2 = paintContext.getGraphics();

        // Set interpolation to "nearest neighbor" to avoid JDK 1.5 performance problems.
        g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );

        _imageTransform.setToTranslation( bounds.getX(), bounds.getY() );
        g2.drawRenderedImage( _chartImage, _imageTransform );
    }

    /**
     * If the bounds area is greater than zero, instantiates a new BufferedImage buffer for the chart, and fires a property change.
     */
    private void rebuildBuffer() {
        Rectangle2D bounds = getBoundsReference();
        BufferedImage oldImage = _chartImage;

        int w = (int) bounds.getWidth();
        int h = (int) bounds.getHeight();
        if ( w > 0 && h > 0 ) {
            _chartImage = _chart.createBufferedImage( w, h,
                                                      _bufferedImageType, _info );
            _changeSupport.firePropertyChange( PROPERTY_BUFFERED_IMAGE, oldImage, _chartImage );
        }
    }

    //----------------------------------------------------------------------------
    // ChartChangeListener implementation
    //----------------------------------------------------------------------------

    /**
     * Receives notification of changes to the chart (or any of its components),
     * and redraws the chart.
     *
     * @param event
     */
    public void chartChanged( ChartChangeEvent event ) {

        /* 
         * Do not look at event.getSource(), since the source of the event is
         * likely to be one of the chart's components rather than the chart itself.
         */
        if ( event.getType() == ChartChangeEventType.DATASET_UPDATED ) {
            repaint();
        }
        else if ( event.getType() == ChartChangeEventType.NEW_DATASET ) {
            repaint();
        }
        else {
            updateAll();
        }
    }
}
