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
import java.awt.geom.Point2D;

/**
 * Class: TravelingWave
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Nov 22, 2004
 * <p/>
 * A sinusoidal traveling wave.
 */
public class TravelingWaveGraphic extends WaveGraphic {

    public TravelingWaveGraphic( Component component, Point2D origin, double extent,
                                 double lambda, double period, double amplitude,
                                 AtomicState atomicState, LaserModel model ) {
        super( component, atomicState, model.getResonatingCavity() );
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

        update();

        wavePath.reset();
        elapsedTime += dt;
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

//    public void paint( Graphics2D g2 ) {
//        GraphicsState gs = new GraphicsState( g2 );
////        g2.setColor( actualColor );
////        g2.fill( wavePath.getBounds() );
//        super.paint( g2 );
//        gs.restoreGraphics();
//    }

}
