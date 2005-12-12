/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model;

import edu.colorado.phet.solublesalts.model.crystal.Crystal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * LatticeTracker
 * <p/>
 * An agent that tracks the lattices in the model.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class LatticeTracker implements SolubleSaltsModel.LatticeListener {

    private HashMap latticeMap = new HashMap();

    public void latticeAdded( SolubleSaltsModel.LatticeEvent event ) {
        Crystal crystal = event.getLattice();
        List latticeSet = (List)latticeMap.get( crystal.getClass() );
        if( latticeSet == null ) {
            latticeSet = new ArrayList();
            latticeMap.put( crystal.getClass(), latticeSet );
        }
        latticeSet.add( crystal );
    }

    public void latticeRemoved( SolubleSaltsModel.LatticeEvent event ) {
        Crystal crystal = event.getLattice();
        List latticeSet = (List)latticeMap.get( crystal.getClass() );
        latticeSet.remove( crystal );
    }

    public List getLattices() {
    	List result = new ArrayList();
    	Collection lists = latticeMap.values();
    	for( Iterator it = lists.iterator(); it.hasNext(); ) {
    		List l = (List)it.next();
    		result.addAll( l );
    	}
    	return result;
    }
}
