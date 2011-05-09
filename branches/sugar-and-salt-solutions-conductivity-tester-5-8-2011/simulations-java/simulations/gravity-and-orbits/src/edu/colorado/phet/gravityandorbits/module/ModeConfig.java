// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.module;

import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

import static edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsClock.DEFAULT_DT;

/**
 * Configuration for setting up a particular GravityAndOrbitsMode, enumerated in ModeList
 *
 * @author Sam Reid
 */
public abstract class ModeConfig {
    double zoom;
    double dt = DEFAULT_DT;
    protected double forceScale;
    public Line2D.Double initialMeasuringTapeLocation;

    public ModeConfig( double zoom ) {
        this.zoom = zoom;
    }

    public void center() {
        ImmutableVector2D deltaVelocity = getTotalMomentum().times( -1.0 / getTotalMass() );
        for ( BodyConfiguration prototype : getBodies() ) {
            prototype.vx += deltaVelocity.getX();
            prototype.vy += deltaVelocity.getY();
        }
    }

    //Compute the total momentum for purposes of centering the camera on the center of momentum frame
    private ImmutableVector2D getTotalMomentum() {
        ImmutableVector2D totalMomentum = new ImmutableVector2D();
        for ( BodyConfiguration body : getBodies() ) {
            totalMomentum = totalMomentum.plus( body.getMomentum() );
        }
        return totalMomentum;
    }

    private double getTotalMass() {
        double totalMass = 0.0;
        for ( BodyConfiguration prototype : getBodies() ) {
            totalMass += prototype.mass;
        }
        return totalMass;
    }

    protected abstract BodyConfiguration[] getBodies();
}
