/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.test;

import edu.colorado.phet.energyskatepark.EnergySkateParkApplication;
import edu.colorado.phet.timeseries.TimeSeriesModel;

public class TestModelRecord {
    public static void main( String[] args ) {
        EnergySkateParkApplication app = new EnergySkateParkApplication( args );

        app.startApplication();

        TimeSeriesModel model = app.getModule().getTimeSeriesModel();

        model.setRecordMode();

        try {
            Thread.sleep( 1000 );
        }
        catch( InterruptedException e ) {
        }

        model.rewind();
        model.setRecordMode();
        model.setLiveMode();
    }
}
