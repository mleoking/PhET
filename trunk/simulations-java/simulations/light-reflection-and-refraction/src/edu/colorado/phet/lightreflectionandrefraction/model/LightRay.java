// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.model;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction0;

/**
 * @author Sam Reid
 */
public class LightRay {
    public final Property<ImmutableVector2D> tip;
    public final Property<ImmutableVector2D> tail;
    public final Property<Double> phase;
    public final double indexOfRefraction;
    public final double wavelength; // wavelength in meters
    private final double powerFraction;
    private ArrayList<VoidFunction0> removalListeners = new ArrayList<VoidFunction0>();
    private ArrayList<VoidFunction0> moveToFrontListeners = new ArrayList<VoidFunction0>();
    private Color color;
    private double waveWidth;
    private double numWavelengthsPhaseOffset;
    private final Shape oppositeMedium;
    public final boolean extendBackwards;
    private boolean extend;

    public LightRay( ImmutableVector2D tail, ImmutableVector2D tip, double indexOfRefraction, double wavelength,
                     double powerFraction, Color color, double waveWidth, double numWavelengthsPhaseOffset, Shape oppositeMedium//for clipping
            , double initialPhase,
                     boolean extend,//has to be an integral number of wavelength so that the phases work out correctly, turing this up too high past 1E6 causes things not to render properly
                     boolean extendBackwards ) {
        this.oppositeMedium = oppositeMedium;
        this.extendBackwards = extendBackwards;
        this.phase = new Property<Double>( initialPhase );
        this.color = color;
        this.waveWidth = waveWidth;
        this.tip = new Property<ImmutableVector2D>( tip );
        this.tail = new Property<ImmutableVector2D>( tail );
        this.indexOfRefraction = indexOfRefraction;
        this.wavelength = wavelength;
        this.powerFraction = powerFraction;
        this.numWavelengthsPhaseOffset = numWavelengthsPhaseOffset;
        this.extend = extend;
    }

    public void addRemovalListener( VoidFunction0 listener ) {
        removalListeners.add( listener );
    }

    public void addMoveToFrontListener( VoidFunction0 listener ) {
        moveToFrontListeners.add( listener );
    }

    public void addObserver( SimpleObserver simpleObserver ) {
        tip.addObserver( simpleObserver );
        tail.addObserver( simpleObserver );
    }

    public double getSpeed() {
        return LRRModel.SPEED_OF_LIGHT / indexOfRefraction;
    }

    public void propagate( double dt ) {
        propagateTip( dt );
        tail.setValue( tail.getValue().getAddedInstance( getDelta( dt ) ) );
    }

    public void propagateTip( double dt ) {
        tip.setValue( tip.getValue().getAddedInstance( getDelta( dt ) ) );
    }

    private ImmutableVector2D getDelta( double dt ) {
        ImmutableVector2D unitDirection = tip.getValue().getSubtractedInstance( tail.getValue() ).getNormalizedInstance();
        return unitDirection.getScaledInstance( getSpeed() * dt );
    }

    public void remove() {
        for ( VoidFunction0 removalListener : removalListeners ) {
            removalListener.apply();
        }
        tip.removeAllObservers();
        tail.removeAllObservers();
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
        return new Line2D.Double( tail.getValue().toPoint2D(), tip.getValue().toPoint2D() );
    }

    public double getLength() {
        return tip.getValue().getSubtractedInstance( tail.getValue() ).getMagnitude();
    }

    public ImmutableVector2D toVector2D() {
        return new ImmutableVector2D( tail.getValue().toPoint2D(), tip.getValue().toPoint2D() );
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
        return new Line2D.Double( tail.getValue().toPoint2D(), tip.getValue().getAddedInstance( getUnitVector().getScaledInstance( getExtensionFactor() ) ).toPoint2D() );
    }

    //Use this one for the transmitted beam
    private Line2D.Double getExtendedLineBackwards() {
        return new Line2D.Double( tail.getValue().getAddedInstance( getUnitVector().getScaledInstance( -getExtensionFactor() ) ).toPoint2D(), tip.getValue().toPoint2D() );
    }

    private ImmutableVector2D getUnitVector() {
        return new ImmutableVector2D( tail.getValue().toPoint2D(), tip.getValue().toPoint2D() ).getNormalizedInstance();
    }

    public double getAngle() {
        return toVector2D().getAngle();
    }

    public void step( double dt ) {
        final double deltaPhase = getSpeed() / LRRModel.SPEED_OF_LIGHT * 100;
        phase.setValue( phase.getValue() + deltaPhase * dt );
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
}
