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

import edu.colorado.phet.collision.CollisionExpert;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

/**
 * SolubleSaltsModel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SolubleSaltsModel extends BaseModel {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------

    private static final double MIN_SPEED = 1E-3;


    //----------------------------------------------------------------
    // Instance data and methods
    //----------------------------------------------------------------

    // The vessel
    private Vessel vessel;
    private Point2D vesselLoc = SolubleSaltsConfig.VESSEL_ULC;
    private double vesselWidth = SolubleSaltsConfig.VESSEL_SIZE.getWidth();
    private double vesselDepth = SolubleSaltsConfig.VESSEL_SIZE.getHeight();

    // Collision mechanism objects
    IonIonCollisionExpert ionIonCollisionExpert = new IonIonCollisionExpert( this );
    private IonTracker ionTracker;
    private HeatSource heatSource;

    //---------------------------------------------------------------
    // Constructor and lifecycle methods
    //---------------------------------------------------------------
    
    public SolubleSaltsModel() {

        // Add an agent that will track the ions of various classes
        ionTracker = new IonTracker();
        addIonListener( ionTracker );

        // Add an agent that will track the creation and destruction of salt lattices
            Lattice.addInstanceLifetimeListener( new LatticeLifetimeTracker() );

        // Create a vessel
        vessel = new Vessel( vesselWidth, vesselDepth, vesselLoc );
        addModelElement( vessel );

        // Add a model element that will handle collisions between ions and the vessel
        addModelElement( new IonVesselCollisionAgent() );

        // Add a heat source/sink
        heatSource = new HeatSource( this );
        addModelElement( heatSource );
    }

    /**
     *
     * @param modelElement
     */
    public void addModelElement( ModelElement modelElement ) {
        super.addModelElement( modelElement );

        if( modelElement instanceof Ion ) {
            ionListenerProxy.ionAdded( new IonEvent( modelElement ) );
        }
    }

    public void removeModelElement( ModelElement modelElement ) {
        super.removeModelElement( modelElement );

        if( modelElement instanceof Ion ) {
        	Ion ion = (Ion)modelElement;
            ionListenerProxy.ionRemoved( new IonEvent( ion ) );
            if( ion.isBound() ) {
            	ion.getBindingLattice().removeIon( ion );
            }
        }
    }

    public void reset() {
    	List ions = ionTracker.getIons();
    	for( int i = 0; i < ions.size(); i++ ) {
    		Ion ion = (Ion)ions.get(i);
    		removeModelElement(ion);
    	}
    	heatSource.setHeatChangePerClockTick( 0 );
    }
    
    
    //----------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------

    public Vessel getVessel() {
        return vessel;
    }

    public int getNumIonsOfType( Class ionClass ) {
        return ionTracker.numIonsOfType( ionClass );
    }

    public List getIonsOfType( Class ionClass ) {
        return ionTracker.getIonsOfType( ionClass );
    }
    
    public List getIons() {
    	return ionTracker.getIons();
    }

    public HeatSource getHeatSource() {
        return heatSource;
    }

    //----------------------------------------------------------------
    // Events and listeners for Ions
    //----------------------------------------------------------------

    private EventChannel ionEventChannel = new EventChannel( IonListener.class );
    private IonListener ionListenerProxy = (IonListener)ionEventChannel.getListenerProxy();

    public void addIonListener( IonListener listener ) {
        ionEventChannel.addListener( listener );
    }

    public void removeIonListener( IonListener listener ) {
        ionEventChannel.removeListener( listener );
    }

    /**
     * Adds kinetic energy to all the ions in the system
     * @param heat
     */
    public void addHeat( double heat ) {
        List ions = getIons();
        for( int i = 0; i < ions.size(); i++ ) {
            Ion ion = (Ion)ions.get( i );
            double speed0 = ion.getVelocity().getMagnitude();
            double speed1 = Math.sqrt( Math.max( MIN_SPEED, speed0 * speed0 + ( 2 * heat / ion.getMass() )));
            System.out.println( "speed1 = " + speed1 );
            ion.setVelocity( ion.getVelocity().normalize().scale( speed1 ));
        }
    }

    //----------------------------------------------------------------
    // Events and listeners
    //----------------------------------------------------------------

    public class IonEvent extends EventObject {
        public IonEvent( Object source ) {
            super( source );
            if( !( source instanceof Ion ) ) {
                throw new RuntimeException( "source of wrong type" );
            }
        }

        public Ion getIon() {
            return (Ion)getSource();
        }
    }

    public interface IonListener extends EventListener {
        void ionAdded( IonEvent event );

        void ionRemoved( IonEvent event );
    }

    public static class IonListenerAdapter implements IonListener {
        public void ionAdded( IonEvent event ) {
        }

        public void ionRemoved( IonEvent event ) {
        }
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    /**
     * Detects and handles collisions between ions and the vessel
     */
    private class IonVesselCollisionAgent implements ModelElement {
        IonVesselCollisionExpert ionVesselCollisionExpert = new IonVesselCollisionExpert( SolubleSaltsModel.this );

        public void stepInTime( double dt ) {
            for( int i = 0; i < numModelElements(); i++ ) {
                if( modelElementAt( i ) instanceof Ion ) {
                    Ion ion = (Ion)modelElementAt( i );
                    ionVesselCollisionExpert.detectAndDoCollision( ion, vessel );

                    for( int j = 0; j < numModelElements(); j++ ) {
                        if( modelElementAt( i ) != modelElementAt( j )
                            && modelElementAt( j ) instanceof Ion ) {
                            ionIonCollisionExpert.detectAndDoCollision( (Ion)modelElementAt( i ),
                                                                        (Ion)modelElementAt( j ) );
                        }
                    }
                }
            }
        }
    }

    /**
     * Tracks the creation and destruction of lattice instances
     */
    private class LatticeLifetimeTracker implements Lattice.InstanceLifetimeListener {
        public void instanceCreated( Lattice.InstanceLifetimeEvent event ) {
            addModelElement( event.getInstance() );
        }

        public void instanceDestroyed( Lattice.InstanceLifetimeEvent event ) {
            removeModelElement( event.getInstance() );
        }
    }
}
