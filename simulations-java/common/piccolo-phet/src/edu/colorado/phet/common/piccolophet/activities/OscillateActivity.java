// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.piccolophet.activities;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;

/**
 * User: Sam Reid
 * Date: Jul 15, 2005
 * Time: 1:28:54 PM
 */

public class OscillateActivity extends PActivity {
    private double frequencyHz = 3.5;
    private PNode target;
    private Point2D center;
    private Vector2D amplitude;

    public OscillateActivity( final PNode target, double x, double y ) {
        this( target, x, y, new Vector2D( 0, 25 ), 3.5 );
    }

    public OscillateActivity( PNode target, double x, double y, Vector2D amplitude, double frequencyHz ) {
        super( -1 );
        this.center = new Point2D.Double( x, y );
        this.frequencyHz = frequencyHz;
        this.target = target;
        this.amplitude = amplitude;
    }

    protected void activityStep( long elapsedTime ) {
        super.activityStep( elapsedTime );
        long t = getStartTime() - elapsedTime;
        Point2D location = amplitude.getScaledInstance( Math.sin( frequencyHz * t / 1000.0 ) ).getDestination( center );
        target.setOffset( location.getX(), location.getY() );
    }

    public void setOscillationCenter( Point2D center ) {
        this.center = center;
    }
}