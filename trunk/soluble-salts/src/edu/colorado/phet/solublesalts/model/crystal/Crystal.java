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
import edu.colorado.phet.solublesalts.model.Atom;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.model.Vessel;
import edu.colorado.phet.solublesalts.model.ion.Ion;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

/**
 * Lattice
 * <p/>
 * A lattice is a crystal of ions. It has a Form that defines how it is constructed.
 * <p/>
 * The lattice has a seed ion. The lattice's position is that of it's seed ion.
 * <p/>
 * At each time step, a crystal determines if it should release an ion. The determination is
 * based on the following factors
 * <ul>
 * <li>Is the crystal in water?
 * <li>Is a random number between 0 and 1 less than the crystal's dissociationLikelihood?
 * </ul>
 * <p/>
 * To know if and how it can release an ion, the crystal must know whether or not it is in
 * the water. Currently, the crystal has knowledge of the vessel. This is not a very good
 * design, and should be changed.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Crystal extends Body {

    //================================================================
    // Class data and methods
    //================================================================

    private static EventChannel instanceLifetimeEventChannel = new EventChannel( InstanceLifetimeListener.class );
    protected static InstanceLifetimeListener instanceLifetimeListenerProxy =
            (InstanceLifetimeListener)instanceLifetimeEventChannel.getListenerProxy();
    private static Random random = new Random( System.currentTimeMillis() );
    private static double dissociationLikelihood;
    private SolubleSaltsModel model;

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

    private Lattice lattice;
    // The list of ions that cannot be bound to this lattice at this time
    private Vector noBindList = new Vector();
    // Keeps track of the bounds of the water in the vessel
    private VesselListener vesselListener = new VesselListener();
    private Rectangle2D waterBounds;

    //----------------------------------------------------------------
    // Lifecycle
    //----------------------------------------------------------------

    /**
     * @param model
     * @param lattice Prototype lattice. A clone is created for this crystal
     */
    public Crystal( SolubleSaltsModel model, Lattice lattice ) {
        this( model, lattice, new ArrayList() );
    }

    /**
     * Creates a crystal from a list of ions. The first ion in the list is the seed. This constructor
     * makes a crystal regardless of whether the ions are in the water. It is used by the Shaker.
     *
     * @param model
     * @param lattice Prototype lattice. A clone is created for this crystal
     * @param ions
     */
    private Crystal( SolubleSaltsModel model, Lattice lattice, List ions ) {
        this.lattice = (Lattice)lattice.clone();
        this.model = model;

        // Open up the bounds to include the whole model so we can make the lattice
        lattice.setBounds( model.getBounds() );

        // We need to interleave the positive and negative ions in the list, as much as possible, so the
        // lattice can be built out without any of them not finding an ion of the opposite polarity to
        // stick to
        ArrayList anions = new ArrayList();
        ArrayList cations = new ArrayList();
        for( int i = 0; i < ions.size(); i++ ) {
            Ion ion = (Ion)ions.get( i );
            if( ion.getCharge() < 0 ) {
                cations.add( ion );
            }
            else if( ion.getCharge() > 0 ) {
                anions.add( ion );
            }
            else {
                throw new RuntimeException( "ion with 0 charge" );
            }
        }
        ArrayList ions2 = new ArrayList();
        List listA = model.getCurrentSalt().getNumAnionsInUnit() < model.getCurrentSalt().getNumCationsInUnit() ?
                     anions : cations;
        List listB = listA == anions ? cations : anions;

        Iterator itA = listA.iterator();
        Iterator itB = listB.iterator();
        while( itA.hasNext() && itB.hasNext() ) {
            ions2.add( itA.next() );
            ions2.add( itB.next() );
        }
        while( itA.hasNext() ) {
            ions2.add( itA.next() );
        }
        while( itB.hasNext() ) {
            ions2.add( itB.next() );
        }

        // Add all the ions to the lattice. Because of the possible ratios of different ions and the order
        // in which we try to add them, some ions might not find a spot to bond the first time we try to
        // add them. Those that don't get successfully added the first time are put on a retry list.
        List retryList = new ArrayList();
        for( int i = 0; i < ions2.size(); i++ ) {
            Ion ion = (Ion)ions2.get( i );
            if( !addIon( ion ) ) {
                retryList.add( ion );
            }
        }

        // Retry adding ions that couldn't find a bond the first time through the list.
        int maxRetries = 10;
        int numRetries = 0;
        for( int i = 0; i <= maxRetries && !retryList.isEmpty(); i++ ) {
            numRetries = i;
            for( int j = 0; j < retryList.size(); j++ ) {
                Ion ion = (Ion)retryList.get( j );
                if( addIon( ion ) ) {
                    retryList.remove( ion );
                    break;
                }
            }
        }
        if( numRetries >= maxRetries ) {
            throw new RuntimeException( "maxRetries exceeded" );
        }

        // todo: Is this doing anything? It isn't done in the other constructors
        model.getVessel().addChangeListener( vesselListener );

        // Reset the water bounds so that they will be respeced by other constructors
        setWaterBounds( model.getVessel() );

        instanceLifetimeListenerProxy.instanceCreated( new InstanceLifetimeEvent( this ) );
    }

    /**
     * Sets the seed to be the ion closest to the bottom of the vessel
     * todo: This is the wrong way to to this!!!!
     *
     * @param model
     * @param lattice
     * @param ions
     * @param seed
     */
    public Crystal( SolubleSaltsModel model, Lattice lattice, List ions, Ion seed ) {
        this( model, lattice, ions );
        seed = null;
        double maxY = Double.MIN_VALUE;
        for( int i = 0; i < ions.size(); i++ ) {
            Ion testIon = (Ion)ions.get( i );
            if( testIon.getPosition().getY() + testIon.getRadius() > maxY ) {
                maxY = testIon.getPosition().getY() + testIon.getRadius();
                seed = testIon;
            }
        }

        // Sanity check
        if( seed == null ) {
            throw new RuntimeException( "seed == null" );
        }
        setSeed( seed );
    }

    /**
     * Sets the bounds in which the crystal can grow
     *
     * @param vessel
     */
    void setWaterBounds( Vessel vessel ) {
        waterBounds = vessel.getWater().getBounds();
        lattice.setBounds( waterBounds );
    }

    public Point2D getPosition() {
        return lattice.getSeed().getPosition();
    }

    public void leaveModel() {
        model.getVessel().removeChangeListener( vesselListener );
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

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for( int i = 0; i < ions.size(); i++ ) {
            Ion ion = (Ion)ions.get( i );
            String s2 = new String( "ion: type = " + ion.getClass() + "  position = " + ion.getPosition() + "\n" );
            sb.append( s2 );
        }
        return sb.toString();
    }

    //----------------------------------------------------------------
    // Lattice building
    //----------------------------------------------------------------

    /**
     * Add and ion to the crystal in a site adjacent to another ion
     *
     * @param ionA The ion to be added
     * @param ionB The ion next to which the other ion should be placed
     * @return true if the ion was added, false if not
     */
    public boolean addIonNextToIon( Ion ionA, Ion ionB ) {
        boolean added = false;

        // If the ion is prevented from binding, don't do anything
        if( noBindList.contains( ionA ) ) {
            return false;
        }

        // Check that the ions are of opposite charges
        if( ionA.getCharge() * ionB.getCharge() < 0 ) {

            // Sanity check
            Node nodeB = lattice.getNode( ionB );
            if( nodeB == null ) {
                throw new RuntimeException( "nodeB = null" );
            }

            boolean b = lattice.addAtIonNode( ionA, ionB );
            if( b ) {
                ions.add( ionA );
                ionA.bindTo( this );
                updateCm();
                added = true;

                // debug
                for( int i = 0; i < ions.size(); i++ ) {
                    Ion ion1 = (Ion)ions.get( i );
                    if( Math.abs( ion1.getPosition().getX() - ionA.getPosition().getX() ) < 2
                        && Math.abs( ion1.getPosition().getY() - ionA.getPosition().getY() ) < 2
                        && ion1 != ionA ) {
                        removeIon( ionA );
                        ionA.unbindFrom( this );
                        added = false;
                        updateCm();
//                        lattice.addAtIonNode( ionA, ionB );
//                        for( int j = 0; j < ions.size(); j++ ) {
//                            Ion ion = (Ion)ions.get( j );
//                            if( Math.abs( ion.getPosition().getX() - ionA.getPosition().getX() ) < 2
//                                && Math.abs( ion.getPosition().getY() - ionA.getPosition().getY() ) < 2
//                                && ion != ionA ) {
//                                System.out.println( "Crystal.addIonNextToIon" );
//                            }
//                        }
                    }
                }
            }

            // Sanity check
            if( added && !waterBounds.contains( ionA.getPosition() ) ) {
                throw new RuntimeException( "!waterBounds.contains( ionA.getPosition() )" );
            }

        }
        return added;
    }

    /**
     * Returns true if the ion was added. If there wasn't a place to put it in
     * the lattice, returns false
     */
    public boolean addIon( Ion ion ) {
        boolean added = false;

        if( noBindList.contains( ion ) ) {
            System.out.println( "Crystal.addIon: on nobind list" );
            return false;
        }
        else {
            added = lattice.add( ion );
            if( added ) {
                ions.add( ion );
                ion.bindTo( this );
                updateCm();
            }
        }

        // Debug: Sanity check to see ifwe are putting an ion where one already is
        for( int i = 0; i < ions.size(); i++ ) {
            Ion ion1 = (Ion)ions.get( i );
            if( Math.abs( ion1.getPosition().getX() - ion.getPosition().getX() ) < 2
                && Math.abs( ion1.getPosition().getY() - ion.getPosition().getY() ) < 2
                && ion1 != ion ) {
                System.out.println( "Crystal.addIon" );
            }
        }
        return added;
    }

    /**
     * This method is only to be used when a client removes an ion from the
     * crystal. It is not to be used when the crystal itself releases the ion
     *
     * @param ion
     */
    public void removeIon( Ion ion ) {
        getIons().remove( ion );
        lattice.removeIon( ion );
        // If there aren't any ions left in the crystal, the crystal should be removed
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
    public void releaseIon( double dt ) {

        Class preferredIonType = model.getPreferredTypeOfIonToRelease();

        // Get the best ion to release
        Ion ionToRelease = lattice.getBestIonToRelease( getIons(), preferredIonType );

        // Sanity check
        if( ionToRelease == getSeed() && ions.size() > 1 ) {
//            ionToRelease = lattice.getBestIonToRelease( getIons(), preferredIonType );
            throw new RuntimeException( "ionToRelease == getSeed() && ions.size() > 1" );
        }

        // Sanity check
        if( ionToRelease == null ) {
            throw new RuntimeException( "no ion found to release" );
        }

        Thread t = new Thread( new NoBindTimer( ionToRelease ) );
        t.start();

        Vector2D v = determineReleaseVelocity( ionToRelease );
        ionToRelease.setVelocity( v );
        ionToRelease.unbindFrom( this );
        removeIon( ionToRelease );

        // Give the ion a step so that it isn't in contact with the crystal
        ionToRelease.stepInTime( dt );

        // If there aren't any ions left in the crystal, remove it from the model
        if( getIons().size() == 0 ) {
            leaveModel();
        }
    }

    /**
     * Determine the velocity an ion that is about to be release should have
     *
     * @param ionToRelease
     * @return the release velocity vector
     */
    private Vector2D determineReleaseVelocity( Ion ionToRelease ) {
        Vector2D releaseVelocity = null;

        // If this is the seed ion, then just send it off with the velocity it had before it seeded the
        // crystal. This prevents odd looking behavior if the ion is released soon after it nucleates.
        if( ionToRelease == this.getSeed() ) {
            return ionToRelease.getVelocity();
        }

        // Get the unoccupied sites around the ion being release
        List openSites = lattice.getOpenNeighboringSites( ionToRelease );

        if( openSites.size() == 0 ) {
            double angle = random.nextDouble() * Math.PI * 2;
            releaseVelocity = new Vector2D.Double( ionToRelease.getVelocity().getMagnitude(), 0 ).rotate( angle );
            return releaseVelocity;
        }

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
                        releaseVelocity = new Vector2D.Double( ionToRelease.getVelocity().getMagnitude(), 0 ).rotate( angle );
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
            releaseVelocity = new Vector2D.Double( ionToRelease.getVelocity().getMagnitude(), 0 ).rotate( angle );
            return releaseVelocity;
        }

        // If we get here, there is only one open site adjacent to the ion being released
        Point2D point2D = (Point2D)openSites.get( 0 );
        Vector2D.Double v = new Vector2D.Double( point2D.getX() - ionToRelease.getPosition().getX(),
                                                 point2D.getY() - ionToRelease.getPosition().getY() );
        double angle = ( v.getAngle() + Math.PI * 2 ) % ( Math.PI * 2 );
        maxAngle = angle > maxAngle ? angle : maxAngle;
        minAngle = angle < minAngle ? angle : minAngle;

        angle = random.nextDouble() * ( maxAngle - minAngle ) + minAngle;
        releaseVelocity = new Vector2D.Double( ionToRelease.getVelocity().getMagnitude(), 0 ).rotate( angle );

        // Sanity check
        if( releaseVelocity.getMagnitude() < 0.0001 ) {
            System.out.println( "Crystal.determineReleaseVelocity < 0.0001" );
//            throw new RuntimeException( "releaseVelocity.getMagnitude() < 0.0001" );
        }
        return releaseVelocity;
    }

    /**
     * If the crystal is translated, all its ions must be translated, too
     *
     * @param dx
     * @param dy
     */
    public void translate( double dx, double dy ) {

        if( dx == 0 && dy == 0 ) {
            System.out.println( "Crystal.translate" );
        }

        for( int i = 0; i < ions.size(); i++ ) {
            Ion ion = (Ion)ions.get( i );
            ion.translate( dx, dy );
        }
        super.translate( dx, dy );
    }

    //----------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------

    public Atom getSeed() {
        return lattice.getSeed();
    }

    public void setSeed( Ion ion ) {
        lattice.setSeed( ion );
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

    /**
     * Sets the bounds of the model. I don't know if this is used anymore or not
     *
     * @param bounds
     */
    public void setBounds( Rectangle2D bounds ) {
        lattice.setBounds( bounds );
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
        if( waterBounds.contains( getPosition() ) && random.nextDouble() < dissociationLikelihood ) {
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

    private class VesselListener implements Vessel.ChangeListener {
        public void stateChanged( Vessel.ChangeEvent event ) {
            setWaterBounds( event.getVessel() );
        }
    }
}
