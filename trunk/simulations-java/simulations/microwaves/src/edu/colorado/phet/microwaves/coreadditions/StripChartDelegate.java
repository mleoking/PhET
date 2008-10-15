/**
 * Class: StripChartDelegate
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Aug 18, 2003
 */
package edu.colorado.phet.microwaves.coreadditions;

import edu.colorado.phet.microwaves.coreadditions.chart.StripChart;

import java.util.Observable;
import java.util.Observer;

public class StripChartDelegate implements Observer {

    private StripChart chart;

    public StripChartDelegate( StripChartSubject subject, StripChart chart ) {
        this.chart = chart;
        subject.addObserver( this );
    }

    public StripChartDelegate( StripChart chart ) {
        this.chart = chart;
    }

    public void update( Observable o, Object arg ) {
        if( o instanceof StripChartSubject ) {
            StripChartSubject subject = (StripChartSubject)o;
            chart.addDatum( subject.getDatum(), 1 );
        }
    }
}
