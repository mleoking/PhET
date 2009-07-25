package edu.colorado.phet.wickettest.panels;

import java.util.Locale;

import javax.servlet.ServletContext;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebApplication;

import edu.colorado.phet.wickettest.util.PhetPage;

public class PhetPanel extends Panel {

    private Locale myLocale;
    private ServletContext context;

    // TODO: refactor out unneeded servlet context for hibernate usage

    public PhetPanel( String id, PhetPage page ) {
        super( id );
        this.myLocale = page.getMyLocale();
        context = ( (WebApplication) getApplication() ).getServletContext();
    }

    public PhetPanel( String id, Locale myLocale ) {
        super( id );
        this.myLocale = myLocale;
        context = ( (WebApplication) getApplication() ).getServletContext();
    }

    public PhetPanel( String id, IModel iModel, Locale myLocale ) {
        super( id, iModel );
        this.myLocale = myLocale;
        context = ( (WebApplication) getApplication() ).getServletContext();
    }

    public Locale getMyLocale() {
        return myLocale;
    }

    public ServletContext getContext() {
        return context;
    }

    /**
     * Override locale, so that localized strings with wicket:message will use this panel's locale
     */
    @Override
    public Locale getLocale() {
        return myLocale;
    }
}
