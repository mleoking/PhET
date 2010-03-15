package edu.colorado.phet.website.cache;

import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;

/**
 * Represents a cached panel
 */
public interface IPanelCacheEntry {

    public Class getPanelClass();

    public Class getParentClass();

    public String getParentCacheID();

    public PhetPanel fabricate( String id, PageContext context );

    public void onEnterCache();

    public void onExitCache();

    public void addDependency( EventDependency dependency );

}
