/* Copyright 2004, Sam Reid */
package edu.colorado.phet.piccolo;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;

/**
 * User: Sam Reid
 * Date: Jul 15, 2005
 * Time: 1:28:54 PM
 * Copyright (c) Jul 15, 2005 by Sam Reid
 */

public class OscillateActivity extends PActivity {
    private double frequencyHz = 3.5;
    private PNode target;
    private double x;
    private double y;
    private double amplitude = 25;

    public OscillateActivity( final PNode target, double x, double y ) {
        this( target, x, y, 25, 3.5 );
    }

    public OscillateActivity( PNode target, double x, double y, double amplitude, double frequencyHz ) {
        super( -1 );
        this.x = x;
        this.y = y;
        this.frequencyHz = frequencyHz;
        this.target = target;
        this.amplitude = amplitude;
    }

    protected void activityStep( long elapsedTime ) {
        super.activityStep( elapsedTime );
        long t = getStartTime() - elapsedTime;
        double dy = amplitude * Math.sin( frequencyHz * t / 1000.0 );
        target.setOffset( x, y + dy );
    }
}
