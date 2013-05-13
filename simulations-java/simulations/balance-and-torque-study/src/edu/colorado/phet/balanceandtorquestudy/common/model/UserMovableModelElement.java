// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.common.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;

/**
 * Interface for model elements that can be moved about by the user.
 *
 * @author John Blanco
 */
public interface UserMovableModelElement {
    void setPosition( double x, double y );

    void setPosition( Point2D p );

    /**
     * Method that is called when the user releases the element, i.e. they had
     * been dragging it with the mouse and then let it go.
     */
    void release();

    /**
     * Get the "user component" identifier.  This supports the sim sharing
     * feature.
     *
     * @return user component identifier.
     */
    IUserComponent getUserComponent();

    /**
     * Get the "user component type" identifier.  This supports the sim sharing
     * feature.
     *
     * @return
     */
    IUserComponentType getUserComponentType();
}
