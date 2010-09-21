package edu.colorado.phet.mazegame;

import java.awt.*;

public class Particle {
    private double x;
    private double y;
    private int radius;

    public Particle( double x, double y ) {
        this.x = x;
        this.y = y;
        this.radius = 8;
    }

    public void setXY( double x, double y ) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public int getRadius() {
        return this.radius;
    }

}