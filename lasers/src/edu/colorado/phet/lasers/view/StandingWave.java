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
 * CVS Info:
 * Current revision:   $Revision$
 * On branch:          $Name$
 * Latest change by:   $Author$
 * On date:            $Date$
 */
public class StandingWave extends PhetGraphic implements ModelElement {

    private Point2D origin;
    private double lambda;
    private double period;
    private double amplitude;
    private Color color;
    private double dx = 2;
    private GeneralPath wavePath = new GeneralPath();;
    private int numPts;
    double elapsedTime = 0;


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
        return null;
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
        //        for( int i = 0; i < numPts - 3; i+=3 ) {
        for( int i = 0; i < numPts; i += 3 ) {
            double x = dx * i;
            double y = amplitude * ( a * Math.sin( ( x / lambda ) * Math.PI ) );
            //            double x1 = dx * (i + 1);
            //            double y1 = amplitude * ( a * Math.sin( ( x1 / lambda ) * Math.PI ) );
            //            double x2 = dx * (i + 2);
            //            double y2 = amplitude * ( a * Math.sin( ( x2 / lambda ) * Math.PI ) );
            //            wavePath.curveTo( (float)( x + origin.getX() ), (float)( y + origin.getY() ),
            //                              (float)( x1 + origin.getX() ), (float)( y1 + origin.getY() ),
            //                              (float)( x2 + origin.getX() ), (float)( y2 + origin.getY() ));
            wavePath.lineTo( (float)( x + origin.getX() ), (float)( y + origin.getY() ) );
        }
    }
}
