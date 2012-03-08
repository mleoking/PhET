// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies;

import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 * @author John Blanco
 */
public class CompositeMotionStrategy extends MotionStrategy {

    private final Entry[] entries;

    public static class Entry {
        MotionStrategy strategy;
        double weight;

        public Entry( MotionStrategy strategy, double weight ) {
            this.strategy = strategy;
            this.weight = weight;
        }
    }

    public CompositeMotionStrategy( Entry... entries ) {
        this.entries = entries;
    }

    @Override public Point2D getNextLocation( Point2D currentLocation, Shape shape, double dt ) {
        for ( Entry entry : entries ) {
            currentLocation = entry.strategy.getNextLocation( currentLocation, shape, dt * entry.weight );
        }
        return currentLocation;
    }
}
