/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck.elements.kirkhoff.equations;


/**
 * User: Sam Reid
 * Date: Sep 3, 2003
 * Time: 5:24:19 AM
 * Copyright (c) Sep 3, 2003 by Sam Reid
 */
public class OhmicEquation extends KirkhoffEquation {
    public OhmicEquation( int numBranches ) {
        super( numBranches );
    }

    public String toString() {
        return "OhmicEquation:" + super.toString();
    }
}
