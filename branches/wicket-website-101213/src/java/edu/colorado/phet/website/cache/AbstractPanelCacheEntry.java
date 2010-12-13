package edu.colorado.phet.website.cache;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

/**
 * Default implementation of IPanelCacheEntry. Takes care of handling the panel class, parent class, parent cache ID
 * and locale. The parentCacheId (specified on creation) is combined with the locale to form the ID that is passed to
 * the cache.
 * <p/>
 * Also handles dependencies (adding, registering, and deregistering), implements equality that is compliant with the
 * IPanelCacheEntry standard, and a toString() that is useful for debugging
 * <p/>
 * Assumes a single-lifetime usage, and does not assume that it is ever added to a cache. In fact, many entries will
 * not be added to the cache, but will be constructed to decide whether the panel they represent is cached.
 */
public abstract class AbstractPanelCacheEntry implements IPanelCacheEntry {

    private Class panelClass;
    private Class parentClass;
    private String parentCacheId;
    private Locale locale;
    private List<EventDependency> dependencies = new LinkedList<EventDependency>();

    private static final Logger logger = Logger.getLogger( AbstractPanelCacheEntry.class.getName() );

    protected AbstractPanelCacheEntry( Class panelClass, Class parentClass, Locale locale, String parentCacheId ) {
        this.panelClass = panelClass;
        this.parentClass = parentClass;
        this.locale = locale;
        this.parentCacheId = parentCacheId;
    }

    public Class getPanelClass() {
        return panelClass;
    }

    public Class getParentClass() {
        return parentClass;
    }

    public String getParentCacheID() {
        return parentCacheId;
    }

    public String getCacheId() {
        // combine the specified cache ID from the parent with the locale, so translated pages are distinctly cached
        return ( parentCacheId == null ? "" : parentCacheId ) + "_" + LocaleUtils.localeToString( locale );
    }

    public void onEnterCache( PanelCache cache ) {
        logger.debug( "onEnterCache: " + this );
        for ( EventDependency dependency : dependencies ) {
            dependency.register( cache, this );
        }
    }

    public void onExitCache() {
        logger.debug( "onExitCache: " + this );
        for ( EventDependency dependency : dependencies ) {
            dependency.deregister();
        }
    }

    public void addDependency( EventDependency dependency ) {
        logger.debug( "addDependency: " + this );
        dependencies.add( dependency );
    }

    @Override
    public String toString() {
        String ret = getPanelClass().getSimpleName();
        if ( getParentClass() != null ) {
            ret += "{" + getParentClass().getSimpleName() + "}";
        }
        ret += "#" + getCacheId();
        return ret;
    }

    /*---------------------------------------------------------------------------*
    * equals / hashcode implementation
    *----------------------------------------------------------------------------*/

    @Override
    public boolean equals( Object o ) {
        if ( o == null ) {
            return false;
        }

        /*
        Must have the same panel class, cache ID (including locale), and if specified the parent class
         */
        if ( o instanceof IPanelCacheEntry ) {
            IPanelCacheEntry entry = (IPanelCacheEntry) o;
            return (
                    getCacheId().equals( entry.getCacheId() )
                    && getPanelClass().equals( entry.getPanelClass() )
                    && (
                            getParentClass() == null
                            ? ( entry.getParentClass() == null )
                            : ( getParentClass().equals( entry.getParentClass() ) )
                    )
            );
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int ret = getPanelClass().hashCode();
        ret *= 13;
        if ( getParentClass() != null ) {
            ret += getParentClass().hashCode();
            ret *= 17;
        }
        ret += getCacheId().hashCode();
        return ret;
    }
}
