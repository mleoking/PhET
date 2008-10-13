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

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.view.util.MakeDuotoneImageOp;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.quantum.model.Tube;
import edu.colorado.phet.lasers.controller.LasersConfig;

/**
 * WaveGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public abstract class WaveGraphic extends PhetGraphic implements ModelElement {
    private Point2D origin;
    private double extent;
    private double lambda;
    private double period;
    private double amplitude;
    private Color color;
    // Steps in x for which each piece-wise segment of the standing wave is computed
    private double dx = 1;

    private GeneralPath wavePath = new GeneralPath();
    private int numPts;
    private double elapsedTime = 0;
    private Stroke stroke = new BasicStroke( 2f );

    private Tube cavity;
    private int level;
    private Rectangle curtainBounds = new Rectangle();
    // Controls the maximum darkness of the visible beam. Smaller == darker
    private int minLevel;
    // Controls how the visible darkness of the beam changes with the number of lasing photons. Small means
    // the color gets darker faster
    private double rampUpExponent;

    /**
     * @param component
     * @param cavity
     */
    public WaveGraphic( Component component, Point2D origin, double extent,
                        double lambda, double period, double amplitude,
                        Color color, Tube cavity ) {
        super( component );
        this.origin = origin;
        this.extent = extent;
        this.lambda = lambda;
        this.period = period;
        this.amplitude = amplitude;
        this.cavity = cavity;
        this.color = color;
        numPts = (int) ( extent / dx ) + 1;
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

    protected double getExtent() {
        return extent;
    }

    protected double getPeriod() {
        return period;
    }

    protected double getDx() {
        return dx;
    }

    protected GeneralPath getWavePath() {
        return wavePath;
    }

    protected int getNumPts() {
        return numPts;
    }

    protected double getElapsedTime() {
        return elapsedTime;
    }

    protected Rectangle determineBounds() {
        return wavePath.getBounds();
    }

    public void paint( Graphics2D g2 ) {
        saveGraphicsState( g2 );
        if ( amplitude > 0 && isVisible() ) {
            g2.setStroke( stroke );
            g2.setColor( color );
            g2.draw( wavePath );
        }
        restoreGraphicsState();
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

    public void stepInTime( double dt ) {
        elapsedTime += dt;
    }

    protected void update() {
        minLevel = 150;
        // The power function here controls the ramp-up of actualColor intensity
        rampUpExponent = .5;
        level = Math.max( minLevel, 255 - (int) ( ( 255 - minLevel ) * Math.pow( ( getAmplitude() / getMaxInternalAmplitude() ), rampUpExponent ) ) );
        level = Math.min( level, 255 );
        curtainBounds.setRect( wavePath.getBounds().getMinX(), cavity.getBounds().getMinY(),
                               wavePath.getBounds().getWidth(), cavity.getBounds().getHeight() );
        setBoundsDirty();
        repaint();
    }

    private double getMaxInternalAmplitude() {
        return LasersConfig.LASING_THRESHOLD;
    }
}
