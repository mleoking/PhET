/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.model;

import edu.colorado.phet.boundstates.test.schmidt_lee.PotentialFunction;


public class PotentialFunctionAdapter implements PotentialFunction {
    
    private BSAbstractPotential _potential;
    
    public PotentialFunctionAdapter( BSAbstractPotential potential ) {
        _potential = potential;
    }
    
    public double evaluate( double x ) {
        return _potential.getEnergyAt( x );
    }

}
