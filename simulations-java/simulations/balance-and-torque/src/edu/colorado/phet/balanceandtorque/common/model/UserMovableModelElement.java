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

    //REVIEW the other methods are obvious, but this one is not, please doc
    void release();
}
