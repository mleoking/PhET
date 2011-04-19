// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.model;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

import static edu.colorado.phet.bendinglight.model.BendingLightModel.SPEED_OF_LIGHT;

/**
 * A LightRay models one straight segment of a beam (completely within a single medium), with a specific wavelength.
 *
 * @author Sam Reid
 */
public class LightRay {
    //Directionality is important for propagation
    public final ImmutableVector2D tip;
    public final ImmutableVector2D tail;
    public final double indexOfRefraction;//The index of refraction of the medium the lightray inhabits
    public final double wavelength; // wavelength in meters
    private final double powerFraction;//amount of power this light has (full strength is 1.0)
    private ArrayList<VoidFunction0> removalListeners = new ArrayList<VoidFunction0>();
    private ArrayList<VoidFunction0> moveToFrontListeners = new ArrayList<VoidFunction0>();
    private Color color;
    private double waveWidth;
    private double numWavelengthsPhaseOffset; //This number indicates how many wavelengths have passed before this light ray begins; it is zero for the light coming out of the laser.
    private final Shape oppositeMedium;//Used to create a clipped shape for wave mode

    //fill in the triangular chip near y=0 even for truncated beams, if it is the transmitted beam
    public final boolean extendBackwards;//Light must be extended backwards for the transmitted wave shape to be correct
    private boolean extend;//has to be an integral number of wavelength so that the phases work out correctly, turing this up too high past 1E6 causes things not to render properly

    private double time;
    private ArrayList<VoidFunction0> stepListeners = new ArrayList<VoidFunction0>();

    public LightRay( ImmutableVector2D tail, ImmutableVector2D tip, double indexOfRefraction, double wavelength,
                     double powerFraction, Color color, double waveWidth, double numWavelengthsPhaseOffset, Shape oppositeMedium,
                     boolean extend, boolean extendBackwards ) {
        this.oppositeMedium = oppositeMedium;
        this.extendBackwards = extendBackwards;
        this.color = color;
        this.waveWidth = waveWidth;
        this.tip = tip;
        this.tail = tail;
        this.indexOfRefraction = indexOfRefraction;
        this.wavelength = wavelength;
        this.powerFraction = powerFraction;
        this.numWavelengthsPhaseOffset = numWavelengthsPhaseOffset;
        this.extend = extend;
    }

    public void addRemovalListener( VoidFunction0 listener ) {
        removalListeners.add( listener );
    }

    //Used for getting the right z-ordering for wave mode
    public void addMoveToFrontListener( VoidFunction0 listener ) {
        moveToFrontListeners.add( listener );
    }

    public double getSpeed() {
        return SPEED_OF_LIGHT / indexOfRefraction;
    }

    public void remove() {
        for ( VoidFunction0 removalListener : removalListeners ) {
            removalListener.apply();
        }
        removalListeners.clear();
    }

    public double getPowerFraction() {
        return powerFraction;
    }

    public boolean intersects( Shape sensorShape ) {
        return !new Area( sensorShape ) {{
            intersect( new Area( new BasicStroke( 1E-10f ).createStrokedShape( toLine2D() ) ) );
        }}.isEmpty();
    }

    public Line2D toLine2D() {
        return new Line2D.Double( tail.toPoint2D(), tip.toPoint2D() );
    }

    public double getLength() {
        return tip.minus( tail ).getMagnitude();
    }

    public ImmutableVector2D toVector2D() {
        return new ImmutableVector2D( tail.toPoint2D(), tip.toPoint2D() );
    }

    public Color getColor() {
        return color;
    }

    public double getWavelength() {
        return wavelength;
    }

    public void moveToFront() {
        for ( VoidFunction0 moveToFrontListener : moveToFrontListeners ) {
            moveToFrontListener.apply();
        }
    }

    @Override
    public String toString() {
        return "tail = " + tail + ", tip = " + tip;
    }

    public double getExtensionFactor() {
        if ( extendBackwards ||//fill in the triangular chip near y=0 even for truncated beams, if it is the transmitted beam
             extend ) {
            return wavelength * 1E6;
        }
        else {
            return 0;
        }
    }

    public Shape getWaveShape() {
        final BasicStroke stroke = new BasicStroke( (float) ( waveWidth ), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER );
        final Shape strokedShape = stroke.createStrokedShape( extendBackwards ? getExtendedLineBackwards() : getExtendedLine() );
        Area area = new Area( strokedShape ) {{
            if ( oppositeMedium != null ) { subtract( new Area( oppositeMedium ) ); }
        }};
        return area;
    }

    //Have to extend the line so that it can be clipped against the opposite medium, so it will won't show any missing triangular chips.
    private Line2D.Double getExtendedLine() {
        return new Line2D.Double( tail.toPoint2D(), tip.plus( getUnitVector().times( getExtensionFactor() ) ).toPoint2D() );
    }

    //Use this one for the transmitted beam
    private Line2D.Double getExtendedLineBackwards() {
        return new Line2D.Double( tail.plus( getUnitVector().times( -getExtensionFactor() ) ).toPoint2D(), tip.toPoint2D() );
    }

    public ImmutableVector2D getUnitVector() {
        return new ImmutableVector2D( tail.toPoint2D(), tip.toPoint2D() ).getNormalizedInstance();
    }

    public double getAngle() {
        return toVector2D().getAngle();
    }

    public void addStepListener( VoidFunction0 stepListener ) {
        stepListeners.add( stepListener );
    }

    public void setTime( double time ) {
        this.time = time;
        for ( VoidFunction0 stepListener : stepListeners ) {
            stepListener.apply();
        }
    }

    public double getWaveWidth() {
        return waveWidth;
    }

    public double getNumberOfWavelengths() {
        return getLength() / wavelength;
    }

    public double getNumWavelengthsPhaseOffset() {
        return numWavelengthsPhaseOffset;
    }

    public Shape getOppositeMedium() {
        return oppositeMedium;
    }

    public boolean contains( ImmutableVector2D position, boolean waveMode ) {
        if ( waveMode ) {
            return getWaveShape().contains( position.getX(), position.getY() );
        }
        else {
            return new BasicStroke( (float) getRayWidth() ).createStrokedShape( toLine2D() ).contains( position.toPoint2D() );
        }
    }

    public double getRayWidth() {
        return 1.5992063492063494E-7;//At the default transform, this yields a 4 pixel wide stroke
    }

    public ImmutableVector2D getVelocity() {
        return tip.minus( tail ).getNormalizedInstance().times( getSpeed() );
    }

    public double getFrequency() {
        return getSpeed() / getWavelength();
    }

    public double getAngularFrequency() {
        return getFrequency() * Math.PI * 2;
    }

    public double getPhaseOffset() {
        return getAngularFrequency() * time - 2 * Math.PI * numWavelengthsPhaseOffset;
    }

    public double getCosArg( double distanceAlongRay ) {
        double w = getAngularFrequency();
        double k = 2 * Math.PI / getWavelength();
        double x = distanceAlongRay;
        double t = time;

        return k * x - w * t - 2 * Math.PI * numWavelengthsPhaseOffset;
    }

    public double getTime() {
        return time;
    }
}
