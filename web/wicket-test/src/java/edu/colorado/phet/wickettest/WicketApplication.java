package edu.colorado.phet.wickettest;

import org.apache.wicket.protocol.http.WebApplication;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.util.PhetUrlMapper;
import edu.colorado.phet.wickettest.util.PhetUrlStrategy;

public class WicketApplication extends WebApplication {

    private PhetUrlMapper mapper;

    public Class getHomePage() {
        return SimulationDisplay.class;
    }

    @Override
    protected void init() {
        mapper = new PhetUrlMapper();

        mapper.addMap( "^simulations$", SimulationDisplay.class );
        mapper.addMap( "^all-simulations$", SimulationList.class );

        mount( new PhetUrlStrategy( LocaleUtils.stringToLocale( "en" ), mapper ) );
        mount( new PhetUrlStrategy( LocaleUtils.stringToLocale( "ar" ), mapper ) );
        mount( new PhetUrlStrategy( LocaleUtils.stringToLocale( "el" ), mapper ) );
    }
}
