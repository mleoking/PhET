/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.physics2d.rotation;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 24, 2003
 * Time: 4:39:10 PM
 * Copyright (c) Jun 24, 2003 by Sam Reid
 */
public class RotationalParticle {
    private double angle = 0;//angle =0 represents level, facing right.
    private double angularVelocity = 0;
    private double angularAcceleration = 0;
    private double momentOfInertia = 1;
    private ArrayList torques = new ArrayList();
    private ArrayList impulseTorques = new ArrayList();

    public void setAngularVelocity(double angularVelocity) {
        this.angularVelocity = angularVelocity;
    }

    public Torque torqueAt(int i) {
        return (Torque) torques.get(i);
    }

    public Torque impulseTorqueAt(int i) {
        return (Torque) impulseTorques.get(i);
    }

    public void addImpulseTorque(Torque t) {
        impulseTorques.add(t);
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void doRotationalNewton(double dt) {
        double torque = 0;
        for (int i = 0; i < torques.size(); i++) {
            torque += torqueAt(i).getTorque();
        }
        for (int i = 0; i < impulseTorques.size(); i++) {
            torque += impulseTorqueAt(i).getTorque();
        }

        impulseTorques = new ArrayList();
        angularAcceleration = torque / momentOfInertia;
        double initAngVel = angularVelocity;
        angularVelocity += angularAcceleration * dt;
        double avgAngVel = (initAngVel + angularVelocity) / 2.0;
        angle += avgAngVel * dt;
    }

    public double getAngularVelocity() {
        return angularVelocity;
    }

    public void addTorque(Torque t) {
        torques.add(t);
    }
}
