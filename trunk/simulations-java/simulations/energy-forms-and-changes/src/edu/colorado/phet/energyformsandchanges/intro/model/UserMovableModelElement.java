// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Base class for model elements that can be moved around by the user.
 *
 * @author John Blanco
 */
public abstract class UserMovableModelElement extends ModelElement {

    public final BooleanProperty userControlled = new BooleanProperty( false );

    // Position of the center of the bottom of the block.
    public final Property<ImmutableVector2D> position;

    // Velocity in the up/down direction.
    public final Property<Double> verticalVelocity = new Property<Double>( 0.0 );

    // Observer that moves this model element if an when the surface that is
    // supporting it moves.
    private VoidFunction1<HorizontalSurface> surfaceMotionObserver = new VoidFunction1<HorizontalSurface>() {
        public void apply( final HorizontalSurface horizontalSurface ) {
            final ImmutableVector2D value = new ImmutableVector2D( horizontalSurface.getCenterX(), horizontalSurface.yPos );
            position.set( value );
        }
    };

    /**
     * Constructor.
     */
    public UserMovableModelElement( ImmutableVector2D initialPosition ) {
        position = new Property<ImmutableVector2D>( initialPosition );
        userControlled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean userControlled ) {
                if ( userControlled ) {
                    // The user has grabbed this model element, so it is no
                    // longer sitting on any surface.
                    if ( getSupportingSurface() != null ) {
                        getSupportingSurface().removeObserver( surfaceMotionObserver );
                        setSupportingSurface( null );
                    }
                }
            }
        } );
    }

    @Override public void setSupportingSurface( Property<HorizontalSurface> surfaceProperty ) {
        super.setSupportingSurface( surfaceProperty );
        if ( surfaceProperty != null ) {
            surfaceProperty.addObserver( surfaceMotionObserver );
        }
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

    public abstract Property<HorizontalSurface> getBottomSurfaceProperty();

    public void setX( final double x ) {
        position.set( new ImmutableVector2D( x, position.get().getY() ) );
    }
}