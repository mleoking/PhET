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

    // The vessel
    private Vessel vessel;
    private Point2D vesselLoc = new Point2D.Double( 50, 150 );
    private double vesselWidth = 800;
    private double vesselDepth = 500;

    // Collision mechanism objects
    IonIonCollisionExpert ionIonCollisionExpert = new IonIonCollisionExpert( this );
    private IonTracker ionTracker;

    public SolubleSaltsModel() {

        // Add an agent that will track the ions of various classes
        ionTracker = new IonTracker();
        addIonListener( ionTracker );

        // Add an agent that will track the creation and destruction of salt lattices
        {
            Lattice.InstanceLifetimeListener listener = new Lattice.InstanceLifetimeListener() {
                public void instanceCreated( Lattice.InstanceLifetimeEvent event ) {
                    addModelElement( event.getInstance() );
//                    System.out.println( "SolubleSaltsModel.instanceCreated" );
                }

                public void instanceDestroyed( Lattice.InstanceLifetimeEvent event ) {
                    removeModelElement( event.getInstance() );
//                    System.out.println( "SolubleSaltsModel.instanceDestroyed" );
                }
            };
            Lattice.addInstanceLifetimeListener( listener );
        }

        // Create a vessel
        vessel = new Vessel( vesselWidth, vesselDepth, vesselLoc );
        addModelElement( vessel );

        addModelElement( new ModelElement() {
            IonVesselCollisionExpert ionVesselCollisionExpert = new IonVesselCollisionExpert();

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
        } );

    }

    public void addModelElement( ModelElement modelElement ) {
        super.addModelElement( modelElement );

        if( modelElement instanceof Ion ) {
            ionListenerProxy.ionAdded( new IonEvent( modelElement ) );
        }
    }

    public void removeModelElement( ModelElement modelElement ) {
        super.removeModelElement( modelElement );

        if( modelElement instanceof Ion ) {
            ionListenerProxy.ionRemoved( new IonEvent( modelElement ) );
        }
    }

    public Vessel getVessel() {
        return vessel;
    }

    public int getNumIonsOfType( Class ionClass ) {
        return ionTracker.numIonsOfType( ionClass );
    }

    public List getIonsOfType( Class ionClass ) {
        return ionTracker.getIonsOfType( ionClass );
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

    private class CollisionMechanism implements ModelElement {
        private ArrayList experts = new ArrayList();

        void addCollisionExpert( CollisionExpert expert ) {
            experts.add( expert );
        }

        public void stepInTime( double dt ) {

        }
    }
}
