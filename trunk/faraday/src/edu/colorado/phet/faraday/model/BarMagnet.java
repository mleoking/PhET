/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.model;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;


/**
 * BarMagnet
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BarMagnet extends AbstractMagnet {

    // Arbitrary positive "fudge factors".
    // These should be adjusted so that transitions between inside and outside
    // the magnet don't result in abrupt changes in the magnetic field.
    private static final double C_INSIDE  = 1.0;
    private static final double C_OUTSIDE = 1.0;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BarMagnet() {
        super();
    }
    
    //----------------------------------------------------------------------------
    // AbstractMagnet implementation
    //----------------------------------------------------------------------------
    
    /**
     * @see edu.colorado.phet.faraday.model.IMagnet#getStrengthVector(java.awt.geom.Point2D)
     */
    public AbstractVector2D getStrength( Point2D p ) {

        // Assumptions:
        // (1) The magnet's location is at its physical center.
        // (2) The magnet's width > height.
        
        // Bounds that define "inside" the magnet, before rotating the magnet. 
        double x = getX() - getWidth()/2 + getHeight()/2;  // south dipole
        double y = getY() - getHeight() / 2;
        double width = getWidth() - getHeight();
        double height = getHeight();
        Rectangle2D bounds = new Rectangle2D.Double( x, y, width, height );
        
        // Rotate the point to account for magnet rotation.
        double radians = Math.toRadians( -(getDirection()) );
        AffineTransform rotation = AffineTransform.getRotateInstance( radians );
//        Point2D p2 = rotation.transform( p, null );
        Point2D p2 = p; //DEBUG
        
        // Choose the appropriate algorithm based on
        // whether the point is inside or outside the magnet.
        AbstractVector2D vB = null;
        if ( bounds.contains( p2 ) )  {
            vB = getStrengthInside( p2 );
        }
        else
        {
            vB = getStrengthOutside( p2 );
        }
        
        // Adjust the field vector to match the magnet's direction.
        //DEBUG vB = vB.getRotatedInstance( Math.toRadians( getDirection() ) );

        return vB;
    }
    
    private AbstractVector2D getStrengthInside( Point2D p ) {
        return new ImmutableVector2D.Double( C_INSIDE, 0 );
    }
    
    private AbstractVector2D getStrengthOutside( Point2D p ) {
        
        // Assumptions: 
        // (1) The magnet's location is at its physical center.
        // (2) Point p has been adjusted for magnet rotation.
        // (3) The magnet's width > height.
        
        // Dipole locations, before rotating the magnet.
        Point2D pN = new Point2D.Double( getX() + getWidth()/2 - getHeight()/2, getY() );
        Point2D pS = new Point2D.Double( getX() - getWidth()/2 + getHeight()/2, getY() );
        
        // Distance between the dipoles
        double L = pS.distance( pN );
        
        // Distances between the dipoles and the point.
        double rN = pN.distance( p );
        double rS = pS.distance( p );
        
        System.out.println( "magnet=(" + getX() + "," + getY() + ") " + getWidth() + "x" + getHeight() + " " +
                            "p=(" + p.getX() + "," + p.getY() + ") " +
                            "pN=(" + pN.getX() + "," + pN.getY() + ") " +
                            "pS=(" + pS.getX() + "," + pS.getY() + ") " +
                            "rN=" + rN + " " +
                            "rS=" + rS + " "
                          );
        
        // North dipole vector.
        double mN = +(C_OUTSIDE / Math.pow( rN, 3.0 ));
        double xN = mN * (p.getX() - L/2);
        double yN = mN * p.getY();
        AbstractVector2D vBN = new ImmutableVector2D.Double( xN, yN );
        
        // South dipole vector.
        double mS = -(C_OUTSIDE / Math.pow( rS, 3.0 ));
        double xS = mS * (p.getX() + L/2);
        double yS = mS * p.getY();
        AbstractVector2D vBS = new ImmutableVector2D.Double( xS, yS );
        
        // Add north and south vectors.
        AbstractVector2D vBTotal = vBN.getAddedInstance( vBS );
            
        return vBTotal;
    }
}
