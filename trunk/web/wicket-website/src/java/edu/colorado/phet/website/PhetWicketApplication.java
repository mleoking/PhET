package edu.colorado.phet.website;

import java.io.File;
import java.util.*;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.wicket.*;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.request.IRequestCycleProcessor;
import org.apache.wicket.request.target.coding.HybridUrlCodingStrategy;
import org.apache.wicket.resource.loader.ClassStringResourceLoader;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.common.phetcommon.util.PhetLocales;
import edu.colorado.phet.website.admin.AdminMainPage;
import edu.colorado.phet.website.authentication.*;
import edu.colorado.phet.website.content.*;
import edu.colorado.phet.website.content.about.*;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingFlashPanel;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingJavaPanel;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingJavascriptPanel;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingMainPanel;
import edu.colorado.phet.website.data.Translation;
import edu.colorado.phet.website.menu.NavMenu;
import edu.colorado.phet.website.templates.StaticPage;
import edu.colorado.phet.website.translation.PhetLocalizer;
import edu.colorado.phet.website.translation.TranslationUrlStrategy;
import edu.colorado.phet.website.util.*;

/**
 * Main entry and configuration point for the Wicket-based PhET website. Initializes pages (and the mappings), along
 * with many other things.
 */
public class PhetWicketApplication extends WebApplication {

    private PhetUrlMapper mapper;
    private NavMenu menu;

    private List<Translation> translations = new LinkedList<Translation>();

    private static Logger logger = Logger.getLogger( PhetWicketApplication.class.getName() );

    public static final String PHET_DOCUMENT_ROOT = "phet-document-root";
    public static final String PHET_DOWNLOAD_ROOT = "phet-download-root";

    public Class getHomePage() {
        return IndexPage.class;
    }

    @Override
    protected void init() {
        super.init();

        // set up error pages
        getApplicationSettings().setPageExpiredErrorPage( ErrorPage.class );
        getApplicationSettings().setAccessDeniedPage( ErrorPage.class );
        getApplicationSettings().setInternalErrorPage( ErrorPage.class );

        // add static pages, that are accessed through reflection. this is used so that separate page AND panel classes
        // are not needed for each visual page.
        // NOTE: do this before adding StaticPage into the mapper. Checked, and violation will result in a fatal error. 
        StaticPage.addPanel( TroubleshootingMainPanel.class );
        StaticPage.addPanel( AboutMainPanel.class );
        StaticPage.addPanel( WorkshopsPanel.class );
        StaticPage.addPanel( ContributePanel.class );
        StaticPage.addPanel( RunOurSimulationsPanel.class );
        StaticPage.addPanel( FullInstallPanel.class );
        StaticPage.addPanel( OneAtATimePanel.class );
        StaticPage.addPanel( ResearchPanel.class );
        StaticPage.addPanel( TroubleshootingJavaPanel.class );
        StaticPage.addPanel( TroubleshootingFlashPanel.class );
        StaticPage.addPanel( TroubleshootingJavascriptPanel.class );
        StaticPage.addPanel( AboutSourceCodePanel.class );
        StaticPage.addPanel( AboutLegendPanel.class );
        StaticPage.addPanel( AboutContactPanel.class );
        StaticPage.addPanel( AboutWhoWeArePanel.class );
        StaticPage.addPanel( AboutLicensingPanel.class );
        StaticPage.addPanel( AboutSponsorsPanel.class );
        StaticPage.addPanel( TranslatedSimsPanel.class );

        // create a url mapper, and add the page classes to it
        mapper = new PhetUrlMapper();
        SimulationDisplay.addToMapper( mapper );
        SimulationPage.addToMapper( mapper );
        StaticPage.addToMapper( mapper );
        IndexPage.addToMapper( mapper );
        SimsByKeywordPage.addToMapper( mapper );
        SearchResultsPage.addToMapper( mapper );
        EditProfilePage.addToMapper( mapper );
        SignInPage.addToMapper( mapper );
        SignOutPage.addToMapper( mapper );
        RegisterPage.addToMapper( mapper );

        // set up the custom localizer
        getResourceSettings().setLocalizer( new PhetLocalizer() );

        // set up the locales that will be accessible
        initializeTranslations();
        mount( new PhetUrlStrategy( "en", mapper ) );
        for ( Translation translation : translations ) {
            mount( new PhetUrlStrategy( LocaleUtils.localeToString( translation.getLocale() ), mapper ) );
        }
        mount( new TranslationUrlStrategy( "translation", mapper ) );

        mountBookmarkablePage( "admin", AdminMainPage.class );

        // this will remove the default string resource loader. essentially this new one has better locale-handling,
        // so that if a string is not found for a more specific locale (es_MX), it would try "es", then the default
        // properties file
        // NOTE: This may break in Wicket 1.4
        getResourceSettings().addStringResourceLoader( new ClassStringResourceLoader( PhetWicketApplication.class ) );

        // initialize the navigation menu
        menu = new NavMenu();

        // get rid of wicket:id's and other related tags in the produced HTML.
        getMarkupSettings().setStripWicketTags( true );

        mount( new HybridUrlCodingStrategy( "/error", ErrorPage.class ) );
        mount( new HybridUrlCodingStrategy( "/error/404", NotFoundPage.class ) );
        mount( new HybridUrlCodingStrategy( "/activities", BlankPage.class ) );

        logger.info( "Running as: " + getConfigurationType() );
        logger.info( "Detected phet-document-root: " + getServletContext().getInitParameter( PHET_DOCUMENT_ROOT ) );

        if ( getConfigurationType().equals( Application.DEPLOYMENT ) ) {
            Level loggerLevel = Level.WARN;
            logger.info( "Setting logging level for edu.colorado.phet.website to " + loggerLevel );
            Logger baseLogger = Logger.getLogger( "edu.colorado.phet.website" );
            baseLogger.setLevel( loggerLevel );
        }

    }

