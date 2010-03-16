package edu.colorado.phet.website.cache;

import org.apache.log4j.Logger;
import org.hibernate.event.PostDeleteEvent;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostUpdateEvent;

import edu.colorado.phet.website.data.IChangeListener;

/**
 * A dependency for a particular cached panel. Should set up listeners to detect when this dependency is invalidated,
 * and then is responsible for removing the entry from the cache.
 * <p/>
 * Only designed to handle one PanelCache, and each instance is effectively tied to one cache and one entry in that
 * cache
 */
public abstract class EventDependency {

    /**
     * A reference to the cache we are (possibly) in so that we can invalidate our entry in that cache
     */
    private PanelCache cache;

    /**
     * A reference to the cache entry that we are a dependency of, so we can invalidate the entry later
     */
    private IPanelCacheEntry entry;

    /**
     * Whether listeners have been added. It should be possible to remove and then add listeners back, but is not
     * expected within normal use
     */
    private boolean active = false;

    /**
     * For convenience, hold a spot for an instance of the change listener that will invalidate for ANY changes
     */
    private IChangeListener anyChangeListener;

    private static Logger logger = Logger.getLogger( EventDependency.class.getName() );

    protected EventDependency() {
    }

    //----------------------------------------------------------------------------
    // abstract methods to implement
    //----------------------------------------------------------------------------

    /**
     * Should add any necessary listeners that could later call invalidate()
     */
    protected abstract void addListeners();

    /**
     * Should remove all of the listeners added previously, so as to avoid memory leaks
     */
    protected abstract void removeListeners();

    //----------------------------------------------------------------------------
    // register / deregister for cache control
    //----------------------------------------------------------------------------

    public final void register( PanelCache cache, IPanelCacheEntry entry ) {
        logger.debug( "registering dependency for entry " + entry );
        if ( !active ) {
            active = true;
            this.cache = cache;
            this.entry = entry;
            addListeners();
        }
        else {
            throw new RuntimeException( "attempted to register entry while active: " + entry );
        }
    }

    public final void deregister() {
        logger.debug( "deregistering dependency for entry " + entry );
        if ( active ) {
            active = false;
            removeListeners();
        }
        else {
            throw new RuntimeException( "attempted to deregister entry while inactive: " + entry );
        }
    }

    //----------------------------------------------------------------------------
    // methods for subclasses
    //----------------------------------------------------------------------------

    protected final void invalidate() {
        logger.debug( "invalidating dependency for entry " + entry );
        if ( cache != null ) {
            cache.remove( entry );
        }
    }

    protected final IChangeListener getAnyChangeInvalidator() {
        synchronized( this ) {
            if ( anyChangeListener == null ) {
                anyChangeListener = new IChangeListener() {
                    public void onInsert( Object object, PostInsertEvent event ) {
                        invalidate();
                    }

                    public void onUpdate( Object object, PostUpdateEvent event ) {
                        invalidate();
                    }

                    public void onDelete( Object object, PostDeleteEvent event ) {
                        invalidate();
                    }
                };
            }
        }
        return anyChangeListener;
    }

}
