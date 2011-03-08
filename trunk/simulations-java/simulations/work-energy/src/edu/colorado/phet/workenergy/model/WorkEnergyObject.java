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
    private final MutableVector2D gravityForce = new MutableVector2D( 0, -9.8 * mass.getValue() );
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
                if ( position.getValue().getY() <= 0 || isUserControlled() ) {
                    netForce.setValue( new ImmutableVector2D() );
                }
                else {
                    netForce.setValue( appliedForce.getValue().getAddedInstance( frictionForce.getValue() ).getAddedInstance( gravityForce.getValue() ) );
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
                totalEnergy.setValue( kineticEnergy.getValue() + potentialEnergy.getValue() + thermalEnergy.getValue() );
            }
        };
        kineticEnergy.addObserver( updateEnergy );
        potentialEnergy.addObserver( updateEnergy );
        thermalEnergy.addObserver( updateEnergy );

        //Velocity changing should trigger KE changing
        final SimpleObserver updateKineticEnergy = new SimpleObserver() {
            public void update() {
                kineticEnergy.setValue( 1 / 2.0 * mass.getValue() * velocity.getValue().getMagnitudeSq() );
            }
        };
        mass.addObserver( updateKineticEnergy );
        velocity.addObserver( updateKineticEnergy );

        //Position changing should possibly trigger PE changing
        final SimpleObserver updatePotentialEnergy = new SimpleObserver() {
            public void update() {
                potentialEnergy.setValue( -mass.getValue() * gravity.getValue() * position.getValue().getY() );
            }
        };
        mass.addObserver( updatePotentialEnergy );
        gravity.addObserver( updatePotentialEnergy );
        position.addObserver( updatePotentialEnergy );

        final SimpleObserver updateGravityForce = new SimpleObserver() {
            public void update() {
                gravityForce.setValue( new ImmutableVector2D( 0, gravity.getValue() * mass.getValue() ) );
            }
        };
        gravity.addObserver( updateGravityForce );
        mass.addObserver( updateGravityForce );
    }

    private boolean isUserControlled() {
        return userControlled.getValue();
    }

    static {
//        System.out.println( "time\tposition\tvelocity\tacceleration\tpotential-energy\tkinetic-energy" );
    }

    public void stepInTime( double dt ) {
        double initialEnergy = getTotalEnergy();
        time.setValue( time.getValue() + dt );
        //Assumes driven by applied force, not user setting position manually
        acceleration.setValue( netForce.times( 1.0 / mass.getValue() ) );
        velocity.setValue( acceleration.times( dt ).getAddedInstance( velocity.getValue() ) );
        position.setValue( velocity.times( dt ).getAddedInstance( position.getValue() ) );

        double deltaEnergy = initialEnergy - getTotalEnergy();
        //find a good vertical location for the object so energy is conserved
        double deltaH = -deltaEnergy / mass.getValue() / gravity.getValue();
        position.setValue( new ImmutableVector2D( position.getValue().getX(), position.getValue().getY() + deltaH ) );

        if ( getY() <= 0 ) {
            position.setValue( new ImmutableVector2D( getX(), 0 ) );
            velocity.setValue( new ImmutableVector2D() );
            thermalEnergy.setValue( initialEnergy - getKineticEnergyProperty().getValue() - getPotentialEnergyProperty().getValue() );
        }
//        System.out.println( time.getValue() + "\t" + position.getValue().getY() + "\t" + velocity.getValue().getY() + "\t" + acceleration.getValue().getY() + "\t" + potentialEnergy.getValue() + "\t" + kineticEnergy.getValue() );
    }

    public void setAppliedForce( double fx, double fy ) {
        appliedForce.setValue( new ImmutableVector2D( fx, fy ) );
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
        snapshot.mass.setValue( mass.getValue() );
        snapshot.gravity.setValue( gravity.getValue() );
        snapshot.position.setValue( position.getValue() );
        snapshot.velocity.setValue( velocity.getValue() );
        snapshot.acceleration.setValue( acceleration.getValue() );
        snapshot.appliedForce.setValue( appliedForce.getValue() );
        snapshot.frictionForce.setValue( frictionForce.getValue() );
        snapshot.gravityForce.setValue( gravityForce.getValue() );
        snapshot.netForce.setValue( netForce.getValue() );
        snapshot.kineticEnergy.setValue( kineticEnergy.getValue() );
        snapshot.thermalEnergy.setValue( thermalEnergy.getValue() );
        snapshot.potentialEnergy.setValue( potentialEnergy.getValue() );
        snapshot.totalEnergy.setValue( totalEnergy.getValue() );
        snapshot.netWork.setValue( netWork.getValue() );
        snapshot.gravityWork.setValue( gravityWork.getValue() );
        snapshot.frictionWork.setValue( frictionWork.getValue() );
        snapshot.appliedWork.setValue( appliedWork.getValue() );
        return snapshot;
    }

    public void translate( double dx, double dy ) {
        position.setValue( new ImmutableVector2D( getX() + dx, getY() + dy ) );
    }

    public double getY() {
        return getPositionProperty().getValue().getY();
    }

    public double getX() {
        return getPositionProperty().getValue().getX();
    }

    public BufferedImage getImage() {
        return image;
    }

    public double getHeight() {
        return height.getValue();
    }

    public double getWidth() {
        return image.getWidth() * getHeight() / image.getHeight();
    }

    public void setUserControlled( boolean b ) {
        userControlled.setValue( b );
        velocity.setValue( new ImmutableVector2D() );
    }

    public Point2D getTopCenter() {
        return new Point2D.Double( getX(), getY() + getHeight() );
    }

    public DoubleProperty getTotalEnergyProperty() {
        return totalEnergy;
    }

    public double getTotalEnergy() {
        return totalEnergy.getValue();
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
        position.setValue( new ImmutableVector2D( x, y ));
    }
}