/* Copyright 2004, Sam Reid */
package edu.colorado.phet.piccolo;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * User: Sam Reid
 * Date: Jul 15, 2005
 * Time: 1:28:54 PM
 * Copyright (c) Jul 15, 2005 by Sam Reid
 */

public class Oscillate extends PActivity {
    private double frequencyHz = 3.5;
    private PNode target;
    private double amplitude = 25;

    public Oscillate( final PNode target ) {
        super( -1 );
        this.target = target;
        target.addPropertyChangeListener( PNode.PROPERTY_VISIBLE, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                System.out.println( "evt = " + evt );
                if( target.getVisible() ) {
                    target.getRoot().addActivity( Oscillate.this );
                }
                else {
                    terminate();
                }
            }
        } );
    }

    public Oscillate( PNode target, double amplitude, double frequencyHz ) {
        super( -1 );
        this.frequencyHz = frequencyHz;
        this.target = target;
        this.amplitude = amplitude;
    }

    protected void activityStep( long elapsedTime ) {
        super.activityStep( elapsedTime );
        long t = getStartTime() - elapsedTime;
        double dy = amplitude * Math.sin( frequencyHz * t / 1000.0 );
        target.setOffset( 0, dy );
    }
}
