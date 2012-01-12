// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.workenergy.model;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * @author Sam Reid
 */
public class WorkEnergyObject {
    private final DoubleProperty mass = new DoubleProperty( 20.0 );//kg
    private final DoubleProperty gravity = new DoubleProperty( -9.8 );//kg/m/m

    private final MutableVector2D position = new MutableVector2D( 0, 0 );
    private final MutableVector2D velocity = new MutableVector2D( 0, 0 );
    private final MutableVector2D acceleration = new MutableVector2D( 0, 0 );

    private final MutableVector2D appliedForce = new MutableVector2D( 0, 0 );
    private final MutableVector2D frictionForce = new MutableVector2D( 0, 0 );
    private final MutableVector2D gravityForce = new MutableVector2D( 0, -9.8 * mass.get() );
    private final MutableVector2D netForce = new MutableVector2D( 0, 0 );

    private final DoubleProperty kineticEnergy = new DoubleProperty( 0 );
    private final DoubleProperty thermalEnergy = new DoubleProperty( 0 );
    private final DoubleProperty potentialEnergy = new DoubleProperty( 0 );
    private final DoubleProperty totalEnergy = new DoubleProperty( 0 );

    private final DoubleProperty netWork = new DoubleProperty( 0 );
    private final DoubleProperty gravityWork = new DoubleProperty( 0 );
    private final DoubleProperty frictionWork = new DoubleProperty( 0 );
    private final DoubleProperty appliedWork = new DoubleProperty( 0 );
    private final BufferedImage image;//image is stored in the model so that we can obtain the correct aspect ratio
    private final DoubleProperty height;
    private final BooleanProperty userControlled = new BooleanProperty( false );

    private final DoubleProperty time = new DoubleProperty( 0.0 );

    public WorkEnergyObject( BufferedImage image, double height ) {
        this.image = image;
        this.height = new DoubleProperty( height );
        final SimpleObserver updateNetForce = new SimpleObserver() {
            public void update() {
                if ( position.get().getY() <= 0 || isUserControlled() ) {
                    netForce.set( new ImmutableVector2D() );
                }
                else {
                    netForce.set( appliedForce.get().getAddedInstance( frictionForce.get() ).getAddedInstance( gravityForce.get() ) );
                }
            }
        };
        userControlled.addObserver( updateNetForce );
        appliedForce.addObserver( updateNetForce );
        position.addObserver( updateNetForce );
        frictionForce.addObserver( updateNetForce );
        gravityForce.addObserver( updateNetForce );
        mass.addObserver( updateNetForce );

        SimpleObserver updateEnergy = new SimpleObserver() {
            public void update() {
                totalEnergy.set( kineticEnergy.get() + potentialEnergy.get() + thermalEnergy.get() );
            }
        };
        kineticEnergy.addObserver( updateEnergy );
        potentialEnergy.addObserver( updateEnergy );
        thermalEnergy.addObserver( updateEnergy );

        //Velocity changing should trigger KE changing
        final SimpleObserver updateKineticEnergy = new SimpleObserver() {
            public void update() {
                kineticEnergy.set( 1 / 2.0 * mass.get() * velocity.get().getMagnitudeSq() );
            }
        };
        mass.addObserver( updateKineticEnergy );
        velocity.addObserver( updateKineticEnergy );

        //Position changing should possibly trigger PE changing
        final SimpleObserver updatePotentialEnergy = new SimpleObserver() {
            public void update() {
                potentialEnergy.set( -mass.get() * gravity.get() * position.get().getY() );
            }
        };
        mass.addObserver( updatePotentialEnergy );
        gravity.addObserver( updatePotentialEnergy );
        position.addObserver( updatePotentialEnergy );

        final SimpleObserver updateGravityForce = new SimpleObserver() {
            public void update() {
                gravityForce.set( new ImmutableVector2D( 0, gravity.get() * mass.get() ) );
            }
        };
        gravity.addObserver( updateGravityForce );
        mass.addObserver( updateGravityForce );
    }

    private boolean isUserControlled() {
        return userControlled.get();
    }

    static {
//        System.out.println( "time\tposition\tvelocity\tacceleration\tpotential-energy\tkinetic-energy" );
    }

