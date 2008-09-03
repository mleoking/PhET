/**
 * Class: StripChartDelegate
 * Package: edu.colorado.phet.emf.view
 * Author: Another Guy
 * Date: Aug 5, 2003
 */
package edu.colorado.phet.radiowaves.view;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.radiowaves.model.Electron;
import edu.colorado.phet.radiowaves.util.StripChart;

/**
 * This class acts as a receiver of messages from the model to add data to
 * a strip chart.
 */
public class StripChartDelegate implements SimpleObserver {
    private StripChart chart;
    private Electron electron;

    public StripChartDelegate( Electron electron, StripChart chart ) {
        this.chart = chart;
        electron.addObserver( this );
        this.electron = electron;
    }

    public void update() {
        chart.addDatum( electron.getCurrentPosition().getY(), 1 );
    }
}
