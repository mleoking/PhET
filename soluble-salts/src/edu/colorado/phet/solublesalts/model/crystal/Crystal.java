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

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.mechanics.Body;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.model.*;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

/**
 * Lattice
 * <p>
 * A lattice is a crystal of ions. It has a Form that defines how it is constructed.
 * <p>
 * The lattice has a seed ion. The lattice's position is that of it's seed ion.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Crystal extends Body implements Binder {

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
        Crystal.dissociationLikelihood = dissociationLikelihood;
    }

    public static class InstanceLifetimeEvent extends EventObject {
        public InstanceLifetimeEvent( Object source ) {
            super( source );
        }

        public Crystal getInstance() {
            return (Crystal)getSource();
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
//    private Lattice lattice = new PlainCubicLattice( Sodium.RADIUS + Chloride.RADIUS ); // Sodium radius + Chloride radius
    private Lattice lattice;
    // The list of ions that cannot be bound to this lattice at this time
    private Vector noBindList = new Vector();

    //----------------------------------------------------------------
    // Lifecycle
    //----------------------------------------------------------------

    /**
     * @param ion    The ion that seeds the lattice
     * @param bounds Growth bounds for the lattice
     */
    public Crystal( Ion ion, Rectangle2D bounds, Lattice lattice ) {
        this.bounds = bounds;
        this.lattice = lattice;
        lattice.setBounds( bounds );
        addIon( ion );
        updateCm();
        instanceLifetimeListenerProxy.instanceCreated( new InstanceLifetimeEvent( this ) );
    }

    public Point2D getPosition() {
        return seed.getPosition();
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

    public ArrayList getIons() {
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

    public void setBounds( Rectangle2D bounds ) {
        this.bounds = bounds;
        lattice.setBounds( bounds );
    }

    //----------------------------------------------------------------
    // Lattice building
    //----------------------------------------------------------------

    /**
     * Returns true if the ion was added. If there wasn't a place to put it in
     * the lattice, returns false
     */
    public boolean addIon( Ion ion ) {

        if( noBindList.contains( ion ) ) {
            return false;
        }

        Point2D placeToPutIon = null;
        switch( getIons().size() ) {
            case 0:
                seed = ion;
                lattice.setSeed( ion );
                placeToPutIon = ion.getPosition();
                break;
            case 1:
                orientation = Math.atan2( ion.getPosition().getY() - seed.getPosition().getY(),
                                          ion.getPosition().getX() - seed.getPosition().getX() );
                placeToPutIon = lattice.getNearestOpenSite( ion, ions, orientation );
                break;
            default:
                placeToPutIon = lattice.getNearestOpenSite( ion, ions, orientation );
        }

        if( placeToPutIon != null ) {
            ion.setPosition( placeToPutIon );
            ions.add( ion );
            ion.bindTo( this );
            updateCm();
        }

        return placeToPutIon != null;
    }

    /**
     * This method is only to be used when a client remves an ion from the
     * lattice. It is not to be used when the lattice itself releases the ion
     *
     * @param ion
     */
    public void removeIon( Ion ion ) {
        getIons().remove( ion );

        // If there aren't any ions left in the lattice, the lattice should be removed
        // from the model
        if( getIons().size() == 0 ) {
            instanceLifetimeListenerProxy.instanceDestroyed( new InstanceLifetimeEvent( this ) );
        }
    }

    /**
     * Releases an ion from the lattice.
     *
     * @param dt
     */
    private void releaseIon( double dt ) {
        Ion ionToRelease = lattice.getLeastBoundIon( getIons(), orientation );

        if( ionToRelease == null ) {
            System.out.println( "No ion found to release!!!!!" );
            return;
        }

        Thread t = new Thread( new NoBindTimer( ionToRelease ) );
        t.start();

        Vector2D v = determineReleaseVelocity( ionToRelease );
        ionToRelease.setVelocity( v );
        removeIon( ionToRelease );
        ionToRelease.unbindFrom( this );
        ionToRelease.stepInTime( dt );
    }

    /**
     * Determine the velocity an ion that is about to be release should have
     *
     * @param ionToRelease
     * @return
     */
    private Vector2D determineReleaseVelocity( Ion ionToRelease ) {

        // Get the unoccupied sites around the ion being release
        List openSites = lattice.getOpenNeighboringSites( ionToRelease, ions, orientation );
        double sumX = 0, sumY = 0;
        double maxAngle = Double.MIN_VALUE;
        double minAngle = Double.MAX_VALUE;

        if( openSites.size() > 1 ) {
            for( int i = 0; i < openSites.size() - 1; i++ ) {
                Point2D p1 = (Point2D)openSites.get( i );
                Vector2D.Double v1 = new Vector2D.Double( p1.getX() - ionToRelease.getPosition().getX(),
                                                          p1.getY() - ionToRelease.getPosition().getY() );
                double angle1 = ( v1.getAngle() + Math.PI * 2 ) % ( Math.PI * 2 );
                for( int j = i + 1; j < openSites.size(); j++ ) {

                    // If the two open sites we're now looking at are adjacent, set the velocity to point between them
                    Point2D p2 = (Point2D)openSites.get( j );
                    Vector2D.Double v2 = new Vector2D.Double( p2.getX() - ionToRelease.getPosition().getX(),
                                                              p2.getY() - ionToRelease.getPosition().getY() );
                    double angle2 = ( v2.getAngle() + Math.PI * 2 ) % ( Math.PI * 2 );
                    if( Math.abs( angle2 - angle1 ) < Math.PI ) {
                        double angle = random.nextDouble() * ( angle2 - angle1 ) + angle1;
                        Vector2D releaseVelocity = new Vector2D.Double( ionToRelease.getVelocity().getMagnitude(), 0 ).rotate( angle );
                        return releaseVelocity;
                    }
                }
            }

            Point2D p1 = (Point2D)openSites.get( 0 );
            Vector2D.Double v1 = new Vector2D.Double( p1.getX() - ionToRelease.getPosition().getX(),
                                                      p1.getY() - ionToRelease.getPosition().getY() );
            double angle1 = ( v1.getAngle() + Math.PI * 2 ) % ( Math.PI * 2 );
            Point2D p2 = (Point2D)openSites.get( 1 );
            Vector2D.Double v2 = new Vector2D.Double( p2.getX() - ionToRelease.getPosition().getX(),
                                                      p2.getY() - ionToRelease.getPosition().getY() );
            double angle2 = ( v2.getAngle() + Math.PI * 2 ) % ( Math.PI * 2 );
            double angle = random.nextDouble() * ( angle2 - angle1 ) + angle1;
            Vector2D releaseVelocity = new Vector2D.Double( ionToRelease.getVelocity().getMagnitude(), 0 ).rotate( angle );
            return releaseVelocity;
        }

        for( int i = 0; i < openSites.size(); i++ ) {
            Point2D point2D = (Point2D)openSites.get( i );
            Vector2D.Double v = new Vector2D.Double( point2D.getX() - ionToRelease.getPosition().getX(),
                                                     point2D.getY() - ionToRelease.getPosition().getY() );
            double angle = ( v.getAngle() + Math.PI * 2 ) % ( Math.PI * 2 );
            maxAngle = angle > maxAngle ? angle : maxAngle;
            minAngle = angle < minAngle ? angle : minAngle;
        }

        double angle = random.nextDouble() * ( maxAngle - minAngle ) + minAngle;
        Vector2D releaseVelocity = new Vector2D.Double( ionToRelease.getVelocity().getMagnitude(), 0 ).rotate( angle );
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

        // Only dissociate if the lattice is in the water
        if( bounds.contains( getPosition() ) && random.nextDouble() < dissociationLikelihood ) {
            releaseIon( dt );
        }
        super.stepInTime( dt );
    }


    //================================================================
    // Inner classes
    //================================================================

    /**
     * An agent that keeps an ion from being bound to this lattice for a
     * specified period of time
     */
    private class NoBindTimer implements Runnable {
        private Ion ion;

        public NoBindTimer( Ion ion ) {
            this.ion = ion;
            noBindList.add( ion );
        }

        public void run() {
            try {
                Thread.sleep( SolubleSaltsConfig.RELEASE_ESCAPE_TIME );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
            noBindList.remove( ion );
        }
    }

//
//    //----------------------------------------------------------------
//    // Interface and implementation(s) for the structure of a lattice
//    //----------------------------------------------------------------
//
//    interface Lattice {
//        /**
//         * Returns the location of the open lattice point that is nearest to a specified ion
//         *
//         * @param ion
//         * @param ionsInLattice
//         * @param orientation
//         * @return
//         */
//        Point2D getNearestOpenSite( Ion ion, List ionsInLattice, double orientation );
//
//        /**
//         * Returns the ion with the greatest number of unoccupied neighboring lattice sites. The
//         * seed ion is not eligible for consideration.
//         *
//         * @param ionsInLattice
//         * @param orientation
//         * @return
//         */
//        Ion getLeastBoundIon( List ionsInLattice, double orientation );
//
//        /**
//         * Returns a list of the lattice sites that are neighboring a specified ion that are
//         * not occupied.
//         *
//         * @param ion
//         * @param ionsInLattice
//         * @param orientation
//         * @return
//         */
//        List getOpenNeighboringSites( Ion ion, List ionsInLattice, double orientation );
//    }
//
//    /**
//     * A simple lattice form laid out on a plain grid
//     */
//    class PlainCubicLattice implements Lattice {
//        private double spacing;
//
//        public PlainCubicLattice( double spacing ) {
//            this.spacing = spacing;
//        }
//
//        /**
//         * Returns the location of the open lattice point that is nearest to a specified ion
//         *
//         * @param ion
//         * @param ionsInLattice
//         * @param orientation
//         * @return
//         */
//        public Point2D getNearestOpenSite( Ion ion, List ionsInLattice, double orientation ) {
//            Point2D p = null;
//            List openSites = new ArrayList();
//
//            // Get a list of all the open sites next to ions of opposite polarity to
//            // the one we are adding
//            for( int i = 0; i < ionsInLattice.size(); i++ ) {
//                Ion testIon = (Ion)ionsInLattice.get( i );
//                if( testIon.getCharge() * ion.getCharge() < 0 ) {
//                    List ns = getNeighboringSites( testIon.getPosition(), orientation );
//                    for( int j = 0; j < ns.size(); j++ ) {
//                        Point2D n = (Point2D)ns.get( j );
//                        boolean isOccupied = false;
//                        for( int k = 0; k < ionsInLattice.size() && !isOccupied; k++ ) {
//                            Ion ion1 = (Ion)ionsInLattice.get( k );
//                            if( n.equals( ion1.getPosition() ) ) {
//                                isOccupied = true;
//                            }
//                        }
//                        if( !isOccupied ) {
//                            openSites.add( n );
//                        }
//                    }
//                }
//            }
//
//            // Find the nearest of the open sites to the input parameter point
//            double dMin = Double.MAX_VALUE;
//            Point2D closestPt = null;
//            for( int i = 0; i < openSites.size(); i++ ) {
//                Point2D testPt = (Point2D)openSites.get( i );
//                double d = ion.getPosition().distance( testPt );
//                if( d < dMin ) {
//                    closestPt = testPt;
//                    dMin = d;
//                }
//            }
//            return closestPt;
//        }
//
//        private List getNeighboringSites( Point2D p, double orientation ) {
//            List sites = new ArrayList();
//            for( int i = 0; i < 4; i++ ) {
//                double x = p.getX() + spacing * Math.cos( i * Math.PI / 2 + orientation );
//                double y = p.getY() + spacing * Math.sin( i * Math.PI / 2 + orientation );
//                Point2D pNew = new Point2D.Double( x, y );
//                if( bounds.contains( pNew ) ) {
//                    sites.add( pNew );
//                }
//            }
//            return sites;
//        }
//
//        /**
//         * Returns the ion with the greatest number of unoccupied neighboring lattice sites. The
//         * seed ion is not eligible for consideration.
//         *
//         * @param ionsInLattice
//         * @param orientation
//         * @return
//         */
//        public Ion getLeastBoundIon( List ionsInLattice, double orientation ) {
//            // Go through all the ionsInLattice, looking for the one with the highest ratio of
//            // occupied neighboring sites to possible neighboring sites (which does not include
//            // sites that would be outside the bounds of the vessel
//            Ion leastBoundIon = null;
//            int greatestNumUnccupiedNeightborSites = 0;
//            double highestOccupiedSiteRatio = 0;
//            for( int i = 0; i < ionsInLattice.size(); i++ ) {
//                Ion ion = (Ion)ionsInLattice.get( i );
//
//                // If this is the seed ion, skip it
//                if( ion == seed && ionsInLattice.size() > 1 ) {
//                    continue;
//                }
//
//                List ns = getNeighboringSites( ion.getPosition(), orientation );
//                if( ns.size() == 0 ) {
//                    System.out.println( "ns = " + ns );
//                    getNeighboringSites( ion.getPosition(), orientation );
//                }
//                int numUnoccupiedNeighborSites = 0;
//                for( int j = 0; j < ns.size(); j++ ) {
//                    Point2D n = (Point2D)ns.get( j );
//                    boolean isOccupied = false;
//                    for( int k = 0; k < ionsInLattice.size() && !isOccupied; k++ ) {
//                        Ion ion1 = (Ion)ionsInLattice.get( k );
//                        if( n.equals( ion1.getPosition() ) ) {
//                            isOccupied = true;
//                        }
//                    }
//                    if( !isOccupied ) {
//                        numUnoccupiedNeighborSites++;
//                    }
//                }
//                double occupiedSiteRatio = (double)numUnoccupiedNeighborSites / ns.size();
//                if( occupiedSiteRatio >= highestOccupiedSiteRatio ) {
//                    // Don't always choose the same ion
//                    if( occupiedSiteRatio == highestOccupiedSiteRatio
//                        && random.nextBoolean() ) {
//                        leastBoundIon = ion;
//                    }
//                    else {
//                        leastBoundIon = ion;
//                    }
//                    highestOccupiedSiteRatio = occupiedSiteRatio;
//                }
//            }
//
//            if( leastBoundIon == null ) {
////            	throw new RuntimeException("leastBoundIon == null");
//            }
//            return leastBoundIon;
//        }
//
//        /**
//         * Returns a list of the lattice sites that are neighboring a specified ion that are
//         * not occupied.
//         *
//         * @param ion
//         * @param ionsInLattice
//         * @param orientation
//         * @return
//         */
//        public List getOpenNeighboringSites( Ion ion, List ionsInLattice, double orientation ) {
//            List results = new ArrayList();
//            List neighboringSites = getNeighboringSites( ion.getPosition(), orientation );
//            for( int i = 0; i < neighboringSites.size(); i++ ) {
//                Point2D neighboringSite = (Point2D)neighboringSites.get( i );
//                boolean occupied = false;
//                for( int j = 0; j < ionsInLattice.size() && !occupied; j++ ) {
//                    Ion testIon = (Ion)ionsInLattice.get( j );
//                    if( neighboringSite.equals( testIon.getPosition() ) ) {
//                        occupied = true;
//                    }
//                }
//                if( !occupied ) {
//                    results.add( neighboringSite );
//                }
//            }
//            return results;
//        }
//    }
//
//    Lattice getForm() {
//        return lattice;
//    }
//
//
//    public static void main( String[] args ) {
//        Ion a = new Sodium();
//        a.setPosition( 300, 400 );
//        Crystal l = new Crystal( a, new Rectangle2D.Double( 0, 0, 500, 500 ) );
//        Ion b = new Chloride();
//        b.setPosition( new Point2D.Double( 290, 400 ) );
//        l.addIon( b );
//
//        Ion b2 = new Chloride();
//        b2.setPosition( new Point2D.Double( 290, 400 ) );
//        l.addIon( b2 );
//
//        printLattice( l );
//
//        l.releaseIon( 10 );
//
//        printLattice( l );
//
////        PlainCubicForm pcf = (PlainCubicForm)l.getForm();
////        List ap = pcf.getNeighboringSites( a.getPosition(), Math.PI / 4 );
////        for( int i = 0; i < ap.size(); i++ ) {
////            Point2D point2D = (Point2D)ap.get(i);
////            System.out.println( "point2D = " + point2D );
////        }
//    }
//
//    private static void printLattice( Crystal l ) {
//        List ions = l.getIons();
//        for( int i = 0; i < ions.size(); i++ ) {
//            Ion ion = (Ion)ions.get( i );
//            System.out.println( "ion.getPosition() = " + ion.getPosition() );
//        }
//    }
}
