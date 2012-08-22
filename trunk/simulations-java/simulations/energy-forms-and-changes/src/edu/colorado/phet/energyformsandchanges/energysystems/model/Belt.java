// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;

/**
 * Class that represents a belt that connects two rotating wheels together,
 * like a fan belt in an automobile engine.
 *
 * @author John Blanco
 */
public class Belt extends PositionableModelElement {
    public final double distanceBetweenWheelCenters; // In meters.
    public final double wheel1Radius; // In meters.
    public final double wheel2Radius; // In meters.
    public final double angleFromHorizontal; // In radians.

    public Belt( Vector2D centerPosition, double distanceBetweenWheelCenters, double wheel1Radius, double wheel2Radius, double angleFromHorizontal ) {
        super( centerPosition );
        this.distanceBetweenWheelCenters = distanceBetweenWheelCenters;
        this.wheel1Radius = wheel1Radius;
        this.wheel2Radius = wheel2Radius;
        this.angleFromHorizontal = angleFromHorizontal;
    }
}
