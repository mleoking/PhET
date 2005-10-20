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

import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Point2D;
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

    public Lattice() {
        instanceLifetimeListenerProxy.instanceCreated( new InstanceLifetimeEvent( this ) );
    }

    public Lattice( Ion ion ) {
        addIon( ion );
        updateCm();
        instanceLifetimeListenerProxy.instanceCreated( new InstanceLifetimeEvent( this ) );
    }

    public Lattice( ArrayList ions ) {
        this.ions = ions;
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

        System.out.println( "Lattice.releaseIon" );

        Ion ionToRelease = form.getLeastBoundIon( getIons(), orientation );
        ionToRelease.setVelocity( -ionToRelease.getVelocity().getX(),
                                  -ionToRelease.getVelocity().getY() );
        getIons().remove( ionToRelease );
        ionToRelease.unbindFrom( this );
        ionToRelease.stepInTime( dt );

        // If there aren't any ions left in the lattice, the lattice should be removed
        // from the model
        if( getIons().size() == 0 ) {
            instanceLifetimeListenerProxy.instanceDestroyed( new InstanceLifetimeEvent( this ) );
        }
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
    }

    static class PlainCubicForm implements Form {
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
                    Point2D[] ns = getNeighboringSites( testIon.getPosition(), orientation );
                    for( int j = 0; j < ns.length; j++ ) {
                        Point2D n = ns[j];
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

        private Point2D[] getNeighboringSites( Point2D p, double orientation ) {
            Point2D[] sites = new Point2D[4];
            for( int i = 0; i < sites.length; i++ ) {
                double x = p.getX() + spacing * Math.cos( i * Math.PI / 2 + orientation );
                double y = p.getY() + spacing * Math.sin( i * Math.PI / 2 + orientation );
                sites[i] = new Point2D.Double( x, y );
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
            // Go through all the ionsInLattice, looking for the one with the most unoccupied neighboring sites
            Ion leastBoundIon = null;
            int greatestNumUnccupiedNeightborSites = 0;
            for( int i = 0; i < ionsInLattice.size(); i++ ) {
                Ion ion = (Ion)ionsInLattice.get( i );
                Point2D[] ns = getNeighboringSites( ion.getPosition(), orientation );
                int numUnoccupiedNeighborSites = 0;
                for( int j = 0; j < ns.length; j++ ) {
                    Point2D n = ns[j];
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
                if( numUnoccupiedNeighborSites > greatestNumUnccupiedNeightborSites ) {
                    greatestNumUnccupiedNeightborSites = numUnoccupiedNeighborSites;
                    leastBoundIon = ion;
                }
            }
            return leastBoundIon;
        }
    }


    public static void main( String[] args ) {
        Lattice l = new Lattice();
        Ion a = new Sodium();
        l.addIon( a );
        Ion b = new Sodium();
//        b.setPosition( new Point2D.Double( 15, 0 ) );
        l.addIon( b );
        PlainCubicForm pcf = new PlainCubicForm( 15 );
        Point2D[] ap = pcf.getNeighboringSites( a.getPosition(), Math.PI / 4 );
        for( int i = 0; i < ap.length; i++ ) {
            Point2D point2D = ap[i];
            System.out.println( "point2D = " + point2D );
        }


//        Point2D p = pcf.getNearestOpenSite( new Point2D.Double( 12, 0 ), l.getOccupiedSites(), 0 );
//        System.out.println( "p = " + p );
    }
}
