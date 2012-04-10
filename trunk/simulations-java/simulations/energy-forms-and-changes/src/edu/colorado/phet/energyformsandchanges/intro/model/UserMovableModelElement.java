// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Base class for model elements that can be moved around by the user.
 *
 * @author John Blanco
 */
public abstract class UserMovableModelElement {

    public final BooleanProperty userControlled = new BooleanProperty( false );

    //Position of the center of the bottom of the block.
    public final Property<ImmutableVector2D> position = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );

    // Velocity in the up/down direction.
    public final Property<Double> verticalVelocity = new Property<Double>( 0.0 );

    // The surface upon which this model element is resting.  If null, the
    // object is either on the ground or falling.
    private ObservableProperty<HorizontalSurface> parentSurface = null;
    private VoidFunction1<HorizontalSurface> observer = new VoidFunction1<HorizontalSurface>() {
        @Override public void apply( final HorizontalSurface horizontalSurface ) {
            final ImmutableVector2D value = new ImmutableVector2D( horizontalSurface.getCenterX(), horizontalSurface.yPos );
            System.out.println( "value = " + value );
            position.set( value );
        }
    };

    protected UserMovableModelElement() {
        // Whenever a movable model element becomes user controlled, it is no
        // longer resting on any surface.
        userControlled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean userControlled ) {
                if ( userControlled ) {
                    parentSurface = null;
                }
            }
        } );
    }

    /**
     * Get the "user component" identifier.  This supports the sim sharing
     * feature.
     *
     * @return user component identifier.
     */
    public abstract IUserComponent getUserComponent();

    /**
     * Get the "user component type" identifier.  This supports the sim sharing
     * feature.
     *
     * @return
     */
    public abstract IUserComponentType getUserComponentType();

    /**
     * Translate, a.k.a. move, the model element by the indicated amount.
     *
     * @param modelDelta Vector that describes the desired motion.
     */
    public void translate( ImmutableVector2D modelDelta ) {
        position.set( position.get().getAddedInstance( modelDelta ) );
    }

    public ObservableProperty<HorizontalSurface> getParentSurface() {
        return parentSurface;
    }

    public void setParentSurface( ObservableProperty<HorizontalSurface> parentSurface ) {
        if ( this.parentSurface != null ) {
            this.parentSurface.removeObserver( observer );
        }
        this.parentSurface = parentSurface;

        if ( this.parentSurface != null ) {

//            if ( parentSurface == getTop() || parentSurface == getBottom() ) {
//                throw new RuntimeException( "Same as top or bottom" );
//            }
//               ()
            this.parentSurface.addObserver( observer );
        }
    }

    public abstract HorizontalSurface getBottomSurface();

    public void setX( final double x ) {
        position.set( new ImmutableVector2D( x, position.get().getY() ) );
    }
}