/**
 * Class: DataSetGraphic
 * Package: edu.colorado.phet.chart
 * Author: Another Guy
 * Date: Sep 15, 2004
 */
package edu.colorado.phet.forces1d.force1d_tag_chart;

import java.awt.*;
import java.awt.geom.Point2D;

public abstract class DataSetGraphic implements DataSet.Observer {
    private DataSet dataSet;
    private Chart chart;

    public DataSetGraphic( DataSet dataSet ) {
        this.dataSet = dataSet;
        dataSet.addObserver( this );
    }

    public void setChart( Chart chart ) {
        this.chart = chart;
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    protected void addAllPoints() {
        DataSet dataSet = getDataSet();
        for ( int i = 0; i < dataSet.size(); i++ ) {
//            System.out.println( "i = " + i + ", point=" + dataSet.pointAt( i ) );
            Point2D point = dataSet.pointAt( i );
            pointAdded( point );
        }
    }

    protected Chart getChart() {
        return chart;
    }

    public abstract void paint( Graphics2D graphics2D );

    public abstract void transformChanged();
}
