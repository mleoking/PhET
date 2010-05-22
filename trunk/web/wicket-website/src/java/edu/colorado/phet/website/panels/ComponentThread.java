package edu.colorado.phet.website.panels;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;

import edu.colorado.phet.website.util.PageContext;

/**
 * A thread that can display its status (and end result) as a Wicket component. This is useful for AJAX behavior where
 * a component is updated every so often while a background thread is running.
 */
public abstract class ComponentThread extends Thread {

    private boolean done = false;

    private static Logger logger = Logger.getLogger( ComponentThread.class.getName() );

    /**
     * @return A Wicket component that represents the current status of this thread
     */
    public abstract Component getComponent( String id, PageContext context );

    /**
     * @return Whether the component will not be updated any more.
     */
    public boolean isDone() {
        // TODO: if not running, also trigger this!
        return done;
    }

    public void finish() {
        done = true;
    }
}
