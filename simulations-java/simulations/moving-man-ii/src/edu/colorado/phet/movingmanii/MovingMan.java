package edu.colorado.phet.movingmanii;

import java.util.ArrayList;

/**
 * @author Sam Reid
 */
public class MovingMan {
    private MotionStrategy motionStrategy = new PositionDriven();
    private ArrayList<Listener> listeners = new ArrayList<Listener>();
    private double position;
    private double velocity;
    private double acceleration;

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
        return motionStrategy instanceof PositionDriven;
    }

    public boolean isVelocityDriven() {
        return motionStrategy instanceof VelocityDriven;
    }

    public boolean isAccelerationDriven() {
        return motionStrategy instanceof AccelerationDriven;
    }

    public void setVelocityDriven() {
        this.motionStrategy = new VelocityDriven();
        notifyListeners();
    }

    public void setPositionDriven() {
        this.motionStrategy = new PositionDriven();
        notifyListeners();
    }

    public void setAccelerationDriven() {
        this.motionStrategy = new AccelerationDriven();
        notifyListeners();
    }

    public ManState getState() {
        return new ManState(position, velocity, acceleration, motionStrategy);
    }

    public void setState(ManState manState) {
        setPosition(manState.getPosition());
        setVelocity(manState.getVelocity());
        setAcceleration(manState.getAcceleration());
        setMotionStrategy(manState.getMotionStrategy());//todo: could bundle listener notifications if necessary
    }

    private void setMotionStrategy(MotionStrategy motionStrategy) {
        this.motionStrategy = motionStrategy;
        notifyListeners();
    }

    static interface Listener {
        void changed();
    }

    public static class MotionStrategy {
    }

    public static class VelocityDriven extends MotionStrategy {
    }

    private class PositionDriven extends MotionStrategy {
    }

    private class AccelerationDriven extends MotionStrategy {
    }
}
