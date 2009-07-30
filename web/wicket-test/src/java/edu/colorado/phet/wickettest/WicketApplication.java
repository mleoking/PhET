package edu.colorado.phet.wickettest;

import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.resource.loader.ClassStringResourceLoader;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.content.IndexPage;
import edu.colorado.phet.wickettest.content.SimulationDisplay;
import edu.colorado.phet.wickettest.content.SimulationList;
import edu.colorado.phet.wickettest.content.SimulationPage;
import edu.colorado.phet.wickettest.menu.NavMenu;
import edu.colorado.phet.wickettest.translation.TranslationStringResourceLoader;
import edu.colorado.phet.wickettest.util.PhetRequestCycle;
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
        mapper = new PhetUrlMapper();

        SimulationDisplay.addToMapper( mapper );
        mapper.addMap( SimulationList.getMappingString(), SimulationList.class );
        SimulationPage.addToMapper( mapper );

        mount( new PhetUrlStrategy( LocaleUtils.stringToLocale( "en" ), mapper ) );
        mount( new PhetUrlStrategy( LocaleUtils.stringToLocale( "es" ), mapper ) );
        mount( new PhetUrlStrategy( LocaleUtils.stringToLocale( "el" ), mapper ) );
        mount( new PhetUrlStrategy( LocaleUtils.stringToLocale( "ar" ), mapper ) );

        getResourceSettings().addStringResourceLoader( new TranslationStringResourceLoader() );
        getResourceSettings().addStringResourceLoader( new ClassStringResourceLoader( WicketApplication.class ) );

        menu = new NavMenu();

        // get rid of wicket:id's and other related tags in the produced HTML.
        getMarkupSettings().setStripWicketTags( true );

        //remove thread monitoring from resource watcher
        //enable this line to run under GAE
//        this.getResourceSettings().setResourcePollFrequency(null);
    }

    public NavMenu getMenu() {
        return menu;
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
}
