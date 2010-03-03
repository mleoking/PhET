package edu.colorado.phet.website.data;

/**
 * Hibernate data classes implementing this will have this method called AFTER a successfully committed update. This is
 * useful for performing actions that should happen after updates from multiple unknown locations, and reduces
 * those dependencies.
 */
public interface UpdateListener {
    public void onUpdate();
}
