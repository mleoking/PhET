/* Copyright University of Colorado, 2004 */
/*
 * CVS Info:
 * Current revision:   $Revision$
 * On branch:          $Name$
 * Latest change by:   $Author$
 * On date:            $Date$
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.atom.AtomicState;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Random;

/**
 * Class: StandingWave
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Nov 22, 2004
 * <p/>
 * A sinusoidal standing wave.
 */
public class NonLasingWaveGraphic extends WaveGraphic {
    private AffineTransform rtx;
    private Point2D.Double p1 = new Point2D.Double();
    private Point2D.Double p2 = new Point2D.Double();
    private Random random = new Random();

    /**
     * @param component
     * @param origin
     * @param extent
     * @param lambda
     * @param period
     * @param amplitude
     * @param atomicState
     * @param model
     * @param angle
     */
    public NonLasingWaveGraphic( Component component, Point2D origin, double extent,
                                 double lambda, double period, double amplitude,
                                 AtomicState atomicState, LaserModel model, double angle ) {
        super( component, atomicState, model.getResonatingCavity() );
        this.origin = origin;
        this.lambda = lambda;
        this.period = period;
        this.amplitude = amplitude;
        this.color = VisibleColor.wavelengthToColor( atomicState.getWavelength() );
        numPts = (int)( extent / dx ) + 1;
        model.addModelElement( this );

        rtx = AffineTransform.getRotateInstance( angle );

        atomicState.addListener( this );
    }

    public void stepInTime( double dt ) {
        wavePath.reset();
        elapsedTime += dt;
        double a = Math.sin( ( elapsedTime / period ) * Math.PI );
        double phase = random.nextDouble() * Math.PI;
        wavePath.moveTo( (float)origin.getX(), (float)origin.getY() );
        for( int i = 0; i < numPts; i += 3 ) {
            double x = dx * i;
            double y = amplitude * ( Math.sin( phase + ( x / lambda ) * Math.PI ) );
//            double y = amplitude * ( a * Math.sin( ( x / lambda + phase ) * Math.PI ) );
            p1.setLocation( x, y );
            rtx.transform( p1, p2 );
            if( i == 0 ) {
                wavePath.moveTo( (float)( p2.x + origin.getX() ), (float)( p2.y + origin.getY() ) );
            }
            else {
                wavePath.lineTo( (float)( p2.x + origin.getX() ), (float)( p2.y + origin.getY() ) );
            }
        }
        update();
    }
}
