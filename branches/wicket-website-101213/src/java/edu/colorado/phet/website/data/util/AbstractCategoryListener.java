package edu.colorado.phet.website.data.util;

import edu.colorado.phet.website.data.Category;
import edu.colorado.phet.website.data.Simulation;

public abstract class AbstractCategoryListener implements CategoryChangeHandler.Listener {
    public AbstractCategoryListener() {
    }

    public void categorySimulationChanged( Category category, Simulation simulation ) {
        anyChange( category );
    }

    public void categoryAdded( Category category ) {
        anyChange( category );
    }

    public void categoryRemoved( Category category ) {
        anyChange( category );
    }

    public void categoryChildrenReordered( Category category ) {
        anyChange( category );
    }

    public void anyChange( Category category ) {

    }
}
