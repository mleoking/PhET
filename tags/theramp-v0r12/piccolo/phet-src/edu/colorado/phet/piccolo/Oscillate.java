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
    private double x;
    private double y;
    private double amplitude = 25;

    public Oscillate( final PNode target, double x, double y ) {
        super( -1 );
        this.target = target;
        this.x = x;
        this.y = y;
//        target.addPropertyChangeListener( PNode.PROPERTY_VISIBLE, new PropertyChangeListener() {
//            public void propertyChange( PropertyChangeEvent evt ) {
//                System.out.println( "evt = " + evt );
//                if( target.getVisible() ) {
//                    target.getRoot().addActivity( Oscillate.this );
//                }
//                else {
//                    terminate();
//                }
//            }
//        } );
    }

    public Oscillate( PNode target, int x, int y, double amplitude, double frequencyHz ) {
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
