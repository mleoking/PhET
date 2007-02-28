/*
 * Class: IdealGasSystem
 * Package: edu.colorado.phet.idealgas.physics
 *
 * Created by: Ron LeMaster
 * Date: Nov 8, 2002
 */
package edu.colorado.phet.idealgas.physics;

import edu.colorado.phet.idealgas.controller.IdealGasConfig;
import edu.colorado.phet.physics.collision.Box2D;
import edu.colorado.phet.physics.Gravity;
import edu.colorado.phet.physics.PhysicalSystem;
import edu.colorado.phet.physics.Vector2D;
import edu.colorado.phet.physics.collision.CollidableBody;
import edu.colorado.phet.physics.body.Particle;
import edu.colorado.phet.idealgas.controller.RemoveParticleCommand;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class IdealGasSystem extends PhysicalSystem {

    private Gravity gravity;
    private float heatSource;
    private PressureSensingBox box;
    private boolean constantVolume = true;
    private boolean constantPressure = false;
    private float targetPressure = 0;

    /**
     *
     */
    public IdealGasSystem() {
        super( new IdealGasConfig() );
    }

    /**
     *
     */
    public void clear() {

        // Clear our own items BEFORE we call the superclass. This is important!
        if( box != null ) {
            box.clear();
        }
        HeavySpecies.clear();
        LightSpecies.clear();
        super.clear();
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
    public void setGravity( Gravity gravity ) {
        // We remove first, then add. This handles situations where
        // gravity is already in the system, and when it is not
        if( gravity != null ) {
            super.removeExternalForce( gravity );
            super.addExternalForce( gravity );
        }
        else {
            super.removeExternalForce( this.gravity );
        }
        this.gravity = gravity;
        setChanged();
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

    /**
     *
     */
    public void addBox( Box2D box ) {
        this.box = (PressureSensingBox)box;
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

    public void addBody( CollidableBody body ) {
        super.addBody( body );
        this.box.addContainedBody( body );
    }

    public void stepInTime( float dt ) {

        // TODO: see if we can avoid the synchronized block
        List bodies = this.getBodies();
        synchronized( bodies ) {

            addHeatFromStove();

            super.stepInTime( dt );

            // Remove any molecules from the system that have escaped the box
            // The s_escapeOffset in the if statement is to let the molecules float outside
            // the box before they go away completely
            for( int i = 0; i < bodies.size(); i++ ) {
                Object body = bodies.get( i );
                if( body instanceof GasMolecule ) {
                    GasMolecule gasMolecule = (GasMolecule)body;
                    if( getBox().isInOpening( gasMolecule )
                            && gasMolecule.getPosition().getY() < getBox().getMinY() + s_escapeOffset ) {
                        RemoveParticleCommand removeCmd = new RemoveParticleCommand( gasMolecule );
                        removeList.add( removeCmd );
                    }
                }
            }
            for( int i = 0; i < removeList.size(); i++ ) {
                RemoveParticleCommand removeCmd = (RemoveParticleCommand)removeList.get( i );
                removeCmd.doIt();
            }
            removeList.clear();

            HeavySpecies.computeAveSpeed();
            HeavySpecies.computeTemperature();
            LightSpecies.computeAveSpeed();
            LightSpecies.computeTemperature();

            // Update either pressure or volume
            updateFreeParameter();
        }
    }

    /**
     *
     */
    private void updateFreeParameter() {

        if( constantPressure ) {
            float currPressure = box.getPressure();

            float diffPressure = ( currPressure - targetPressure ) / targetPressure;
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
            synchronized( getBodies() ) {
                for( Iterator bodyIt = getBodies().iterator(); bodyIt.hasNext(); ) {
                    Object obj = bodyIt.next();
                    if( obj instanceof CollidableBody ) {
                        CollidableBody body = (CollidableBody)obj;
                        body.setVelocity( body.getVelocity().multiply( 1 + heatSource / 10000 ) );
                    }
                }
            }
        }
    }

    /**
     *
     */
    private void adjustHeat( float percent ) {
        for( Iterator bodyIt = getBodies().iterator(); bodyIt.hasNext(); ) {
            CollidableBody body = (CollidableBody)bodyIt.next();
            body.setVelocity( body.getVelocity().multiply( 1 + percent ) );
        }
    }

    /**
     *
     * @param body
     * @return
     */
    public float getBodyEnergy( Particle body ) {
        // The super class will give up the kinetic energy. We need to add
        // the potential energy
        float energy = super.getBodyEnergy( body ) + getPotentialEnergy( body );
        return energy;
    }

    /**
     *
     * @param body
     * @return
     */
    private float getPotentialEnergy( Particle body ) {
        float pe = 0;
        if( this.gravity != null ) {
            float gravity = this.getGravity().getAmt();
            if( gravity != 0 ) {
                float origin = this.getBox().getMaxY();
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
    public void relocateBodyY( CollidableBody body, float newY ) {
        float currY = body.getPosition().getY();
        super.relocateBodyY( body, newY );

        // Adjust the body's kinetic energy to compensate for any change we may have
        // made in its potential ential
        if( this.gravity != null ) {
            float gravity = this.getGravity().getAmt();
            if( gravity != 0 ) {

                // Note that the inverted y axis that we use requires this
                // subtraction to be performed in the order shown.
                float dY = currY - newY;

                // What is the change in energy represented by moving the body?
                float dE = -( gravity * dY * body.getMass() );
                float currSpeed = body.getSpeed();
                float newSpeed = (float)Math.sqrt( ( 2 * dE ) / body.getMass()
                                                   + currSpeed * currSpeed );

                // Flip the sign because our y axis is positive downward
                if( Double.isNaN( newSpeed ) ) {
//                    System.out.println( "newSpeed" );
//                    newSpeed = 0;
                    newSpeed = currSpeed;
                }
                body.getVelocity().multiply( newSpeed / currSpeed );
            }
        }
    }

    /**
     *
     */
    public void adjustSystemEnergy( float eRatio ) {
        if( true ) {
            this.adjustHeat( eRatio );
            return;
        }

        List bodies = this.getBodies();
        for( int i = 0; i < bodies.size(); i++ ) {
            Particle body = (Particle)bodies.get( i );
            float pe = getPotentialEnergy( body );

            float m = body.getMass();
            float r = eRatio;
            float sSq = body.getVelocity().getLength() * body.getVelocity().getLength();
            float q = ( 2 / m ) * ( ( r * ( ( m * sSq / 2 ) + pe ) ) - pe );
            float sPrime = (float)Math.sqrt( q );
            Vector2D vHat = new Vector2D( body.getVelocity() );
            vHat.normalize();
            Vector2D vPrime = vHat;
            vPrime = vPrime.multiply( sPrime );
        }
    }

    //
    // Static fields and methods
    //
    // The distance that a molecule travels out from the box before it
    // is removed from the system.
    private double s_escapeOffset = -30;
}

