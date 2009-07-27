package edu.colorado.phet.wickettest;

import org.apache.wicket.protocol.http.WebApplication;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.content.IndexPage;
import edu.colorado.phet.wickettest.content.SimulationDisplay;
import edu.colorado.phet.wickettest.content.SimulationList;
import edu.colorado.phet.wickettest.content.SimulationPage;
import edu.colorado.phet.wickettest.menu.NavMenu;
import edu.colorado.phet.wickettest.test.SubPage;
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

        mapper.addMap( SimulationDisplay.getMappingString(), SimulationDisplay.class );
        mapper.addMap( SimulationList.getMappingString(), SimulationList.class );
        mapper.addMap( SimulationPage.getMappingString(), SimulationPage.class, SimulationPage.getMappingParameters() );
        mapper.addMap( SubPage.getMappingString(), SubPage.class );

        mount( new PhetUrlStrategy( LocaleUtils.stringToLocale( "en" ), mapper ) );
        mount( new PhetUrlStrategy( LocaleUtils.stringToLocale( "es" ), mapper ) );
        mount( new PhetUrlStrategy( LocaleUtils.stringToLocale( "el" ), mapper ) );
        mount( new PhetUrlStrategy( LocaleUtils.stringToLocale( "ar" ), mapper ) );

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

}
