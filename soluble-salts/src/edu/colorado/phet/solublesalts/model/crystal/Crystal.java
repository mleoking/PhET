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
import edu.colorado.phet.solublesalts.model.ion.Ion;

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
//    private Atom seed;
    private Lattice lattice;
    // The list of ions that cannot be bound to this lattice at this time
    private Vector noBindList = new Vector();

    //----------------------------------------------------------------
    // Lifecycle
    //----------------------------------------------------------------

    /**
     *
     * @param bounds
     * @param lattice
     */
    public Crystal( Rectangle2D bounds, Lattice lattice ) {
        this.bounds = bounds;
        this.lattice = lattice;
        lattice.setBounds( bounds );
        instanceLifetimeListenerProxy.instanceCreated( new InstanceLifetimeEvent( this ) );
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

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for( int i = 0; i < ions.size(); i++ ) {
            Ion ion = (Ion)ions.get( i );
            String s2 = new String("ion: type = " + ion.getClass() + "  position = " + ion.getPosition() + "\n" );
            sb.append( s2 );
        }
        return sb.toString();
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
                lattice.setSeed( ion );
                placeToPutIon = ion.getPosition();
                break;
            case 1:
                orientation = Math.atan2( ion.getPosition().getY() - lattice.getSeed().getPosition().getY(),
                                          ion.getPosition().getX() - lattice.getSeed().getPosition().getX() );
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
     * crystal. It is not to be used when the crystal itself releases the ion
     *
     * @param ion
     */
    public void removeIon( Ion ion ) {
        getIons().remove( ion );

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
    private void releaseIon( double dt ) {
        Ion ionToRelease = lattice.getLeastBoundIon( getIons(), orientation );

        if( ionToRelease == null ) {
            System.out.println( "No ion found to release!!!!!" );
            System.out.println( "ions.size() = " + ions.size() );
            return;
        }

        Thread t = new Thread( new NoBindTimer( ionToRelease ) );
        t.start();

        Vector2D v = determineReleaseVelocity( ionToRelease );
        ionToRelease.setVelocity( v );
        removeIon( ionToRelease );
        ionToRelease.unbindFrom( this );

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
     * @return
     */
    private Vector2D determineReleaseVelocity( Ion ionToRelease ) {

        // Get the unoccupied sites around the ion being release
        List openSites = lattice.getOpenNeighboringSites( ionToRelease, ions, orientation );
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

    public void setBounds( Rectangle2D bounds ) {
        this.bounds = bounds;
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
}
