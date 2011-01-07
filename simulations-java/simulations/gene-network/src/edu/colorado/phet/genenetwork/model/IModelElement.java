// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.genenetwork.model;

import java.awt.Shape;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

public interface IModelElement {
	void addListener(IModelElementListener listener);
	void removeListener(IModelElementListener listener);
    Shape getShape();
    Point2D getPositionRef();
    void setPosition(Point2D newPosition);
    void setPosition(double xPos, double yPos);
    Vector2D getVelocityRef();
    void setVelocity( Vector2D newVelocity);
    void setVelocity(double xVel, double yVel);
    void stepInTime(double dt);
	boolean isPartOfDnaStrand();
    boolean isUserControlled();
    
    /**
     * Obtain the "existence strength" for this model element.  This value,
     * which ranges between 0 and 1, indicates how strongly this element
     * should be portrayed in the view.  Low numbers mean that the element is
     * either coming in to existence or going out of existence, and should
     * thus be portrayed as somewhat transparent.
     */
    double getExistenceStrength();
    
    /**
     * Take whatever actions are necessary when being removed from the model,
     * such as cleaning up memory references and sending out notifications of
     * removal.
     */
    void removeFromModel();
}
