package edu.colorado.phet.wickettest.panels;

import java.util.Locale;

import javax.servlet.ServletContext;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebApplication;

public class PhetPanel extends Panel {

    private Locale myLocale;
    private ServletContext context;

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
