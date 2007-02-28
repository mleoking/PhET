/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.physics2d;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.coreadditions.math.PhetVector;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 24, 2003
 * Time: 4:22:09 PM
 * Copyright (c) Jun 24, 2003 by Sam Reid
 */
public class Particle2d extends ModelElement {
    private double x;
    private double y;

    private double vx;
    private double vy;
    private ArrayList forces = new ArrayList();
    private ArrayList impulse = new ArrayList();
    double mass = 1;

    public Particle2d(double x, double y, double vx, double vy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public void doLinearNewton(double dt) {
        double fx = 0;
        double fy = 0;
        for (int i = 0; i < forces.size(); i++) {
            Point2D.Double force = forceAt(i).getForce();
            fx += force.x;
            fy += force.y;
        }
        for (int i = 0; i < impulse.size(); i++) {
            Point2D.Double imp = impulseAt(i).getForce();
            fx += imp.x;
            fy += imp.y;
        }
        impulse = new ArrayList();

        double ax = fx / mass;
        double ay = fy / mass;
        double vxInit = vx;
        double vyInit = vy;
        vx += ax * dt;
        vy += ay * dt;
        double vxavg = (vxInit + vx) / 2.0;
        double vyavg = (vyInit + vy) / 2.0;
        x += vxavg * dt;
        y += vyavg * dt;
    }

    private Force impulseAt(int i) {
        return (Force) impulse.get(i);
    }

    private Force forceAt(int i) {
        return (Force) forces.get(i);
    }

    public void stepInTime(double dt) {
        doLinearNewton(dt);
        updateObservers();
    }

    public void addForce(Force f) {
        forces.add(f);
    }

    public double getKineticEnergy() {
        PhetVector vel = getVelocity();
        double mag = vel.getMagnitude();
        return .5 * mag * mag * mass;
    }

    private PhetVector getVelocity() {
        return new PhetVector(vx, vy);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        updateObservers();
    }

    public PhetVector getVelocityVector() {
        return new PhetVector(vx, vy);
    }

    public void setVelocity(double vx, double vy) {
        this.vx = vx;
        this.vy = vy;
    }

    public void addImpulseForce(Force f) {
        impulse.add(f);
    }

    public double getMass() {
        return mass;
    }

    public void removeForce(Force force) {
        forces.remove(force);
    }

    public void setY(double y) {
        this.y = y;
        updateObservers();
    }

    public void setX(double x) {
        this.x = x;
        updateObservers();
    }

    public void setVelocityX(double vx) {
        this.vx = vx;
        updateObservers();
    }

    public PhetVector getPosition() {
        return new PhetVector(x, y);
    }

    public void setVelocityY(double vy) {
        this.vy = vy;
        updateObservers();
    }
}
