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
 * CrystalTracker
 * <p/>
 * An agent that tracks the crystals in the model.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CrystalTracker {

    private HashMap crystalMap = new HashMap();

    public void crystalAdded( Crystal crystal ) {
        List crystalSet = (List)crystalMap.get( crystal.getClass() );
        if( crystalSet == null ) {
            crystalSet = new ArrayList();
            crystalMap.put( crystal.getClass(), crystalSet );
        }
        crystalSet.add( crystal );
    }

    public void crystalRemoved( Crystal crystal ) {
        List crystalSet = (List)crystalMap.get( crystal.getClass() );
        crystalSet.remove( crystal );
    }

    public List getCrystals() {
    	List result = new ArrayList();
    	Collection lists = crystalMap.values();
    	for( Iterator it = lists.iterator(); it.hasNext(); ) {
    		List l = (List)it.next();
    		result.addAll( l );
    	}
    	return result;
    }
}
