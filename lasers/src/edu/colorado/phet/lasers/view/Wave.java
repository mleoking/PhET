/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.EventChannel;
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
 * Wave
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public abstract class Wave extends PhetGraphic implements ModelElement, AtomicState.Listener {
    protected Point2D origin;
    protected double lambda;
    protected double period;
    protected double amplitude;
    protected Color color;
    // Steps in x for which each piece-wise segment of the standing wave is computed
    protected double dx = 1;
//    protected double dx = 2;
    protected GeneralPath wavePath = new GeneralPath();
    protected int numPts;
    protected double elapsedTime = 0;
    protected Stroke stroke = new BasicStroke( 2f );

    //-----------------------------------------------------------------------
    // Inner classes
    //-----------------------------------------------------------------------
    private EventChannel eventChannel = new EventChannel( Listener.class );
    protected Listener listenerProxy = (Listener)eventChannel.getListenerProxy();

    public Wave( Component component ) {
        super( component );
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
        g2.setStroke( stroke );
        g2.setColor( color );
        g2.draw( wavePath );
        restoreGraphicsState();
    }

    public void energyLevelChanged( AtomicState.Event event ) {
        double lambda = Photon.energyToWavelength( event.getEnergy() );
        color = VisibleColor.wavelengthToColor( lambda );
    }

    public void meanLifetimechanged( AtomicState.Event event ) {

    }

    public void addListener( Listener listener ) {
        eventChannel.addListener( listener );
    }

    public void removeListener( Listener listener ) {
        eventChannel.removeListener( listener );
    }

    public interface Listener extends EventListener {
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
