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

import edu.colorado.phet.collision.Box2D;
import edu.colorado.phet.collision.Collidable;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.solublesalts.model.affinity.Affinity;
import edu.colorado.phet.solublesalts.model.affinity.RandomAffinity;
import edu.colorado.phet.solublesalts.model.crystal.Crystal;
import edu.colorado.phet.solublesalts.model.crystal.PlainCubicLattice;
import edu.colorado.phet.solublesalts.model.crystal.TwoToOneLattice;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;

/**
 * Vessel
 * <p/>
 * A rectangular vessel. It's location is specified by its upper left corner.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Vessel implements ModelElement, Collidable, Binder {
    // The shape of the vessel's interior
    private Rectangle2D shape;
    private double wallThickness;
    private Point2D location = new Point2D.Double();
    private double waterLevel;
    private Box2D collisionBox;
    private ArrayList boundIons = new ArrayList();
    private Affinity ionReleaseAffinity = new RandomAffinity( 1E-3 );
    private Affinity ionStickAffinity = new RandomAffinity( .2 );


    public Vessel( double width, double depth, double wallThickness ) {
        this( width, depth, wallThickness, new Point2D.Double() );
    }

    public Vessel( double width, double depth, double wallThickness, Point2D location ) {
        this.wallThickness = wallThickness;
        shape = new Rectangle2D.Double( location.getX(), location.getY(), width, depth );
        this.location = location;
        waterLevel = depth;
        collisionBox = new Box2D();
        updateCollisionBox();

        // Add a change listener to ourselves so that the bounds of the collision
        // box will be updated if our state changes.
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                updateCollisionBox();
            }
        } );
    }

    /**
     * Updates the collisionBox
     */
    private void updateCollisionBox() {
        collisionBox.setBounds( getShape().getMinX(),
                                getShape().getMaxY() - waterLevel,
                                getShape().getMaxX(),
                                getShape().getMaxY() );
    }

    /**
     * Binds an ion to the vessel. Creates a new lattice
     *
     * @param ion
     */
    public void bind( Ion ion ) {
        Crystal crystal = new Crystal( ion, collisionBox.getBounds(),
                                       SolubleSaltsConfig.LATTICE );
        ion.bindTo( crystal );
    }

    //----------------------------------------------------------------
    // Setters and Getters
    //----------------------------------------------------------------

    /**
     * Returns the water in the vessel as a Box2D, so it can be used for
     * collision detection.
     *
     * @return
     */
    public Box2D getWater() {
        return collisionBox;
    }

    public double getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel( double waterLevel ) {
        this.waterLevel = Math.min( waterLevel, getDepth() );
        changeListenerProxy.stateChanged( new ChangeEvent( this ) );
    }

    public Rectangle2D getShape() {
        return shape;
    }

    public Point2D getLocation() {
        return location;
    }

    public void setLocation( Point2D location ) {
        this.location = location;
    }

    public double getWidth() {
        return shape.getWidth();
    }

    public double getDepth() {
        return shape.getHeight();
    }

    public double getWallThickness() {
        return wallThickness;
    }

    public void setIonReleaseAffinity( Affinity affinity ) {
        ionReleaseAffinity = affinity;
    }

    public void setIonStickAffinity( Affinity affinity ) {
        ionStickAffinity = affinity;
    }

    public Affinity getIonStickAffinity() {
        return ionStickAffinity;
    }

    public double getVolume() {
        return getWidth() * getDepth();
    }

    //----------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------
    public void stepInTime( double dt ) {
        for( int i = 0; i < boundIons.size(); i++ ) {
            Ion ion = (Ion)boundIons.get( i );
            if( ionReleaseAffinity.stick( ion, this ) ) {
                boundIons.remove( ion );
            }
        }
    }

    //----------------------------------------------------------------
    // Collidable implementation
    //----------------------------------------------------------------
    public Vector2D getVelocityPrev() {
        return collisionBox.getVelocityPrev();
    }

    public Point2D getPositionPrev() {
        return collisionBox.getPositionPrev();
    }

    //----------------------------------------------------------------
    // Events and listeners
    //----------------------------------------------------------------
    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    //----------------------------------------------------------------
    // Events and listeners
    //----------------------------------------------------------------

    public class ChangeEvent extends EventObject {
        public ChangeEvent( Object source ) {
            super( source );
        }

        public Vessel getVessel() {
            return (Vessel)getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        void stateChanged( ChangeEvent event );
    }
}
