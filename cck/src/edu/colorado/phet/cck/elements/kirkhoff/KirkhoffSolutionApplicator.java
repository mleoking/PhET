package edu.colorado.phet.cck.elements.kirkhoff;

import edu.colorado.phet.cck.elements.kirkhoff.equations.Equation;

import java.util.logging.Logger;

/**
 * User: Sam Reid
 * Date: Sep 3, 2003
 * Time: 11:44:16 PM
 * Copyright (c) Sep 3, 2003 by Sam Reid
 */
public interface KirkhoffSolutionApplicator {
    public void apply(Equation[] ke, CircuitGraph circuit, MatrixTable mt);

    void setLogger(Logger logger);
}
