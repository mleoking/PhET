/* Copyright University of Colorado, 2004 */
/*
 * CVS Info:
 * Current revision:   $Revision$
 * On branch:          $Name$
 * Latest change by:   $Author$
 * On date:            $Date$
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.lasers.model.LaserModel;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 * StandingWave
 * <p/>
 * A sinusoidal standing wave
 *
 * @author Ron LeMaster
 */
public class StandingWaveGraphic extends WaveGraphic {

    public StandingWaveGraphic( Component component, Point2D origin, double extent,
                                double lambda, double period, double amplitude,
                                Color color, LaserModel model ) {
        super( component, origin, extent, lambda, period, amplitude, color, model.getResonatingCavity() );
        model.addModelElement( this );
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        GeneralPath wavePath = getWavePath();
        wavePath.reset();
        double a = Math.sin( ( getElapsedTime() / getPeriod() ) * Math.PI );
        Point2D origin = getOrigin();
        wavePath.moveTo( (float)origin.getX(), (float)origin.getY() );
        for( int i = 0; i < getNumPts(); i += 3 ) {
            double x = getDx() * i;
            double y = getAmplitude() * ( a * Math.sin( ( x / getLambda() ) * Math.PI ) );
            wavePath.lineTo( (float)( x + origin.getX() ), (float)( y + origin.getY() ) );
        }
        update();
    }
}
