package edu.colorado.phet.website.cache;

import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;

/**
 * A cache entry for a single version of a cached panel. A cache entry should be identified by its cached panel class,
 * the class of its parent panel / page (possibly null to ignore), and a particular cache ID. If all of these are
 * equal, then the entries represent the same cached panel.
 */
public interface IPanelCacheEntry {

    /**
     * @return The class of the panel that should be cached
     */
    public Class getPanelClass();

    /**
     * @return The class of the component that the cached panel will be added to, or null if this should not be used for
     *         comparing entries. This is useful if a panel's content doesn't matter on where it is placed.
     */
    public Class getParentClass();

    /**
     * @return A string used to discriminate between different entries (different cached panels) that have the same
     *         combination of panel and parent classes.
     */
    public String getCacheId();

    /**
     * Creates a stand-in PhetPanel using cached data in place of the regular panel.
     *
     * @param id      Wicket id to use
     * @param context Page context
     * @return A fresh new (hopefully lightweight) panel
     */
    public PhetPanel fabricate( String id, PageContext context );

    /**
     * Event called when the entry is inserted into the cache. It should be safe to assume that entries will only be
     * added to one cache
     *
     * @param cache The cache
     */
    public void onEnterCache( PanelCache cache );

    /**
     * Event called when the entry is removed from the cache. Most likely this is due to the cached panel being
     * invalidated by one of the dependencies, but it could also be due to the cache dumping the entry due to
     * expiry or memory use.
     */
    public void onExitCache();

    /**
     * Adds an event dependency to this entry. Should only be called before onEnterCache()
     *
     * @param dependency The dependency to add
     */
    public void addDependency( EventDependency dependency );

}
