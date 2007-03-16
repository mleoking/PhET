package edu.colorado.phet.energyskatepark.model;

/**
 * This interface provides the derivative for ODE calculations.
 */
public interface Derivable {
    /**
     * Implement the interface method for the derivative
     * of the variables with respect to the independent
     * variable in an ordinary differential equation. Second
     * order ODE's can be turned into first order by a variable
     * substitution for the first derivative of a dependent variable.
     * This creates coupled first order ODEs.
     * Here the vel represents this  substitute variable. <br>
     *
     * @param i   identifies the dependent variable
     * @param pos dependent variable for which a derivative wrt the
     *            independent variable (t) will be calculated.
     * @param v   variable substituted for the the first deriative
     *            with respect to the independent variable.
     * @param t   the independent variable.
     */
    double deriv( int i, double pos, double v, double t );
} // Derivable