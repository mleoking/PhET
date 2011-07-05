// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class for sugar and salt dispensers
 *
 * @author Sam Reid
 */
public abstract class Dispenser {
    //Start centered above the fluid
    public final Property<ImmutableVector2D> center;

    //Model the angle of rotation, 0 degrees is straight up (not tilted)
    public final DoubleProperty angle;

    //True if the user has selected this dispenser type
    public final Property<Boolean> enabled = new Property<Boolean>( false );
    protected final Beaker beaker;

    //True if the user is allowed to add more solute, false if the limit has been reached (10 moles per solute).
    public final ObservableProperty<Boolean> moreAllowed;

    //The name of the dispenser contents, to be displayed on the side of the dispenser node
    public final String name;

    //The amount to scale model translations so that micro tab emits solute at the appropriate time.  Without this factor, the tiny (1E-9 meters) drag motion in the Micro tab wouldn't be enough to emit solute
    public final double distanceScale;

    public Dispenser( double x, double y, double angle, Beaker beaker, ObservableProperty<Boolean> moreAllowed, String name, double distanceScale ) {
        this.beaker = beaker;
        this.moreAllowed = moreAllowed;
        this.name = name;
        this.angle = new DoubleProperty( angle );
        center = new Property<ImmutableVector2D>( new ImmutableVector2D( x, y ) );
        this.distanceScale = distanceScale;
    }

    //Translate the dispenser by the specified delta in model coordinates
    public void translate( Dimension2D delta ) {

        //Translate the center, but make sure it doesn't go out of bounds
        ImmutableVector2D proposedPoint = center.get().plus( delta );
        double y = MathUtil.clamp( beaker.getTopY(), proposedPoint.getY(), Double.POSITIVE_INFINITY );
        center.set( new ImmutableVector2D( proposedPoint.getX(), y ) );
    }

    public void rotate( double v ) {
        angle.add( v );
    }

    //Reset the dispenser's position and orientation
    public void reset() {
        //Only need to set the primary properties, others (e.g., open/enabled) are derived and will auto-reset
        center.reset();
        angle.reset();
    }

    //Give the crystal an appropriate velocity when it comes out so it arcs.  This method is used by subclasses when creating crystals
    protected ImmutableVector2D getCrystalVelocity( ImmutableVector2D outputPoint ) {
        ImmutableVector2D directionVector = outputPoint.minus( center.get() );
        double anglePastTheHorizontal = angle.get() - Math.PI / 2;
        return directionVector.getInstanceOfMagnitude( 0.2 + 0.3 * Math.sin( anglePastTheHorizontal ) );
    }

    public abstract void updateModel( SugarAndSaltSolutionModel model );

    public abstract PNode createNode( ModelViewTransform transform, double beakerHeight );
}