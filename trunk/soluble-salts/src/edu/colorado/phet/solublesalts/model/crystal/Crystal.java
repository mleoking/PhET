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

    private Lattice_new_new lattice;
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
    public Crystal( SolubleSaltsModel model, Lattice_new_new lattice ) {
        this( model, lattice, new ArrayList() );
    }

    /**
     * Creates a crystal from a list of ions. The first ion in the list is the seed
     *
     * @param model
     * @param lattice Prototype lattice. A clone is created for this crystal
     * @param ions
     */
    public Crystal( SolubleSaltsModel model, Lattice_new_new lattice, List ions ) {
        this.lattice = (Lattice_new_new)lattice.clone();
        this.model = model;

        // Open up the bounds to include the whole model so we can make lattice
        setWaterBounds( model.getVessel() );

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
        int n = Math.max( anions.size(), cations.size() );
        List list1 = anions.size() > cations.size() ? anions : cations;
        List list2 = list1 == cations ? anions : cations;
        for( int i = 0; i < n; i++ ) {
            if( i < list1.size() ) {
                ions2.add( list1.get( i ) );
            }
            if( i < list2.size() ) {
                ions2.add( list2.get( i ) );
            }
        }

        for( int i = 0; i < ions2.size(); i++ ) {
            Ion ion = (Ion)ions2.get( i );
            if( !addIon( ion ) ) {
                throw new RuntimeException( "can't add ion" );
            }
        }

        model.getVessel().addChangeListener( vesselListener );
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
    public Crystal( SolubleSaltsModel model, Lattice_new_new lattice, List ions, Ion seed ) {
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
        if( seed == null ) {
            throw new RuntimeException( "seed == null" );
        }
        setSeed( seed );
    }

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
     * @return
     */
    public boolean addIon( Ion ionA, Ion ionB ) {
        boolean added = false;

        if( noBindList.contains( ionB ) ) {
            return false;
        }

        // Check that the ions are of opposite charges
        if( ionA.getCharge() * ionB.getCharge() < 0 ) {
            Node nodeB = lattice.getNode( ionB );

            // Sanity check
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
                        System.out.println( "Crystal.addIon" );
                        removeIon( ionA );
                        lattice.addAtIonNode( ionA, ionB );
                        for( int j = 0; j < ions.size(); j++ ) {
                            Ion ion = (Ion)ions.get( j );
                            if( Math.abs( ion.getPosition().getX() - ionA.getPosition().getX() ) < 2
                                && Math.abs( ion.getPosition().getY() - ionA.getPosition().getY() ) < 2
                                && ion != ionA ) {
                                System.out.println( "Crystal.addIon" );
                            }
                        }
                    }
                }
            }

            // Sanity check
            if( !waterBounds.contains( ionA.getPosition() ) ) {
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
            return false;
        }
        else if( true ) {
            added = lattice.add( ion );
            if( added ) {
                ions.add( ion );
                ion.bindTo( this );
                updateCm();
            }
        }

        // debug
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
     * This method is only to be used when a client remves an ion from the
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
        Ion ionToRelease = lattice.getLeastBoundIon( getIons(), 0 );

        // Sanity check
        if( ionToRelease == getSeed() && ions.size() > 1 ) {
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


    public void releaseIonDebug( double dt ) {
        releaseIon( dt );
    }

    /**
     * Determine the velocity an ion that is about to be release should have
     *
     * @param ionToRelease
     * @return
     */
    private Vector2D determineReleaseVelocity( Ion ionToRelease ) {

        // If this is the seed ion, then just send it off with the velocity it had before it seeded the
        // crystal. This prevents odd looking behavior if the ion is released soon after it nucleates.
        if( ionToRelease == this.getSeed() ) {
            return ionToRelease.getVelocity();
        }

        // Get the unoccupied sites around the ion being release
        List openSites = lattice.getOpenNeighboringSites( ionToRelease );

        if( openSites.size() == 0 ) {
            System.out.println( "openSites.size() = " + openSites.size() );
            return new Vector2D.Double();
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

        // If we get here, there is only one open site adjacent to the ion being released
        Point2D point2D = (Point2D)openSites.get( 0 );
        Vector2D.Double v = new Vector2D.Double( point2D.getX() - ionToRelease.getPosition().getX(),
                                                 point2D.getY() - ionToRelease.getPosition().getY() );
        double angle = ( v.getAngle() + Math.PI * 2 ) % ( Math.PI * 2 );
        maxAngle = angle > maxAngle ? angle : maxAngle;
        minAngle = angle < minAngle ? angle : minAngle;

        angle = random.nextDouble() * ( maxAngle - minAngle ) + minAngle;
        Vector2D releaseVelocity = new Vector2D.Double( ionToRelease.getVelocity().getMagnitude(), 0 ).rotate( angle );

        // Sanity check
        if( releaseVelocity.getMagnitude() < 0.0001 ) {
            throw new RuntimeException( "releaseVelocity.getMagnitude() < 0.0001" );
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
