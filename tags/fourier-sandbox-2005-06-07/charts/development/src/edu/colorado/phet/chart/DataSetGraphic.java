/**
 * Class: DataSetGraphic
 * Package: edu.colorado.phet.chart
 * Author: Another Guy
 * Date: Sep 15, 2004
 */
package edu.colorado.phet.chart;

import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;

import java.awt.*;
import java.awt.geom.Point2D;

public abstract class DataSetGraphic extends GraphicLayerSet implements DataSet.Observer {
    private DataSet dataSet;
    private Chart chart;

    public DataSetGraphic( Component component, Chart chart, DataSet dataSet ) {
        super( component );
        this.chart = chart;
        this.dataSet = dataSet;
        dataSet.addObserver( this );
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    protected void addAllPoints() {
        DataSet dataSet = getDataSet();
        for( int i = 0; i < dataSet.size(); i++ ) {
//            System.out.println( "i = " + i + ", point=" + dataSet.pointAt( i ) );
            Point2D point = dataSet.pointAt( i );
            pointAdded( point );
        }
    }

    protected Chart getChart() {
        return chart;
    }

    public abstract void transformChanged();
}
