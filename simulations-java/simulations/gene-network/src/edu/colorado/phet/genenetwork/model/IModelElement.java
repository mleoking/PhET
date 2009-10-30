package edu.colorado.phet.genenetwork.model;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

public interface IModelElement {
    Shape getShape();
    Point2D getPositionRef();
    void setPosition(Point2D newPosition);
    void setPosition(double xPos, double yPos);
    Vector2D getVelocityRef();
    void setVelocity(Vector2D newVelocity);
    void setVelocity(double xVel, double yVel);
    void updatePositionAndMotion();
    boolean availableForBonding(SimpleElementType elementType);
    void updatePotentialBondingPartners(ArrayList<IModelElement> modelElements);
    boolean considerProposal(IModelElement modelElement);
    boolean releaseBondWith(IModelElement modelElement);
	SimpleElementType getType();
	BindingPoint getBindingPointForElement(SimpleElementType type);
}
