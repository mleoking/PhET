package edu.colorado.phet.website.templates;

import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletContext;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.authentication.*;
import edu.colorado.phet.website.components.*;
import edu.colorado.phet.website.constants.CSS;
import edu.colorado.phet.website.constants.Images;
import edu.colorado.phet.website.content.IndexPage;
import edu.colorado.phet.website.content.getphet.FullInstallPanel;
import edu.colorado.phet.website.data.Translation;
import edu.colorado.phet.website.menu.NavMenu;
import edu.colorado.phet.website.panels.LogInOutPanel;
import edu.colorado.phet.website.panels.SearchPanel;
import edu.colorado.phet.website.translation.PhetLocalizer;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

/**
 * This is a page that generally has the PhET header (logo, search, sign off, etc), but can be instantiated without
 * any extras with PhetPage( params, false ). Base class for all PhET web pages.
 * <p/>
 * For now, all direct subclasses should call addTitle exactly once
 */
public abstract class PhetPage extends WebPage implements Stylable {

    private Locale myLocale;
    private String prefix;
    private String path;
    private String variation;
    private String title = null; // initialize as null
    private RawLabel titleLabel;

    private Long initStart;

    private static final Logger logger = Logger.getLogger( PhetPage.class.getName() );

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

        logger.debug( "Loading " + this.getClass().getCanonicalName() + " with Locale: " + LocaleUtils.localeToString( myLocale ) );
        logger.debug( "path of this page is: " + path );
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
            Link link = IndexPage.getLinker().getLink( "page-header-home-link", getPageContext(), getPhetCycle() );
            if ( DistributionHandler.redirectHeaderToProduction( (PhetRequestCycle) getRequestCycle() ) ) {
                link = new RawLink( "page-header-home-link", "http://phet.colorado.edu" );
            }
            add( link );
            // TODO: localize alt attributes
            link.add( new StaticImage( "page-header-logo-image", Images.PHET_LOGO, null ) );
            add( new StaticImage( "page-header-title-image", Images.LOGO_TITLE, null ) );

            add( HeaderContributor.forCss( CSS.PHET_PAGE ) );

            switch( DistributionHandler.getSearchBoxVisibility( getPhetCycle() ) ) {
                case NONE:
                    add( new InvisibleComponent( "search-panel" ) );
                    break;
                case OFFLINE_INSTALLER:
                    add( new LocalizedText( "search-panel", "installer.mostUpToDate", new Object[] {
                            new Date(),
                            FullInstallPanel.getLinker().getHref( getPageContext(), getPhetCycle() )
                    } ) );
                    break;
                case NORMAL:
                    add( new SearchPanel( "search-panel", getPageContext() ) );
                    break;
            }

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

            boolean isAdmin = PhetSession.get().isSignedIn() && PhetSession.get().getUser().isTeamMember();

            if ( !isAdmin && ( this instanceof SignInPage || this instanceof RegisterPage || this instanceof EditProfilePage ) ) {
                add( new InvisibleComponent( "log-in-out-panel" ) );
            }
            else {
                add( new LogInOutPanel( "log-in-out-panel", getPageContext() ) );
            }
        }

        logger.debug( "request cycle is a : " + getRequestCycle().getClass().getSimpleName() );

        if ( PhetWicketApplication.get().isDevelopment() ) {
            add( new RawBodyLabel( "debug-page-class", "<!-- class " + getClass().getCanonicalName() + " -->" ) );
            add( new InvisibleComponent( "autotracking" ) ); // disable autotracking
        }
        else {
            add( new InvisibleComponent( "debug-page-class" ) );
            add( new Label( "autotracking" ) );
        }
        add( new RawBodyLabel( "debug-page-host", "<!-- host " + getPhetCycle().getWebRequest().getHttpServletRequest().getServerName() + " -->" ) );
    }

    public Locale getMyLocale() {
        return myLocale;
    }

    /**
     * @return The prefix of the URL, which generally represents /XX/ where XX is the locale.
     */
    public String getMyPrefix() {
        return prefix;
    }

    /**
     * @return The relative path underneath the prefix. Thus for a URL of http://phet.colorado.edu/en/simulations/new,
     *         this would return 'simulations/new' (the prefix would be /en/)
     */
    public String getMyPath() {
        if ( path == null ) {
            return "";
        }
        return path;
    }

    /**
     * @return Everything past 'http://phet.colorado.edu' in the URI.
     */
    public String getFullPath() {
        String p = prefix + path;
        String queryString = getPhetCycle().getQueryString();
        if ( queryString != null ) {
            p += "?" + queryString;
        }
        return p;
    }

    public PageContext getPageContext() {
        return new PageContext( getMyPrefix(), getMyPath(), getMyLocale() );
    }

    @Override
    public Locale getLocale() {
        return myLocale;
    }

    /**
     * @param title The string passed in should be properly escaped!
     */
    public void setTitle( String title ) {
        if ( hasTitle() ) {
            remove( titleLabel );
        }
        this.title = title;
        titleLabel = new RawLabel( "page-title", title );
        add( titleLabel );
    }

    public Boolean hasTitle() {
        return this.title != null && titleLabel != null;
    }

    public String getTitle() {
        return title;
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
        logger.debug( "Pre-render: " + ( renderStart - initStart ) + " ms" );
        //logger.debug( "stack trace: ", new Exception() );
        logger.debug( "Debug: page stateless = " + isPageStateless() );
    }

    @Override
    protected void onAfterRender() {
        super.onAfterRender();
        logger.debug( "Render: " + ( System.currentTimeMillis() - renderStart ) + " ms" );
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

    public PhetLocalizer getPhetLocalizer() {
        return (PhetLocalizer) getLocalizer();
    }

    public ServletContext getServletContext() {
        return ( (PhetWicketApplication) getApplication() ).getServletContext();
    }

    public String getStyle( String key ) {
        if ( key.equals( "style.body.id" ) ) {
            if ( getPhetCycle().isOfflineInstaller() ) {
                return "offline-installer-body";
            }
            else {
                return "other-body";
            }
        }
        return "";
    }

    /**
     * If the user is not signed in, redirect them to the sign-in page.
     */
    protected void verifySignedIn() {
        AuthenticatedPage.checkSignedIn();
    }

}