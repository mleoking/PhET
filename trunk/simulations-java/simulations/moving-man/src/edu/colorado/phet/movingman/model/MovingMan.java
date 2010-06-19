package edu.colorado.phet.movingman.model;

import java.util.ArrayList;

/**
 * @author Sam Reid
 */
public class MovingMan {
    private MotionStrategy motionStrategy = POSITION_DRIVEN;
    private ArrayList<Listener> listeners = new ArrayList<Listener>();
    private double position;
    private double velocity;
    private double acceleration;

    public MovingMan() {
        resetAll();
    }

    public void resetAll() {
        setPosition(0.0);
        setVelocity(0.0);
        setAcceleration(0.0);
        setPositionDriven();
    }

    public void setPosition(double x) {
        this.position = x;
//        System.out.println("x = " + x);
        notifyListeners();
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public double getPosition() {
        return position;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
        notifyListeners();
    }

    private void notifyListeners() {
        for (Listener listener : listeners) {
            listener.changed();
        }
    }

    public void setAcceleration(double value) {
        this.acceleration = value;
        notifyListeners();
    }

    public double getAcceleration() {
        return acceleration;
    }

    public boolean isPositionDriven() {
        return motionStrategy == POSITION_DRIVEN;
    }

    public boolean isVelocityDriven() {
        return motionStrategy == VELOCITY_DRIVEN;
    }

    public boolean isAccelerationDriven() {
        return motionStrategy == ACCELERATION_DRIVEN;
    }

    public void setVelocityDriven() {
        this.motionStrategy = VELOCITY_DRIVEN;
        notifyListeners();
    }

    public void setPositionDriven() {
        this.motionStrategy = POSITION_DRIVEN;
        notifyListeners();
    }

    public void setAccelerationDriven() {
        this.motionStrategy = ACCELERATION_DRIVEN;
        notifyListeners();
    }

    public ManState getState() {
        return new ManState(position, velocity, acceleration, motionStrategy);
    }

    public void setState(ManState manState) {
        setPosition(manState.getPosition());
        setVelocity(manState.getVelocity());
        setAcceleration(manState.getAcceleration());
        setMotionStrategy(manState.getMotionStrategy());//todo: could bundle listener notifications for performance improvements, if necessary
    }

    private void setMotionStrategy(MotionStrategy motionStrategy) {
        this.motionStrategy = motionStrategy;
        notifyListeners();
    }

    public MotionStrategy getMotionStrategy() {
        return motionStrategy;
    }

    public static interface Listener {
        void changed();
    }

    /**
     * Mostly used as an object ID for type purposes, but can be used to configure the MovingMan
     */
    public static interface MotionStrategy {
        void apply(MovingMan man);
    }

    public static MotionStrategy POSITION_DRIVEN = new MotionStrategy() {
        public void apply(MovingMan man) {
            man.setPositionDriven();
        }

        public String toString() {
            return "Position driven";
        }
    };


    public static MotionStrategy VELOCITY_DRIVEN = new MotionStrategy() {
        public void apply(MovingMan man) {
            man.setVelocityDriven();
        }

        public String toString() {
            return "Velocity driven";
        }
    };

    public static MotionStrategy ACCELERATION_DRIVEN = new MotionStrategy() {
        public void apply(MovingMan man) {
            man.setAccelerationDriven();
        }

        public String toString() {
            return "Acceleration driven";
        }
    };
}