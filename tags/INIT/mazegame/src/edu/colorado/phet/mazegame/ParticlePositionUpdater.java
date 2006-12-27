package edu.colorado.phet.mazegame;

public class ParticlePositionUpdater {
    private double x;		//position
    private double y;
    private double vX;		//velocity
    private double vY;
    private double aX;		//acceleration
    private double aY;

    public ParticlePositionUpdater(double x, double y) {
        this.x = x;
        this.y = y;
        this.vX = 0;
        this.vY = 0;
        this.aX = 0;
        this.aY = 0;
    }

    //Update position given position Vector
    public void updateWithPos(double X, double Y) {
        this.x = X;
        this.y = Y;
    }

    //Update position given velocity vector
    public void updateWithVel(double vX, double vY, double deltaTime) {
        x += vX * deltaTime;
        y += vY * deltaTime;
    }

    //Update position given acceleration vector
    public void updateWithAcc(double aX, double aY, double dt) {
        vX += aX * dt;
        vY += aY * dt;
        x += vX * dt + (0.5) * aX * dt * dt;
        y += vY * dt + (0.5) * aY * dt * dt;
    }
}