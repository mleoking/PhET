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
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 * Class: StandingWave
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Nov 22, 2004
 * <p/>
 * A sinusoidal standing wave.
 */
public class StandingWave extends PhetGraphic implements ModelElement {

    private Point2D origin;
    private double lambda;
    private double period;
    private double amplitude;
    private Color color;
    // Steps in x for which each piece-wise segment of the standing wave is computed
    private double dx = 2;
    private GeneralPath wavePath = new GeneralPath();;
    private int numPts;
    private double elapsedTime = 0;


    public StandingWave( Component component, Point2D origin, double extent,
                         double lambda, double period, double amplitude,
                         Color color, BaseModel model ) {
        super( component );
        this.origin = origin;
        this.lambda = lambda;
        this.period = period;
        this.amplitude = amplitude;
        this.color = color;
        numPts = (int)( extent / dx ) + 1;
        model.addModelElement( this );
    }

    public Point2D getOrigin() {
        return origin;
    }

    public double getLambda() {
        return lambda;
    }

    public void setLambda( double lambda ) {
        this.lambda = lambda;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public void setAmplitude( double amplitude ) {
        this.amplitude = amplitude;
    }

    public Color getColor() {
        return color;
    }

    public void setColor( Color color ) {
        this.color = color;
    }

    protected Rectangle determineBounds() {
        return wavePath.getBounds();
    }

    public void paint( Graphics2D g2 ) {
        saveGraphicsState( g2 );
        g2.setColor( color );
        g2.draw( wavePath );
        restoreGraphicsState();
    }

    public void stepInTime( double dt ) {
        wavePath.reset();
        elapsedTime += dt;
        double a = Math.sin( ( elapsedTime / period ) * Math.PI );
        wavePath.moveTo( (float)origin.getX(), (float)origin.getY() );
        for( int i = 0; i < numPts; i += 3 ) {
            double x = dx * i;
            double y = amplitude * ( a * Math.sin( ( x / lambda ) * Math.PI ) );
            wavePath.lineTo( (float)( x + origin.getX() ), (float)( y + origin.getY() ) );
        }
    }
}
