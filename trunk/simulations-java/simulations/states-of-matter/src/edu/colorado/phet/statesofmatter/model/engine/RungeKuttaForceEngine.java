package edu.colorado.phet.statesofmatter.model.engine;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;

public class RungeKuttaForceEngine implements ForceEngine {
    private double[] x, y, vx, vy;
    // temporary variables for 4th order Runge-Kutta
    private double[] x1, y1, vx1, vy1, vx2, vy2, fx1, fy1, fx2, fy2, fx3, fy3;

    private Vector2D.Double[] newPositions, newVelocities;

    private EngineConfig config;
    private StatesOfMatterParticle[] particles;
    private double rangeSquared;

    private static final double CM_TO_PIXELS = 35;
    private static final double PIXELS_TO_CM = 1.0 / CM_TO_PIXELS;

    private double radius(int i) {
        return particles[i].getRadius() * CM_TO_PIXELS;
    }

    private double inverseMass(int i) {
        return particles[i].getInverseMass();
    }

    private double width() {
        return config.bounds.width * CM_TO_PIXELS;
    }

    private double height() {
        return config.bounds.height * CM_TO_PIXELS;
    }

    // returns Lennard-Jones potential
    private double phi(double s2) {
        double s4, s6, s12;

        if (s2 > rangeSquared) {
            return 0.0;
        }

        s4 = s2 * s2;
        s6 = s2 * s4;
        s12 = s6 * s6;
        return 4.0 / s12 - 4.0 / s6;
    }

    private double phiWall(double s2) {
        double s4, s6, s12;

        if (s2 > 1.2599210498949) {
            return 0.0;
        }

        s4 = s2 * s2;
        s6 = s2 * s4;
        s12 = s6 * s6;
        return 1.0 + 4.0 / s12 - 4.0 / s6;
    }

// returns Lennard-Jones force divided by scaled radius.
// it is only a function of scaled radius squared

    private double phiPrime(double s2) {
        double s4, s6, s8, s14;

        if (s2 > rangeSquared) {
            return 0.0;
        }

        s4 = s2 * s2;
        s6 = s2 * s4;
        s8 = s4 * s4;
        s14 = s6 * s8;
        return 24.0 / s8 - 48.0 / s14;
    }

    private double phiPrimeWall(double s2) {
        //static final rangeSquared=Math.pow(2.0,1.0/3.0);
        double s4, s6, s8, s14;

        if (s2 > 1.2599210498949) {
            return 0.0;
        }

        s4 = s2 * s2;
        s6 = s2 * s4;
        s8 = s4 * s4;
        s14 = s6 * s8;
        return (24.0 / s8 - 48.0 / s14);
    }

    private void calculateForces(double x[], double y[], double vx[], double vy[], double fx[], double fy[]) {
        double dx, dy, s, s2, rij, rijInverseSquared, phiPr;

        for (int i = 0; i < particles.length; i++) {
            fx[i] = 0.0; //-gamma*vx[i];
            fy[i] = config.gravity; //-gamma*vy[i];
        }

        // wall forces

        //left wall
        for (int i = 0; i < particles.length; i++) {
            dx = x[i];
            s  = dx / radius(i);
            s2 = s * s;
            fx[i] -= inverseMass(i) * dx * phiPrimeWall(s2) / (radius(i) * radius(i));
        }

        //right wall
        for (int i = 0; i < particles.length; i++) {
            dx = width() - x[i];
            s  = dx / radius(i);
            s2 = s * s;
            fx[i] += inverseMass(i) * dx * phiPrimeWall(s2) / (radius(i) * radius(i));
        }

        //top wall
        for (int i = 0; i < particles.length; i++) {
            dy = y[i] - config.piston;
            s  = dy / radius(i);
            s2 = s * s;
            fy[i] -= inverseMass(i) * dy * phiPrimeWall(s2) / (radius(i) * radius(i));
        }

        //bottom wall
        for (int i = 0; i < particles.length; i++) {
            dy = height() - y[i];
            s  = dy / radius(i);
            s2 = s * s;
            fy[i] += inverseMass(i) * dy * phiPrimeWall(s2) / (radius(i) * radius(i));
        }

        //interactions, full force calculation
        for (int i = 0; i < particles.length; i++) {
            for (int j = i + 1; j < particles.length; j++) {
                dx = x[i] - x[j];
                dy = y[i] - y[j];
                rij = radius(i) + radius(j);
                rijInverseSquared = 1.0 / (rij * rij);
                s2 = (dx * dx + dy * dy) * rijInverseSquared;
                phiPr = phiPrime(s2) * rijInverseSquared;
                fx[i] -= dx * phiPr * inverseMass(i);
                fy[i] -= dy * phiPr * inverseMass(i);
                fx[j] += dx * phiPr * inverseMass(j);
                fy[j] += dy * phiPr * inverseMass(j);
            }
        }
    }

