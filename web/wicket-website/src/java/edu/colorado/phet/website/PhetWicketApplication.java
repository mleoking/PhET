package edu.colorado.phet.website;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.common.phetcommon.util.PhetLocales;
import edu.colorado.phet.website.admin.AdminMainPage;
import edu.colorado.phet.website.admin.AdminNewInstallerPage;
import edu.colorado.phet.website.admin.deploy.DeployProjectPage;
import edu.colorado.phet.website.admin.deploy.DeployTranslationPage;
import edu.colorado.phet.website.admin.doc.TechnicalDocPage;
import edu.colorado.phet.website.authentication.*;
import edu.colorado.phet.website.cache.InstallerCache;
import edu.colorado.phet.website.content.*;
import edu.colorado.phet.website.content.about.*;
import edu.colorado.phet.website.content.contribution.*;
import edu.colorado.phet.website.content.getphet.FullInstallPanel;
import edu.colorado.phet.website.content.getphet.OneAtATimePanel;
import edu.colorado.phet.website.content.getphet.RunOurSimulationsPanel;
import edu.colorado.phet.website.content.search.SearchResultsPage;
import edu.colorado.phet.website.content.simulations.*;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingFlashPanel;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingJavaPanel;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingJavascriptPanel;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingMainPanel;
import edu.colorado.phet.website.content.workshops.UgandaWorkshopPhotosPanel;
import edu.colorado.phet.website.content.workshops.UgandaWorkshopsPanel;
import edu.colorado.phet.website.content.workshops.WorkshopsPanel;
import edu.colorado.phet.website.data.Translation;
import edu.colorado.phet.website.menu.NavMenu;
import edu.colorado.phet.website.notification.NotificationHandler;
import edu.colorado.phet.website.services.PhetInfoServicePage;
import edu.colorado.phet.website.services.SimJarRedirectPage;
import edu.colorado.phet.website.templates.StaticPage;
import edu.colorado.phet.website.test.NestedFormTest;
import edu.colorado.phet.website.translation.PhetLocalizer;
import edu.colorado.phet.website.translation.TranslationMainPage;
import edu.colorado.phet.website.translation.TranslationUrlStrategy;
import edu.colorado.phet.website.util.*;

/**
 * Main entry and configuration point for the Wicket-based PhET website. Initializes pages (and the mappings), along
 * with many other things.
 */
public class PhetWicketApplication extends WebApplication {

    private PhetUrlMapper mapper;
    private NavMenu menu;
    private WebsiteProperties websiteProperties;

    // TODO: flesh out and improve thread-safeness of translations part
    private List<Translation> translations = new LinkedList<Translation>();

    private static final Logger logger = Logger.getLogger( PhetWicketApplication.class.getName() );

    /**
     * Wicket likes to hardwire the home page in
     *
     * @return The home page class
     */
    public Class getHomePage() {
        return IndexPage.class;
    }

