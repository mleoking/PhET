// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;

/**
 * Class that represents a thermometer in the model.  The thermometer has
 * only a point and a temperature in the model.  Its visual representation is
 * left entirely to the view.
 *
 * @author John Blanco
 */
public class Thermometer extends UserMovableModelElement {

    public Thermometer( ImmutableVector2D initialPosition ) {
        position.set( initialPosition );
    }

    @Override public IUserComponent getUserComponent() {
        //TODO
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public IUserComponentType getUserComponentType() {
        // Movable elements are considered sprites.
        return UserComponentTypes.sprite;
    }

    @Override public Property<HorizontalSurface> getBottomSurfaceProperty() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
