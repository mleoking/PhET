/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.piccolo;

import edu.umd.cs.piccolo.PNode;

import java.awt.geom.Point2D;

/**
 * RegisterablePNode
 * <p>
 * A PNode that can have a specified registration point. This is the point within the local reference frame
 * of the PNode that will be placed at a specified point by a call to setOffset(). If, for example, you want to
 * center and image at a specified point you would do the following:
 * <code>
 *      RegisteredPNode rpn = new RegisteredPNode();
 *      PImage pi = PImageFactory.create( "my-image.png" );
 *      rpn.addChild( pi );
 *      rpn.setRegistrationPoint( pi.getWidth()/2, pi.getHeight()/2 );
 *      rpn.setOffset( location );
 * </code>
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class RegisterablePNode extends PNode {
    private Point2D registrationPoint = new Point2D.Double();

    /**
     * Returns the registration point
     * @return The registration point
     */
    public Point2D getRegistrationPoint() {
        return registrationPoint;
    }

    /**
     * Sets the registraion point. The origin of this point's reference frame is the upper left corner
     * of the PNode.
     *
     * @param registrationPoint     The registration point
     */
    public void setRegistrationPoint( Point2D registrationPoint ) {
        // We need to find the un-registered location of the PNode in case the registration point is being
        // set more than once. Otherwise, registration point offsets become cummulative
        Point2D p0 = getOffset();
        p0.setLocation( p0.getX() + this.registrationPoint.getX(), p0.getY() + this.registrationPoint.getY());
        this.registrationPoint = registrationPoint;
        setOffset( p0 );
    }

    /**
     * Sets the registraion point. The origin of this point's reference frame is the upper left corner
     * of the PNode.
     *
     * @param x The x coordinate of the registration point
     * @param y The y coordinate of the registration point
     */
    public void setRegistrationPoint( double x, double y ) {
        Point2D rp = new Point2D.Double( x, y );
        setRegistrationPoint( rp );
    }

    /**
     * Sets the place where the registration point will be placed on the canvas when the
     * RegisterablePNode is rendered.
     * @param point
     */
    public void setOffset( Point2D point ) {
        super.setOffset( point.getX() - registrationPoint.getX(),
                         point.getY() - registrationPoint.getY());
    }

    /**
     * Gets the spot on the canvas where the registration point will be placed
     * @return
     */
    public Point2D getOffset() {
        Point2D parentOffset = super.getOffset();
        Point2D p = new Point2D.Double( parentOffset.getX() + registrationPoint.getX(),
                                        parentOffset.getY() + registrationPoint.getY() );
        return p;
    }

    /**
     * Sets the place where the registration point will be placed on the canvas when the
     * RegisterablePNode is rendered.
     * @param x
     * @param y
     */
    public void setOffset( double x, double y ) {
        super.setOffset( x, y );
    }
}