    public void stepInTime( double dt ) {
        double initialEnergy = getTotalEnergy();
        time.set( time.get() + dt );
        //Assumes driven by applied force, not user setting position manually
        acceleration.set( netForce.times( 1.0 / mass.get() ) );
        velocity.set( acceleration.times( dt ).getAddedInstance( velocity.get() ) );
        position.set( velocity.times( dt ).getAddedInstance( position.get() ) );

        double deltaEnergy = initialEnergy - getTotalEnergy();
        //find a good vertical location for the object so energy is conserved
        double deltaH = -deltaEnergy / mass.get() / gravity.get();
        position.set( new ImmutableVector2D( position.get().getX(), position.get().getY() + deltaH ) );

        if ( getY() <= 0 ) {
            position.set( new ImmutableVector2D( getX(), 0 ) );
            velocity.set( new ImmutableVector2D() );
            thermalEnergy.set( initialEnergy - getKineticEnergyProperty().get() - getPotentialEnergyProperty().get() );
        }
//        System.out.println( time.getValue() + "\t" + position.getValue().getY() + "\t" + velocity.getValue().getY() + "\t" + acceleration.getValue().getY() + "\t" + potentialEnergy.getValue() + "\t" + kineticEnergy.getValue() );
    }

    public void setAppliedForce( double fx, double fy ) {
        appliedForce.set( new ImmutableVector2D( fx, fy ) );
    }

    public MutableVector2D getPositionProperty() {
        return position;
    }

    public MutableVector2D getVelocityProperty() {
        return velocity;
    }

    public DoubleProperty getKineticEnergyProperty() {
        return kineticEnergy;
    }

    public WorkEnergyObject copy() {
        final WorkEnergyObject snapshot = new WorkEnergyObject( image, getHeight() );
        snapshot.mass.set( mass.get() );
        snapshot.gravity.set( gravity.get() );
        snapshot.position.set( position.get() );
        snapshot.velocity.set( velocity.get() );
        snapshot.acceleration.set( acceleration.get() );
        snapshot.appliedForce.set( appliedForce.get() );
        snapshot.frictionForce.set( frictionForce.get() );
        snapshot.gravityForce.set( gravityForce.get() );
        snapshot.netForce.set( netForce.get() );
        snapshot.kineticEnergy.set( kineticEnergy.get() );
        snapshot.thermalEnergy.set( thermalEnergy.get() );
        snapshot.potentialEnergy.set( potentialEnergy.get() );
        snapshot.totalEnergy.set( totalEnergy.get() );
        snapshot.netWork.set( netWork.get() );
        snapshot.gravityWork.set( gravityWork.get() );
        snapshot.frictionWork.set( frictionWork.get() );
        snapshot.appliedWork.set( appliedWork.get() );
        return snapshot;
    }

    public void translate( double dx, double dy ) {
        position.set( new ImmutableVector2D( getX() + dx, getY() + dy ) );
    }

    public double getY() {
        return getPositionProperty().get().getY();
    }

    public double getX() {
        return getPositionProperty().get().getX();
    }

    public BufferedImage getImage() {
        return image;
    }

    public double getHeight() {
        return height.get();
    }

    public double getWidth() {
        return image.getWidth() * getHeight() / image.getHeight();
    }

    public void setUserControlled( boolean b ) {
        userControlled.set( b );
        velocity.set( new ImmutableVector2D() );
    }

    public Point2D getTopCenter() {
        return new Point2D.Double( getX(), getY() + getHeight() );
    }

    public DoubleProperty getTotalEnergyProperty() {
        return totalEnergy;
    }

    public double getTotalEnergy() {
        return totalEnergy.get();
    }

    public DoubleProperty getPotentialEnergyProperty() {
        return potentialEnergy;
    }

    public DoubleProperty getThermalEnergyProperty() {
        return thermalEnergy;
    }

    public void reset() {
        mass.reset();
        gravity.reset();
        position.reset();
        velocity.reset();
        acceleration.reset();
        appliedForce.reset();
        frictionForce.reset();
        gravityForce.reset();
        netForce.reset();
        kineticEnergy.reset();
        thermalEnergy.reset();
        potentialEnergy.reset();
        totalEnergy.reset();
        netWork.reset();
        gravityWork.reset();
        frictionWork.reset();
        appliedWork.reset();
        height.reset();
        userControlled.reset();
        time.reset();
    }

    public void setPosition( double x, double y ) {
        position.set( new ImmutableVector2D( x, y ) );
    }
}