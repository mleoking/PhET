/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck.elements.kirkhoff.equations;


/**
 * User: Sam Reid
 * Date: Sep 3, 2003
 * Time: 5:26:23 AM
 * Copyright (c) Sep 3, 2003 by Sam Reid
 */
public class LoopEquation extends KirkhoffEquation {
    public LoopEquation( int numBranches ) {
        super( numBranches );
    }

    public String toString() {
        String current = "LoopEquation:<voltage coeffs=[";
        for( int i = 0; i < numBranches; i++ ) {
            current += "" + data[i + numBranches];
            if( i < numBranches - 1 ) {
                current += ", ";
            }
        }
        current += "]>, RHS=" + getRHS();

        return current;
    }

    public boolean isAllZeros() {
        for( int i = 0; i < data.length; i++ ) {
            if( data[i] != 0 ) {
                return false;
            }
        }
        return true;
    }
}
