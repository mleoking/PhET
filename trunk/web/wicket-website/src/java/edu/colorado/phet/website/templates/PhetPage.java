package edu.colorado.phet.website.templates;

import java.util.Locale;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.model.IModel;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.authentication.PhetSession;
import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.components.PhetLink;
import edu.colorado.phet.website.components.StaticImage;
import edu.colorado.phet.website.content.IndexPage;
import edu.colorado.phet.website.data.Translation;
import edu.colorado.phet.website.menu.NavMenu;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public abstract class PhetPage extends WebPage {

    private Locale myLocale;
    private String prefix;
    private String path;
    private String variation;

    private Long initStart;

    private static Logger logger = Logger.getLogger( PhetPage.class.getName() );

    public PhetPage( PageParameters parameters ) {
        this( parameters, true );
    }

    public PhetPage( PageParameters parameters, boolean addTemplateBindings ) {

        initStart = System.currentTimeMillis();

        if ( parameters.get( "locale" ) != null ) {
            myLocale = (Locale) parameters.get( "locale" );
        }
        else {
            // try again with localeString, but use english as default
            myLocale = LocaleUtils.stringToLocale( parameters.getString( "localeString", "en" ) );
        }

        if ( parameters.getString( "prefixString" ) != null ) {
            prefix = parameters.getString( "prefixString" );
        }
        else {
            prefix = "/" + LocaleUtils.localeToString( myLocale ) + "/";
        }

        if ( prefix.equals( "/error/" ) ) {
            prefix = "/";
        }

        path = parameters.getString( "path" );

        // should usually default to null
        variation = parameters.getString( "variation" );

        Session wicketSession = getSession();
        wicketSession.setLocale( myLocale );

        logger.info( "Loading " + this.getClass().getCanonicalName() + " with Locale: " + LocaleUtils.localeToString( myLocale ) );
        logger.info( "path of this page is: " + path );
        logger.debug( "prefix of this page is: " + prefix );
        logger.debug( "Session id is: " + wicketSession.getId() );

        if ( logger.isEnabledFor( Level.DEBUG ) ) {
            for ( Object o : parameters.keySet() ) {
                logger.debug( "[" + o.toString() + "] = " + parameters.get( o ).toString() );
            }
        }

        // visual display
        if ( addTemplateBindings ) {
            // TODO: refactor static images to a single location, so paths / names can be quickly changed
            Link link = IndexPage.createLink( "page-header-home-link", getPageContext() );
            if ( DistributionHandler.redirectHeaderToProduction( (PhetRequestCycle) getRequestCycle() ) ) {
                link = new PhetLink( "page-header-home-link", "http://phet.colorado.edu" );
            }
            add( link );
            link.add( new StaticImage( "page-header-logo-image", "/images/phet-logo.gif", null ) );
            add( new StaticImage( "page-header-title-image", "/images/logo-title.jpg", null ) );

            add( HeaderContributor.forCss( "/css/phetpage-v1.css" ) );

            if ( prefix.startsWith( "/translation" ) && getVariation() != null ) {
                final Translation translation = new Translation();

                HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                    public boolean run( org.hibernate.Session session ) {
                        session.load( translation, Integer.valueOf( getVariation() ) );
                        return true;
                    }
                } );

                add( new Label( "translation-preview-notification", "This is a preview for translation #" + getVariation() +
                                                                    " of " + translation.getLocale().getDisplayName() +
                                                                    " (" + LocaleUtils.localeToString( translation.getLocale() ) +
                                                                    ")" ) );
            }
            else {
                add( new InvisibleComponent( "translation-preview-notification" ) );
            }

            // TODO: change sign out link to a mini panel with options (one of which will be to sign out)
            final PhetSession psession = PhetSession.get();
            if ( psession != null && psession.isSignedIn() ) {
                add( new StatelessLink( "sign-out" ) {
                    public void onClick() {
                        PhetSession.get().signOut();
                        setResponsePage( IndexPage.class );
                    }
                } );
            }
            else {
                add( new InvisibleComponent( "sign-out" ) );
            }
        }

        logger.debug( "request cycle is a : " + getRequestCycle().getClass().getSimpleName() );
    }

    public Locale getMyLocale() {
        return myLocale;
    }

    public String getMyPrefix() {
        return prefix;
    }

    public String getMyPath() {
        if ( path == null ) {
            return "";
        }
        return path;
    }

    public PageContext getPageContext() {
        return new PageContext( getMyPrefix(), getMyPath(), getMyLocale() );
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
        return ( (PhetRequestCycle) getRequestCycle() ).getHibernateSession();
    }

    public NavMenu getNavMenu() {
        return ( (PhetWicketApplication) getApplication() ).getMenu();
    }

    private Long renderStart;

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        renderStart = System.currentTimeMillis();
        logger.debug( "Debug: page stateless = " + isPageStateless() );
        logger.info( "Pre-render: " + ( renderStart - initStart ) + " ms" );
    }

    @Override
    protected void onAfterRender() {
        super.onAfterRender();
        logger.info( "Render: " + ( System.currentTimeMillis() - renderStart ) + " ms" );
    }

    @Override
    protected void onDetach() {
        logger.debug( "Detaching page" );
        super.onDetach();
    }

    @Override
    public String getVariation() {
        return variation;
    }

    public PhetRequestCycle getPhetCycle() {
        return (PhetRequestCycle) getRequestCycle();
    }
}