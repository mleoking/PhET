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
    private double extent;
    private double lambda;
    private double amplitude;
    private Color color;
    private Point2D[] pts;
    private double dx = 2;
    private GeneralPath wavePath;

    public StandingWave( Component component, Point2D origin, double extent,
                         double lambda, double amplitude, Color color, BaseModel model ) {
        super( component );
        this.origin = origin;
        this.extent = extent;
        this.lambda = lambda;
        this.amplitude = amplitude;
        this.color = color;
        pts = new Point2D[(int)( extent / ( dx + 1 ) )];
        for( int i = 0; i < pts.length; i++ ) {
            pts[i] = new Point2D.Double();
        }

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
        if( wavePath != null ) {
            saveGraphicsState( g2 );
            g2.setColor( color );
            g2.draw( wavePath );
            restoreGraphicsState();
        }
    }

    double elapsedTime = 0;

    public void stepInTime( double dt ) {
        wavePath = new GeneralPath();
        elapsedTime += dt;
        wavePath.moveTo( (float)pts[0].getX(), (float)pts[0].getY() );
        for( int i = 0; i < pts.length; i++ ) {
            double x = origin.getX() + ( dx * i );
            double y = origin.getY() + Math.sin( ( Math.PI / 2 ) * ( x / lambda ) ) * amplitude;
            wavePath.lineTo( (float)x, (float)y );
        }
    }
}
