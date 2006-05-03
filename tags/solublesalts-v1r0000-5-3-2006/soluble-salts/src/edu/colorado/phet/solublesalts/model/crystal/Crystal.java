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
import edu.colorado.phet.solublesalts.model.salt.Salt;
import edu.colorado.phet.solublesalts.model.ion.Ion;
import edu.colorado.phet.solublesalts.model.ion.IonFactory;
import edu.colorado.phet.collision.Collidable;
import edu.colorado.phet.collision.CollidableAdapter;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

/**
 * Crystal
 * <p/>
 * A crystal has a lattice, which it clones from the salt of which the crystal is an instance
 * <p/>
 * The crystal has a seed ion. The crystal's position is that of it's seed ion.
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
public class Crystal extends Body implements Collidable {

    //================================================================
    // Class data and methods
    //================================================================

    public final static int NORTH = 0;
    public final static int SOUTH = 1;
    public final static int EAST = 2;
    public final static int WEST = 3;


    private static EventChannel instanceLifetimeEventChannel = new EventChannel( InstanceLifetimeListener.class );
    protected static InstanceLifetimeListener instanceLifetimeListenerProxy =
            (InstanceLifetimeListener)instanceLifetimeEventChannel.getListenerProxy();
    private static Random random = new Random( System.currentTimeMillis() );
    private static double dissociationLikelihood;
    private SolubleSaltsModel model;
    private Salt salt;

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

    private CollidableAdapter collidableAdapter;
    private Point2D cm = new Point2D.Double();
    private ArrayList ions = new ArrayList();

    private Lattice lattice;
    // The list of ions that cannot be bound to this lattice at this time
    private Vector noBindList = new Vector();
    // Keeps track of the bounds of the water in the vessel
    private Rectangle2D waterBounds;
    // An array to keep track of the most northerly, easterly, southerly and westerly ions in the crystal
    private Ion[] boundaryIons = new Ion[4];
    private boolean isBound;

    //----------------------------------------------------------------
    // Lifecycle
    //----------------------------------------------------------------

    /**
     * @param model
     * @param lattice Prototype lattice. A clone is created for this crystal
     */
    public Crystal( SolubleSaltsModel model, Lattice lattice, Ion ion ) {
        ArrayList ions = new ArrayList();
        ions.add( ion );
        init( lattice, model, ions, ion );
    }

    /**
     * Creates a crystal from a list of ions. The first ion in the list is the seed. This constructor
     * makes a crystal regardless of whether the ions are in the water. It is used by the Shaker.
     *
     * @param model
     * @param lattice Prototype lattice. A clone is created for this crystal
     * @param ions
     */
    private Crystal( SolubleSaltsModel model, Lattice lattice, List ions, Ion seed ) {
        init( lattice, model, ions, seed );
    }

    /**
     * Sets the seed to be the ion closest to the bottom of the vessel
     *
     * @param model
     * @param lattice
     * @param ions
     */
    public Crystal( SolubleSaltsModel model, Lattice lattice, List ions ) {
        init( lattice, model, ions, (Ion)ions.get( 0 ) );
        Ion seed = null;
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
        trackExtremities();
        setSeed( seed );
    }

    /**
     * Builds a lattice from a list of ions, and sets the seed
     *
     * @param lattice
     * @param model
     * @param ions
     * @param seed
     */
    private void init( Lattice lattice, SolubleSaltsModel model, List ions, Ion seed ) {
        this.lattice = (Lattice)lattice.clone();
        this.model = model;
        this.salt = model.getCurrentSalt();

        // Open up the bounds to include the whole model so we can make the lattice
        lattice.setBounds( model.getBounds() );

        // We need to interleave the positive and negative ions in the list, as much as possible, so the
        // lattice can be built out without any of them not finding an ion of the opposite polarity to
        // stick to

        // Determine which type of ion is more plentiful in the lattice
        Salt.Component[] components = salt.getComponents();
        Class majorComponentClass = null;
        Class minorComponentClass = null;
        if( components[0].getLatticeUnitFraction().intValue() > components[1].getLatticeUnitFraction().intValue() ) {
            majorComponentClass = components[0].getIonClass();
            minorComponentClass = components[1].getIonClass();
        }
        else {
            majorComponentClass = components[1].getIonClass();
            minorComponentClass = components[0].getIonClass();
        }

        ArrayList majorityIons = new ArrayList();
        ArrayList minorityIons = new ArrayList();
        for( int i = 0; i < ions.size(); i++ ) {
            Ion ion = (Ion)ions.get( i );
            if( ion.getClass() == minorComponentClass ) {
                minorityIons.add( ion );
            }
            else if( ion.getClass() == majorComponentClass ) {
                majorityIons.add( ion );
            }
            else {
                throw new RuntimeException( "ion with 0 charge" );
            }
        }
        ArrayList ions2 = new ArrayList();
        List listA = model.getCurrentSalt().getNumAnionsInUnit() < model.getCurrentSalt().getNumCationsInUnit() ?
                     majorityIons : minorityIons;
        List listB = listA == majorityIons ? minorityIons : majorityIons;

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

        // Reset the water bounds so that they will be respeced by other methods
        setWaterBounds( model.getVessel() );

        instanceLifetimeListenerProxy.instanceCreated( new InstanceLifetimeEvent( this ) );

        setSeed( seed );
        if( collidableAdapter == null ) {
            collidableAdapter = new CollidableAdapter( this );
        }
    }

    /**
     * @return
     * @throws CloneNotSupportedException
     */
    public Object clone() throws CloneNotSupportedException {
        ArrayList newIons = new ArrayList();
        IonFactory ionFactory = new IonFactory();
        Ion newSeed = null;
        for( int i = 0; i < ions.size(); i++ ) {
            Ion ion = (Ion)ions.get( i );
            Ion newIon = ionFactory.create( ion.getClass(),
                                            new Point2D.Double( ion.getPosition().getX(), ion.getPosition().getY() ),
                                            new Vector2D.Double( ion.getVelocity() ),
                                            new Vector2D.Double( ion.getAcceleration() ) );
            if( this.getSeed() == ion ) {
                newSeed = newIon;
            }
            newIons.add( newIon );
            model.addModelElement( newIon );
        }
        // Note that using this constructor is a real hack. We use it to set the seed
        Crystal crystal = new Crystal( model, lattice, newIons, newSeed );
        trackExtremities();

        crystal.setVelocity( new Vector2D.Double( this.getVelocity() ) );
        crystal.setAcceleration( new Vector2D.Double( this.getAcceleration() ) );

        return crystal;
    }

    /**
     * Keeps track of the most northerly, easterly, southerly and westerly ions in the crystal
     */
    private void trackExtremities() {
        double maxNorth = Double.MAX_VALUE;
        double maxWest = Double.MAX_VALUE;
        double maxEast = -Double.MAX_VALUE;
        double maxSouth = -Double.MAX_VALUE;
        Ion maxNorthIon = null;
        Ion maxEastIon = null;
        Ion maxSouthIon = null;
        Ion maxWestIon = null;

        for( int i = 0; i < ions.size(); i++ ) {
            Ion ion = (Ion)ions.get( i );
            if( ion.getPosition().getY() < maxNorth ) {
                maxNorth = ion.getPosition().getY();
                maxNorthIon = ion;
            }
            if( ion.getPosition().getX() < maxWest ) {
                maxWest = ion.getPosition().getX();
                maxWestIon = ion;
            }
            if( ion.getPosition().getY() > maxSouth ) {
                maxSouth = ion.getPosition().getY();
                maxSouthIon = ion;
            }
            if( ion.getPosition().getX() > maxEast ) {
                maxEast = ion.getPosition().getX();
                maxEastIon = ion;
            }
        }
        boundaryIons[NORTH] = maxNorthIon;
        boundaryIons[EAST] = maxEastIon;
        boundaryIons[SOUTH] = maxSouthIon;
        boundaryIons[WEST] = maxWestIon;
    }

    /**
     * Returns the most northerly, easterly, southerly or westerly ion in the crystal
     *
     * @param direction Constants are defined as final static in this class
     * @return the ion in the crystal that is farthest in the specified direction
     */
    public Ion getExtremeIon( int direction ) {
        return boundaryIons[direction];
    }

    /**
     * Sets the bounds in which the crystal can grow
     *
     * @param vessel
     */
    public void setWaterBounds( Vessel vessel ) {
        waterBounds = vessel.getWater().getBounds();
        lattice.setBounds( waterBounds );
    }

    public Point2D getPosition() {
        return lattice.getSeed().getPosition();
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

    public boolean isBound() {
        return isBound;
    }

    public void setBound( boolean bound ) {
        isBound = bound;
    }

    //----------------------------------------------------------------
    // Utility
    //----------------------------------------------------------------

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
                    }
                }
            }

            // Track extremity ions
            trackExtremities();

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

        // Track extremity ions
        trackExtremities();

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

        // Track extremity ions
        trackExtremities();
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
    // Collidable implementation
    //----------------------------------------------------------------

    public Vector2D getVelocityPrev() {
        return collidableAdapter.getVelocityPrev();
    }

    public Point2D getPositionPrev() {
        return collidableAdapter.getPositionPrev();
    }

    //----------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------

    public Atom getSeed() {
        return lattice.getSeed();
    }

    public void setSeed( Ion ion ) {
        lattice.setSeed( ion );
        this.collidableAdapter = new CollidableAdapter( this );
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

    /**
     * Tells if the entire crystal is in the water. Note that if the crystal is moving downward (eg, came out of the
     * shaker), all ions in the crystal must be at least one diameter below the surface of the water for this to be true.
     * This prevents ions from being released into the air when a crystal first enters the water from the shaker. There
     * should be a better way to prevent that from happening, but I haven't found one yet.
     *
     * @param waterBounds
     * @return true if the crystal is in the water, false otherwise
     */
    public boolean isInWater( Rectangle2D waterBounds ) {
        boolean isInWater = true;
        Point2D p = new Point2D.Double();
        // Check that all ions are in the water
        Ion east = getExtremeIon( EAST );
        Ion west = getExtremeIon( WEST );
        Ion north = getExtremeIon( NORTH );
        Ion south = getExtremeIon( SOUTH );

        // Note that the ion's radius isn't included in the test. This turns out not to work properly
        // with the lattice building code, since it produces open bonds without regard to the radius of
        // ions that might occupy them.
        isInWater &= east.getPosition().getX() < waterBounds.getMaxX();
        isInWater &= west.getPosition().getX() > waterBounds.getMinX();
        isInWater &= south.getPosition().getY() < waterBounds.getMaxY();

//        isInWater &= east.getPosition().getX() + east.getRadius() < waterBounds.getMaxX();
//        isInWater &= west.getPosition().getX() - west.getRadius() > waterBounds.getMinX();
//        isInWater &= south.getPosition().getY() + south.getRadius() < waterBounds.getMaxY();

        // North is a special case. If the crystal is moving downward, we don't want to say that it is completely
        // in the water unless it is far enough in that an ion release by the crystal will be sure to stay
        // in the water
        double dy = ( getVelocity().getY() > 0 ) ? 2 * north.getRadius() : 0;
        isInWater &= north.getPosition().getY() - dy > waterBounds.getMinY();
//        isInWater &= north.getPosition().getY() - north.getRadius() - dy > waterBounds.getMinY();

        return isInWater;
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
        if( isInWater( waterBounds ) && random.nextDouble() < dissociationLikelihood ) {
            releaseIon( dt );
        }

        if( collidableAdapter == null ) {
            System.out.println( "Crystal.stepInTime" );
        }
        collidableAdapter.stepInTime( dt );
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
}
