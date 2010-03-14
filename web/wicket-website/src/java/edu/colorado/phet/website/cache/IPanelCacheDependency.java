package edu.colorado.phet.website.cache;

/**
 * A dependency on certain data objects or events. Specifically when the data object changes or the event is fired,
 * the cache entry is invalidated.
 */
public interface IPanelCacheDependency {
    public Class[] getDependentClasses();

    public String[] getDependentEvents();

    public boolean willInvalidateOnObject( Object object );

}
