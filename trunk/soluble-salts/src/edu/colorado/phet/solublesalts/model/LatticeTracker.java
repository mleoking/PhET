/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: 
 * Branch : $Name:  
 * Modified by : $Author: 
 * Revision : $Revision: 
 * Date modified : $Date: 
 */

package edu.colorado.phet.solublesalts.model;

import java.util.ArrayList;
import java.util.List;

public class LatticeTracker implements SolubleSaltsModel.LatticeListener {
    private ArrayList lattices = new ArrayList();

    public void latticeAdded( SolubleSaltsModel.LatticeEvent event ) {
        lattices.add( event.getLattice() );
    }

    public void latticeRemoved( SolubleSaltsModel.LatticeEvent event ) {
        lattices.remove( event.getLattice() );
    }

    public List getLattices() {
        return lattices;
    }
}
