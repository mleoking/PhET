/**
 * Class: Microwave
 * Package: edu.colorado.phet.microwave.model
 * Author: Another Guy
 * Date: Aug 18, 2003
 */
package edu.colorado.phet.microwaves.model;

import edu.colorado.phet.microwaves.model.waves.PlaneWavefront;
import edu.colorado.phet.microwaves.model.waves.SineFunction;
import edu.colorado.phet.microwaves.model.waves.Wave;

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
