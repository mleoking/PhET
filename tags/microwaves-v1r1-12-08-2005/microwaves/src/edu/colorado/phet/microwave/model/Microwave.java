/**
 * Class: Microwave
 * Package: edu.colorado.phet.microwave.model
 * Author: Another Guy
 * Date: Aug 18, 2003
 */
package edu.colorado.phet.microwave.model;

import edu.colorado.phet.waves.model.PlaneWavefront;
import edu.colorado.phet.waves.model.SineFunction;
import edu.colorado.phet.waves.model.Wave;

public class Microwave extends Wave {

    private double t;

    public Microwave( float freq, float amp ) {
        super( new PlaneWavefront(), new SineFunction(),
                freq, amp );
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
    }
}
