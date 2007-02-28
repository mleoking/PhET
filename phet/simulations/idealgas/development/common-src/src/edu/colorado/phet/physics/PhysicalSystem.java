/*
 * Class: PhysicalSystem
 * Package: edu.colorado.phet.physicaldomain
 *
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 9:46:11 AM
 */
package edu.colorado.phet.physics;

import edu.colorado.phet.controller.command.Command;
import edu.colorado.phet.controller.Config;
import edu.colorado.phet.physics.body.Particle;
import edu.colorado.phet.physics.body.PhysicalEntity;
import edu.colorado.phet.controller.command.RemoveParticleCmd;

import java.util.*;

/**
 * This class represents a system of physical entities. It is a singleton class, and is the
 * central class of the model part of a PhET application.
 * <p>
 * A PhysicalSystem contains a list of bodies that each has its own time-dependent behavior
 * and a list of external forces acting on the system. In addition, the system has a list of Laws.
 * These are objects that represent physical principles that govern the behavior of the
 * PhysicalSystem. Examples are a CollisionLaw that expresses how hard bodies collide with
 * each other, a MagneticFieldLaw that expresses how a magnetic field in the system
 * (which would be represented by a Particle) would interact with other bodies in the system, and
 * a GravitationalLaw that expresses how massive bodies interact.
 *
 */
public class PhysicalSystem extends Observable implements TimeStep {

    // The clock that drives the system
    private Clock systemClock;
    // The duration of each time step the system takes
    private float  timeStep;
    private int waitTime;
    // The bodies in the physical system
    private ArrayList bodies = new ArrayList( 1028 );
    // The list of forces in the system that are not produced by other entities
    // in the system. This could, for example, be the Earth's gravity, where the
    // Earth is not represented explicitly in the physical context
    private ArrayList externalForces = new ArrayList();
    // The list of physical laws that are to be applied to the physical context
    private ArrayList physicalLaws = new ArrayList();
    // Monitor for locking on requests from clients not running in the model thread
    private Object clientThreadMonitor = new Object();
    // A list of bodies to to be added to the system. This list is used by clients in
    // threads other than the model thread
    private ArrayList externalClientAddList = new ArrayList( 1028 );

    private TreeMap physicalEntities = new TreeMap();

    private ArrayList prepCommands = new ArrayList();

    /**
     *
     * @param config The configuration class for the specific application
     */
    public PhysicalSystem( Config config ) {

        // This should be a singleton class. If an s_instance already exists,
        // throw an exception and stop the program
        if( s_instance != null ) {
            throw new RuntimeException( "Attempt to instantiate more than one PhysicalSystem" );
        }

        this.timeStep = config.getTimeStep();
        this.waitTime = config.getWaitTime();
        systemClock = new Clock( this, timeStep, waitTime );
        s_instance = this;
    }

    /**
     *
     * @return True if the system is running, false otherwise
     */
    public boolean isRunning() {
        return systemClock.isRunning;
    }

    /**
     *
     */
    public void addPrepCmd( Command command ) {
        prepCommands.add( command );
    }

    /**
     *
     * @param physicalEntity
     * @param level
     */
    public void addPhysicalEntity( PhysicalEntity physicalEntity, int level ) {
        Integer levelKey = new Integer( level );
        List entityLevel = (List)physicalEntities.get( levelKey );
        if( entityLevel == null ) {
            entityLevel = new ArrayList();
            physicalEntities.put( levelKey, entityLevel );
        }
        entityLevel.add( physicalEntity );
    }

    /**
     *
     */
    public synchronized void clear() {

        clearParticles();
        this.physicalLaws = new ArrayList();
        getSystemClock().stop();
        this.systemClock = new Clock( this, timeStep, waitTime );
        getSystemClock().reset();

        // This statement was added 4/1/03 while working on the Laser app
        getSystemClock().start();

        // Notify observers of the system as a whole
        this.setChanged();
        this.notifyObservers();
    }

    /**
     *
      */
    public void clearParticles() {
        while( !bodies.isEmpty() ) {
            Particle particle = (Particle)bodies.remove( 0 );
            new RemoveParticleCmd( particle ).doIt();
        }
    }

    public Map getPhysicalEntities() {
        return physicalEntities;
    }

