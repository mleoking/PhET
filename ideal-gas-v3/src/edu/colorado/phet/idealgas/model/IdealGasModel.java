/*
 * Class: IdealGasSystem
 * Package: edu.colorado.phet.idealgas.model
 *
 * Created by: Ron LeMaster
 * Date: Nov 8, 2002
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.collision.*;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.Command;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.controller.RemoveModelElementCmd;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class IdealGasModel extends BaseModel {

    //
    // Static fields and methods
    //
    // The distance that a molecule travels out from the box before it
    // is removed from the system.
    private static double s_escapeOffset = -30;

    IdealGasConfig config = new IdealGasConfig();
    private Gravity gravity;
    private float heatSource;
    private PressureSensingBox box;
    private boolean constantVolume = true;
    private boolean constantPressure = false;
    private double targetPressure = 0;
    private double averageMoleculeEnergy;
    // Accumulates kinetic energy added to the system in a single time step.
    private double deltaKE = 0;
    private ArrayList externalForces = new ArrayList();
    private List prepCommands = Collections.synchronizedList( new ArrayList() );

    private ArrayList bodies = new ArrayList();

    // todo: this attribute should proabably belong to the Pump
    private Class currentGasSpecies = HeavySpecies.class;

    public IdealGasModel( double dt ) {
        // Add a collision collisionGod
        CollisionGod collisionGod = new CollisionGod( this, dt,
                                                      new Rectangle2D.Double( 0, 0,
                                                                              600,
                                                                              600 ),
                                                      10, 10 );
        this.addModelElement( collisionGod );
        // Set up collision classes
//        new SphereHotAirBalloonContactDetector();
        new SphereSphereContactDetector();
//        new SphereWallContactDetector();
//        new SphereBoxContactDetector();

//        BalloonSphereCollision.register();
        SphereSphereCollision.register();
//        SphereWallCollision.register();
//        SphereBoxCollision.register();

    }

    /**
     *
     */
    public synchronized void clear() {

        // Clear our own items BEFORE we call the superclass. This is important!
        if( box != null ) {
            box.clear();
        }
        HeavySpecies.clear();
        LightSpecies.clear();

        //        super.clear();
        super.removeAllModelElements();
    }

    public boolean isConstantVolume() {
        return constantVolume;
    }

    public Class getCurrentGasSpecies() {
        return currentGasSpecies;
    }

    public void setConstantVolume( boolean constantVolume ) {
        this.constantVolume = constantVolume;
    }

    public boolean isConstantPressure() {
        return constantPressure;
    }

    public void setConstantPressure( boolean constantPressure ) {
        this.targetPressure = box.getPressure();
        this.constantPressure = constantPressure;
    }

    /**
     *
     */
    public void setGravity( Gravity gravity ) {
        // We remove first, then add. This handles situations where
        // gravity is already in the system, and when it is not
        if( gravity != null ) {
            this.removeExternalForce( gravity );
            this.addExternalForce( gravity );
        }
        else {
            this.removeExternalForce( this.gravity );
        }
        this.gravity = gravity;
        //        setChanged();
        notifyObservers();
    }

    /**
     *
     */
    public Gravity getGravity() {
        return gravity;
    }

    /**
     *
     */
    public void setHeatSource( float value ) {
        heatSource = value;
    }

    public float getHeatSource() {
        return heatSource;
    }

    /**
     *
     */
    public void addBox( PressureSensingBox box ) {
        this.box = box;
        //        this.box = (PressureSensingBox)box;
    }

    /**
     *
     */
    public Box2D getBox() {
        return box;
    }

    /**
     *
     */
    private ArrayList removeList = new ArrayList();

    public void removeBody( ModelElement p ) {
        super.removeModelElement( p );
        if( p instanceof Body ) {
            addKineticEnergyToSystem( -( (Body)p ).getKineticEnergy() );
            bodies.remove( p );
        }
    }

    public void addModelElement( ModelElement modelElement ) {
        super.addModelElement( modelElement );
        if( modelElement instanceof Body ) {
            Body body = (Body)modelElement;
            //            this.box.addContainedBody( body );
            addKineticEnergyToSystem( body.getKineticEnergy() );
            bodies.add( body );
        }
    }

    public synchronized /* 3/11/04 */ void addExternalForce( Force force ) {
        externalForces.add( force );
    }

    public synchronized /* 3/11/04 */ void removeExternalForce( Force force ) {
        externalForces.remove( force );
    }

    public void addKineticEnergyToSystem( double keIncr ) {
        deltaKE += keIncr;
    }

    /**
     * @return
     */
    public double getTotalKineticEnergy() {
        double totalKE = 0;
        //        synchronized( clientThreadMonitor ) {
        for( int i = 0; i < this.numModelElements(); i++ ) {
            //            for( Iterator iterator = bodies.iterator(); iterator.hasNext(); ) {
            //                Particle body = (Particle)iterator.next();
            ModelElement element = this.modelElementAt( i );
            if( element instanceof Body ) {
                Body body = (Body)element;
                double ke = body.getKineticEnergy();
                if( Double.isNaN( ke ) ) {
                    System.out.println( "Total kinetic energy in system NaN" );
                }
                else {
                    totalKE += ke;
                }
            }
        }
        //        }
        return totalKE;
    }

    public synchronized void stepInTime( float dt ) {
        // Managing energy step 1: Get the amount of kinetic energy in the system
        // before anything happens
        double totalPreKE = this.getTotalKineticEnergy();

        addHeatFromStove();

        super.stepInTime( dt );

        // Managing energy, step 2: Get the total kinetic energy in the system,
        // and adjust it if neccessary
        //        List bodies = this.getBodies();
        double totalPostKE = this.getTotalKineticEnergy();
        //        double totalPostKE = this.getTotalKineticEnergyDouble();
        // Adjust the kinetic energy of all particles to account for the heat we
        // added
        double ratio;
        double r1 = Math.sqrt( totalPreKE + deltaKE );
        deltaKE = 0;
        double r2 = Math.sqrt( totalPostKE );
        ratio = r1 / r2;

        if( totalPreKE != 0 && ratio != 1 ) {
            for( int i = 0; i < this.numModelElements(); i++ ) {
                ModelElement element = this.modelElementAt( i );
                if( element instanceof Body ) {
                    Body body = (Body)element;

                    //            List allBodies = this.getBodies();
                    //            for( int i = 0; !Double.isNaN( ratio ) && i < allBodies.size(); i++ ) {
                    //                Particle body = (Particle)allBodies.get( i );
                    double vx = body.getVelocity().getX();
                    double vy = body.getVelocity().getY();
                    vx *= ratio;
                    vy *= ratio;
                    if( Double.isNaN( ratio ) ) {
                        System.out.println( "halt!" );
                    }
                    else if( body.getKineticEnergy() > 0 ) {
                        //                    else if( body.getKineticEnergyDouble() > 0 ) {
                        body.setVelocity( (float)vx, (float)vy );
                    }
                }
            }
        }

        // Remove any molecules from the system that have escaped the box
        // The s_escapeOffset in the if statement is to let the molecules float outside
        // the box before they go away completely
        for( int i = 0; i < this.numModelElements(); i++ ) {
            ModelElement body = this.modelElementAt( i );
            //        for( int i = 0; i < bodies.size(); i++ ) {
            //            Object body = bodies.get( i );
            if( body instanceof GasMolecule ) {
                //            if( body instanceof GasMolecule ) {
                GasMolecule gasMolecule = (GasMolecule)body;
                if( getBox().isInOpening( gasMolecule )
                    && gasMolecule.getPosition().getY() < getBox().getMinY() + s_escapeOffset ) {
                    RemoveModelElementCmd removeCmdCmd = new RemoveModelElementCmd( this, gasMolecule );
                    removeList.add( removeCmdCmd );
                }
            }
        }
        for( int i = 0; i < removeList.size(); i++ ) {
            RemoveModelElementCmd removeCmdCmd = (RemoveModelElementCmd)removeList.get( i );
            removeCmdCmd.doIt();
        }
        removeList.clear();

        HeavySpecies.computeAveSpeed();
        HeavySpecies.computeTemperature();
        LightSpecies.computeAveSpeed();
        LightSpecies.computeTemperature();

        int numGasMolecules = 0;
        int totalEnergy = 0;
        for( int i = 0; i < numModelElements(); i++ ) {
            Object body = modelElementAt( i );
            //        for( int i = 0; i < bodies.size(); i++ ) {
            //            Object body = bodies.get( i );
            if( body instanceof GasMolecule ) {
                GasMolecule gasMolecule = (GasMolecule)body;
                totalEnergy += this.getBodyEnergy( gasMolecule );
                numGasMolecules++;
            }
        }
        averageMoleculeEnergy = numGasMolecules != 0 ?
                                totalEnergy / numGasMolecules
                                : 0;

        // Update either pressure or volume
        updateFreeParameter();
    }

    /**
     *
     */
    private void updateFreeParameter() {

        if( constantPressure ) {
            double currPressure = box.getPressure();

            double diffPressure = ( currPressure - targetPressure ) / targetPressure;
            if( currPressure > 0 && diffPressure > s_pressureAdjustmentFactor ) {
                box.setBounds( box.getMinX() - 1,
                               box.getMinY(),
                               box.getMaxX(),
                               box.getMaxY() );
            }
            else if( currPressure > 0 && diffPressure < -s_pressureAdjustmentFactor ) {
                box.setBounds( box.getMinX() + 1,
                               box.getMinY(),
                               box.getMaxX(),
                               box.getMaxY() );
            }
        }
    }

    private static final float s_pressureAdjustmentFactor = 0.05f;

    /**
     *
     */
    private void addHeatFromStove() {
        if( heatSource != 0 ) {
            //            for( int i = 0; i < getBodies().sizebj(); i++ ) {
            //                Object obj = getBodies().get( i );
            for( int i = 0; i < numModelElements(); i++ ) {
                Object modelElement = modelElementAt( i );
                if( modelElement instanceof CollidableBody && !IdealGasConfig.heatOnlyFromFloor ) {
                    CollidableBody body = (CollidableBody)modelElement;
                    double preKE = body.getKineticEnergy();
                    body.setVelocity( body.getVelocity().scale( 1 + heatSource / 10000 ) );
                    double incrKE = body.getKineticEnergy() - preKE;
                    this.addKineticEnergyToSystem( incrKE );
                }
            }
        }
    }

    /**
     *
     */
    //    private void adjustHeat( float percent ) {
    //        for( int i = 0; i < numModelElements(); i++ ) {
    //            Object modelElement = modelElementAt( i );
    //            if( modelElement instanceof CollidableBody ) {
    //                CollidableBody body = (CollidableBody)modelElement;
    //                //        for( int i = 0; i < getBodies().size(); i++ ) {
    //                //        for( Iterator bodyIt = getBodies().iterator(); bodyIt.hasNext(); ) {
    //                //            CollidableBody body = (CollidableBody)getBodies().get( i );
    //                //            CollidableBody body = (CollidableBody)bodyIt.next();
    //                body.setVelocity( body.getVelocity().scale( 1 + percent ) );
    //                //            body.setVelocity( body.getVelocity().multiply( 1 + percent ) );
    //            }
    //        }
    //    }

    /**
     * @param body
     * @return
     */
    public double getBodyEnergy( Body body ) {
        double energy = body.getKineticEnergy() + getPotentialEnergy( body );
        return energy;
    }

    /**
     * @param body
     * @return
     */
    private double getPotentialEnergy( Body body ) {
        double pe = 0;
        if( this.gravity != null ) {
            double gravity = this.getGravity().getAmt();
            if( gravity != 0 ) {
                double origin = this.getBox().getMaxY();
                if( body.getMass() != Float.POSITIVE_INFINITY ) {
                    pe = ( origin - body.getPosition().getY() ) * gravity * body.getMass();
                }
            }
        }
        return pe;
    }

    /**
     * Moves a body to a y coordinate while preserving its total energy
     */
    public void relocateBodyY( Body body, double newY ) {

        double currY = body.getPosition().getY();

        // todo: This was commented out, 9/14/04
        //        relocateBodyY( body, newY );

        // Adjust the body's kinetic energy to compensate for any change we may have
        // made in its potential ential
        if( this.gravity != null ) {
            double gravity = this.getGravity().getAmt();
            if( gravity != 0 ) {

                // Note that the inverted y axis that we use requires this
                // subtraction to be performed in the order shown.
                double dY = currY - newY;

                // What is the change in energy represented by moving the body?
                double dE = -( gravity * dY * body.getMass() );
                double currSpeed = body.getVelocity().getMagnitude();
                double newSpeed = (float)Math.sqrt( ( 2 * dE ) / body.getMass()
                                                    + currSpeed * currSpeed );

                // Flip the sign because our y axis is positive downward
                if( Double.isNaN( newSpeed ) ) {
                    newSpeed = currSpeed;
                }
                body.getVelocity().scale( newSpeed / currSpeed );
            }
        }
    }

    public double getAverageMoleculeEnergy() {
        return averageMoleculeEnergy;
    }

    public void addPrepCmd( Command command ) {
        prepCommands.add( command );
    }

    public List getBodies() {
        return bodies;
    }
}

