/*
*   Class RungeKutta
*       requires interfaces DerivFunction and DerivnFunction
*
*   Contains the methods for the Runge-Kutta procedures
*   for solving single or solving sets of ordinary
*   differential equations (ODEs).
*
*   A single ODE is supplied by means of an interface,
*       DerivFunction
*   A set of ODEs is supplied by means of an interface,
*       DerivnFunction
*
*   WRITTEN BY: Dr Michael Thomas Flanagan
*
*   DATE:	 February 2002
*   UPDATE:  22 June 2003
*
*   DOCUMENTATION:
*   See Michael Thomas Flanagan's Java library on-line web page:
*   RungeKutta.html
*
*   Copyright (c) April 2004
*
*   PERMISSION TO COPY:
*   Permission to use, copy and modify this software and its documentation for
*   NON-COMMERCIAL purposes is granted, without fee, provided that an acknowledgement
*   to the author, Michael Thomas Flanagan at www.ee.ucl.ac.uk/~mflanaga, appears in all copies.
*
*   Dr Michael Thomas Flanagan makes no representations about the suitability
*   or fitness of the software for any or for a particular purpose.
*   Michael Thomas Flanagan shall not be liable for any damages suffered
*   as a result of using, modifying or distributing this software or its derivatives.
*
***************************************************************************************/

package edu.colorado.phet.energyskatepark.model;

// Interface for RungeKutta class (single ODE)

interface DerivFunction {
    double deriv( double x, double y );
}

// Interface for RungeKutta class (n ODEs)

interface DerivnFunction {
    double[] derivn( double x, double[] y );
}

// Class for Runge-Kutta solution of ordinary differential equations






