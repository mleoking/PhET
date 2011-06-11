// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model;

import java.util.*;

import edu.colorado.phet.solublesalts.model.crystal.Crystal;

/**
 * CrystalTracker
 * <p/>
 * An agent that tracks the creation and destruction of crystals, and informs the model when crystals
 * enter and leave it.
 *
 * @author Ron LeMaster
 */
public class CrystalTracker implements Crystal.InstanceLifetimeListener {

    private SolubleSaltsModel model;
    private HashMap crystalMap = new HashMap();

    public CrystalTracker( SolubleSaltsModel model ) {
        this.model = model;
    }

    public void instanceCreated( Crystal.InstanceLifetimeEvent event ) {
        crystalAdded( event.getInstance() );
    }

    public void instanceDestroyed( Crystal.InstanceLifetimeEvent event ) {
        crystalRemoved( event.getInstance() );
    }

    private void crystalAdded( Crystal crystal ) {
        List crystalSet = (List) crystalMap.get( crystal.getClass() );
        if ( crystalSet == null ) {
            crystalSet = new ArrayList();
            crystalMap.put( crystal.getClass(), crystalSet );
        }
        crystalSet.add( crystal );

        model.addModelElement( crystal );
    }

    private void crystalRemoved( Crystal crystal ) {
        List crystalSet = (List) crystalMap.get( crystal.getClass() );
        crystalSet.remove( crystal );

        model.removeModelElement( crystal );
    }

    public List getCrystals() {
        List result = new ArrayList();
        Collection lists = crystalMap.values();
        for ( Iterator it = lists.iterator(); it.hasNext(); ) {
            List l = (List) it.next();
            result.addAll( l );
        }
        return result;
    }
}
