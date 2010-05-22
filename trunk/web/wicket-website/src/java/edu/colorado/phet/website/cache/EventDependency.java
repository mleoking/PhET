package edu.colorado.phet.website.cache;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.event.PostDeleteEvent;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostUpdateEvent;

import edu.colorado.phet.website.data.TranslatedString;
import edu.colorado.phet.website.data.util.IChangeListener;

/**
 * A dependency for a particular cached panel. Should set up listeners to detect when this dependency is invalidated,
 * and then is responsible for removing the entry from the cache.
 * <p/>
 * Only designed to handle one PanelCache, and each instance is effectively tied to one cache and one entry in that
 * cache.
 * <p/>
 * HIGHLY recommended to create new instances each time, since if different pages share the same dependency, they will
 * invalidate each other or cause other major errors.
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

    private static final Logger logger = Logger.getLogger( EventDependency.class.getName() );

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

    /**
     * Called when the parent cache entry is put into the cache. Responsible for setting everything up so listeners can
     * listen and the entry can be invalidated.
     * <p/>
     * DO NOT CALL in subclass
     *
     * @param cache The cache
     * @param entry The cache entry
     */
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

    /**
     * Called when the parent cache entry is invalidated. Clean up method, and we make sure there would be no memory
     * leaks.
     * <p/>
     * DO NOT CALL in subclass
     */
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

    /**
     * Invalidates the parent cache entry. Do not call deregister() on ourself, as the entry should take care of that.
     */
    protected final void invalidate() {
        logger.debug( "invalidating dependency for entry " + entry );
        if ( cache != null ) {
            cache.remove( entry );
        }
    }

    /**
     * @return A convenience listener that will invalidate the cache entry upon any insert, update or delete events
     */
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

    protected final IChangeListener<TranslatedString> createTranslationChangeInvalidator( final Locale locale ) {
        return new IChangeListener<TranslatedString>() {
            public void onInsert( final TranslatedString object, PostInsertEvent event ) {
                process( object, event.getSession() );
            }

            public void onUpdate( TranslatedString object, PostUpdateEvent event ) {
                process( object, event.getSession() );
            }

            public void onDelete( TranslatedString object, PostDeleteEvent event ) {
                process( object, event.getSession() );
            }

            private void process( final TranslatedString object, Session session ) {
                //logger.debug( "checking string change on " + object.getKey() + " with panel locale " + locale + " for event: " + EventDependency.this + " this: " + this.toString() );
                // assuming getTranslation() works! careful....
                if ( object.getTranslation().getLocale().equals( locale ) ) {
                    invalidate();
                }
            }
        };
    }

}
