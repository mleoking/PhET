package edu.colorado.phet.wickettest;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.resource.loader.ClassStringResourceLoader;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.admin.AdminMainPage;
import edu.colorado.phet.wickettest.content.*;
import edu.colorado.phet.wickettest.data.Translation;
import edu.colorado.phet.wickettest.menu.NavMenu;
import edu.colorado.phet.wickettest.templates.StaticPage;
import edu.colorado.phet.wickettest.translation.PhetLocalizer;
import edu.colorado.phet.wickettest.translation.TranslationUrlStrategy;
import edu.colorado.phet.wickettest.util.PhetRequestCycle;
import edu.colorado.phet.wickettest.util.PhetSession;
import edu.colorado.phet.wickettest.util.PhetUrlMapper;
import edu.colorado.phet.wickettest.util.PhetUrlStrategy;

public class WicketApplication extends WebApplication {

    private PhetUrlMapper mapper;
    private NavMenu menu;

    public Class getHomePage() {
        return IndexPage.class;
    }

    @Override
    protected void init() {
        super.init();

        //getApplicationSettings().setPageExpiredErrorPage( MyExpiredPage.class );
        //getApplicationSettings().setAccessDeniedPage( MyAccessDeniedPage.class );
        //getApplicationSettings().setInternalErrorPage( MyInternalErrorPage.class );

        // add static pages, that are accessed through reflection. this is used so that separate page AND panel classes
        // are not needed for each visual page.
        // NOTE: do this before adding StaticPage into the mapper
        StaticPage.addPanel( TroubleshootingMainPanel.class );
        StaticPage.addPanel( AboutPhetPanel.class );

        // create a url mapper, and add the page classes to it
        mapper = new PhetUrlMapper();
        SimulationDisplay.addToMapper( mapper );
        SimulationPage.addToMapper( mapper );
        StaticPage.addToMapper( mapper );
        IndexPage.addToMapper( mapper );

        // set up the custom localizer
        getResourceSettings().setLocalizer( new PhetLocalizer() );

        // set up the locales that will be accessible
        mount( new PhetUrlStrategy( "en", mapper ) );
        for ( String localeString : getTranslations() ) {
            mount( new PhetUrlStrategy( localeString, mapper ) );
        }
        mount( new TranslationUrlStrategy( "translation", mapper ) );

        mountBookmarkablePage( "admin", AdminMainPage.class );

        // this will remove the default string resource loader. essentially this new one has better locale-handling,
        // so that if a string is not found for a more specific locale (es_MX), it would try "es", then the default
        // properties file
        getResourceSettings().addStringResourceLoader( new ClassStringResourceLoader( WicketApplication.class ) );

        // initialize the navigation menu
        menu = new NavMenu();

        // get rid of wicket:id's and other related tags in the produced HTML.
        getMarkupSettings().setStripWicketTags( true );

        //remove thread monitoring from resource watcher
        //enable this line to run under GAE
//        this.getResourceSettings().setResourcePollFrequency(null);
    }

    public static List<String> getTranslations() {
        //return Arrays.asList( "ar", "es", "el" );
        return Arrays.asList( "zh_CN" );
    }

    public NavMenu getMenu() {
        return menu;
    }

    @Override
    public Session newSession( Request request, Response response ) {
        return new PhetSession( request );
    }

    //enable this override to run under GAE
//    	@Override
//	protected ISessionStore newSessionStore()
//	{
//		return new HttpSessionStore(this);
//	}

    @Override
    public RequestCycle newRequestCycle( Request request, Response response ) {
        return new PhetRequestCycle( this, (WebRequest) request, response );
    }

    public static Locale getDefaultLocale() {
        return LocaleUtils.stringToLocale( "en" );
    }

    public void addTranslation( Translation translation ) {
        getResourceSettings().getLocalizer().clearCache();
    }

    public void removeTranslation( Translation translation ) {
        getResourceSettings().getLocalizer().clearCache();
    }
}
