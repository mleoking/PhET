/* Copyright University of Colorado, 2004 */
/*
 * CVS Info:
 * Current revision:   $Revision$
 * On branch:          $Name$
 * Latest change by:   $Author$
 * On date:            $Date$
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;
import edu.colorado.phet.lasers.model.LaserModel;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.Random;

/**
 * NonLasingWaveGraphic
 * <p/>
 * A sine wave with randomized phase.
 *
 * @author Ron LeMaster
 */
public class NonLasingWaveGraphic extends WaveGraphic {
    private AffineTransform rtx;
    private Point2D.Double p1 = new Point2D.Double();
    private Point2D.Double p2 = new Point2D.Double();
    private Random random;
    private LaserModel model;
    private BaseLaserModule module;

    /**
     * @param component
     * @param origin
     * @param extent
     * @param lambda
     * @param period
     * @param amplitude
     * @param module
     * @param angle
     */
    public NonLasingWaveGraphic( Component component, Point2D origin, double extent,
                                 double lambda, double period, double amplitude,
                                 Color color, BaseLaserModule module, double angle ) {
        super( component, origin, extent, lambda, period, amplitude, color, module.getLaserModel().getResonatingCavity() );
        this.module = module;
        this.model = module.getLaserModel();
        model.addModelElement( this );

        // Create a random number generator. Sleep first to make sure it gets seeded
        // differently than others that might have been create recently
        try {
            Thread.sleep( 10 );
            random = new Random( System.currentTimeMillis() );
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }

        rtx = AffineTransform.getRotateInstance( angle );
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        GeneralPath wavePath = getWavePath();
        wavePath.reset();
        double phase = random.nextDouble() * Math.PI;
        Point2D origin = getOrigin();
        wavePath.moveTo( (float)origin.getX(), (float)origin.getY() );
        for( int i = 0; i < getNumPts(); i += 3 ) {
            double x = getDx() * i;
            double y = getAmplitude() * ( Math.sin( phase + ( x / getLambda() ) * Math.PI ) );
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


    public void paint( Graphics2D g2 ) {
        super.saveGraphicsState( g2 );
        double alpha = Math.min( getAmplitude() / 20, 1 );
        if( module.isMirrorsEnabled() ) {
            alpha *= 1 - module.getRightMirror().getReflectivity();
        }
        GraphicsUtil.setAlpha( g2, alpha );
        super.paint( g2 );
        super.restoreGraphicsState();
    }
}
