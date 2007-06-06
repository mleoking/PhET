package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

import org.jfree.data.xy.XYSeries;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.GeneralPath;

/**
 * Base class strategy for painting a data series.
 */
public abstract class SeriesView {
    private DynamicJFreeChartNode dynamicJFreeChartNode;
    private SeriesData seriesData;
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

    public Point2D.Double getPoint( int i ) {
        return new Point2D.Double( getSeries().getX( i ).doubleValue(), getSeries().getY( i ).doubleValue() );
    }

    public Point2D getNodePoint( int i ) {
        return getDynamicJFreeChartNode().plotToNode( getPoint( i ) );
    }
    public Rectangle2D getDataArea(){
        return getDynamicJFreeChartNode().getDataArea();
    }

    protected GeneralPath toGeneralPath() {
        GeneralPath path = new GeneralPath();
        if( getSeries().getItemCount() > 0 ) {
            path.moveTo( (float)getNodePoint( 0 ).getX(), (float)getNodePoint( 0 ).getY() );
        }
        for( int i = 1; i < getSeries().getItemCount(); i++ ) {
            path.lineTo( (float)getNodePoint( i ).getX(), (float)getNodePoint( i ).getY() );
        }
        return path;
    }
}
