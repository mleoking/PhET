package edu.colorado.phet.wickettest.content;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.util.PhetLink;
import edu.colorado.phet.wickettest.util.PhetPage;

public class IndexPage extends PhetPage {
    public IndexPage( PageParameters parameters ) {
        super( parameters );

        Label title = new Label( "page-title", "Wicket test index page" );
        add( title );

        add( SimulationDisplay.createLink( "en-simulations", LocaleUtils.stringToLocale( "en" ) ) );
        add( SimulationDisplay.createLink( "es-simulations", LocaleUtils.stringToLocale( "es" ) ) );
        add( SimulationDisplay.createLink( "el-simulations", LocaleUtils.stringToLocale( "el" ) ) );
        add( SimulationDisplay.createLink( "ar-simulations", LocaleUtils.stringToLocale( "ar" ) ) );
    }

    public static PhetLink createLink( String id ) {
        return new PhetLink( id, "/" );
    }
}