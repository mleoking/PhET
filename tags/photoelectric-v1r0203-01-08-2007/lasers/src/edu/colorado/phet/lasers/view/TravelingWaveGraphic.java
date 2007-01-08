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
 * TravelingWave
 * <p/>
 * A traveling sinusoidal wave
 *
 * @author Ron LeMaster
 */
public class TravelingWaveGraphic extends WaveGraphic {

    public TravelingWaveGraphic( Component component, Point2D origin, double extent,
                                 double lambda, double period, double amplitude,
                                 Color color, LaserModel model ) {
        super( component, origin, extent, lambda, period, amplitude, color, model.getResonatingCavity() );
        model.addModelElement( this );
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        GeneralPath wavePath = getWavePath();
        wavePath.reset();
        for( int i = 0; i < getNumPts(); i++ ) {
            double x = getDx() * i;
            double y = getAmplitude() * Math.sin( ( ( x - getElapsedTime() ) / getLambda() ) * Math.PI );
            Point2D origin = getOrigin();
            if( i == 0 ) {
                wavePath.moveTo( (float)( x + origin.getX() ), (float)( y + origin.getY() ) );
            }
            else {
                wavePath.lineTo( (float)( x + origin.getX() ), (float)( y + origin.getY() ) );
            }
        }
        update();
    }
}
