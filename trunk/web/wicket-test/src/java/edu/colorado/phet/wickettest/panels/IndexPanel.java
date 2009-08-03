package edu.colorado.phet.wickettest.panels;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.StaticImage;
import edu.colorado.phet.wickettest.util.PhetLink;
import edu.colorado.phet.wickettest.content.SimulationDisplay;
import edu.colorado.phet.wickettest.translation.TranslationTestPage;
import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

public class IndexPanel extends PhetPanel {
    public IndexPanel( String id, PageContext context ) {
        super( id, context );

        add( new StaticImage( "ksu-logo", "/images/ksu-logo.gif", null ) );
        add( new StaticImage( "jila-logo", "/images/jila_logo_small.gif", null ) );
        add( new StaticImage( "nsf-logo", "/images/nsf-logo-small.gif", null ) );
        add( new StaticImage( "hewlett-logo", "/images/hewlett-logo-small.jpg", null ) );

        PhetLink imageLink = SimulationDisplay.createLink( "image-link", LocaleUtils.stringToLocale( "en" ) );
        add( imageLink );
        imageLink.add( new StaticImage( "index-animated-screenshot", "/images/mass-spring-lab-animated-screenshot.gif", null ) );

        add( SimulationDisplay.createLink( "play-sims-link", LocaleUtils.stringToLocale( "en" ) ) );
        add( SimulationDisplay.createLink( "es-simulations", LocaleUtils.stringToLocale( "es" ) ) );
        add( SimulationDisplay.createLink( "el-simulations", LocaleUtils.stringToLocale( "el" ) ) );
        add( SimulationDisplay.createLink( "ar-simulations", LocaleUtils.stringToLocale( "ar" ) ) );

        add( new BookmarkablePageLink( "test-translation", TranslationTestPage.class ) );
    }

}
