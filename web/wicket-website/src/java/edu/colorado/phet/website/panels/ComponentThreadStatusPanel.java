package edu.colorado.phet.website.panels;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.util.time.Duration;

import edu.colorado.phet.website.util.PageContext;

/**
 * Displays a status panel for an executing thread that uses AJAX to update every X milliseconds until done.
 */
public class ComponentThreadStatusPanel extends PhetPanel {

    private static final Map<Integer, ComponentThread> threadMap = new HashMap<Integer, ComponentThread>();
    private static int nextIndex = 1;

    private int index;

    private Component status;

    private static Logger logger = Logger.getLogger( ComponentThreadStatusPanel.class.getName() );

    public ComponentThreadStatusPanel( String id, final PageContext context, ComponentThread thread, int milliseconds ) {
        super( id, context );

        index = addThread( thread );

        status = thread.getComponent( "status", context );
        add( status );

        add( new AjaxSelfUpdatingTimerBehavior( Duration.milliseconds( milliseconds ) ) {
            @Override
            protected void onPostProcessTarget( AjaxRequestTarget target ) {
                Component newStatus;
                boolean done;
                logger.info( "tick on " + index );
                // synchronize on the same lock as the static methods
                synchronized( ComponentThreadStatusPanel.this.getClass() ) {
                    ComponentThread thread = getThread( index );
                    if ( thread != null ) {
                        newStatus = thread.getComponent( "status", context );
                        done = thread.isDone();
                        status.replaceWith( newStatus );
                        status = newStatus;
                    }
                    else {
                        // thread was null? abort, and stop updating
                        done = true;
                    }
                }

                if ( done ) {
                    // don't leak memory like crazy
                    removeThread( index );

                    // stop ajax updating on the timer
                    stop();
                }

            }
        } );
    }

    private static synchronized int addThread( ComponentThread thread ) {
        int index = nextIndex++;
        logger.info( "adding thread " + index );
        threadMap.put( index, thread );
        return index;
    }

    private static synchronized void removeThread( int index ) {
        logger.info( "removing thread " + index );
        threadMap.remove( index );
    }

    private static synchronized ComponentThread getThread( int index ) {
        return threadMap.get( index );
    }
}
