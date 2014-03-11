// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.circuitconstructionkit.CCKSimSharing;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 1:37:51 AM
 */
public class Junction extends SimpleObservableDebug {
    private double x;
    private double y;
    private int label = 0;
    private static int nextLabel = 0;
    private boolean selected = false;
    private double voltage;//voltage relative to reference node.  To be used in computing potential drops, to avoid graph traversal.
    private static int instanceCount = 0;
    private final IUserComponent userComponent = UserComponentChain.chain( CCKSimSharing.UserComponents.junction, instanceCount++ ); // For sim sharing.

    //Make existing elements unpickable for black box, see https://phet.unfuddle.com/a#/projects/9404/tickets/by_number/3602
    public boolean fixed = false;

    public Junction( double x, double y ) {
        this.x = x;
        this.y = y;
        this.label = nextLabel++;
    }

    public String toString() {
        return "Junction_" + label + "[" + x + "," + y + "]";
    }

    public Point2D.Double getPosition() {
        return new Point2D.Double( x, y );
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void translateNoNotify( double dx, double dy ) {
        x += dx;
        y += dy;
    }

    public void notifyChanged() {
        notifyObservers();
    }

    public void translate( double dx, double dy ) {
        translateNoNotify( dx, dy );
        notifyObservers();
    }

    public void setPosition( double x, double y ) {
        this.x = x;
        this.y = y;
        notifyObservers();
    }

    public double getDistance( Junction junction ) {
        return getPosition().distance( junction.getPosition() );
    }

    public int getLabel() {
        return label;
    }

    public void delete() {
        removeAllObservers();
    }

    public void setSelected( boolean selected ) {
        this.selected = selected;
        notifyObservers();
    }

    public boolean isSelected() {
        return selected;
    }

    /**
     * This is for experimental use in MNA solution.
     *
     * @param voltage
     */
    public void setVoltage( double voltage ) {
        this.voltage = voltage;
    }

    public double getVoltage() {
        return voltage;
    }

    public void setPosition( Point2D location ) {
        setPosition( location.getX(), location.getY() );
    }

    public Shape getShape() {
        return createCircle( CCKModel.JUNCTION_RADIUS * 1.1 );
    }

    public Ellipse2D createCircle( double radius ) {
        Ellipse2D.Double circle = new Ellipse2D.Double();
        circle.setFrameFromCenter( getX(), getY(), getX() + radius, getY() + radius );
        return circle;
    }

    public IUserComponent getUserComponentID() {
        return userComponent;
    }

    // The model component ID is the same as the user component.  User component must be set for this to work correctly.
    public IModelComponent getModelComponentID() {
        return new IModelComponent() {
            @Override public String toString() {
                return getUserComponentID().toString();
            }
        };
    }

    //Make existing elements unpickable for black box, see https://phet.unfuddle.com/a#/projects/9404/tickets/by_number/3602
    public void setFixed( boolean fixed ) {
        this.fixed = fixed;
    }
}