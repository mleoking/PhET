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
    public final double indexOfRefraction;
    public final double wavelength; // wavelength in meters
    private final double powerFraction;
    private ArrayList<VoidFunction0> removalListeners = new ArrayList<VoidFunction0>();
    private Color color;

    public LightRay( ImmutableVector2D tail, ImmutableVector2D tip, double indexOfRefraction, double wavelength, double powerFraction, Color color ) {
        this.color = color;
        this.tip = new Property<ImmutableVector2D>( tip );
        this.tail = new Property<ImmutableVector2D>( tail );
        this.indexOfRefraction = indexOfRefraction;
        this.wavelength = wavelength;
        this.powerFraction = powerFraction;
    }

    public void addRemovalListener( VoidFunction0 listener ) {
        removalListeners.add( listener );
    }

    public void addObserver( SimpleObserver simpleObserver ) {
        tip.addObserver( simpleObserver );
        tail.addObserver( simpleObserver );
    }

    public double getSpeed() {
        return LRRModel.C / indexOfRefraction;
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
}
