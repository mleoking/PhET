/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

/**
 * Molecule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Lattice extends Body implements Binder {

    //================================================================
    // Class data and methods
    //================================================================

    private static EventChannel instanceLifetimeEventChannel = new EventChannel( InstanceLifetimeListener.class );
    protected static InstanceLifetimeListener instanceLifetimeListenerProxy =
            (InstanceLifetimeListener)instanceLifetimeEventChannel.getListenerProxy();
    private static Random random = new Random( System.currentTimeMillis() );
    private static double dissociationLikelihood;
    private Rectangle2D bounds;

    public static void setDissociationLikelihood( double dissociationLikelihood ) {
        Lattice.dissociationLikelihood = dissociationLikelihood;
    }

    public static class InstanceLifetimeEvent extends EventObject {
        public InstanceLifetimeEvent( Object source ) {
            super( source );
        }

        public Lattice getInstance() {
            return (Lattice)getSource();
        }
    }

    public static interface InstanceLifetimeListener extends EventListener {
        void instanceCreated( InstanceLifetimeEvent event );

        void instanceDestroyed( InstanceLifetimeEvent event );
    }

    public static void addInstanceLifetimeListener( InstanceLifetimeListener listener ) {
        instanceLifetimeEventChannel.addListener( listener );
    }

    public static void removeInstanceLifetimeListener( InstanceLifetimeListener listener ) {
        instanceLifetimeEventChannel.removeListener( listener );
    }

    //================================================================
    // Instance data and methods
    //================================================================

    private Point2D cm = new Point2D.Double();
    private ArrayList ions = new ArrayList();
    // The angle that the lattice is oriented at, relative to the x axis
    private double orientation;
    private Atom seed;
    private Form form = new PlainCubicForm( 27 );

    //----------------------------------------------------------------
    // Lifecycle
    //----------------------------------------------------------------

    /**
     * @param ion    The ion that seeds the lattice
     * @param bounds Growth bounds for the lattice
     */
    public Lattice( Ion ion, Rectangle2D bounds ) {
        this.bounds = bounds;
        addIon( ion );
        updateCm();
        instanceLifetimeListenerProxy.instanceCreated( new InstanceLifetimeEvent( this ) );
    }

    public void leaveModel() {
        instanceLifetimeListenerProxy.instanceDestroyed( new InstanceLifetimeEvent( this ) );
    }

    public Point2D getCM() {
        return cm;
    }

    public double getMomentOfInertia() {
        throw new RuntimeException( "not implemented " );
    }

    private void updateCm() {
        cm.setLocation( 0, 0 );
        for( int i = 0; i < ions.size(); i++ ) {
            Atom atom = (Atom)ions.get( i );
            cm.setLocation( cm.getX() + atom.getPosition().getX(),
                            cm.getY() + atom.getPosition().getY() );
        }
        cm.setLocation( cm.getX() / ions.size(),
                        cm.getY() / ions.size() );
    }

    //----------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------

    public Atom getSeed() {
        return seed;
    }

    protected ArrayList getIons() {
        return ions;
    }

    public double getDissociationLikelihood() {
        return dissociationLikelihood;
    }

    public List getOccupiedSites() {
        List l = new ArrayList();
        for( int i = 0; i < ions.size(); i++ ) {
            Atom atom = (Atom)ions.get( i );
            l.add( atom.getPosition() );
        }
        return l;
    }

    //----------------------------------------------------------------
    // Lattice building
    //----------------------------------------------------------------

    public void addIon( Ion ion ) {
        Point2D nearestOpenLatticePoint = null;
        switch( getIons().size() ) {
            case 0:
                seed = ion;
                nearestOpenLatticePoint = ion.getPosition();
                break;
            case 1:
                orientation = Math.atan2( ion.getPosition().getY() - seed.getPosition().getY(),
                                          ion.getPosition().getX() - seed.getPosition().getX() );
                nearestOpenLatticePoint = form.getNearestOpenSite( ion, ions, orientation );
                break;
            default:
                nearestOpenLatticePoint = form.getNearestOpenSite( ion, ions, orientation );
        }

        if( nearestOpenLatticePoint != null ) {
            ion.setPosition( nearestOpenLatticePoint );
            ions.add( ion );
            ion.bindTo( this );
            updateCm();
        }
    }

    /**
     * Releases an ion from the lattice.
     *
     * @param dt
     */
    private void releaseIon( double dt ) {

        Ion ionToRelease = form.getLeastBoundIon( getIons(), orientation );
        Vector2D v = determineReleaseVelocity( ionToRelease );
        ionToRelease.setVelocity( v );
        getIons().remove( ionToRelease );
        ionToRelease.unbindFrom( this );
        ionToRelease.stepInTime( dt );

        // If there aren't any ions left in the lattice, the lattice should be removed
        // from the model
        if( getIons().size() == 0 ) {
            instanceLifetimeListenerProxy.instanceDestroyed( new InstanceLifetimeEvent( this ) );
        }
    }

    private Vector2D determineReleaseVelocity( Ion ionToRelease ) {
        // Get the unoccupied sites around the ion being release
        List openSites = form.getOpenNeighboringSites( ionToRelease, ions, orientation );
        double sumX = 0, sumY = 0;
        for( int i = 0; i < openSites.size(); i++ ) {
            Point2D point2D = (Point2D)openSites.get( i );
            sumX += point2D.getX() - ionToRelease.getPosition().getX();
            sumY += point2D.getY() - ionToRelease.getPosition().getY();
        }

        if( sumX == 0 && sumY == 0 ) {
            sumX = random.nextDouble();
            sumY = random.nextDouble();
            System.out.println( "Lattice.determineReleaseVelocity:  release velocity = 0" );
//            throw new RuntimeException( "release velocity = (0:0)" );
        }
        Vector2D releaseVelocity = new Vector2D.Double( sumX / openSites.size(),
                                                        sumY / openSites.size() );
        releaseVelocity.normalize().scale( ionToRelease.getVelocity().getMagnitude() );
        return releaseVelocity;
    }

    //----------------------------------------------------------------
    // Time-dependent behavior
    //----------------------------------------------------------------

    /**
     * If the lattice choses to dissociate, it releases its bound ions and removes
     * itself from the model
     *
     * @param dt
     */
    public void stepInTime( double dt ) {
        if( random.nextDouble() < dissociationLikelihood ) {
            releaseIon( dt );
        }
        else {
            super.stepInTime( dt );
        }
    }

    //================================================================
    // Inner classes
    //================================================================

    interface Form {
        Point2D getNearestOpenSite( Ion ion, List ionsInLattice, double orientation );

        Ion getLeastBoundIon( List ions, double orientation );

        List getOpenNeighboringSites( Ion ion, List ionsInLattice, double orientation );
    }

    class PlainCubicForm implements Form {
        private double spacing;

        public PlainCubicForm( double spacing ) {
            this.spacing = spacing;
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
         * Returns the ion with the greatest number of unoccupied neighboring lattice sites
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
                List ns = getNeighboringSites( ion.getPosition(), orientation );
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
                for( int j = 0; j < ionsInLattice.size() & !occupied; j++ ) {
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
    }


    public static void main( String[] args ) {
//        Lattice l = new Lattice();
//        Ion a = new Sodium();
//        l.addIon( a );
//        Ion b = new Sodium();
//        b.setPosition( new Point2D.Double( 15, 0 ) );
//        l.addIon( b );
//        PlainCubicForm pcf = new PlainCubicForm( 15 );
//        Point2D[] ap = pcf.getNeighboringSites( a.getPosition(), Math.PI / 4 );
//        for( int i = 0; i < ap.length; i++ ) {
//            Point2D point2D = ap[i];
//            System.out.println( "point2D = " + point2D );
//        }


//        Point2D p = pcf.getNearestOpenSite( new Point2D.Double( 12, 0 ), l.getOccupiedSites(), 0 );
//        System.out.println( "p = " + p );
    }
}
