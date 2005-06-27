/**
 * Class: DataSetGraphic
 * Package: edu.colorado.phet.chart
 * Author: Another Guy
 * Date: Sep 15, 2004
 */
package edu.colorado.phet.chart;

import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;

import java.awt.*;

public abstract class DataSetGraphic extends GraphicLayerSet implements DataSet.Observer {
    private DataSet dataSet;
    private Chart chart;

    public DataSetGraphic( Component component, Chart chart, DataSet dataSet ) {
        super( component );
        this.chart = chart;
        setDataSet( dataSet );
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

    public abstract void transformChanged();
}
