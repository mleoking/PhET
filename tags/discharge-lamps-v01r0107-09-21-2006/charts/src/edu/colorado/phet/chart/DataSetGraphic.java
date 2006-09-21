/**
 * Class: DataSetGraphic
 * Package: edu.colorado.phet.chart
 * Author: Another Guy
 * Date: Sep 15, 2004
 */
package edu.colorado.phet.chart;

import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphicListener;

import java.awt.*;

public abstract class DataSetGraphic extends GraphicLayerSet implements DataSet.Observer, PhetGraphicListener {
    private DataSet dataSet;
    private Chart chart;

    public DataSetGraphic( Component component, Chart chart, DataSet dataSet ) {
        super( component );
        this.chart = chart;
        setDataSet( dataSet );
        
        // Clip to the chart boundary, and change the clip when the chart changes.
        setClip( chart.getChartBounds() );
        chart.addPhetGraphicListener( this );
    }

    /**
     * Call this method before releasing all references to an object of this type.
     * If you don't call this, you will have created a memory leak.
     * The object should not be used after calling this method.
     */
    public void cleanup() {
        chart.removePhetGraphicListener( this );
        chart = null;
        if ( dataSet != null ) {
            dataSet.removeObserver( this );
            dataSet = null;
        }
    }
    
    public void setDataSet( DataSet dataSet ) {
        if( dataSet != this.dataSet ) {
            if( this.dataSet != null ) {
                this.dataSet.removeObserver( this );
            }
            this.dataSet = dataSet;
            this.dataSet.addObserver( this );
        }
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    protected void addAllPoints() {
        DataSet dataSet = getDataSet();
        if( dataSet != null ) {
            pointsAdded( dataSet.getPoints() );
        }
    }

    protected Chart getChart() {
        return chart;
    }

    /**
     * Called when the associated Chart's size or range changes.
     */
    public abstract void transformChanged();
    
    //----------------------------------------------------------------------------
    // PhetGraphicListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Keeps the graphic clipped to the chart.
     */
    public void phetGraphicChanged( PhetGraphic phetGraphic ) {
        setClip( chart.getChartBounds() );
    }

    /**
     * Does nothing.
     */
    public void phetGraphicVisibilityChanged( PhetGraphic phetGraphic ) {
    }
}
