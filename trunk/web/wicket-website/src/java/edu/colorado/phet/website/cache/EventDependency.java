package edu.colorado.phet.website.cache;

public abstract class EventDependency {

    private PanelCache cache;
    private IPanelCacheEntry entry;
    private boolean removed = false;

    protected EventDependency() {
    }

    public final void register( PanelCache cache, IPanelCacheEntry entry ) {
        this.cache = cache;
        this.entry = entry;
        addListeners();
    }

    public final void deregister() {
        if ( !removed ) {
            removed = true;
            removeListeners();
        }
    }

    public abstract void addListeners();

    public abstract void removeListeners();

    protected final void invalidate() {
        cache.remove( entry );
    }

}
