package edu.colorado.phet.densityjava.model;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: May 18, 2009
 * Time: 7:16:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class Range {
    private double min;
    private double max;

    public Range(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public String toString() {
        return "[" + min + "," + max + "]";
    }

    public double getOverlap(Range b) {
        if (this.contains(b)) {
            return b.range();
        } else if (b.contains(this)) {
            return this.range();
        } else if (this.contains(b.min)) {
            return new Range(b.min, this.max).range();
        } else if (this.contains(b.max)) {
            return new Range(this.min, b.max).range();
        } else if (b.contains(this.min)) {
            return new Range(this.min, b.max).range();
        } else if (b.contains(this.max)) {
            return new Range(b.min, this.max).range();
        } else {
            return 0;
        }
    }

    public double range() {
        return max - min;
    }

    public boolean contains(Range b) {
        return contains(b.min) && contains(b.max);
    }

    public boolean contains(double x) {
        return x >= min && x <= max;
    }

    public boolean intersects(Range r) {
        //ranges intersect if either contains any end point of the other.
        return contains(r.min) || contains(r.max) || r.contains(min) || r.contains(max);
    }

    //gets the smallest distance between any two points in the ranges
    public double distanceTo(Range that) {
        if (intersects(that)) {
            return 0;
        } else {
            double highDist = Math.abs(this.max - that.min);
            double lowDist = Math.abs(this.min - that.max);
            return Math.min(highDist, lowDist);
        }
    }
}
