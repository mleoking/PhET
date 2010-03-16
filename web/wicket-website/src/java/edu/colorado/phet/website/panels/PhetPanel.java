package edu.colorado.phet.website.panels;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.internal.HtmlHeaderContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.response.StringResponse;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.cache.EventDependency;
import edu.colorado.phet.website.cache.PanelCache;
import edu.colorado.phet.website.cache.SimplePanelCacheEntry;
import edu.colorado.phet.website.menu.NavMenu;
import edu.colorado.phet.website.translation.PhetLocalizer;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

/**
 * Standard panel that almost all panels in the website should inherit from. Allows it to be translated, pulls the
 * correct locale out of the page context automatically, has enough convenience functions, and organizes stylesheets.
 */
public class PhetPanel extends Panel {

    private Locale myLocale;
    private List<EventDependency> dependencies = new LinkedList<EventDependency>();

    /**
     * If set, this will cause the panel to be cached.
     */
    private SimplePanelCacheEntry cacheEntry;

    private static Logger logger = Logger.getLogger( PhetPanel.class.getName() );

    //----------------------------------------------------------------------------
    // constructor
    //----------------------------------------------------------------------------

    public PhetPanel( String id, PageContext context ) {
        super( id );
        this.myLocale = context.getLocale();
    }

    //----------------------------------------------------------------------------
    // public getters
    //----------------------------------------------------------------------------

    /**
     * Considered immutable
     *
     * @return Direct access to the panel's locale
     */
    public Locale getMyLocale() {
        return myLocale;
    }

    /**
     * @return The currently active Hibernate session. The session is bound at the start of the request, and released
     *         after the response, and can be used to start transactions. @see HibernateUtils#wrapTransaction(HibernateTask)
     */
    public org.hibernate.Session getHibernateSession() {
        return ( (PhetRequestCycle) getRequestCycle() ).getHibernateSession();
    }

    /**
     * @return The navigation menu data structure for the website
     */
    public NavMenu getNavMenu() {
        return ( (PhetWicketApplication) getApplication() ).getMenu();
    }

    /**
     * Override locale, so that localized strings with wicket:message will use this panel's locale
     * <p/>
     * Considered immutable, do not modify
     */
    @Override
    public Locale getLocale() {
        return myLocale;
    }

    /**
     * @return The request cycle associated with the request and response. Contains information about the request, and
     *         can be used to control the behavior of the response (and redirections)
     */
    public PhetRequestCycle getPhetCycle() {
        return (PhetRequestCycle) getRequestCycle();
    }

    /**
     * @return The PhET localizer, which can be used to directly receive translated strings. It caches them, so this is
     *         much better than (and easier than) searching the database
     */
    public PhetLocalizer getPhetLocalizer() {
        return (PhetLocalizer) getLocalizer();
    }

    /**
     * @return The Java J2EE servlet context associated with this response / thread.
     */
    public ServletContext getServletContext() {
        return ( (PhetWicketApplication) getApplication() ).getServletContext();
    }

    //----------------------------------------------------------------------------
    // caching implementation
    //----------------------------------------------------------------------------

    @Override
    public void renderHead( HtmlHeaderContainer container ) {
        if ( cacheEntry == null ) {
            super.renderHead( container );
        }
        else {
            StringResponse fakeResponse = new StringResponse();
            RequestCycle cycle = getRequestCycle();
            Response response = cycle.getResponse();

            cycle.setResponse( fakeResponse );
            super.renderHead( container );
            cycle.setResponse( response );

            CharSequence buffer = fakeResponse.getBuffer();
            cacheEntry.setHeader( buffer );
            response.write( buffer );
        }
    }

    @Override
    protected void onRender( MarkupStream markupStream ) {
        if ( cacheEntry == null ) {
            // not caching the component. render as normal
            super.onRender( markupStream );
        }
        else {
            StringResponse fakeResponse = new StringResponse();

            RequestCycle cycle = getRequestCycle();
            Response response = cycle.getResponse();

            cycle.setResponse( fakeResponse );
            super.onRender( markupStream );
            cycle.setResponse( response );

            CharSequence buffer = fakeResponse.getBuffer();
            cacheEntry.setBody( buffer );
            response.write( buffer );

            for ( EventDependency dependency : getDependencies() ) {
                cacheEntry.addDependency( dependency );
            }

            PanelCache.get().addIfMissing( cacheEntry );

        }
    }

    /**
     * Trigger the panel to be cached with this particular entry
     *
     * @param cacheEntry
     */
    public void setCacheEntry( SimplePanelCacheEntry cacheEntry ) {
        this.cacheEntry = cacheEntry;
    }

    @Override
    protected void onDetach() {
        super.onDetach();

        // don't try to make the dependencies serializable
        dependencies = null;
    }

    public List<EventDependency> getDependencies() {
        List<EventDependency> ret = new LinkedList<EventDependency>( dependencies );
        Iterator iter = iterator();
        while ( iter.hasNext() ) {
            Object o = iter.next();
            if ( o instanceof PhetPanel ) {
                ret.addAll( ( (PhetPanel) o ).getDependencies() );
            }
        }
        return ret;
    }
}
