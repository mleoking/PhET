/* Copyright University of Colorado, 2004 */
/*
 * CVS Info:
 * Current revision:   $Revision$
 * On branch:          $Name$
 * Latest change by:   $Author$
 * On date:            $Date$
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.lasers.model.atom.AtomicState;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Class: TravelingWave
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Nov 22, 2004
 * <p/>
 * A sinusoidal traveling wave.
 */
public class TravelingWave extends Wave {

    public TravelingWave( Component component, Point2D origin, double extent,
                          double lambda, double period, double amplitude,
                          AtomicState atomicState, BaseModel model ) {
        super( component );
        this.origin = origin;
        this.lambda = lambda;
        this.period = period;
        this.amplitude = amplitude;
        this.color = VisibleColor.wavelengthToColor( atomicState.getWavelength() );
        numPts = (int)( extent / dx ) + 1;
        model.addModelElement( this );

        atomicState.addListener( this );
    }

    public void stepInTime( double dt ) {
        wavePath.reset();
        elapsedTime += dt;
//        wavePath.moveTo( (float)origin.getX(), (float)origin.getY() );
        for( int i = 0; i < numPts; i++ ) {
            double x = dx * i;
            double y = amplitude * Math.sin( ( ( x - elapsedTime ) / lambda ) * Math.PI );
            if( i == 0 ) {
                wavePath.moveTo( (float)( x + origin.getX() ), (float)( y + origin.getY() ) );
            }
            else {
                wavePath.lineTo( (float)( x + origin.getX() ), (float)( y + origin.getY() ) );
            }
        }
        listenerProxy.waveChanged( new ChangeEvent( this ) );
    }
}
