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
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.MakeDuotoneImageOp;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.ResonatingCavity;
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

    private EventChannel eventChannel = new EventChannel( Listener.class );
    protected Listener listenerProxy = (Listener)eventChannel.getListenerProxy();
    private AtomicState atomicState;
    private ResonatingCavity cavity;
    protected Color actualColor;
    private int level;
    private Color baseColor;
    private Rectangle curtainBounds = new Rectangle();
    // Controls the maximum darkness of the visible beam. Smaller == darker
    private int minLevel;
    // Controls how the visible darkness of the beam changes with the number of lasing photons. Small means
    // the color gets darker faster
    private double rampUpExponent;

    public Wave( Component component, AtomicState atomicState, ResonatingCavity cavity ) {
        super( component );
        this.atomicState = atomicState;
        this.cavity = cavity;
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

        GraphicsUtil.setAlpha( g2, (double)( 255 - level ) / 255 );
        g2.setColor( baseColor );
        g2.fill( curtainBounds );
//        g2.fill( wavePath.getBounds() );
        GraphicsUtil.setAlpha( g2, 1 );

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

    /**
     * Determines the color to paint the rectangle.
     *
     * @param baseColor
     * @param level
     * @return
     */
    protected Color getActualColor( Color baseColor, int level ) {
        double grayRefLevel = MakeDuotoneImageOp.getGrayLevel( baseColor );
        int newRGB = MakeDuotoneImageOp.getDuoToneRGB( level, level, level, 255, grayRefLevel, baseColor );
        return new Color( newRGB );
    }

    protected void update() {
        baseColor = VisibleColor.wavelengthToColor( atomicState.getWavelength() );
        minLevel = 150;
        // The power function here controls the ramp-up of actualColor intensity
        rampUpExponent = .5;
        level = Math.max( minLevel, 255 - (int)( ( 255 - minLevel ) * Math.pow( ( getAmplitude() / getMaxInternalAmplitude() ), rampUpExponent ) ) );
        level = Math.min( level, 255 );
//        actualColor = getActualColor( baseColor, level );

        curtainBounds.setRect( wavePath.getBounds().getMinX(), cavity.getBounds().getMinY(),
                               wavePath.getBounds().getWidth(), cavity.getBounds().getHeight() );
    }

    private double getMaxInternalAmplitude() {
        return LaserConfig.LASING_THRESHOLD;
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