    @Override
    protected void init() {
        super.init();

        websiteProperties = new WebsiteProperties( getServletContext() );

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
        StaticPage.addPanel( AboutLicensingPanel.class );
        StaticPage.addPanel( AboutSponsorsPanel.class );
        StaticPage.addPanel( TeacherIdeasPanel.class );
        StaticPage.addPanel( ContributionGuidelinesPanel.class );
        StaticPage.addPanel( DonatePanel.class );
        StaticPage.addPanel( ForTranslatorsPanel.class );
        StaticPage.addPanel( TranslationUtilityPanel.class );
        StaticPage.addPanel( AboutNewsPanel.class );
        StaticPage.addPanel( ClassroomUsePanel.class );
        StaticPage.addPanel( UgandaWorkshopsPanel.class );
        StaticPage.addPanel( UgandaWorkshopPhotosPanel.class );

        // create a url mapper, and add the page classes to it
        mapper = new PhetUrlMapper();
        ByGradeLevelPage.addToMapper( mapper ); // always add this before CategoryPage so it can display the icons
        CategoryPage.addToMapper( mapper );
        SimulationPage.addToMapper( mapper );
        SimulationChangelogPage.addToMapper( mapper );
        StaticPage.addToMapper( mapper );
        IndexPage.addToMapper( mapper );
        SimsByKeywordPage.addToMapper( mapper );
        SearchResultsPage.addToMapper( mapper );
        EditProfilePage.addToMapper( mapper );
        SignInPage.addToMapper( mapper );
        SignOutPage.addToMapper( mapper );
        RegisterPage.addToMapper( mapper );
        ContributionPage.addToMapper( mapper );
        ContributionBrowsePage.addToMapper( mapper );
        ContributionCreatePage.addToMapper( mapper );
        ContributionEditPage.addToMapper( mapper );
        ContributionManagePage.addToMapper( mapper );
        TranslatedSimsPage.addToMapper( mapper );
        TranslationMainPage.addToMapper( mapper );
        AddContributionCommentPage.addToMapper( mapper );
        NominateContributionPage.addToMapper( mapper );

        // don't error if a string isn't found
        getResourceSettings().setThrowExceptionOnMissingResource( false );

        // set up the custom localizer
        getResourceSettings().setLocalizer( PhetLocalizer.get() );

        // set up the locales that will be accessible
        initializeTranslations();
        mount( new PhetUrlStrategy( "en", mapper ) );
        for ( Translation translation : translations ) {
            mount( new PhetUrlStrategy( LocaleUtils.localeToString( translation.getLocale() ), mapper ) );
        }
        mount( new TranslationUrlStrategy( "translation", mapper ) );

        mountBookmarkablePage( "admin", AdminMainPage.class );
        mountBookmarkablePage( "admin/deploy", DeployProjectPage.class );
        mountBookmarkablePage( "admin/deploy-translation", DeployTranslationPage.class );
        mountBookmarkablePage( "admin/new-installer", AdminNewInstallerPage.class );
        mountBookmarkablePage( "admin/tech-docs", TechnicalDocPage.class );

        // services
        mountBookmarkablePage( "services/phet-info", PhetInfoServicePage.class );
        mountBookmarkablePage( "services/phet-info.php", PhetInfoServicePage.class );
        mountBookmarkablePage( "services/sim-jar-redirect", SimJarRedirectPage.class );
        mountBookmarkablePage( "services/sim-jar-redirect.php", SimJarRedirectPage.class );
        mountBookmarkablePage( "robots.txt", RobotsTxtPage.class );

        // FOR XSS TESTING
        //mountBookmarkablePage( "xsstest", PreventXSSTest.class );

        // For nested for mtesting
        mountBookmarkablePage( "nested-form-test", NestedFormTest.class );

        // this will remove the default string resource loader. essentially this new one has better locale-handling,
        // so that if a string is not found for a more specific locale (es_MX), it would try "es", then the default
        // properties file
        // NOTE: This may break in Wicket 1.4 (hopefully fixed?)
        assert ( getResourceSettings().getStringResourceLoaders().size() == 1 );
        getResourceSettings().addStringResourceLoader( 0, new ClassStringResourceLoader( PhetWicketApplication.class ) );

        // initialize the navigation menu
        menu = new NavMenu();

        // get rid of wicket:id's and other related tags in the produced HTML.
        getMarkupSettings().setStripWicketTags( true );

        mount( new HybridUrlCodingStrategy( "/error", ErrorPage.class ) );
        mount( new HybridUrlCodingStrategy( "/error/404", NotFoundPage.class ) );
        mount( new HybridUrlCodingStrategy( "/activities", BlankPage.class ) );

        logger.info( "Running as: " + getConfigurationType() );
        logger.debug( "Detected phet-document-root: " + getWebsiteProperties().getPhetDocumentRoot().getAbsolutePath() );

        if ( getConfigurationType().equals( Application.DEPLOYMENT ) ) {
            Level loggerLevel = Level.WARN;
            logger.info( "Setting logging level for edu.colorado.phet.website to " + loggerLevel );
            Logger baseLogger = Logger.getLogger( "edu.colorado.phet.website" );
            baseLogger.setLevel( loggerLevel );
        }

        NotificationHandler.initialize();

        SearchUtils.initialize();

        BuildLocalProperties.initFromPropertiesFile( getWebsiteProperties().getBuildLocalPropertiesFile() );

        setInstallerTimestampFromFile();

    }

    private void setInstallerTimestampFromFile() {
        InstallerCache.setDefault();
        try {
            File propsFile = new File( getWebsiteProperties().getPhetDocumentRoot(), "installer/version.properties" );
            Properties props = new Properties();
            FileInputStream stream = new FileInputStream( propsFile );
            props.load( stream );
            long ver = Long.valueOf( props.getProperty( "timestamp" ) );
            InstallerCache.setTimestamp( ver );
            stream.close();
        }
        catch( RuntimeException e ) {
            //e.printStackTrace();
            logger.warn( "setInstallerTimestamp runtime exception" );
        }
        catch( IOException e ) {
            //e.printStackTrace();
            logger.warn( "setInstallerTimestamp IO exception" );
        }
    }

    public boolean isDeployment() {
        return getConfigurationType().equals( Application.DEPLOYMENT );
    }

    public boolean isDevelopment() {
        return getConfigurationType().equals( Application.DEVELOPMENT );
    }

    /*---------------------------------------------------------------------------*
    * server-specific configuration locations
    *----------------------------------------------------------------------------*/

    public File getActivitiesRoot() {
        return new File( getWebsiteProperties().getPhetDownloadRoot(), "activities" );
    }

    public File getTeachersGuideRoot() {
        return new File( getWebsiteProperties().getPhetDownloadRoot(), "teachers-guide" );
    }

    public File getSimulationsRoot() {
        return new File( getWebsiteProperties().getPhetDocumentRoot(), "sims" );
    }

    public File getStagingRoot() {
        return new File( getWebsiteProperties().getPhetDocumentRoot(), "staging" );
    }

    public String getActivitiesLocation() {
        return getWebsiteProperties().getPhetDownloadLocation() + "/activities";
    }

    public String getTeachersGuideLocation() {
        return getWebsiteProperties().getPhetDownloadLocation() + "/teachers-guide";
    }

    public WebsiteProperties getWebsiteProperties() {
        return websiteProperties;
    }

    /*---------------------------------------------------------------------------*
    * supported locales and translations
    *----------------------------------------------------------------------------*/

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

    public boolean isVisibleLocale( Locale locale ) {
        if ( locale.equals( getDefaultLocale() ) ) {
            return true;
        }
        for ( Translation translation : translations ) {
            if ( translation.getLocale().equals( locale ) ) {
                return true;
            }
        }
        return false;
    }

    public synchronized NavMenu getMenu() {
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

    public static PhetWicketApplication get() {
        return (PhetWicketApplication) WebApplication.get();
    }

    @Override
    protected void onDestroy() {
        logger.info( "Shutting down PhetWicketApplication" );
        NotificationHandler.destroy();
        SearchUtils.destroy();

        logger.info( HibernateUtils.getInstance().getCache().getClass().getCanonicalName() );
    }

    public static String getProductionServerName() {
        return "phet.colorado.edu";
    }

}
