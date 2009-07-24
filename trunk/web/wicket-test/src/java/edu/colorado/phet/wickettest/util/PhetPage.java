package edu.colorado.phet.wickettest.util;

import java.util.Locale;

import javax.servlet.ServletContext;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebApplication;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

public abstract class PhetPage extends WebPage {

    private Locale myLocale;
    private ServletContext context;
    private PageParameters parameters;

    public PhetPage( PageParameters parameters ) {
        this( parameters, false );
    }

    public PhetPage( PageParameters parameters, boolean addTemplateBindings ) {
        this.parameters = parameters;
        context = ( (WebApplication) getApplication() ).getServletContext();

        if ( this.parameters.get( "locale" ) != null ) {
            myLocale = (Locale) this.parameters.get( "locale" );
        }
        else {
            // try again with localeString, but use english as default
            myLocale = LocaleUtils.stringToLocale( this.parameters.getString( "localeString", "en" ) );
        }

        getSession().setLocale( myLocale );
        System.out.println( "Loading " + this.getClass().getCanonicalName() + " with Locale: " + LocaleUtils.localeToString( myLocale ) );
        System.out.println( "getRequestPath() of this page is: " + getRequestPath() );

        for ( Object o : parameters.keySet() ) {
            System.out.println( "[" + o.toString() + "] = " + parameters.get( o ).toString() );
        }

        // visual display

        if ( addTemplateBindings ) {
            // TODO: refactor static images to a single location, so paths / names can be quickly changed
            add( new StaticImage( "page-header-logo-image", "/images/phet-logo.gif", null ) );
            add( new StaticImage( "page-header-title-image", "/images/logo-title.jpg", null ) );
        }
    }

    public Locale getMyLocale() {
        return myLocale;
    }

    public ServletContext getContext() {
        return context;
    }

    public String getRequestPath() {
        return parameters.getString( "path" );
    }

    public String getUrlPrefix() {
        return "/" + LocaleUtils.localeToString( myLocale ) + "/";
    }

    @Override
    public Locale getLocale() {
        return myLocale;
    }

    public void addTitle( String title ) {
        add( new Label( "page-title", title ) );
    }

    public void addTitle( IModel title ) {
        add( new Label( "page-title", title ) );
    }

    /*
    @Override
    protected void onAfterRender() {
        super.onAfterRender();

        //remove this page because it should only be used in a bookmarkable sense
        getPageMap().remove( this );
    }
    */
}