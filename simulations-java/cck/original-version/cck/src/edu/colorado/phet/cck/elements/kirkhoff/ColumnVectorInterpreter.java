/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck.elements.kirkhoff;

import Jama.Matrix;

/**
 * User: Sam Reid
 * Date: Nov 12, 2003
 * Time: 12:37:20 PM
 * Copyright (c) Nov 12, 2003 by Sam Reid
 */
public class ColumnVectorInterpreter implements Interpreter {
    private Matrix solve;
    private MatrixTable mt;

    public ColumnVectorInterpreter( Matrix solve, MatrixTable mt ) {
        this.solve = solve;
        this.mt = mt;
    }

    public double getCurrent( int componentIndex ) {
        int entry = mt.getCurrentColumn( componentIndex );
        return solve.get( entry, 0 );
    }

    public double getVoltage( int componentIndex ) {
        int entry = mt.getVoltageColumn( componentIndex );
        return solve.get( entry, 0 );
    }

    public boolean isValidSolution() {
        return true;
    }
}