    private void updatePositions() {
        double oneSixth = 1.0 / 6.0;

        // step 1 for second order runga-kutta

        calculateForces(x, y, vx, vy, fx1, fy1);

        for (int i = 0; i < particles.length; i++) {
            x1[i]  = x[i] + 0.5 * vx[i];
            y1[i]  = y[i] + 0.5 * vy[i];
            vx1[i] = vx[i] + 0.5 * fx1[i];
            vy1[i] = vy[i] + 0.5 * fy1[i];
        }

        // step 2 for second order runga-kutta

        calculateForces(x1, y1, vx1, vy1, fx2, fy2);

        for (int i = 0; i < particles.length; i++) {
            x1[i] = x[i] + 0.5 * vx1[i];
            y1[i] = y[i] + 0.5 * vy1[i];
            vx2[i] = vx[i] + 0.5 * fx2[i];
            vy2[i] = vy[i] + 0.5 * fy2[i];
        }

        // step 3 for second order runga-kutta

        calculateForces(x1, y1, vx2, vy2, fx3, fy3);

        for (int i = 0; i < particles.length; i++) {
            x1[i] = x[i] + vx2[i];
            y1[i] = y[i] + vy2[i];

            vx1[i] += vx2[i];
            vy1[i] += vy2[i];

            vx2[i] = vx[i] + fx3[i];
            vy2[i] = vy[i] + fy3[i];

            fx2[i] += fx3[i];
            fy2[i] += fy3[i];
        }

        // step 4 for second order runga-kutta

        calculateForces(x1, y1, vx2, vy2, fx3, fy3);


        for (int i = 0; i < particles.length; i++) {
            x[i]  += oneSixth * (vx[i] + 2 * vx1[i] + vx2[i]);
            y[i]  += oneSixth * (vy[i] + 2 * vy1[i] + vy2[i]);
            vx[i] += oneSixth * (fx1[i] + 2 * fx2[i] + fx3[i]);
            vy[i] += oneSixth * (fy1[i] + 2 * fy2[i] + fy3[i]);
        }
    }

	double potentialEnergy() {
		double dx,dy,s2;
		double pe=0.0;

		for (int i=0;i<particles.length;i++) pe += (height()-y[i])* config.gravity/ inverseMass(i);

		for (int i=0;i<particles.length;i++)
		{
			s2=x[i]*x[i]/(radius(i)* radius(i));
			pe += phiWall(s2);
			s2=(width()-x[i])*(width()-x[i])/(radius(i)* radius(i));
			pe += phiWall(s2);
			s2=y[i]*y[i]/(radius(i)* radius(i));
			pe += phiWall(s2);
			s2=(height()-y[i])*(height()-y[i])/(radius(i)* radius(i));
			pe += phiWall(s2);
		}

		for (int i=0;i<particles.length;i++) for (int j=i+1;j<particles.length;j++)
		{
			dx=x[i]-x[j];
			dy=y[i]-y[j];
			s2=(dx*dx+dy*dy)/((radius(i)+ radius(j))*(radius(i)+ radius(j)));
			pe += phi(s2);
		}

		return pe;

	}

    public ForceComputation compute(StatesOfMatterParticle[] particles, EngineConfig descriptor) {
        initialize(descriptor, particles);

        allocateArrays();

        packDataIntoArrays();

        updatePositions();

        storeResultsInVectorArrays(particles);

        return new ForceComputation(newPositions, newVelocities);
    }

    public double getPotentialEnergy() {
        return potentialEnergy();
    }

    private void initialize(EngineConfig config, StatesOfMatterParticle[] particles) {
        this.config       = config;
        this.particles    = particles;
        this.rangeSquared = config.range * config.range;
    }

    private void storeResultsInVectorArrays(StatesOfMatterParticle[] particles) {
        for (int i = 0; i < particles.length; i++) {
            newPositions[i].setComponents((x[i] * PIXELS_TO_CM) + config.bounds.x,
                                          (y[i] * PIXELS_TO_CM) + config.bounds.y);

            newVelocities[i].setComponents(vx[i], vy[i]);
        }
    }

    private void packDataIntoArrays() {
        for (int i = 0; i < particles.length; i++) {
            x[i]  = (particles[i].getX() - config.bounds.x) * CM_TO_PIXELS;
            y[i]  = (particles[i].getY() - config.bounds.y) * CM_TO_PIXELS;

            vx[i] = particles[i].getVx();
            vy[i] = particles[i].getVy();
        }
    }

    private void allocateArrays() {
        if (x == null || x.length != particles.length) {
            x   = new double[particles.length];
            y   = new double[particles.length];
            vx  = new double[particles.length];
            vy  = new double[particles.length];
            x1  = new double[particles.length];
            y1  = new double[particles.length];
            vx1 = new double[particles.length];
            vy1 = new double[particles.length];
            vx2 = new double[particles.length];
            vy2 = new double[particles.length];
            fx1 = new double[particles.length];
            fy1 = new double[particles.length];
            fx2 = new double[particles.length];
            fy2 = new double[particles.length];
            fx3 = new double[particles.length];
            fy3 = new double[particles.length];

            newPositions  = new Vector2D.Double[particles.length];
            newVelocities = new Vector2D.Double[particles.length];

            for (int i = 0; i < particles.length; i++) {
                newPositions[i]  = new Vector2D.Double();
                newVelocities[i] = new Vector2D.Double();
            }
        }
    }
}
