// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.common.model;

import java.awt.geom.Point2D;

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
}