    public void reInitialize() {
        for( Iterator bodyIt = bodies.iterator(); bodyIt.hasNext(); ) {
            Particle body = (Particle)bodyIt.next();
            body.reInitialize();
        }
    }

    public void addExternalForce( Force force ) {
        externalForces.add( force );
    }

    public void removeExternalForce( Force force ) {
        externalForces.remove( force );
    }

    public void removeBody( PhysicalEntity p ) {
        /*synchronized( bodies )*/ {
            bodies.remove( p );
        }
        p = null;
    }

    public String toString() {
        return "particles=" + bodies + ", laws=" + externalForces;
    }



    public void addBody( PhysicalEntity body ) {
        synchronized( externalClientAddList ) {
//        synchronized( clientThreadMonitor ) {
            externalClientAddList.add( body );
        }
    }


    public List getBodies() {
        return bodies;
    }

    public void addLaw( Law lx ) {
        physicalLaws.add( lx );
    }

    public void addLaw( Force prop ) {
        Law x = new PropagatorLaw( prop );
        addLaw( x );
    }

    public int numLaws() {
        return externalForces.size();
    }

    public void removeAllLaws() {
        externalForces = new ArrayList();
    }

    public void remove( Law a ) {
        externalForces.remove( a );
    }

    public Law lawAt( int i ) {
        return (Law)externalForces.get( i );
    }

    public int numParticles() {
        return bodies.size();
    }

    public boolean containsParticle( PhysicalEntity p ) {
        return bodies.contains( p );
    }

    public Particle particleAt( int i ) {
        return (Particle)bodies.get( i );
    }

