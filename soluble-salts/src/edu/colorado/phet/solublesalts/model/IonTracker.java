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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * IonTracker
 * <p/>
 * An agent that tracks the addition and removal of ions from the model
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class IonTracker implements SolubleSaltsModel.IonListener {

    private HashMap ionMap = new HashMap();

    public void ionAdded( SolubleSaltsModel.IonEvent event ) {
        Ion ion = event.getIon();
        List ionSet = (List)ionMap.get( ion.getClass() );
        if( ionSet == null ) {
            ionSet = new ArrayList();
            ionMap.put( ion.getClass(), ionSet );
        }
        ionSet.add( ion );
    }

    public void ionRemoved( SolubleSaltsModel.IonEvent event ) {
        Ion ion = event.getIon();
        List ionSet = (List)ionMap.get( ion.getClass() );
        ionSet.remove( ion );
    }

    public int numIonsOfType( Class ionClass ) {
        int result = 0;
        List ionSet = (List)ionMap.get( ionClass );
        if( ionSet != null ) {
            result = ionSet.size();
        }
        return result;
    }

    public List getIonsOfType( Class ionClass ) {
        return (List)ionMap.get( ionClass );
    }
    
    public List getIons() {
    	List result = new ArrayList();
    	Collection lists = ionMap.values();
    	for( Iterator it = lists.iterator(); it.hasNext(); ) {
    		List l = (List)it.next();
    		result.addAll( l );
    	}    	
    	return result;
    }
}
