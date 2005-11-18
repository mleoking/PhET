/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model.crystal;

import edu.colorado.phet.solublesalts.model.Ion;
import edu.colorado.phet.solublesalts.model.Sodium;
import edu.colorado.phet.solublesalts.model.Chloride;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * PlainCubicLattice
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PlainCubicLattice implements Lattice {
    private static final Random random = new Random( System.currentTimeMillis() );

    private double spacing;
    private Ion seed;
    private Rectangle2D bounds;

    /**
     *
     * @param spacing
     */
    public PlainCubicLattice( double spacing ) {
        this.spacing = spacing;
    }

    public void setSeed( Ion seed ) {
        this.seed = seed;
    }

    public void setBounds( Rectangle2D bounds ) {
        this.bounds = bounds;
    }

    /**
     * Returns the location of the open lattice point that is nearest to a specified ion
     *
     * @param ion
     * @param ionsInLattice
     * @param orientation
     * @return
     */
    public Point2D getNearestOpenSite( Ion ion, List ionsInLattice, double orientation ) {
        Point2D p = null;
        List openSites = new ArrayList();

        // Get a list of all the open sites next to ions of opposite polarity to
        // the one we are adding
        for( int i = 0; i < ionsInLattice.size(); i++ ) {
            Ion testIon = (Ion)ionsInLattice.get( i );
            if( testIon.getCharge() * ion.getCharge() < 0 ) {
                List ns = getNeighboringSites( testIon.getPosition(), orientation );
                for( int j = 0; j < ns.size(); j++ ) {
                    Point2D n = (Point2D)ns.get( j );
                    boolean isOccupied = false;
                    for( int k = 0; k < ionsInLattice.size() && !isOccupied; k++ ) {
                        Ion ion1 = (Ion)ionsInLattice.get( k );
                        if( n.equals( ion1.getPosition() ) ) {
                            isOccupied = true;
                        }
                    }
                    if( !isOccupied ) {
                        openSites.add( n );
                    }
                }
            }
        }

        // Find the nearest of the open sites to the input parameter point
        double dMin = Double.MAX_VALUE;
        Point2D closestPt = null;
        for( int i = 0; i < openSites.size(); i++ ) {
            Point2D testPt = (Point2D)openSites.get( i );
            double d = ion.getPosition().distance( testPt );
            if( d < dMin ) {
                closestPt = testPt;
                dMin = d;
            }
        }
        return closestPt;
    }

    private List getNeighboringSites( Point2D p, double orientation ) {
        List sites = new ArrayList();
        for( int i = 0; i < 4; i++ ) {
            double x = p.getX() + spacing * Math.cos( i * Math.PI / 2 + orientation );
            double y = p.getY() + spacing * Math.sin( i * Math.PI / 2 + orientation );
            Point2D pNew = new Point2D.Double( x, y );
            if( bounds.contains( pNew ) ) {
                sites.add( pNew );
            }
        }
        return sites;
    }

    /**
     * Returns the ion with the greatest number of unoccupied neighboring lattice sites. The
     * seed ion is not eligible for consideration.
     *
     * @param ionsInLattice
     * @param orientation
     * @return
     */
    public Ion getLeastBoundIon( List ionsInLattice, double orientation ) {
        // Go through all the ionsInLattice, looking for the one with the highest ratio of
        // occupied neighboring sites to possible neighboring sites (which does not include
        // sites that would be outside the bounds of the vessel
        Ion leastBoundIon = null;
        int greatestNumUnccupiedNeightborSites = 0;
        double highestOccupiedSiteRatio = 0;
        for( int i = 0; i < ionsInLattice.size(); i++ ) {
            Ion ion = (Ion)ionsInLattice.get( i );

            // If this is the seed ion, skip it
            if( ion == seed && ionsInLattice.size() > 1 ) {
                continue;
            }

            List ns = getNeighboringSites( ion.getPosition(), orientation );
            if( ns.size() == 0 ) {
                System.out.println( "ns = " + ns );
                getNeighboringSites( ion.getPosition(), orientation );
            }
            int numUnoccupiedNeighborSites = 0;
            for( int j = 0; j < ns.size(); j++ ) {
                Point2D n = (Point2D)ns.get( j );
                boolean isOccupied = false;
                for( int k = 0; k < ionsInLattice.size() && !isOccupied; k++ ) {
                    Ion ion1 = (Ion)ionsInLattice.get( k );
                    if( n.equals( ion1.getPosition() ) ) {
                        isOccupied = true;
                    }
                }
                if( !isOccupied ) {
                    numUnoccupiedNeighborSites++;
                }
            }
            double occupiedSiteRatio = (double)numUnoccupiedNeighborSites / ns.size();
            if( occupiedSiteRatio >= highestOccupiedSiteRatio ) {
                // Don't always choose the same ion
                if( occupiedSiteRatio == highestOccupiedSiteRatio
                    && random.nextBoolean() ) {
                    leastBoundIon = ion;
                }
                else {
                    leastBoundIon = ion;
                }
                highestOccupiedSiteRatio = occupiedSiteRatio;
            }
        }

        if( leastBoundIon == null ) {
//            	throw new RuntimeException("leastBoundIon == null");
        }
        return leastBoundIon;
    }

    /**
     * Returns a list of the lattice sites that are neighboring a specified ion that are
     * not occupied.
     *
     * @param ion
     * @param ionsInLattice
     * @param orientation
     * @return
     */
    public List getOpenNeighboringSites( Ion ion, List ionsInLattice, double orientation ) {
        List results = new ArrayList();
        List neighboringSites = getNeighboringSites( ion.getPosition(), orientation );
        for( int i = 0; i < neighboringSites.size(); i++ ) {
            Point2D neighboringSite = (Point2D)neighboringSites.get( i );
            boolean occupied = false;
            for( int j = 0; j < ionsInLattice.size() && !occupied; j++ ) {
                Ion testIon = (Ion)ionsInLattice.get( j );
                if( neighboringSite.equals( testIon.getPosition() ) ) {
                    occupied = true;
                }
            }
            if( !occupied ) {
                results.add( neighboringSite );
            }
        }
        return results;
    }


    public static void main( String[] args ) {
        Ion a = new Sodium();
        a.setPosition( 300, 400 );
        Crystal l = new Crystal( a, new Rectangle2D.Double( 0, 0, 500, 500 ) );
        Ion b = new Chloride();
        b.setPosition( new Point2D.Double( 290, 400 ) );
        l.addIon( b );

        Ion b2 = new Chloride();
        b2.setPosition( new Point2D.Double( 290, 400 ) );
        l.addIon( b2 );

        printLattice( l );

//        l.releaseIon( 10 );

        printLattice( l );

//        PlainCubicForm pcf = (PlainCubicForm)l.getForm();
//        List ap = pcf.getNeighboringSites( a.getPosition(), Math.PI / 4 );
//        for( int i = 0; i < ap.size(); i++ ) {
//            Point2D point2D = (Point2D)ap.get(i);
//            System.out.println( "point2D = " + point2D );
//        }
    }

    private static void printLattice( Crystal l ) {
        List ions = l.getIons();
        for( int i = 0; i < ions.size(); i++ ) {
            Ion ion = (Ion)ions.get( i );
            System.out.println( "ion.getPosition() = " + ion.getPosition() );
        }
    }
}
