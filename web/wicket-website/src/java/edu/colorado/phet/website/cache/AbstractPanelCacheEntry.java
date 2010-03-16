package edu.colorado.phet.website.cache;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

public abstract class AbstractPanelCacheEntry implements IPanelCacheEntry {

    // TODO: sanity checks for construction orders, etc.

    private Class panelClass;
    private Class parentClass;
    private String parentCacheId;
    private Locale locale;
    private List<EventDependency> dependencies = new LinkedList<EventDependency>();

    private static Logger logger = Logger.getLogger( AbstractPanelCacheEntry.class.getName() );

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

    //----------------------------------------------------------------------------
    // equals / hashcode implementation
    //----------------------------------------------------------------------------

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
