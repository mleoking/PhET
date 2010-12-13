package edu.colorado.phet.website.cache;

/**
 * Should be implemented in Panels meant to be used with StaticPage to indicate that it is acceptable to cache the
 * panel. It will be identified by the URL, panel class and locale, so if those pieces of information do not uniquely
 * determine the state of the panel at any time, this should not be used.
 */
public interface CacheableUrlStaticPanel {
}
