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
        this.dataSet = dataSet;
        dataSet.addObserver( this );
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    protected void addAllPoints() {
        DataSet dataSet = getDataSet();
        pointsAdded( dataSet.getPoints() );
    }

    protected Chart getChart() {
        return chart;
    }

    public abstract void transformChanged();
}
