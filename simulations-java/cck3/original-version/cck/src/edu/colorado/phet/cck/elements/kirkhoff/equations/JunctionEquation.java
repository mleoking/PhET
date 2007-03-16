/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck.elements.kirkhoff.equations;

/**
 * User: Sam Reid
 * Date: Sep 3, 2003
 * Time: 5:21:30 AM
 * Copyright (c) Sep 3, 2003 by Sam Reid
 */
public class JunctionEquation extends KirkhoffEquation {
    public JunctionEquation( int numBranches ) {
        super( numBranches );
    }

    public String toString() {
        String current = "JunctionEquation:<current coeffs=[";
        for( int i = 0; i < numBranches; i++ ) {
            current += "" + data[i];
            if( i < numBranches - 1 ) {
                current += ", ";
            }
        }
        current += "]>";
        return current;
    }
}
