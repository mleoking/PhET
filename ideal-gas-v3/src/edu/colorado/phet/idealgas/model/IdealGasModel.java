/*
 * Class: IdealGasSystem
 * Package: edu.colorado.phet.idealgas.model
 *
 * Created by: Ron LeMaster
 * Date: Nov 8, 2002
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.collision.CollidableBody;
import edu.colorado.phet.collision.CollisionExpert;
import edu.colorado.phet.collision.CollisionGod;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.Command;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class IdealGasModel extends BaseModel {

    // The distance that a molecule travels out from the box before it
    // is removed from the system.
    private static double s_escapeOffset = -30;

    private Gravity gravity;
    private double heatSource;
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
    private CollisionGod collisionGod;
    // Flag used to tell if energy being added to the system should be tracked. If energy
    // is added while within the scope of doYourThing(), it must be tracked
    private boolean currentlyInStepInTimeMethod;

    private List collisionExperts = new ArrayList();
    private int heavySpeciesCnt;
    private int lightSpeciesCnt;
    private double averageHeavySpeciesSpeed;
    private double averageLightSpeciesSpeed;

    public IdealGasModel( double dt ) {
        // Add a collision collisionGod
        collisionGod = new CollisionGod( this, dt,
                                         new Rectangle2D.Double( 0, 0,
                                                                 600,
                                                                 600 ),
                                         10, 10 );

        //        collisionExperts.add( new SphereSphereExpert( this, dt ) );
        //        collisionExperts.add( new SphereBoxExpert( this ) );
        //        collisionExperts.add( new SphereHotAirBalloonExpert( this, dt ) );
        //        collisionExperts.add( new SphereHollowSphereExpert( this, dt ) );

        //        collisionExperts.add( new SphereHollowSphereExpert( this, dt ) );
        //        collisionExperts.add( new SphereSphereExpert( this, dt ) );
        //        collisionExperts.add( new SphereBoxExpert( this ) );

        //        new SphereHotAirBalloonContactDetector();
    }

    public void addCollisionExpert( CollisionExpert expert ) {
        collisionExperts.add( expert );
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
    public Gravity getGravity() {
        return gravity;
    }

    /**
     *
     */
    public void setHeatSource( double value ) {
        heatSource = value;
    }

    public double getHeatSource() {
        return heatSource;
    }

    /**
     *
     */
    public void addBox( PressureSensingBox box ) {
        this.box = box;
        this.addModelElement( box );
    }

    /**
     *
     */
    public PressureSensingBox getBox() {
        return box;
    }

    /**
     *
     */
    private ArrayList removeList = new ArrayList();

    public void removeBody( ModelElement p ) {
        super.removeModelElement( p );
        if( p instanceof Body ) {
            if( currentlyInStepInTimeMethod ) {
                addKineticEnergyToSystem( -( (Body)p ).getKineticEnergy() );
            }
            bodies.remove( p );
        }
    }

    public void addModelElement( ModelElement modelElement ) {
        if( modelElement instanceof Gravity ) {
            addExternalForce( modelElement );
            this.gravity = (Gravity)modelElement;
        }
        else {
            super.addModelElement( modelElement );
            if( modelElement instanceof Body ) {
                Body body = (Body)modelElement;

                if( body instanceof HeavySpecies ) {
                    heavySpeciesCnt++;
                }
                if( body instanceof LightSpecies ) {
                    lightSpeciesCnt++;
                }

                // Since model elements are added outside the doYourThing() loop, their energy
                // is accounted for already, and doesn't need to be added here
                if( currentlyInStepInTimeMethod ) {
                    addKineticEnergyToSystem( body.getKineticEnergy() );
                }
                bodies.add( body );
            }
        }
    }

    public void removeModelElement( ModelElement modelElement ) {
        if( modelElement instanceof Gravity ) {
            removeExternalForce( modelElement );
            this.gravity = null;
        }
        else {
            super.removeModelElement( modelElement );
        }
    }

    public synchronized /* 3/11/04 */ void addExternalForce( ModelElement force ) {
        externalForces.add( force );
    }

    public synchronized /* 3/11/04 */ void removeExternalForce( ModelElement force ) {
        externalForces.remove( force );
    }

    /**
     * To be called by objects that deliberately add energy to the system within the doYourThing() method
     *
     * @param keIncr
     */
    public void addKineticEnergyToSystem( double keIncr ) {
        deltaKE += keIncr;
    }

    private double getTotalEnergy() {
        double eTotal = 0;
        for( int i = 0; i < this.numModelElements(); i++ ) {
            ModelElement element = this.modelElementAt( i );
            if( element instanceof Body ) {
                Body body = (Body)element;
                eTotal += getBodyEnergy( body );
            }
        }
        return eTotal;
    }

    /**
     * @return
     */
    public double getTotalKineticEnergy() {
        double totalKE = 0;
        for( int i = 0; i < this.numModelElements(); i++ ) {
            ModelElement element = this.modelElementAt( i );
            if( element instanceof Body ) {
                Body body = (Body)element;
                double ke = body.getKineticEnergy();
                if( Double.isNaN( ke ) ) {
                    System.out.println( "Total kinetic energy in system NaN: " + body.getClass() );
                }
                else {
                    totalKE += ke;
                }
            }
        }
        return totalKE;
    }

    public /*synchronized */void stepInTime( double dt ) {
        // Managing energy step 1: Get the amount of kinetic energy in the system
        // before anything happens
        currentlyInStepInTimeMethod = true;
        double energyPre = this.getTotalEnergy();
        //        double totalPreKE = this.getTotalKineticEnergy();

        // Clear the accelerations on the bodies in the model
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            body.setAccelerationNoUpdate( 0, 0 );
        }

        // Apply external forces (e.g., gravity )
        for( int i = 0; i < externalForces.size(); i++ ) {
            ModelElement me = (ModelElement)externalForces.get( i );
            me.stepInTime( dt );
        }

        // Add or remove heat depending on the state of the stove
        addHeatFromStove();

        super.stepInTime( dt );

        collisionGod.doYourThing( dt, collisionExperts );
        //        collisionGod.doYourThing( dt );

        // Managing energy, step 2: Get the total kinetic energy in the system,
        // and adjust it if neccessary
        double energyPost = this.getTotalEnergy();
        //        double totalPostKE = this.getTotalKineticEnergy();
        double ratio;
        double r1 = Math.sqrt( energyPre + deltaKE );
        //        double r1 = Math.sqrt( totalPreKE + deltaKE );
        deltaKE = 0;
        double r2 = Math.sqrt( energyPost );
        //        double r2 = Math.sqrt( totalPostKE );
        ratio = r1 / r2;

        if( energyPre != 0 && ratio != 1 ) {
            //        if( totalPreKE != 0 && ratio != 1 ) {
            for( int i = 0; i < this.numModelElements(); i++ ) {
                ModelElement element = this.modelElementAt( i );
                if( element instanceof Body ) {
                    Body body = (Body)element;
                    double vx = body.getVelocity().getX();
                    double vy = body.getVelocity().getY();
                    vx *= ratio;
                    vy *= ratio;
                    if( Double.isNaN( ratio ) ) {
                        System.out.println( "halt!" );
                    }
                    else if( body.getKineticEnergy() > 0 ) {
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
            if( body instanceof GasMolecule ) {
                GasMolecule gasMolecule = (GasMolecule)body;
                if( /* getBox().isInOpening( gasMolecule )
                    && */ gasMolecule.getPosition().getY() < getBox().getMinY() + s_escapeOffset ) {
                    removeList.add( gasMolecule );
                }
            }
        }
        for( int i = 0; i < removeList.size(); i++ ) {
            GasMolecule gasMolecule = (GasMolecule)removeList.get( i );
            gasMolecule.removeYourselfFromSystem();
            this.bodies.remove( gasMolecule );
            super.removeModelElement( gasMolecule );
        }
        removeList.clear();

        int numGasMolecules = 0;
        int totalEnergy = 0;
        double totalLightSpeed = 0;
        double totalHeavySpeed = 0;
        for( int i = 0; i < numModelElements(); i++ ) {
            Object body = modelElementAt( i );
            if( body instanceof GasMolecule ) {
                GasMolecule gasMolecule = (GasMolecule)body;
                totalEnergy += this.getBodyEnergy( gasMolecule );
                if( body instanceof HeavySpecies ) {
                    totalHeavySpeed += gasMolecule.getSpeed();
                }
                if( body instanceof LightSpecies ) {
                    totalLightSpeed += gasMolecule.getSpeed();
                }
                numGasMolecules++;
            }
        }
        averageMoleculeEnergy = numGasMolecules != 0 ?
                                totalEnergy / numGasMolecules
                                : 0;
        averageHeavySpeciesSpeed = getHeavySpeciesCnt() > 0 ? totalHeavySpeed / getHeavySpeciesCnt() : 0;
//        System.out.println( "averageHeavySpeciesSpeed = " + averageHeavySpeciesSpeed );

        averageLightSpeciesSpeed = getLightSpeciesCnt() > 0 ? totalLightSpeed / getLightSpeciesCnt() : 0;
        currentlyInStepInTimeMethod = false;


        // Update either pressure or volume
        updateFreeParameter();

        notifyObservers();
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
                    if( currentlyInStepInTimeMethod ) {
                        this.addKineticEnergyToSystem( incrKE );
                    }
                }
            }
        }
    }

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
    public double getPotentialEnergy( Body body ) {
        double pe = 0;
        if( this.gravity != null ) {
            double gravity = this.getGravity().getAmt();
            if( gravity != 0 ) {
                double origin = this.getBox().getMaxY();
                if( body.getMass() != Double.POSITIVE_INFINITY ) {
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

    public int getHeavySpeciesCnt() {
        return heavySpeciesCnt;
    }

    public int getLightSpeciesCnt() {
        return lightSpeciesCnt;
    }

    public double getHeavySpeciesAveSpeed() {
//        System.out.println( "averageHeavySpeciesSpeed - A = " + averageHeavySpeciesSpeed );
        return averageHeavySpeciesSpeed;
    }

    public double getLightSpeciesAveSpeed() {
        return averageLightSpeciesSpeed;
    }
}

