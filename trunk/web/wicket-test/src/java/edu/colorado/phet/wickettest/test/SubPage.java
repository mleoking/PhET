package edu.colorado.phet.wickettest.test;

import org.apache.wicket.PageParameters;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.panels.SimulationListPanel;
import edu.colorado.phet.wickettest.util.PhetPage;

public class SubPage extends PhetPage {
    public SubPage( PageParameters parameters ) {
        super( parameters );

        add( new SimulationListPanel( "page-1", LocaleUtils.stringToLocale( "en" ) ) );
        add( new SimulationListPanel( "page-2", LocaleUtils.stringToLocale( "ar" ) ) );

    }

}