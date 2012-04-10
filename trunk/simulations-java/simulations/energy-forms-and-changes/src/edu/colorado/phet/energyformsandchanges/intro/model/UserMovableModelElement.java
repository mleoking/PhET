// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;

/**
 * Base class for model elements that can be moved around by the user.
 *
 * @author John Blanco
 */
public abstract class UserMovableModelElement {

    public final BooleanProperty userControlled = new BooleanProperty( false );

    public final Property<ImmutableVector2D> position = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) );

    // Velocity in the up/down direction.
    public final Property<Double> verticalVelocity = new Property<Double>( 0.0 );

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
}
