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
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

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

    //----------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------

    public RegisterablePNode() {
    }

    /**
     *
     * @param wrappedPNode A PNode that is to be wrapped by the new instance
     */
    public RegisterablePNode( PNode wrappedPNode ) {
        addChild( wrappedPNode );
    }

    /**
     *
     * @param wrappedPNode  A PNode that is to be wrapped by the new instance
     * @param registrationPoint The registation point
     */
    public RegisterablePNode( PNode wrappedPNode, Point2D registrationPoint ) {
        addChild( wrappedPNode );
        setRegistrationPoint( registrationPoint );
    }

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
        p0.setLocation( p0.getX() + this.registrationPoint.getX(),
                        p0.getY() + this.registrationPoint.getY() );
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

    public void setTransform( AffineTransform newTransform ) {
        try {
            Point2D rp = getTransform().inverseTransform( registrationPoint, null );
            super.setTransform( newTransform );
            registrationPoint = newTransform.transform( rp, null );
        }
        catch( NoninvertibleTransformException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the spot on the canvas where the registration point will be placed
     * @return
     */
    public Point2D getOffset() {
        Point2D parentOffset = super.getOffset();
        Point2D p = new Point2D.Double( parentOffset.getX() + registrationPoint.getX() * getScale(),
                                        parentOffset.getY() + registrationPoint.getY() * getScale() );
        return p;
    }

    /**
     * Gets the x coordinate of the spot on the canvas where the registration point
     * will be placed
     * @return
     */
    public double getXOffset() {
        return getOffset().getX();
    }

    /**
     * Gets the y coordinate of the spot on the canvas where the registration point
     * will be placed
     * @return
     */
    public double getYOffset() {
        return getOffset().getY();
    }

    /**
     * Sets the place where the registration point will be placed on the canvas when the
     * RegisterablePNode is rendered.
     * @param x
     * @param y
     */
    public void setOffset( double x, double y ) {
        super.setOffset( x - registrationPoint.getX() * getScale(),
                         y - registrationPoint.getY() * getScale() );
    }

    /**
     * Sets the place where the registration point will be placed on the canvas when the
     * RegisterablePNode is rendered.
     * @param point
     */
    public void setOffset( Point2D point ) {
        setOffset( point.getX(), point.getY() );
    }

    /**
     * Prevents the graphic from moving relative to the registration point when the
     * scale changes.
     * @param scale
     */
    public void setScale( double scale ) {
        double dx = getRegistrationPoint().getX() / getScale();
        double dy = getRegistrationPoint().getY() / getScale();

        double regPtAbsX = getOffset().getX() + dx;
        double regPtAbsY = getOffset().getY() + dy;

        super.setScale( scale );
        double x = regPtAbsX - getRegistrationPoint().getX();
        double y = regPtAbsY - getRegistrationPoint().getY();
        setOffset( x, y );
    }
}