    /**
     * Causes the physical systems to move through time by a discrete interval
     * @param dt
     */
    public synchronized void stepInTime( float  dt ) {

//        Iterator physicalEntityLevelIterator = physicalEntities.entrySet().iterator();
//        while( physicalEntityLevelIterator.hasNext() ) {
//            List physicalEntityLevel = (List)((Map.Entry)physicalEntityLevelIterator.next()).getValue();
//            for( int i = 0; i < physicalEntityLevel.size(); i++ ) {
//                PhysicalEntity physicalEntity = (PhysicalEntity)physicalEntityLevel.get( i );
//                physicalEntity.stepInTime( dt );
//            }
//        }

        // Set the acceleration of all bodies to zero. The accelerations will
        // be determined by the forces in the system again for this step
        for( Iterator bIt = bodies.iterator(); bIt.hasNext(); ) {
            Particle body = (Particle)bIt.next();
            body.clearAcceleration();
        }

        // Execute all commands that are to prepare for the time step
        for( int i = 0; i < prepCommands.size(); i++ ) {
            Command command = (Command)prepCommands.get( i );
            command.doIt();
        }
        prepCommands.clear();

        // Allow all external forces to act on all bodies
        for( Iterator efIt = externalForces.iterator(); efIt.hasNext(); ) {
            Force force = (Force)efIt.next();
            for( Iterator pIt = bodies.iterator(); pIt.hasNext(); ) {
                Particle body = (Particle)pIt.next();
                force.act( body );
            }
        }

        // Have all bodies integrate their state changes
        for( Iterator bIt = bodies.iterator(); bIt.hasNext(); ) {
            Particle body = (Particle)bIt.next();
            body.stepInTime( dt );
        }

        // Apply all laws
        for( Iterator lawIt = physicalLaws.iterator(); lawIt.hasNext(); ) {
            Law law = (Law)lawIt.next();
            law.apply( 0, this );
        }

        synchronized( externalClientAddList ) {
            for( int i = 0; i < externalClientAddList.size(); i++ ) {
                Particle body = (Particle)externalClientAddList.get( i );
                if( body == null ) {
                    System.out.println( "" );
                }
                body.setPhysicalSystem( this );
                bodies.add( body );
            }
            externalClientAddList.clear();
        }

        // Notify observers of the PhysicalSystem
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Returns the total engergy in the system
     */
    private float  totalEnergy;
    public float  getTotalEnergy() {
        return totalEnergy;
    }

    private float  computeTotalEnergy() {
        float  totalEnergy = 0;
        synchronized( clientThreadMonitor ) {
            for( Iterator iterator = bodies.iterator(); iterator.hasNext(); ) {
                Particle body = (Particle)iterator.next();
                float  te = this.getBodyEnergy( body );
                if( Double.isNaN( te ) ) {
                    System.out.println( "eee" );
                } else {
                    totalEnergy += te;
                }
            }
        }
        return totalEnergy;
    }

    /**
     *
     * @return
     */
    public float  getTotalKineticEnergy() {
        float  totalKE = 0;
        synchronized( clientThreadMonitor ) {
            for( Iterator iterator = bodies.iterator(); iterator.hasNext(); ) {
                Particle body = (Particle)iterator.next();
                float  ke = body.getKineticEnergy();
                if( Double.isNaN( ke ) ) {
                    System.out.println( "eee" );
                } else {
                    totalKE += ke;
                }
            }
        }
        return totalKE;
    }

    /**
     * Returns the total energy of a Particle
     * @param body
     * @return The total energy of a Particle
     */
    public float getBodyEnergy( Particle body ) {
        return body.getKineticEnergy();
    }

    /**
     * Returns the total mass of the bodies in the system, ignoring those that
     * have infinite mass.
     * @return
     */
    public float  getTotalMass() {
        float  totalMass = 0;
        for( Iterator iterator = bodies.iterator(); iterator.hasNext(); ) {
            Particle body = (Particle)iterator.next();
            totalMass += ( body.getMass() != Double.POSITIVE_INFINITY ) ? body.getMass() : 0;
        }
        return totalMass;
    }

    /**
     * Moves a body to a y coordinate while preserving its total energy
     */
    public void relocateBodyY( Particle body, float  y ) {
        // Note we must not call setPosition() or setPositionY() because
        // those methods modify the body's previous position
        body.getPosition().setY( y );
    }


    //
    // Clock-related methods
    //

    /**
     * Sets the system clock.
     * @param dt
     * @param waitTime
     */
    public void setClockParams( float  dt, int waitTime ) {
        this.setClockParams( dt, waitTime, Float.MAX_VALUE );
    }

    /**
     * Sets the system clock with a time limit at which the system should stop.
     * @param dt
     * @param waitTime
     * @param timeLimit
     */
    public void setClockParams( float  dt, int waitTime, float  timeLimit ) {
        systemClock.setDt( dt );
        systemClock.setWaitTime( waitTime );
        systemClock.setTimeLimit( timeLimit );
    }

    /**
     * Runs the physical system. This starts the system's clock.
     */
    public void run() {
        systemClock.run();
    }

    /**
     * Starts the physical system.
     */
    public void start() {
        systemClock.start();
    }

    /**
     * Stops the physical system.
     */
    public void stop() {
        systemClock.stop();
    }

    /**
     * TODO: This should not be public. It should be hidden in the PhysicalSystem
     */
    public Clock getSystemClock() {
        return this.systemClock;
    }

    /**
     *
     * @return The size of the time step that the PhysicalSystem takes at each
     * clock tick
     */
    public float  getDt() {
        return systemClock.getDt();
    }

    /**
     *
     * @return The CPU time the PhysicalSystem waits at the end of each clock step
     */
    public float  getWaitTime() {
        return systemClock.getWaitTime();
    }

    /**
     *
     * @return The simulated time the PhysicalSystem has been running since the
     * last time it was started
     */
    public float  getRunningTime() {
        return systemClock.getRunningTime();
    }

    /**
     * Enables and disables the single-step mode of the PhysicalSystem. When single-step
     * mode is enabled, the system waits for the Enter key to be hit before making the
     * next time step.
     * @param isEnabled
     */
    public void setSingleStepEnabled( boolean isEnabled ) {
        systemClock.setSingleStepEnabled( isEnabled );
    }

    /**
     *
     * @return Whether single-step mode is enabled or not
     */
    public boolean isSingleStepEnabled() {
        return systemClock.isSingleStepEnabled();
    }


    //
    // Static fields and methods
    //

    private static PhysicalSystem s_instance = null;

    /**
     *
     * @return The singleton s_instance of the PhysicalSystem
     */
    public static PhysicalSystem instance() {
        return s_instance;
    }

    /**
     * Provided for test program use
     */
    public static void setInstanceNull() {
        s_instance = null;
    }

}
