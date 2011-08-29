// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class for sugar and salt dispensers
 *
 * @author Sam Reid
 */
public abstract class Dispenser<T extends SugarAndSaltSolutionModel> {
    //Start centered above the fluid
    public final Property<ImmutableVector2D> center;

    //Model the angle of rotation, 0 degrees is straight up (not tilted)
    public final DoubleProperty angle;

    //True if the user has selected this dispenser type
    public final Property<Boolean> enabled = new Property<Boolean>( false );

    //Beaker into which the solute will be dispensed
    protected final Beaker beaker;

    //True if the user is allowed to add more solute, false if the limit has been reached (10 moles per solute).
    public final ObservableProperty<Boolean> moreAllowed;

    //The name of the dispenser contents, to be displayed on the side of the dispenser node
    public final String name;

    //The amount to scale model translations so that micro tab emits solute at the appropriate time.  Without this factor, the tiny (1E-9 meters) drag motion in the Micro tab wouldn't be enough to emit solute
    public final double distanceScale;

    //The height of the dispenser in meters, for purposes of making sure the crystals come out at the right location relative to the image
    //This is used since we want to keep the view the same in each module, but to have different actual model dimensions
    protected double dispenserHeight;

    //A reference to the model for adding particles to it
    public final T model;

    public Dispenser( double x, double y, double angle, Beaker beaker, ObservableProperty<Boolean> moreAllowed, String name, double distanceScale, ObservableProperty<DispenserType> selectedType, final DispenserType type, T model ) {
        this.beaker = beaker;
        this.moreAllowed = moreAllowed;
        this.name = name;
        this.model = model;
        this.angle = new DoubleProperty( angle );
        center = new Property<ImmutableVector2D>( new ImmutableVector2D( x, y ) );
        this.distanceScale = distanceScale;

        //Wire up the Dispenser so it is enabled when the model has the right type dispenser selected
        selectedType.addObserver( new VoidFunction1<DispenserType>() {
            public void apply( DispenserType dispenserType ) {
                enabled.set( dispenserType == type );
            }
        } );
    }

    //Translate the dispenser by the specified delta in model coordinates
    public void translate( Dimension2D delta ) {

        //Translate the center, but make sure it doesn't go out of bounds
        ImmutableVector2D proposedPoint = center.get().plus( delta );
        double y = MathUtil.clamp( beaker.getTopY(), proposedPoint.getY(), Double.POSITIVE_INFINITY );
        center.set( new ImmutableVector2D( proposedPoint.getX(), y ) );
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

    //After time has passed, update the model by adding any crystals that should be emitted
    public abstract void updateModel();

    //Method for creating a PNode such as a SugarDispenserNode or SaltShakerNode to display this Dispenser and allow the user to interact with it
    public abstract PNode createNode( ModelViewTransform transform, double beakerHeight, boolean micro );

    //Set the height of the dispenser, used to emit crystals in the right location relative to the image
    public void setDispenserHeight( double dispenserHeight ) {
        this.dispenserHeight = dispenserHeight;
    }
}