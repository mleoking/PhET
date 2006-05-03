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
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.model.affinity.Affinity;
import edu.colorado.phet.solublesalts.model.affinity.RandomAffinity;
import edu.colorado.phet.solublesalts.model.crystal.Crystal;
import edu.colorado.phet.solublesalts.model.ion.Ion;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;

/**
 * Vessel
 * <p/>
 * A rectangular vessel. It's location is specified by its upper left corner.
 * <p/>
 * Currently, the water level is screwy in this class. Its internal representation is in pixels. That
 * should be changed.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Vessel implements ModelElement, Collidable {
    // The shape of the vessel's interior
    private Rectangle2D shape;
    private double wallThickness;
    private Point2D location = new Point2D.Double();
    private double waterLevel;
    private Box2D collisionBox;
    private ArrayList boundIons = new ArrayList();
    private Affinity ionReleaseAffinity = new RandomAffinity( 1E-3 );
    private Affinity ionStickAffinity = new RandomAffinity( .2 );
    private SolubleSaltsModel model;


    public Vessel( double width, double depth, double wallThickness, SolubleSaltsModel model ) {
        this( width, depth, wallThickness, new Point2D.Double(), model );
    }

    public Vessel( double width, double depth, double wallThickness, Point2D location, final SolubleSaltsModel model ) {
        this.wallThickness = wallThickness;
        this.model = model;
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

        // Add a listener that will reset when the configuration is recalibrated
        model.addChangeListener( new SolubleSaltsModel.ChangeAdapter() {
            public void reset( SolubleSaltsModel.ChangeEvent event ) {
                super.reset( event );
            }

            public void reset( SolubleSaltsConfig.Calibration calibration ) {
                setWaterLevel( calibration.defaultWaterLevel );
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

        if( !SolubleSaltsConfig.ONE_CRYSTAL_ONLY || model.crystalTracker.getCrystals().size() == 0 ) {
            // todo: combine these lines into a single constructor
            Crystal crystal = new Crystal( model, model.getCurrentSalt().getLattice(), ion );
            crystal.addIon( ion );
            crystal.setBound( true );
        }
    }

    //----------------------------------------------------------------
    // Setters and Getters
    //----------------------------------------------------------------

    /**
     * Returns the water in the vessel as a Box2D, so it can be used for
     * collision detection.
     *
     * @return A Box2D (a collidable object) whose bounds are that of the water in the vessel
     */
    public Box2D getWater() {
        return collisionBox;
    }

    public double getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel( double waterLevel ) {
        this.waterLevel = Math.max( 0, Math.min( waterLevel, getDepth() ));
        changeListenerProxy.stateChanged( new ChangeEvent( this ) );
    }

    public Rectangle2D getShape() {
        return shape;
    }

    /**
     * Determines if a point is outside of the vessel. Note that this test included the thickness
     * of the vessel's walls.
     *
     * @param p
     * @return true if the point is outside the vessel
     */
    public boolean isOutside( Point2D p ) {
//        return !( getShape().getMinX() < p.getX()
//                && getShape().getMinY() < p.getY()
//                && getShape().getMaxX() > p.getX()
//                && getShape().getMaxY() > p.getY()
//            );
        return !( getShape().getMinX() - wallThickness < p.getX()
                && getShape().getMinY() < p.getY()
                && getShape().getMaxX() + wallThickness > p.getX()
                && getShape().getMaxY() + wallThickness > p.getY()
            );
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
        public ChangeEvent( Vessel source ) {
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
