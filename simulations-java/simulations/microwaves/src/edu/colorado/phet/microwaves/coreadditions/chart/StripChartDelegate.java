/**
 * Class: StripChartDelegate
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Aug 18, 2003
 */
package edu.colorado.phet.microwaves.coreadditions.chart;

import edu.colorado.phet.util_microwaves.StripChart;

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
//    public static void main( String[] args ) {
//        Observable o=new Observable();
//        StripChart sc=new StripChart(0,0,0,0,0,0,0,0);
//        o.addObserver(new Observer() {
//            public void update( Observable o, Object arg ) {
//                sc.addDatum();
//            }
//        } );
//    }
}
