/**
 * Class: MicrowaveStripCharSubject
 * Package: edu.colorado.phet.microwave.view
 * Author: Another Guy
 * Date: Aug 18, 2003
 */
package edu.colorado.phet.microwave.view;

import edu.colorado.phet.coreadditions.chart.StripChartSubject;
import edu.colorado.phet.microwave.model.Microwave;

public class MicrowaveStripCharSubject extends StripChartSubject {

    Microwave microwave;

    public MicrowaveStripCharSubject( Microwave microwave ) {
        this.microwave = microwave;
        microwave.addObserver( this );
    }

    public double getDatum() {
        return microwave.getAmplitude()[0];
    }
}
