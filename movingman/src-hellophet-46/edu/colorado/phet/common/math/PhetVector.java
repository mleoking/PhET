/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.math;

/**
 * User: Sam Reid
 * Date: Jun 24, 2003
 * Time: 10:24:31 AM
 * Copyright (c) Jun 24, 2003 by Sam Reid
 */
public class PhetVector {
    double x;
    double y;

    public PhetVector() {
        this(0, 0);
    }

    public PhetVector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public PhetVector(PhetVector init) {
        this(init.x, init.y);
    }

    public String toString() {
        return "x=" + x + ", y=" + y;
    }

    public PhetVector getAddedInstance(double x, double y) {
        return new PhetVector(this.x + x, this.y + y);
    }

    public PhetVector getAddedInstance(PhetVector pv) {
        return new PhetVector(this.x + pv.x, this.y + pv.y);
    }

    public PhetVector getScaledInstance(double scale) {
        return new PhetVector(x * scale, y * scale);
    }

    public double getMagnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public PhetVector getNormalizedInstance() {
        double mag = getMagnitude();
        return new PhetVector(x / mag, y / mag);
    }

    public void add(double x, double y) {
        this.x += x;
        this.y += y;
    }

    public void add(PhetVector vector) {
        this.x += vector.x;
        this.y += vector.y;
    }

    public void subtract(double x, double y) {
        this.x -= x;
        this.y -= y;
    }

    public void normalize() {
        double mag = getMagnitude();
        this.x /= mag;
        this.y /= mag;
    }

    public double getAngle() {
        return Math.atan2(y, x);
    }

    public PhetVector getNormalVector() {
        double ang = getAngle() + Math.PI / 2;
        return new PhetVector(Math.cos(ang), Math.sin(ang));
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void scale(double scale) {
        this.x *= scale;
        this.y *= scale;
    }

    public void setMagnitude(double magnitude) {
        normalize();
        scale(magnitude);
    }

    public double dot(PhetVector vector) {
        double a = this.getMagnitude();
        double b = vector.getMagnitude();
        double ang = vector.getAngle() - this.getAngle();
        return a * b * Math.cos(ang);
    }

    public double getCrossProductMagnitude(PhetVector vector) {
        double a = this.getMagnitude();
        double b = vector.getMagnitude();
        double ang = vector.getAngle() - this.getAngle();
        return a * b * Math.sin(ang);
    }

    public PhetVector getSubtractedInstance(PhetVector vector) {
        return new PhetVector(this.x - vector.x, this.y - vector.y);
    }

    public PhetVector getSubtractedInstance(double x, double y) {
        return new PhetVector(this.x - x, this.y - y);
    }

    public static PhetVector parseAngleAndMagnitude(double angle, double v) {
        PhetVector vector = new PhetVector(Math.cos(angle), Math.sin(angle));
        vector.scale(v);
        return vector;
    }

}
