/*
* The Physics Education Technology (PhET) project provides 
* a suite of interactive educational simulations. 
* Copyright (C) 2004-2006 University of Colorado.
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or 
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful, 
* but WITHOUT ANY WARRANTY; without even the implied warranty of 
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
* See the GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License along
* with this program; if not, write to the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
* 
* For additional licensing options, please contact PhET at phethelp@colorado.edu
*/

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.jfreechartphet.piccolo;

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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
 *
 * @author Sam Reid
 * @version $Revision$
 */
public class DynamicJFreeChartNode extends JFreeChartNode {
    private ArrayList seriesDataList = new ArrayList();//of type SeriesData 
    private ArrayList seriesViewList = new ArrayList();//of type SeriesView
    private PhetPCanvas phetPCanvas;
    private PhetPPath debugBufferRegion;//internal debugging tool for deciphering screen output regions 

    /*
    These internal factories are the built-in strategies for SeriesView construction.
     */
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
    private SeriesViewFactory bufferedImmediateSeriesFactory = new SeriesViewFactory() {
        public SeriesView createSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            return new BufferedImmediateSeriesView( dynamicJFreeChartNode, seriesData );
        }
    };

    private SeriesViewFactory viewFactory = jfreeChartSeriesFactory;//The default SeriesView is JFreeChart rendering.

    public DynamicJFreeChartNode( PhetPCanvas phetPCanvas, JFreeChart chart ) {
        super( chart );
        this.phetPCanvas = phetPCanvas;
        debugBufferRegion = new PhetPPath( new BasicStroke( 1.0f ), Color.green );
//        addChild( debugBufferRegion );//this can destroy the bounds of the graph, use with care
    }

    /**
     * Adds the specified (x,y) pair to the 0th series in this plot.
     *
     * @param x the x-value to add
     * @param y the y-value to add
     */
    public void addValue( double x, double y ) {
        addValue( 0, x, y );
    }

    /**
     * Adds the specified (x,y) pair to the specified series.
     *
     * @param series the series to which data should be added.  This series should have already been added to this dynamic jfreechartnode with addSeries().
     * @param x      the x-value to add
     * @param y      the y-value to add
     */
    public void addValue( int series, double x, double y ) {
        getSeries( series ).addValue( x, y );
    }

    /**
     * Adds a new series to this chart for plotting, with the given name and color.
     *
     * @param title the title for the series
     * @param color the series' color
     */
    public void addSeries( String title, Color color ) {
        SeriesData seriesData = new SeriesData( title, color );
        seriesDataList.add( seriesData );
        updateSeriesViews();
    }

    /**
     * Empties each series associated with this chart.
     */
    public void clear() {
        for( int i = 0; i < seriesDataList.size(); i++ ) {
            SeriesData seriesData = (SeriesData)seriesDataList.get( i );
            seriesData.clear();
        }
        clearBufferAndRepaint();
    }

    private SeriesData getSeries( int series ) {
        return (SeriesData)seriesDataList.get( series );
    }

    /**
     * Sets the rendering strategy to JFreeChart style.
     */
    public void setJFreeChartSeries() {
        setViewFactory( jfreeChartSeriesFactory );
    }

    /**
     * Sets the rendering strategy to Piccolo style.
     */
    public void setPiccoloSeries() {
        setViewFactory( piccoloSeriesFactory );
    }

    /**
     * Sets the rendering strategy to Buffered style.
     */
    public void setBufferedSeries() {
        setViewFactory( bufferedSeriesFactory );
    }

    /**
     * Sets the rendering strategy to Buffered Immediate style.
     */
    public void setBufferedImmediateSeries() {
        setViewFactory( bufferedImmediateSeriesFactory );
    }

    /**
     * Sets an arbitrary (possibly user-defined) view style.
     *
     * @param factory
     */
    public void setViewFactory( SeriesViewFactory factory ) {
        viewFactory = factory;
        updateSeriesViews();
    }

    private void updateSeriesViews() {
        removeAllSeriesViews();
        clearBufferAndRepaint();
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

    public void setBuffered( boolean buffered ) {
        super.setBuffered( buffered );
        updateSeriesViews();
    }

    static interface SeriesViewFactory {
        SeriesView createSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData );
    }

    /**
     * Base class strategy for painting a data series.
     */
    public static abstract class SeriesView {
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
        private PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateClip();
            }
        };

        public PiccoloSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            super( dynamicJFreeChartNode, seriesData );

            pathClip = new PClip();
            pathClip.setStrokePaint( null );//set to non-null for debugging clip area
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
                pathNode.setOffset( dynamicJFreeChartNode.getBounds().getX(), dynamicJFreeChartNode.getBounds().getY() );
            }
            else {
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
            dynamicJFreeChartNode.removeChartRenderingInfoPropertyChangeListener( listener );
            dynamicJFreeChartNode.removeBufferedImagePropertyChangeListener( listener );
        }

        public void install() {
            super.install();
            getDynamicJFreeChartNode().addChild( root );
            dynamicJFreeChartNode.addChartRenderingInfoPropertyChangeListener( listener );
            dynamicJFreeChartNode.addBufferedImagePropertyChangeListener( listener );
            updateClip();
            updateSeriesGraphic();
        }

        public void dataAdded() {
            updateSeriesGraphic();
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
            //todo: this was causing an infinite loop in updateSeriesViews
//            if( dynamicJFreeChartNode.isBuffered() != origStateBuffered ) {
//                dynamicJFreeChartNode.setBuffered( origStateBuffered );
//            }
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

    static class BufferedImmediateSeriesView extends BufferedSeriesView {

        public BufferedImmediateSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
            super( dynamicJFreeChartNode, seriesData );
        }

        protected void repaintPanel( Rectangle2D bounds ) {
            dynamicJFreeChartNode.phetPCanvas.paintImmediately( new Rectangle( (int)bounds.getX(), (int)bounds.getY(), (int)( bounds.getWidth() + 1 ), (int)( bounds.getHeight() + 1 ) ) );
            dynamicJFreeChartNode.debugBufferRegion.setPathTo( bounds );
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

}
