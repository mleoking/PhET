/**
 * Class: DataSetGraphic
 * Package: edu.colorado.phet.chart
 * Author: Another Guy
 * Date: Sep 15, 2004
 */
package edu.colorado.phet.chart;

import java.awt.geom.Point2D;
import java.awt.*;

public abstract class DataSetGraphic implements DataSet.Observer {
    private DataSet dataSet;
    private Chart chart;

    public DataSetGraphic( DataSet dataSet ) {
        this.dataSet = dataSet;
    }

    public void setChart( Chart chart ) {
        this.chart = chart;
    }

    protected DataSet getDataSet() {
        return dataSet;
    }

    protected void addAllPoints() {
        DataSet dataSet = getDataSet();
        for( int i = 0; i < dataSet.size(); i++ ) {
            Point2D point = dataSet.pointAt( i );
            pointAdded( point );
        }
    }

    protected Chart getChart() {
        return chart;
    }
    public abstract void paint(Graphics2D graphics2D);
}