    public File getPhetDocumentRoot() {
        return getFileFromLocation( getServletContext().getInitParameter( PHET_DOCUMENT_ROOT ) );
    }

    public File getPhetDownloadRoot() {
        return getFileFromLocation( getServletContext().getInitParameter( PHET_DOWNLOAD_ROOT ) );
    }

    public static File getFileFromLocation( String location ) {
        if ( location == null ) {
            return null;
        }
        File file = new File( location );
        if ( !file.exists() ) {
            return null;
        }
        return file;
    }

    private PhetLocales supportedLocales = null;

    public PhetLocales getSupportedLocales() {
        if ( supportedLocales == null ) {
            supportedLocales = PhetLocales.getInstance();
        }
        return supportedLocales;
    }

    private void initializeTranslations() {
        org.hibernate.Session session = HibernateUtils.getInstance().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            translations = HibernateUtils.getVisibleTranslations( session );

            tx.commit();
        }
        catch( RuntimeException e ) {
            logger.warn( "WARNING: exception:\n" + e );
            if ( tx != null && tx.isActive() ) {
                try {
                    tx.rollback();
                }
                catch( HibernateException e1 ) {
                    logger.error( "ERROR: Error rolling back transaction", e1 );
                }
                throw e;
            }
        }
        session.close();

        sortTranslations();
    }

    private void sortTranslations() {
        Collections.sort( translations, new Comparator<Translation>() {
            public int compare( Translation a, Translation b ) {
                return a.getLocale().getDisplayName().compareTo( b.getLocale().getDisplayName() );
            }
        } );
    }

    public List<String> getTranslationLocaleStrings() {
        // TODO: maybe cache this list if it's called a lot?
        List<String> ret = new LinkedList<String>();
        for ( Translation translation : translations ) {
            ret.add( LocaleUtils.localeToString( translation.getLocale() ) );
        }
        return ret;
    }

    public NavMenu getMenu() {
        return menu;
    }

    @Override
    public Session newSession( Request request, Response response ) {
        return new PhetSession( request );
    }

    @Override
    public RequestCycle newRequestCycle( Request request, Response response ) {
        return new PhetRequestCycle( this, (WebRequest) request, response );
    }

    private static Locale defaultLocale = LocaleUtils.stringToLocale( "en" );

    public static Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void addTranslation( Translation translation ) {
        String localeString = LocaleUtils.localeToString( translation.getLocale() );
        logger.info( "Adding translation for " + localeString );
        getResourceSettings().getLocalizer().clearCache();
        translations.add( translation );
        mount( new PhetUrlStrategy( localeString, mapper ) );
        sortTranslations();
    }

    public void removeTranslation( Translation translation ) {
        String localeString = LocaleUtils.localeToString( translation.getLocale() );
        logger.info( "Removing translation for " + localeString );
        getResourceSettings().getLocalizer().clearCache();
        int oldNumTranslations = translations.size();
        for ( Translation tr : translations ) {
            if ( tr.getId() == translation.getId() ) {
                translations.remove( tr );
                break;
            }
        }
        if ( translations.size() != oldNumTranslations - 1 ) {
            throw new RuntimeException( "Did not correctly remove old translation" );
        }
        unmount( localeString );
        sortTranslations();
    }

    @Override
    /**
     * Override request cycle processor, so we can handle things like redirection
     */
    protected IRequestCycleProcessor newRequestCycleProcessor() {
        return new PhetCycleProcessor();
    }

}
