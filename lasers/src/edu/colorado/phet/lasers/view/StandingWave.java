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
import edu.colorado.phet.common.util.EventRegistry;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.EventListener;
import java.util.EventObject;

/**
 * Class: StandingWave
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Nov 22, 2004
 * <p/>
 * A sinusoidal standing wave.
 */
public class StandingWave extends PhetGraphic implements ModelElement,
                                                         AtomicState.EnergyLevelChangeListener {

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
    private EventRegistry eventRegistry = new EventRegistry();


    public StandingWave( Component component, Point2D origin, double extent,
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

    public void addListener( ChangeListener listener ) {
        eventRegistry.addListener( listener );
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

        //        g2.setColor( Color.green );
        //        g2.draw( determineBounds() );

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
        eventRegistry.fireEvent( new ChangeEvent( this ) );
    }

    public void energyLevelChangeOccurred( AtomicState.EnergyLevelChangeEvent event ) {
        double lambda = Photon.energyToWavelength( event.getEnergy() );
        color = VisibleColor.wavelengthToColor( lambda );
    }


    ////////////////////////////////////////////////////////////////////////////////////////
    // Inner classes
    public interface ChangeListener extends EventListener {
        public void waveChanged( StandingWave.ChangeEvent event );
    }

    public class ChangeEvent extends EventObject {
        public ChangeEvent( Object source ) {
            super( source );
        }

        public StandingWave getStandingWave() {
            return (StandingWave)getSource();
        }
    }
}
