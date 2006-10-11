package edu.colorado.phet.ec3.model;

/**
 * Provides a static method to calculate the 4th order
 * Runge-Kutta integration calculation.
 */
public class JavaTechRK4th {
    /**
     * Calculated a step of the integration of an ODE with
     * 4th order RK.
     *
     * @param t   independent variable
     * @param dt  step in the independent variable
     * @param var array holding the dependent variable
     * @param vel array holding the first derivative
     */
    public static void step( double t, double dt,
                             double[] var,
                             double[] vel,
                             Derivable func ) {
        double k1_var, k1_vel;
        double k2_var, k2_vel;
        double k3_var, k3_vel;
        double k4_var, k4_vel;

        for( int i = 0; i < var.length; i++ ) {
            k1_var = vel[i] * dt;
            k1_vel = func.deriv( i, var[i], vel[i], t ) * dt;

            k2_var = ( vel[i] + 0.5 * k1_vel ) * dt;
            k2_vel = func.deriv( i, var[i] + 0.5 * k1_var,
                                 vel[i] + 0.5 * k1_vel,
                                 t + 0.5 * dt ) * dt;

            k3_var = ( vel[i] + 0.5 * k2_vel ) * dt;
            k3_vel = func.deriv( i, var[i] + 0.5 * k2_var,
                                 vel[i] + 0.5 * k2_vel,
                                 t + 0.5 * dt ) * dt;

            k4_var = ( vel[i] + k3_vel ) * dt;
            k4_vel = func.deriv( i, var[i] + k3_var,
                                 vel[i] + k3_vel,
                                 t + dt ) * dt;

            var[i] = var[i] +
                     ( k1_var + 2.0 * k2_var
                       + 2.0 * k3_var + k4_var ) / 6.0;
            vel[i] = vel[i] +
                     ( k1_vel + 2.0 * k2_vel
                       + 2.0 * k3_vel + k4_vel ) / 6.0;
        }
        t += dt;
    } // step
} // RungeKutta4th