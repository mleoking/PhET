/**
 * Class: StripChartDelegate
 * Package: edu.colorado.phet.emf.view
 * Author: Another Guy
 * Date: Aug 5, 2003
 */
package edu.colorado.phet.emf.view;

import edu.colorado.phet.emf.model.Electron;
import edu.colorado.phet.util.StripChart;
import edu.colorado.phet.common.util.SimpleObserver;
//import edu.colorado.phet.common.model.simpleobservable.SimpleObserver;

import java.util.Observable;
import java.util.Observer;

/**
 * This class acts as a receiver of messages from the model to add data to
 * a strip chart.
 */
public class StripChartDelegate implements SimpleObserver {
//public class StripChartDelegate implements Observer {
    private StripChart chart;
    private Electron electron;

    public StripChartDelegate( Electron electron, StripChart chart ) {
        this.chart = chart;
        electron.addObserver( this );
        this.electron = electron;
    }

    public void update() {
//    public void update( Observable o, Object arg ) {
//        if( o instanceof Electron ) {
//            Electron electron = (Electron)o;
            chart.addDatum( electron.getCurrentPosition().getY(), 1 );
//        }
    }
}
