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

import edu.colorado.phet.solublesalts.model.ion.Ion;
import edu.colorado.phet.solublesalts.model.ion.Ion;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * Lattice
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public abstract class Lattice {

    private static final double SAME_POSITION_TOLERANCE = 3;
    private static Random random = new Random();

    private Ion seed;
    private Rectangle2D bounds;

    /**
     * Returns an ion with the greates "least bound" characteristic. This characteristic is
     * defined as the ratio of unoccupied neighboring lattice sites divided by the total
     * number of neighboring sites. If more than one ion in the lattice has this same
     * characteristic value, one of them is chosen at random.
     *
     * @param ionsInLattice
     * @param orientation
     * @return
     */
    public Ion getLeastBoundIon( List ionsInLattice, double orientation ) {

        // Sanity check
        if( getSeed() == null ) {
            throw new RuntimeException( "getSeed() == null" );
        }

        // Go through all the ionsInLattice, looking for the one with the highest ratio of
        // occupied neighboring sites to possible neighboring sites (which does not include
        // sites that would be outside the bounds of the vessel
        Ion leastBoundIon = null;
        double highestOccupiedSiteRatio = 0;
        ArrayList candidateIons = new ArrayList();
        for( int i = 0; i < ionsInLattice.size(); i++ ) {
            Ion ion = (Ion)ionsInLattice.get( i );

            // If this is the seed ion and there are other ions in the lattice, skip it
            if( ion == getSeed() && ionsInLattice.size() > 1 ) {
                continue;
            }

            List neighboringSites = getNeighboringSites( ion, orientation );

            // DEBUG
            if( neighboringSites.size() == 0 ) {
//                throw new RuntimeException( "neighboringSites.size() == 0");
//                getNeighboringSites( ion, orientation );
                System.out.println( "Lattice.getLeastBoundIon: neighboringSites.size() == 0" );
                continue;
            }
            List openNeighboringSites = getOpenNeighboringSites( ion, ionsInLattice, orientation );
            double occupiedSiteRatio = (double)openNeighboringSites.size() / neighboringSites.size();
            if( occupiedSiteRatio >= highestOccupiedSiteRatio ) {
                candidateIons.add( ion );
                highestOccupiedSiteRatio = occupiedSiteRatio;
            }
        }

        // Of the ions that have the same "least bound" characteristic, choose one randomly
        if( candidateIons.size() > 0 ) {
            leastBoundIon = (Ion)candidateIons.get( random.nextInt( candidateIons.size() ) );
        }

        // Sanity check
        if( leastBoundIon == null ) {
            throw new RuntimeException( "leastBoundIon == null" );
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
        List neighboringSites = getNeighboringSites( ion, orientation );
        return getOpenNeighboringSites( neighboringSites, ionsInLattice );
    }

    /**
     * Returns a list of the lattice sites that are neighboring a specified ion that are
     * not occupied.
     *
     * @param neighboringSites
     * @param ionsInLattice
     * @return
     */
    public List getOpenNeighboringSites( List neighboringSites, List ionsInLattice ) {
        List results = new ArrayList();
        for( int i = 0; i < neighboringSites.size(); i++ ) {
            Point2D neighboringSite = (Point2D)neighboringSites.get( i );
            if( !isSiteOccupied( neighboringSite, ionsInLattice ) ) {
                results.add( neighboringSite );
            }
        }
        return results;
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
        List openSites = new ArrayList();

        // Get a list of all the open sites next to ions of opposite polarity to
        // the one we are adding
        for( int i = 0; i < ionsInLattice.size(); i++ ) {
            Ion testIon = (Ion)ionsInLattice.get( i );
            if( testIon.getCharge() * ion.getCharge() < 0 ) {
                List ns = getOpenNeighboringSites( testIon, ionsInLattice, orientation );
                openSites.addAll( ns );
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

    /**
     * Tells if a site in the lattice is occupied.
     *
     * @param site
     * @param ionsInLattice
     * @return
     */
    protected boolean isSiteOccupied( Point2D site, List ionsInLattice ) {
        boolean occupied = false;
        for( int j = 0; j < ionsInLattice.size() && !occupied; j++ ) {
            Ion testIon = (Ion)ionsInLattice.get( j );
            if( Math.abs( site.getX() - testIon.getPosition().getX() ) < SAME_POSITION_TOLERANCE
                && Math.abs( site.getY() - testIon.getPosition().getY() ) < SAME_POSITION_TOLERANCE ) {
                occupied = true;
            }
        }
        return occupied;
    }

    //----------------------------------------------------------------
    // Abstract methods
    //----------------------------------------------------------------

    /**
     * Returns the positions of all sites, occupied or not, in the lattice neighboring a specified Ion.
     *
     * @param ion
     * @param orientation
     * @return
     */
    abstract protected List getNeighboringSites( Ion ion, double orientation );

    /**
     * All concrete subclasses are required to implement clone()
     *
     * @return
     */
    abstract public Object clone();

    //----------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------

    /**
     * @param seedIon
     */
    void setSeed( Ion seedIon ) {
        // Sanity check
        Ion oldSeed = null;
        if( seed != null && seed != seedIon ) {
            System.out.println( "seed != null");
            oldSeed = seed;
//            throw new RuntimeException( "seed != null");
        }
        seed = seedIon;
        seed.notifyObservers();
        if( oldSeed != null ) {
            oldSeed.notifyObservers();
        }
    }

    protected Ion getSeed() {
        return seed;
    }

    /**
     * @param bounds
     */
    void setBounds( Rectangle2D bounds ) {
        this.bounds = bounds;
    }

    protected Rectangle2D getBounds() {
        return bounds;
    }
}


