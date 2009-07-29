package edu.colorado.phet.wickettest.util;

import java.util.Locale;

import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.WicketApplication;

public abstract class PhetPage extends WebPage {

    private Locale myLocale;
    private boolean hasHibernateSession = false;
    private org.hibernate.Session hibernateSession;

    public PhetPage( PageParameters parameters ) {
        this( parameters, false );
    }

    public PhetPage( PageParameters parameters, boolean addTemplateBindings ) {

        if ( parameters.get( "locale" ) != null ) {
            myLocale = (Locale) parameters.get( "locale" );
        }
        else {
            // try again with localeString, but use english as default
            myLocale = LocaleUtils.stringToLocale( parameters.getString( "localeString", "en" ) );
        }

        Session wicketSession = getSession();
        wicketSession.setLocale( myLocale );


        System.out.println( "Loading " + this.getClass().getCanonicalName() + " with Locale: " + LocaleUtils.localeToString( myLocale ) );
        System.out.println( "getRequestPath() of this page is: " + parameters.getString( "path" ) );
        System.out.println( "Session id is: " + wicketSession.getId() );

        for ( Object o : parameters.keySet() ) {
            System.out.println( "[" + o.toString() + "] = " + parameters.get( o ).toString() );
        }

        // visual display
        // TODO: look into detecting whether the subclass page is using markup inheritance, so this does not need to be specified
        if ( addTemplateBindings ) {
            // TODO: refactor static images to a single location, so paths / names can be quickly changed
            add( new StaticImage( "page-header-logo-image", "/images/phet-logo.gif", null ) );
            add( new StaticImage( "page-header-title-image", "/images/logo-title.jpg", null ) );
        }
    }

    public Locale getMyLocale() {
        return myLocale;
    }

    public String getUrlPrefix() {
        return "/" + LocaleUtils.localeToString( myLocale ) + "/";
    }

    public PageContext getPageContext() {
        return new PageContext( getMyLocale(), getHibernateSession(), (WicketApplication) getApplication(), this );
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

    public org.hibernate.Session getHibernateSession() {
        if ( !hasHibernateSession ) {
            hibernateSession = HibernateUtils.getInstance().openSession();
            hasHibernateSession = true;
        }
        return hibernateSession;
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        System.out.println( "Debug: page stateless = " + isPageStateless() );
    }

    @Override
    protected void onDetach() {
        System.out.println( "Detaching page" );
        if ( hasHibernateSession ) {
            hibernateSession.close();
            hasHibernateSession = false;
            hibernateSession = null;
        }
        super.onDetach();
    }

}